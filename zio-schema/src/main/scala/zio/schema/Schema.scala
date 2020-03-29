/*
 * Copyright 2019-2020 Alberto Paro
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

import java.time.OffsetDateTime

import io.circe._
import zio.common.{ OffsetDateTimeHelper, StringUtils, UUID }
import zio.schema.SchemaNames.{ AUTO_OWNER, CLASS_NAME, IS_ROOT, STORAGES }
import zio.schema.annotations.{ KeyField, KeyManagement, KeyPostProcessing }
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import zio.exception.{ FrameworkException, MissingFieldException }
import zio.schema.SchemaNames._

/**
 * A schema rappresentation
 * @param name name of the schema
 * @param module module of the schema
 * @param `type` type of the schema
 * @param version version of schema
 * @param description the description of the Schema
 * @param active if this entity is active
 * @param labels a list of labels associated to the Schema
 * @param creationDate the creation date of the Schema
 * @param creationUser the reference of the user that created the Schema
 * @param modificationDate the modification date of the Schema
 * @param modificationUser the reference of last user that changed the Schema
 * @param key key management components
 * @param columnar columanr management components
 * @param index index management components
 * @param isRoot if the object is root
 * @param className possinble class Name
 * @param properties the sub fields
 */
@JsonCodec
final case class Schema(
  name: String,
  module: String,
  version: Int = 1,
  `type`: String = "object",
  description: String = "",
  @JsonKey(AUTO_OWNER) autoOwner: Boolean = false,
  active: Boolean = true,
  labels: List[String] = Nil,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  key: KeyManagement = KeyManagement.empty,
  columnar: GlobalColumnProperties = GlobalColumnProperties(),
  index: GlobalIndexProperties = GlobalIndexProperties(),
  @JsonKey(STORAGES) storages: List[StorageType] = Nil,
  @JsonKey(IS_ROOT) isRoot: Boolean = false,
  @JsonKey(CLASS_NAME) className: Option[String] = None,
  delta: List[Option[DeltaRule]] = Nil,
  properties: List[SchemaField] = Nil
) extends EditingTrait
    with Validable[Schema] {
  // Latest version id
  def id: String = s"$module.$name"

  // Versioned id
  def idVersioned: String = s"$module.$name.$version"

  def required: List[String] = properties.filter(_.required).map(_.name)

  def isSingleJSON: Boolean = columnar.isSingleJson
  def isSingleStorage: Boolean = columnar.singleStorage.isDefined

  /**
   * Check if there is a ownerId field and return it
   * @return Return an optional Owner ID
   */
  def ownerField: Option[SchemaField] = properties.find { field =>
    field.isInstanceOf[StringSchemaField] &&
    field.asInstanceOf[StringSchemaField].subType.contains(StringSubType.UserId)
  }

  /**
   * Return if this schema is filtrable with an UserID
   * @return a boolean
   */
  lazy val isOwnerFiltrable: Boolean = autoOwner && ownerField.isDefined

  def extractKey(json: Json): String = {
    val keyResult = if (key == KeyManagement.empty) {
      UUID.randomBase64UUID()
    } else {
      val components = key.parts.flatMap {
        case k: KeyField =>
          for {
            schemafield <- this.properties.find(_.name == k.field)
            jValue <- json.asObject.get.apply(k.field)
          } yield postProcessScripts(
            jValue.noSpaces.stripPrefix("\"").stripSuffix("\""),
            k.postProcessing
          )
      }
      val keyValue = components.mkString(key.separator.getOrElse(""))
      postProcessScripts(keyValue, key.postProcessing)
    }

    if (isSingleStorage) {
      name + SchemaNames.SINGLE_STORAGE_SEPARATOR + keyResult
    } else {
      keyResult
    }
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
      case KeyPostProcessing.Hash      => result = sha256Hash(result)
      case KeyPostProcessing(_, _)     => //TODO implement generic
    }
    result
  }

  def tableName: String =
    if (isSingleStorage) columnar.singleStorage.get else s"$module.$name"

  def modelName: String =
    if (isSingleStorage) columnar.singleStorage.get else s"$module-$name"

  def resolveId(json: Json, optionalID: Option[String]): String = {
    val rId = optionalID.getOrElse(extractKey(json))
    if (isSingleStorage && !rId.startsWith(name)) {
      name + SchemaNames.SINGLE_STORAGE_SEPARATOR + rId
    } else rId

  }

  def getQualifierName(name: String): String =
    properties.find(_.name == name).map(_.columnProperties.qualifier.getOrElse(name)).getOrElse("$$$$")

  //
  //  /* Return delta field */
  //  def getDeltaFieldAndRule:Option[(SchemaField, DeltaKind)]={
  //    delta.flatMap{
  //      dt =>
  //        getField(dt.field).toOption.map(p => p->dt.kind)
  //    }
  //  }
  /* Return delta field */
  def getDeltaFieldAndRule: List[Option[(SchemaField, DeltaKind)]] =
    delta.map(
      elem =>
        elem.flatMap { dt =>
          getField(dt.field).toOption.map(p => p -> dt.kind)
        }
    )

  def getField(name: String): Either[MissingFieldException, SchemaField] =
    if (name.contains(".")) {
      val tokens = name.split('.')
      var result = getField(tokens.head)
      var i = 1
      while (i < tokens.length - 1 && result.isRight) {
        result = result.right.get.getField(tokens(i))
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
   * We validate the schema.
   * Common actions are:
   * - adding missing values
   * - checking values
   * @return a validate entity or the exception
   */
  //  override def validate(): Either[FrameworkException, Schema] = {
  //    if(delta.isDefined){
  //      val res=getField(delta.get.field)
  //      if(res.isLeft){
  //        return Left(MissingFieldException(s"delta is missing ${delta.get.field}"))
  //      } //else Right(this)
  //    }
  //
  //    Right(this)
  //  }
  override def validate(): Either[FrameworkException, Schema] = {
    delta.foreach(elem => {
      if (elem.isDefined) {
        val res = getField(elem.get.field)
        if (res.isLeft) {
          return Left(MissingFieldException(s"delta is missing ${elem.get.field}"))
        } //else Right(this)
      }
    })
    Right(this)
  }

  /* Returns a map for convert originaName to actual names */
  def extractNameConversions: Map[String, String] =
    properties.flatMap(f => f.originalName.map(fo => fo -> f.name)).toMap

}

object Schema {
  /* an empty schema used a placeholder */
  lazy val empty = Schema("empty", "empty")
}
