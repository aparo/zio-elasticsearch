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

package zio.schema.elasticsearch

import java.time.OffsetDateTime
import zio.schema.elasticsearch.annotations.{ KeyField, KeyManagement, KeyPostProcessing }
import zio.common.{ OffsetDateTimeHelper, StringUtils, UUID }
import zio.exception.{ FrameworkException, MissingFieldException }
import zio.schema.elasticsearch.SchemaNames._
import zio.json.ast.Json
import zio.json._
import io.circe.derivation.annotations.{ JsonKey, jsonDerive }
import zio.schema.{ Schema, TypeId }

import scala.annotation.StaticAnnotation

/**
 * A ElasticSearchSchema rappresentation
 * @param name
 *   name of the ElasticSearchSchema
 * @param module
 *   module of the ElasticSearchSchema
 * @param `type`
 *   type of the ElasticSearchSchema
 * @param version
 *   version of ElasticSearchSchema
 * @param description
 *   the description of the ElasticSearchSchema
 * @param active
 *   if this entity is active
 * @param labels
 *   a list of labels associated to the ElasticSearchSchema
 * @param creationDate
 *   the creation date of the ElasticSearchSchema
 * @param creationUser
 *   the reference of the user that created the ElasticSearchSchema
 * @param modificationDate
 *   the modification date of the ElasticSearchSchema
 * @param modificationUser
 *   the reference of last user that changed the ElasticSearchSchema
 * @param key
 *   key management components
 * @param index
 *   index management components
 * @param isRoot
 *   if the object is root
 * @param className
 *   possinble class Name
 * @param properties
 *   the sub fields
 */
