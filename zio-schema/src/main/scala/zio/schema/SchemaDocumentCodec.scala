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

import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

import zio.schema.generic.{ DerivationHelperTrait, DerivationJsonTrait }

class SchemaDocumentCodec extends scala.annotation.StaticAnnotation {

  def macroTransform(annottees: Any*): Any =
    macro SchemaDocumentCodecMacros.mdocumentMacro
}

private[schema] class SchemaDocumentCodecMacros(val c: blackbox.Context)
    extends DerivationHelperTrait
    with DerivationJsonTrait {

  import c.universe._

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
         ..${codec(clsDef, Nil)}
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

      q"""
       $newClsDef
       object $objName extends { ..$objEarlyDefs } with ..$newParents { $objSelf =>
          ..$objDefs
          ..${codec(clsDef, objDefs)}
          ..${schemaCodec(clsDef, objDefs.map(_.asInstanceOf[Tree]))}
       }
       """

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
    val supports = StorageSupport.classNames()
    val storages = annots.filter(s => supports.contains(s))
    if (storages.isEmpty)
      c.abort(
        c.enclosingPosition,
        s"Invalid annotation: Undefined data storage for ${clsDef.name.toString}: Use one of more of ${supports.mkString(", ")}"
      )

    storages
  }

  protected def enrichClassStorages(
    clsDef: ClassDef,
    storages: List[String]
  ): ClassDef = {
    var currentParents = clsDef.impl.parents
    val extrabody = new ListBuffer[Tree]()

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

    def addToParent(fullClassName: String, objClassName: String, metaName: String): Unit = {
      val tokens = fullClassName.split('.')
      val className = tokens.last
      if (!currentParents.exists(_.toString().contains(className))) {
        currentParents +:= buildTypeName(fullClassName, clsDef.name)
        extrabody += ValDef(
          Modifiers(),
          TermName(metaName),
          buildTypeName(objClassName, clsDef.name),
          Ident(TermName(clsDef.name.toString))
        )
      }
    }

    storages.foreach { storage =>
      StorageSupport.getByName(storage).foreach { ann =>
        addToParent(ann.modelClass, ann.objectClass, ann.metaName)
      }
    }

    ClassDef(
      clsDef.mods,
      clsDef.name,
      clsDef.tparams,
      Template(currentParents, clsDef.impl.self, clsDef.impl.body ++ extrabody.toList)
    )
  }

  private def buildTypeName(fullname: String, typeName: TypeName): Tree = {
    val tokens = fullname.split('.')
    val className = tokens.last
    val select: Tree = tokens.init
      .dropWhile(_ == "_root_")
      .foldLeft[Tree](Ident(termNames.ROOTPKG))((t: Tree, str: String) => Select(t, TermName(str)))
    AppliedTypeTree(
      Select(
        select,
        TypeName(className)
      ),
      List(Ident(typeName))
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

    def addToParent(fullClassName: String): Unit = {
      val className = fullClassName.split('.').last
      if (!currentParents.exists(_.toString().contains(className))) {

        currentParents +:= buildTypeName(fullClassName, clsDef.name)
      }
    }
    storages.foreach { storage =>
      StorageSupport.getByName(storage).foreach { ann =>
        addToParent(ann.objectClass)
      }
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

  protected[this] def productRepr(tpe: Type): Option[ProductRepr] =
    membersFromPrimaryConstr(tpe).map(ProductRepr(_))

  protected[this] def fail(tpe: Type): Nothing = c.abort(
    c.enclosingPosition,
    s"Could not identify primary constructor for $tpe"
  )

  protected[this] case class Member(
    name: TermName,
    decodedName: String,
    tpe: Type,
    keyName: String,
    default: Option[Tree],
    noDefaultValue: Boolean
  )

  protected[this] case class ProductRepr(members: List[Member])

  // Function to extract default values from case class
  protected[this] def caseClassFieldsDefaults(
    tpe: Type
  ): ListMap[String, Option[Tree]] =
    if (tpe.companion == NoType) {
      ListMap()
    } else {
      ListMap(
        tpe.companion
          .member(TermName("apply"))
          .asTerm
          .alternatives
          .find(_.isSynthetic)
          .get
          .asMethod
          .paramLists
          .flatten
          .zipWithIndex
          .map {
            case (field, i) =>
              (
                field.name.toTermName.decodedName.toString, {
                  val method = TermName(s"apply$$default$$${i + 1}")
                  tpe.companion.member(method) match {
                    case NoSymbol => None
                    case _        => Some(q"${tpe.typeSymbol.companion}.$method")
                  }
                }
              )
          }: _*
      )
    }

  private[this] def membersFromPrimaryConstr(tpe: Type): Option[List[Member]] =
    tpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor =>
        val defaults = caseClassFieldsDefaults(tpe)
        m.paramLists.flatten.map { field =>
          val asf = tpe.decl(field.name).asMethod.returnType.asSeenFrom(tpe, tpe.typeSymbol)
          var keyName = field.name.decodedName.toString
          var noDefault = false
          field.annotations.foreach { ann =>
            ann.tree match {
              case Apply(Select(myType2, _), List(value)) =>
                myType2.toString().split('.').last match {
                  case "JsonKey" =>
                    var realValue = value.toString
                    realValue = realValue.substring(1, realValue.length - 1)
                    if (realValue.isEmpty)
                      c.abort(
                        c.enclosingPosition,
                        s"Invalid empty key in $tpe.$field!"
                      )
                    keyName = realValue
                  case "JsonNoDefault" =>
                    noDefault = true
                  case extra =>
                  //                    println(s"extra: $extra")
                }
              case extra =>
                extra.toString().split('.').last match {
                  case "JsonNoDefault()" =>
                    noDefault = true
                  case extra2 =>
                  //                    println(s"extra2: ${showRaw(extra)}")
                  //                    println(s"extra2: ${extra}")
                }
            }
          }

          Member(
            field.name.toTermName,
            field.name.decodedName.toString,
            asf,
            keyName,
            defaults(field.name.decodedName.toString),
            noDefault
          )
        }
    }

}
