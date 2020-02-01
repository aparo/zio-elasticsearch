/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.schema

import zio.schema.generic.DerivationHelperTrait

import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

class SchemaDocumentCodec extends scala.annotation.StaticAnnotation {

  def macroTransform(annottees: Any*): Any =
    macro SchemaDocumentCodecMacros.mdocumentMacro
}

private[schema] class SchemaDocumentCodecMacros(val c: blackbox.Context) extends DerivationHelperTrait {

  import c.universe._

  lazy val annotationStorages: List[String] =
    List(
      "ElasticSearchStorage",
      "MongoDBStorage"
    )

  def mdocumentMacro(annottees: Tree*): Tree =
    constructMdocumentCodec(annottees: _*)

  final def constructMdocumentCodec(annottees: Tree*): Tree = annottees match {
    case List(clsDef: ClassDef) if isCaseClassOrSealed(clsDef) =>
      val storages = retrieveStorages(clsDef)
      val newClsDef = enrichClassStorages(clsDef, storages)
      val newParents = enrichObjectStorages(clsDef, Nil, storages).reverse.tail
      q"""
       $newClsDef
       object ${clsDef.name.toTermName} extends _root_.zio.schema.SchemaMeta[${clsDef.name.toTypeName}] with ..$newParents {
         ..${schemaCodec(clsDef, Nil)}
       }
       """
    case List(
        clsDef: ClassDef,
        q"object $objName extends { ..$objEarlyDefs } with ..$objParents { $objSelf => ..$objDefs }"
        ) if isCaseClassOrSealed(clsDef) =>
      val storages = retrieveStorages(clsDef)
      val newParents = enrichObjectStorages(clsDef, objParents, storages)

      val newClsDef = enrichClassStorages(clsDef, storages)

      val res =
        q"""
       $newClsDef
       object $objName extends { ..$objEarlyDefs } with ..$newParents { $objSelf =>
          ..$objDefs
          ..${schemaCodec(clsDef, objDefs.map(_.asInstanceOf[Tree]))}
       }
       """

      res
    case _ =>
      c.abort(
        c.enclosingPosition,
        "Invalid annotation target: must be a case class or a sealed trait/class"
      )
  }

  protected def retrieveStorages(clsDef: ClassDef): List[String] = {
    //    clsDef.mods.annotations.foreach(s => println(showRaw(s)))
    val annots = clsDef.mods.annotations.flatMap {
      case Apply(
          Select(New(Ident(TypeName(name))), termNames.CONSTRUCTOR),
          _
          ) =>
        Some(name)
      case _ => None
    }
    val storages = annots.filter(s => annotationStorages.contains(s))
    if (storages.isEmpty)
      c.abort(
        c.enclosingPosition,
        s"Invalid annotation: Undefined data storage for ${clsDef.name.toString}: Use one of more of ${annotationStorages
          .mkString(", ")}"
      )

    storages
  }

  protected def enrichClassStorages(
    clsDef: ClassDef,
    storages: List[String]
  ): ClassDef = {
    var currentParents = clsDef.impl.parents
    var body = clsDef.impl.body

    if (!currentParents.exists(_.toString().contains("SchemaDocument["))) {
      currentParents +:= AppliedTypeTree(
        Select(
          Select(
            Select(Ident(termNames.ROOTPKG), TermName("zio")),
            TermName("schema")
          ),
          TypeName("SchemaDocument")
        ),
        List(Ident(clsDef.name))
      )
      currentParents = currentParents.filter(_.toString() != "scala.AnyRef")
    }

    def addToParent(namespace: String, className: String): Unit =
      if (!currentParents.exists(_.toString().contains(className))) {
        currentParents +:= AppliedTypeTree(
          Select(
            Select(
              Select(
                Ident(termNames.ROOTPKG),
                TermName(namespace)
              ),
              TermName("orm")
            ),
            TypeName(className)
          ),
          List(Ident(clsDef.name))
        )
      }

    def addToBody(func: Tree): Unit =
      if (!body.exists(_.toString().contains(func.toString()))) {
        body = body ::: func :: Nil
      }

    storages.foreach {
      case "ElasticSearchStorage" =>
        addToParent("elasticsearch", "ElasticSearchDocument")
        addToBody(q"override def elasticsearchMeta: ElasticSearchMeta[${clsDef.name}]=${clsDef.name.toTermName}")

      case "MongoDBStorage" =>
        addToParent("mongodb", "MongoDBDocument")
    }

    ClassDef(
      clsDef.mods,
      clsDef.name,
      clsDef.tparams,
      Template(currentParents, clsDef.impl.self, body)
    )
  }

  protected def enrichObjectStorages(
    clsDef: ClassDef,
    parents: Seq[Tree],
    storages: List[String]
  ): List[Tree] = {
    var currentParents = parents

    if (!currentParents.exists(_.toString().contains("SchemaMeta["))) {
      currentParents +:= AppliedTypeTree(
        Select(
          Select(
            Select(Ident(termNames.ROOTPKG), TermName("zio")),
            TermName("schema")
          ),
          TypeName("SchemaMeta")
        ),
        List(Ident(clsDef.name))
      )
      currentParents = currentParents.filter(_.toString() != "scala.AnyRef")
    }

    def addToParent(namespace: String, className: String): Unit =
      if (!currentParents.exists(_.toString().contains(className))) {
        currentParents +:= AppliedTypeTree(
          Select(
            Select(
              Select(
                Ident(termNames.ROOTPKG),
                TermName(namespace)
              ),
              TermName("orm")
            ),
            TypeName(className)
          ),
          List(Ident(clsDef.name))
        )
      }

    storages.foreach {
      case "ElasticSearchStorage" =>
        addToParent("elasticsearch", "ElasticSearchMeta")
      case "MongoDBStorage" =>
        addToParent("mongodb", "MongoDBObject")
    }

    currentParents.toList
  }

  protected def schemaCodec(
    clsDef: ClassDef,
    objdefs: Seq[Tree]
  ): List[Tree] = {
    val results = new ListBuffer[Tree]()
    if (!existsImplicit(objdefs, "_schema")) {
      val Type = clsDef.name
      results += q"""implicit val _schema:_root_.zio.schema.generic.JsonSchema[$Type] = _root_.zio.schema.generic.JsonSchema.deriveFor[$Type] """
    }
    if (!existsImplicit(objdefs, "typeClass")) {
      val Type = clsDef.name
      results += q"""val typeClass:Class[$Type] = classOf[$Type]"""
    }

    results.toList
  }

  protected[this] def fail(tpe: Type): Nothing = c.abort(
    c.enclosingPosition,
    s"Could not identify primary constructor for $tpe"
  )

}