@jsonDerive
final case class ElasticSearchSchema(
  name: String,
  module: String,
  version: Int = 1,
  `type`: String = "object",
  description: String = "",
  @jsonField(AUTO_OWNER) autoOwner: Boolean = false,
  active: Boolean = true,
  labels: List[String] = Nil,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  key: KeyManagement = KeyManagement.empty,
  index: GlobalIndexProperties = GlobalIndexProperties(),
  @jsonField(IS_ROOT) isRoot: Boolean = false,
  @jsonField(CLASS_NAME) className: Option[String] = None,
  delta: List[Option[DeltaRule]] = Nil,
  properties: List[SchemaField] = Nil
) extends EditingTrait
    with Validable[ElasticSearchSchema] {
  // Latest version id
  def id: String = s"$module.$name"

  // Versioned id
  def idVersioned: String = s"$module.$name.$version"

  def required: List[String] = properties.filter(_.required).map(_.name)

  lazy val indexRequireType: Boolean = index.requireType
  lazy val indexRequireTypePrefix: String = name + SchemaNames.SINGLE_STORAGE_SEPARATOR

  /**
   * Check if there is a ownerId field and return it
   * @return
   *   Return an optional Owner ID
   */
  def ownerField: Option[SchemaField] = properties.find { field =>
    field.isInstanceOf[StringSchemaField] &&
    field.asInstanceOf[StringSchemaField].subType.contains(StringSubType.UserId)
  }

  /**
   * Return if this ElasticSearchSchema is filtrable with an UserID
   * @return
   *   a boolean
   */
  lazy val isOwnerFiltrable: Boolean = autoOwner && ownerField.isDefined

  private def extractKey(json: Json.Obj): String = {
    val keyResult = if (key == KeyManagement.empty) {
      UUID.randomBase64UUID()
    } else {
      val components = key.parts.flatMap {
        case k: KeyField =>
          for {
            //            schemafield <- this.properties.find(_.name == k.field)
            jValue <- json.apply(k.field)
          } yield postProcessScripts(
            jValue.noSpaces.stripPrefix("\"").stripSuffix("\""),
            k.postProcessing
          )
      }
      val keyValue = components.mkString(key.separator.getOrElse(""))
      postProcessScripts(keyValue, key.postProcessing)
    }

    validateId(keyResult)
  }

  /**
   * Add a single storage prefix based on the name of the sschema
   * @param value
   *   the value to enrich
   * @return
   *   the value processed
   */
  def cleanId(value: String): String = if (this.indexRequireType && value.startsWith(indexRequireTypePrefix)) {
    value.substring(indexRequireTypePrefix.length)
  } else {
    value
  }

  /**
   * Remove a single storage prefix based on the name of the sschema
   * @param value
   *   the value to enrich
   * @return
   *   the value processed
   */
  def validateId(value: String): String = if (this.indexRequireType && !value.startsWith(indexRequireTypePrefix)) {
    indexRequireTypePrefix + value
  } else {
    value
  }

  private def postProcessScripts(
    keyValue: String,
    postprocessing: List[KeyPostProcessing]
  ): String = {
    var result = keyValue
    import StringUtils._
    postprocessing.foreach {
      case KeyPostProcessing.LowerCase => result = result.toLowerCase
      case KeyPostProcessing.UpperCase => result = result.toUpperCase
      case KeyPostProcessing.Slug      => result = result.slug
      case KeyPostProcessing.Hash      => result = result.sha256Hash
      case KeyPostProcessing(_, _)     => //TODO implement generic
    }
    result
  }

  /**
   * Resolve an id given an Json.Obj
   * @param json
   *   the json object to be used
   * @param optionalID
   *   an optional id
   * @return
   *   a valid id
   */
  def resolveId(json: Json.Obj, optionalID: Option[String]): String = {
    val rId = optionalID.getOrElse(extractKey(json))
    if (indexRequireType && !rId.startsWith(indexRequireTypePrefix)) {
      indexRequireTypePrefix + rId
    } else rId
  }

  def getField(name: String): Either[MissingFieldException, SchemaField] =
    if (name.contains(".")) {
      val tokens = name.split('.')
      var result = getField(tokens.head)
      var i = 1
      while (i < tokens.length - 1 && result.isRight) {
        result = result.flatMap(_.getField(tokens(i)))
        i = i + 1
      }
      result

    } else {
      properties.find(_.name == name) match {
        case Some(x) => Right(x)
        case None =>
          Left(MissingFieldException(s"Missing Field $name"))
      }
    }

  /**
   * We validate the ElasticSearchSchema. Common actions are:
   *   - adding missing values
   *   - checking values
   * @return
   *   a validate entity or the exception
   */
  override def validate(): Either[FrameworkException, ElasticSearchSchema] = {
    val iterator = delta.iterator
    var result: Either[FrameworkException, ElasticSearchSchema] = Right(this)
    while (iterator.hasNext && result.isRight) {
      val elem = iterator.next()
      if (elem.isDefined) {
        val res = getField(elem.get.field)
        if (res.isLeft) {
          result = Left(MissingFieldException(s"delta is missing ${elem.get.field}"))
        } //else Right(this)
      }
    }
    result
  }

  /* Returns a map for convert originaName to actual names */
  def extractNameConversions: Map[String, String] =
    properties.flatMap(f => f.originalName.map(fo => fo -> f.name)).toMap

}

object ElasticSearchSchema {
  /* an empty ElasticSearchSchema used a placeholder */
  lazy val empty = ElasticSearchSchema("empty", "empty")

  def gen[A](implicit zschema: Schema[A]): ElasticSearchSchema = {
    zschema match {
      case record: Schema.Record[_] =>
        var name = "empty"
        var module = "empty"
        record.id match {
          case TypeId.Nominal(packageName, objectNames, typeName) =>
            module = packageName.mkString(".")
            name = typeName
          case TypeId.Structural =>
        }
        val classAnnotationManager = new ClassAnnotationManager(s"$module.$name", record.annotations.toList.collect {
          case a: StaticAnnotation => a
        })
//      case enum: Schema.Enum[_] => ???
//      case collection: Schema.Collection[_, _] => ???
//      case Schema.Transform(codec, f, g, annotations, identity) => ???
//      case Schema.Primitive(standardType, annotations) => ???
//      case Schema.Optional(codec, annotations) => ???
//      case Schema.Fail(message, annotations) => ???
//      case Schema.Tuple(left, right, annotations) => ???
//      case Schema.EitherSchema(left, right, annotations) => ???
//      case Schema.Lazy(schema0) => ???
//      case Schema.Meta(ast, annotations) => ???
//      case Schema.Dynamic(annotations) => ???
//      case Schema.SemiDynamic(defaultValue, annotations) => ???

    }

    //    zschema.
//    ElasticSearchSchema(name=)
    empty
  }
}
