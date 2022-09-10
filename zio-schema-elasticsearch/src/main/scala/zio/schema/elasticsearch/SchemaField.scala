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

import java.time.{ LocalDate, LocalDateTime, OffsetDateTime }

import scala.collection.mutable.ListBuffer
import scala.util.control.Exception.allCatch

import zio.common.OffsetDateTimeHelper
import zio.exception.{ FrameworkException, FrameworkMultipleExceptions, MissingFieldException, NoTypeParserException }
import zio.schema.elasticsearch.SchemaNames._
import zio.script.ScriptingService
import cats.implicits._
import io.circe._
import io.circe.derivation._
import io.circe.derivation.annotations._
import io.circe.syntax._

/**
 * Type class for object in which we can add custom parser to go Type level
 * management
 *
 * @tparam T
 */
sealed trait SchemaFieldType[T] {
  protected var stringParsers: List[String => T] = List.empty[String => T]

  def addStringParser(parserFunc: String => T): Unit =
    if (!stringParsers.contains(parserFunc))
      stringParsers ::= parserFunc

  def parse(string: String): Either[FrameworkException, T] = {
    var result: Either[FrameworkException, T] = Left(NoTypeParserException.default)
    val exceptions = new ListBuffer[FrameworkException]
    var i = 0
    while (i < stringParsers.length) {
      val parser = stringParsers(i)
      result = allCatch.either(parser(string)) match {
        case Right(x) => Right(x)
        case Left(x) =>
          val exception = x match {
            case exception1: FrameworkException => exception1
            case _ =>
              FrameworkException(s"ErrorDecoding: $string with $parser", x)
          }
          exceptions += exception
          Left(exception)
      }
      i = i + 1
    }

    if (result.isRight) {
      result
    } else {
      exceptions.length match {
        case 0 | 1 => result
        case _     => Left(FrameworkMultipleExceptions(exceptions.toList))
      }
    }

  }
}

sealed trait SchemaField {
  type Self <: SchemaField

  def className: Option[String]

  // name of the field
  def name: String

  /* original field name */
  def originalName: Option[String]

  /* description of field */
  def description: Option[String]

  def active: Boolean

  def indexProperties: IndexingProperties

  def dataType: String

  def required: Boolean

  def multiple: Boolean

  // Serialization/insering order
  def order: Int

  /* Set the order value */
  def setOrder(order: Int): Self

  // if the field is internal, not data related
  def isInternal: Boolean

  def isEnum: Boolean

  def modifiers: List[FieldModifier]

  /* a list of field validators */
  def validators: List[Validator]

  /* a list of inferred information when the field is reflected */
  def inferrerInfos: List[InferrerInfo]

  // if the field is sensitive
  def isSensitive: Boolean

  def masking: Option[String]

  def checks: Option[Check]

  def creationDate: OffsetDateTime

  def creationUser: User.Id

  def modificationDate: OffsetDateTime

  def modificationUser: User.Id

  def customStringParser: Option[Script]

  def getField(name: String): Either[MissingFieldException, SchemaField]

}

sealed trait TypedSchemaField[T] extends SchemaField {
  def default: Option[T]

  def enum: List[T]

  /* Returns if a string is an enum */
  def isEnum: Boolean = enum.nonEmpty

  /* Meta management for the type: useful for implement common type manage */
  def meta: SchemaFieldType[T]

  def parse(string: String)(implicit scriptingService: ScriptingService): Either[FrameworkException, T] =
    customStringParser match {
      case Some(script) =>
        scriptingService.execute(script, context = Map("value" -> string)).map(_.asInstanceOf[T])
      case _ =>
        meta.parse(string)
    }

  def getField(name: String): Either[MissingFieldException, SchemaField] =
    Left(MissingFieldException(s"Missing Field $name"))
}

object SchemaField {
  implicit final val decodeSchemaField: Decoder[SchemaField] =
    Decoder.instance { c =>
      val tpe = c.downField("type").focus match {
        case Some(v) => v.asString.getOrElse("object")
        case _       => "object"
      }
      val format = c.downField("format").focus.flatMap(_.asString)
      tpe match {
        case "boolean"   => c.as[BooleanSchemaField]
        case "ref"       => c.as[RefSchemaField]
        case "geo_point" => c.as[GeoPointSchemaField]
        case "object" =>
          c.as[SchemaMetaField]

        case "string" =>
          format match {
            case None => c.as[StringSchemaField]
            case Some(fmt) if fmt == "date-time" => //datetimes
              c.downField("format_options").focus.flatMap(_.asString) match {
                case None => c.as[LocalDateTimeSchemaField]
                case Some(option) if option == "offset" =>
                  c.as[OffsetDateTimeSchemaField]
              }
            case Some(fmt) if fmt == "date" => c.as[LocalDateSchemaField]
            case Some(fmt) if fmt == "byte" => c.as[ByteSchemaField]
            case Some(fmt) if fmt == "uuid" =>
              c.as[StringSchemaField] match {
                case Right(field) => Right(field.copy(subType = Some(StringSubType.UUID)))
                case left         => left
              }
            case Some(fmt) if fmt == "time" =>
              c.as[StringSchemaField] match {
                case Right(field) => Right(field.copy(subType = Some(StringSubType.Time)))
                case left         => left
              }
            case Some(fmt) =>
              c.as[StringSchemaField]
          }

        case "integer" =>
          format match {
            case None                        => c.as[IntSchemaField]
            case Some(fmt) if fmt == "int32" => c.as[IntSchemaField]
            case Some(fmt) if fmt == "int64" => c.as[LongSchemaField]
            case Some(fmt) if fmt == "int16" => c.as[ShortSchemaField]
            case Some(fmt) if fmt == "int8"  => c.as[ByteSchemaField]
            case Some(fmt) if fmt == "big"   => c.as[BigIntSchemaField]
          }
        case "number" =>
          format match {
            case None                         => c.as[DoubleSchemaField]
            case Some(fmt) if fmt == "float"  => c.as[FloatSchemaField]
            case Some(fmt) if fmt == "double" => c.as[DoubleSchemaField]
          }

        case "array" =>
          format match {
            case None                         => c.as[ListSchemaField]
            case Some(fmt) if fmt == "list"   => c.as[ListSchemaField]
            case Some(fmt) if fmt == "seq"    => c.as[SeqSchemaField]
            case Some(fmt) if fmt == "set"    => c.as[SetSchemaField]
            case Some(fmt) if fmt == "vector" => c.as[VectorSchemaField]
          }
      }

    }

  implicit final val encodeSchemaField: Encoder.AsObject[SchemaField] = {
    Encoder.AsObject.instance { obj: SchemaField =>
      val jsn = obj match {
        case o: StringSchemaField =>
          o.asJsonObject.add("type", Json.fromString(obj.dataType))
        case o: OffsetDateTimeSchemaField =>
          o.asJsonObject
            .add("type", Json.fromString("string"))
            .add("format", Json.fromString("date-time"))
            .add("format_options", Json.fromString("offset"))
        case o: LocalDateTimeSchemaField =>
          o.asJsonObject.add("type", Json.fromString("string")).add("format", Json.fromString("date-time"))
        case o: LocalDateSchemaField =>
          o.asJsonObject.add("type", Json.fromString("string")).add("format", Json.fromString("date"))

        case o: GeoPointSchemaField =>
          o.asJsonObject.add("type", Json.fromString("string")).add("format", Json.fromString("geo_point"))

        case o: DoubleSchemaField =>
          o.asJsonObject.add("type", Json.fromString("number")).add("format", Json.fromString("double"))
        case o: FloatSchemaField =>
          o.asJsonObject.add("type", Json.fromString("number")).add("format", Json.fromString("float"))
        case o: BigIntSchemaField =>
          o.asJsonObject.add("type", Json.fromString("integer")).add("format", Json.fromString("big"))
        case o: IntSchemaField =>
          o.asJsonObject.add("type", Json.fromString("integer")).add("format", Json.fromString("int32"))
        case o: BooleanSchemaField =>
          o.asJsonObject.add("type", Json.fromString("boolean"))
        case o: LongSchemaField =>
          o.asJsonObject.add("type", Json.fromString("integer")).add("format", Json.fromString("int64"))
        case o: ShortSchemaField =>
          o.asJsonObject.add("type", Json.fromString("integer")).add("format", Json.fromString("int16"))
        case o: ByteSchemaField =>
          o.asJsonObject.add("type", Json.fromString("integer")).add("format", Json.fromString("int8"))
        case o: ListSchemaField =>
          o.asJsonObject.add("type", Json.fromString("array")).add("format", Json.fromString("list"))
        case o: SeqSchemaField =>
          o.asJsonObject.add("type", Json.fromString("array")).add("format", Json.fromString("seq"))
        case o: SetSchemaField =>
          o.asJsonObject.add("type", Json.fromString("array")).add("format", Json.fromString("set"))
        case o: VectorSchemaField =>
          o.asJsonObject.add("type", Json.fromString("array")).add("format", Json.fromString("vector"))
        case o: RefSchemaField =>
          o.asJsonObject.add("type", Json.fromString(obj.dataType))
        case o: SchemaMetaField =>
          o.asJsonObject.add("type", Json.fromString(obj.dataType))
      }

      jsn
    //      jsn.add("type",Json.fromString(obj.dataType))

    //      Json.fromJsonObject(jsn.asObject.get.add("type",
    //          Json.fromString(obj.dataType)))

    }
  }
}

/**
 * This class defines a StringSchemaField entity
 * @param name
 *   the name of the StringSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM StringSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param subType
 *   a Option[StringSubType] entity
 * @param enum
 *   a list of String entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the StringSchemaField
 * @param creationUser
 *   the reference of the user that created the StringSchemaField
 * @param modificationDate
 *   the modification date of the StringSchemaField
 * @param modificationUser
 *   the reference of last user that changed the StringSchemaField
 */
@JsonCodec
final case class StringSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[String] = None,
  @JsonKey(SUB_TYPE) subType: Option[StringSubType] = None,
  enum: List[String] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[String] {

  type Self = StringSchemaField
  def setOrder(order: Int): StringSchemaField = copy(order = order)
  def dataType: String = "string"
  def meta: SchemaFieldType[String] = StringSchemaField

}

object StringSchemaField extends SchemaFieldType[String] {
  override def parse(string: String): Either[FrameworkException, String] =
    Right(string)

  def fromOtherType[A](other: TypedSchemaField[A], subType: Option[StringSubType]): StringSchemaField =
    StringSchemaField(
      name = other.name,
      active = other.active,
      className = other.className,
      originalName = other.originalName,
      description = other.description,
//      default=other.default,
      subType = subType,
//      enum=other.enum,
      required = other.required,
      multiple = other.multiple,
      order = other.order,
      isInternal = other.isInternal,
      customStringParser = other.customStringParser,
      validators = other.validators,
      inferrerInfos = other.inferrerInfos,
      checks = other.checks,
      creationDate = other.creationDate,
      creationUser = other.creationUser,
      modificationDate = other.modificationDate,
      modificationUser = other.modificationUser
    )
}

final case class GeoPointSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey("columnar") columnProperties: ColumnProperties = ColumnProperties.empty,
  @JsonKey("index") indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[String] = None,
  enum: List[String] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[String] {
  type Self = GeoPointSchemaField
  def setOrder(order: Int): GeoPointSchemaField = copy(order = order)
  def dataType: String = "geo_point"
  def meta: SchemaFieldType[String] = GeoPointSchemaField

}

object GeoPointSchemaField extends SchemaFieldType[String] {
  implicit final val decodeGeoPointSchemaField: Decoder[GeoPointSchemaField] =
    deriveDecoder[GeoPointSchemaField]
  implicit final val encodeGeoPointSchemaField: Encoder.AsObject[GeoPointSchemaField] =
    deriveEncoder[GeoPointSchemaField]
}

/**
 * This class defines a OffsetDateTimeSchemaField entity
 * @param name
 *   the name of the OffsetDateTimeSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM OffsetDateTimeSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of OffsetDateTime entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the OffsetDateTimeSchemaField
 * @param creationUser
 *   the reference of the user that created the OffsetDateTimeSchemaField
 * @param modificationDate
 *   the modification date of the OffsetDateTimeSchemaField
 * @param modificationUser
 *   the reference of last user that changed the OffsetDateTimeSchemaField
 */
@JsonCodec
final case class OffsetDateTimeSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[OffsetDateTime] = None,
  enum: List[OffsetDateTime] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[OffsetDateTime] {
  type Self = OffsetDateTimeSchemaField
  def setOrder(order: Int): OffsetDateTimeSchemaField = copy(order = order)

  def dataType: String = "timestamp"

  def meta: SchemaFieldType[OffsetDateTime] = OffsetDateTimeSchemaField

}

object OffsetDateTimeSchemaField extends SchemaFieldType[OffsetDateTime] {}

/**
 * This class defines a LocalDateTimeSchemaField entity
 * @param name
 *   the name of the LocalDateTimeSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM LocalDateTimeSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of LocalDateTime entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the LocalDateTimeSchemaField
 * @param creationUser
 *   the reference of the user that created the LocalDateTimeSchemaField
 * @param modificationDate
 *   the modification date of the LocalDateTimeSchemaField
 * @param modificationUser
 *   the reference of last user that changed the LocalDateTimeSchemaField
 */
@JsonCodec
final case class LocalDateTimeSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[LocalDateTime] = None,
  enum: List[LocalDateTime] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[LocalDateTime] {
  type Self = LocalDateTimeSchemaField
  def setOrder(order: Int): LocalDateTimeSchemaField = copy(order = order)

  def dataType: String = "datetime"

  def meta: SchemaFieldType[LocalDateTime] = LocalDateTimeSchemaField

}

object LocalDateTimeSchemaField extends SchemaFieldType[LocalDateTime] {}

/**
 * This class defines a LocalDateSchemaField entity
 * @param name
 *   the name of the LocalDateSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM LocalDateSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of LocalDate entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the LocalDateSchemaField
 * @param creationUser
 *   the reference of the user that created the LocalDateSchemaField
 * @param modificationDate
 *   the modification date of the LocalDateSchemaField
 * @param modificationUser
 *   the reference of last user that changed the LocalDateSchemaField
 */
@JsonCodec
final case class LocalDateSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[LocalDate] = None,
  enum: List[LocalDate] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[LocalDate] {
  type Self = LocalDateSchemaField
  def setOrder(order: Int): LocalDateSchemaField = copy(order = order)

  def dataType: String = "date"

  def meta: SchemaFieldType[LocalDate] = LocalDateSchemaField

}

object LocalDateSchemaField extends SchemaFieldType[LocalDate] {}

/**
 * This class defines a DoubleSchemaField entity
 * @param name
 *   the name of the DoubleSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM DoubleSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of Double entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the DoubleSchemaField
 * @param creationUser
 *   the reference of the user that created the DoubleSchemaField
 * @param modificationDate
 *   the modification date of the DoubleSchemaField
 * @param modificationUser
 *   the reference of last user that changed the DoubleSchemaField
 */
@JsonCodec
final case class DoubleSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Double] = None,
  enum: List[Double] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[Double] {
  type Self = DoubleSchemaField
  def setOrder(order: Int): DoubleSchemaField = copy(order = order)

  def dataType: String = "double"

  def meta: SchemaFieldType[Double] = DoubleSchemaField

}

object DoubleSchemaField extends SchemaFieldType[Double] {}

/**
 * This class defines a BigIntSchemaField entity
 * @param name
 *   the name of the BigIntSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM BigIntSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of BigInt entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the BigIntSchemaField
 * @param creationUser
 *   the reference of the user that created the BigIntSchemaField
 * @param modificationDate
 *   the modification date of the BigIntSchemaField
 * @param modificationUser
 *   the reference of last user that changed the BigIntSchemaField
 */
@JsonCodec
final case class BigIntSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[BigInt] = None,
  enum: List[BigInt] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[BigInt] {
  type Self = BigIntSchemaField
  def setOrder(order: Int): BigIntSchemaField = copy(order = order)

  def dataType: String = "bigint"

  def meta: SchemaFieldType[BigInt] = BigIntSchemaField

}

object BigIntSchemaField extends SchemaFieldType[BigInt] {}

/**
 * This class defines a IntSchemaField entity
 * @param name
 *   the name of the IntSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM IntSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of Int entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the IntSchemaField
 * @param creationUser
 *   the reference of the user that created the IntSchemaField
 * @param modificationDate
 *   the modification date of the IntSchemaField
 * @param modificationUser
 *   the reference of last user that changed the IntSchemaField
 */
@JsonCodec
final case class IntSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Int] = None,
  enum: List[Int] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[Int] {
  type Self = IntSchemaField
  def setOrder(order: Int): IntSchemaField = copy(order = order)

  def dataType: String = "integer"

  def meta: SchemaFieldType[Int] = IntSchemaField

}

object IntSchemaField extends SchemaFieldType[Int] {}

/**
 * This class defines a BooleanSchemaField entity
 * @param name
 *   the name of the BooleanSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM BooleanSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of Boolean entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the BooleanSchemaField
 * @param creationUser
 *   the reference of the user that created the BooleanSchemaField
 * @param modificationDate
 *   the modification date of the BooleanSchemaField
 * @param modificationUser
 *   the reference of last user that changed the BooleanSchemaField
 */
@JsonCodec
final case class BooleanSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Boolean] = None,
  enum: List[Boolean] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[Boolean] {
  type Self = BooleanSchemaField
  def setOrder(order: Int): BooleanSchemaField = copy(order = order)

  def dataType: String = "boolean"

  def meta: SchemaFieldType[Boolean] = BooleanSchemaField

}

object BooleanSchemaField extends SchemaFieldType[Boolean] {}

/**
 * This class defines a LongSchemaField entity
 * @param name
 *   the name of the LongSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM LongSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of Long entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the LongSchemaField
 * @param creationUser
 *   the reference of the user that created the LongSchemaField
 * @param modificationDate
 *   the modification date of the LongSchemaField
 * @param modificationUser
 *   the reference of last user that changed the LongSchemaField
 */
@JsonCodec
final case class LongSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Long] = None,
  enum: List[Long] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[Long] {
  type Self = LongSchemaField
  def setOrder(order: Int): LongSchemaField = copy(order = order)

  def dataType: String = "long"

  def meta: SchemaFieldType[Long] = LongSchemaField

}

object LongSchemaField extends SchemaFieldType[Long] {}

/**
 * This class defines a ShortSchemaField entity
 * @param name
 *   the name of the ShortSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM ShortSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of Short entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the ShortSchemaField
 * @param creationUser
 *   the reference of the user that created the ShortSchemaField
 * @param modificationDate
 *   the modification date of the ShortSchemaField
 * @param modificationUser
 *   the reference of last user that changed the ShortSchemaField
 */
@JsonCodec
final case class ShortSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Short] = None,
  enum: List[Short] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[Short] {
  type Self = ShortSchemaField
  def setOrder(order: Int): ShortSchemaField = copy(order = order)

  def dataType: String = "integer"

  def meta: SchemaFieldType[Short] = ShortSchemaField

}

object ShortSchemaField extends SchemaFieldType[Short] {}

/**
 * This class defines a FloatSchemaField entity
 * @param name
 *   the name of the FloatSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM FloatSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of Float entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the FloatSchemaField
 * @param creationUser
 *   the reference of the user that created the FloatSchemaField
 * @param modificationDate
 *   the modification date of the FloatSchemaField
 * @param modificationUser
 *   the reference of last user that changed the FloatSchemaField
 */
@JsonCodec
final case class FloatSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Float] = None,
  enum: List[Float] = Nil,
  modifiers: List[FieldModifier] = Nil,
  @JsonNoDefault required: Boolean = false,
  @JsonNoDefault multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[Float] {

  type Self = FloatSchemaField
  def setOrder(order: Int): FloatSchemaField = copy(order = order)

  def dataType: String = "float"

  def meta: SchemaFieldType[Float] = FloatSchemaField

}

object FloatSchemaField extends SchemaFieldType[Float] {}

/**
 * This class defines a ByteSchemaField entity
 * @param name
 *   the name of the ByteSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM ByteSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of Byte entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the ByteSchemaField
 * @param creationUser
 *   the reference of the user that created the ByteSchemaField
 * @param modificationDate
 *   the modification date of the ByteSchemaField
 * @param modificationUser
 *   the reference of last user that changed the ByteSchemaField
 */
@JsonCodec
final case class ByteSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Byte] = None,
  enum: List[Byte] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[Byte] {
  type Self = ByteSchemaField
  def setOrder(order: Int): ByteSchemaField = copy(order = order)

  def dataType: String = "byte"

  def meta: SchemaFieldType[Byte] = ByteSchemaField

}

object ByteSchemaField extends SchemaFieldType[Byte] {}

/**
 * This class defines a ListSchemaField entity
 * @param items
 *   a SchemaField entity
 * @param name
 *   the name of the ListSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM ListSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param enum
 *   a list of SchemaField entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the ListSchemaField
 * @param creationUser
 *   the reference of the user that created the ListSchemaField
 * @param modificationDate
 *   the modification date of the ListSchemaField
 * @param modificationUser
 *   the reference of last user that changed the ListSchemaField
 */
@JsonCodec
final case class ListSchemaField(
  items: SchemaField,
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  //default: Option[List[Json]] = None,
  enum: List[SchemaField] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = true,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends SchemaField {
  type Self = ListSchemaField
  def setOrder(order: Int): ListSchemaField = copy(order = order)

  def dataType: String = "list"

  override def isEnum: Boolean = items.isEnum

  def getField(name: String): Either[MissingFieldException, SchemaField] =
    items.getField(name)

}

object ListSchemaField {
  implicit final val decodeListSchemaField: Decoder[ListSchemaField] =
    deriveDecoder[ListSchemaField]
  implicit final val encodeListSchemaField: Encoder.AsObject[ListSchemaField] =
    deriveEncoder[ListSchemaField]
}

/**
 * This class defines a SeqSchemaField entity
 * @param items
 *   a SchemaField entity
 * @param name
 *   the name of the SeqSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM SeqSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param enum
 *   a list of SchemaField entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the SeqSchemaField
 * @param creationUser
 *   the reference of the user that created the SeqSchemaField
 * @param modificationDate
 *   the modification date of the SeqSchemaField
 * @param modificationUser
 *   the reference of last user that changed the SeqSchemaField
 */
@JsonCodec
final case class SeqSchemaField(
  items: SchemaField,
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  //  default: Option[Seq[Json]] = None,
  enum: List[SchemaField] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = true,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends SchemaField {
  type Self = SeqSchemaField
  def setOrder(order: Int): SeqSchemaField = copy(order = order)

  def dataType: String = "seq"

  override def isEnum: Boolean = items.isEnum

  def getField(name: String): Either[MissingFieldException, SchemaField] =
    items.getField(name)

}

object SeqSchemaField {
  implicit final val decodeSeqSchemaField: Decoder[SeqSchemaField] =
    deriveDecoder[SeqSchemaField]
  implicit final val encodeSeqSchemaField: Encoder.AsObject[SeqSchemaField] =
    deriveEncoder[SeqSchemaField]
}

/**
 * This class defines a SetSchemaField entity
 * @param items
 *   a SchemaField entity
 * @param name
 *   the name of the SetSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM SetSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param enum
 *   a list of SchemaField entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the SetSchemaField
 * @param creationUser
 *   the reference of the user that created the SetSchemaField
 * @param modificationDate
 *   the modification date of the SetSchemaField
 * @param modificationUser
 *   the reference of last user that changed the SetSchemaField
 */
@JsonCodec
final case class SetSchemaField(
  items: SchemaField,
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  //  default: Option[Set[Json]] = None,
  enum: List[SchemaField] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = true,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends SchemaField {
  type Self = SetSchemaField
  def setOrder(order: Int): SetSchemaField = copy(order = order)

  def dataType: String = "set"

  override def isEnum: Boolean = false

  def getField(name: String): Either[MissingFieldException, SchemaField] =
    items.getField(name)

}

object SetSchemaField {
  implicit final val decodeSetSchemaField: Decoder[SetSchemaField] =
    deriveDecoder[SetSchemaField]
  implicit final val encodeSetSchemaField: Encoder.AsObject[SetSchemaField] =
    deriveEncoder[SetSchemaField]
}

/**
 * This class defines a VectorSchemaField entity
 * @param items
 *   a SchemaField entity
 * @param name
 *   the name of the VectorSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM VectorSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param enum
 *   a list of SchemaField entities
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the VectorSchemaField
 * @param creationUser
 *   the reference of the user that created the VectorSchemaField
 * @param modificationDate
 *   the modification date of the VectorSchemaField
 * @param modificationUser
 *   the reference of last user that changed the VectorSchemaField
 */
@JsonCodec
final case class VectorSchemaField(
  items: SchemaField,
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  //  default: Option[Vector[Json]] = None,
  enum: List[SchemaField] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = true,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends SchemaField {
  type Self = VectorSchemaField
  def setOrder(order: Int): VectorSchemaField = copy(order = order)

  def dataType: String = "vector"

  override def isEnum: Boolean = items.isEnum

  def getField(name: String): Either[MissingFieldException, SchemaField] =
    items.getField(name)

}

/**
 * This class defines a RefSchemaField entity
 * @param name
 *   the name of the RefSchemaField entity
 * @param ref
 *   a String
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM RefSchemaField entity namespace
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of String entities
 * @param subType
 *   a Option[StringSubType] entity
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the RefSchemaField
 * @param creationUser
 *   the reference of the user that created the RefSchemaField
 * @param modificationDate
 *   the modification date of the RefSchemaField
 * @param modificationUser
 *   the reference of last user that changed the RefSchemaField
 */
@JsonCodec
final case class RefSchemaField(
  name: String,
  @JsonKey(s"$$ref") ref: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[String] = None,
  enum: List[String] = Nil,
  modifiers: List[FieldModifier] = Nil,
  @JsonKey(SUB_TYPE) subType: Option[StringSubType] = None,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends TypedSchemaField[String] {
  type Self = RefSchemaField
  def setOrder(order: Int): RefSchemaField = copy(order = order)

  def dataType: String = "ref"

  def meta: SchemaFieldType[String] = RefSchemaField

}

object RefSchemaField extends SchemaFieldType[String] {}

/**
 * This class defines a SchemaMetaField entity
 * @param name
 *   the name of the SchemaMetaField entity
 * @param active
 *   if this entity is active
 * @param module
 *   the module associated to the SchemaMetaField entity
 * @param type
 *   the type of the SchemaMetaField entity
 * @param columnProperties
 *   a ColumnProperties entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param className
 *   a string the rappresent the JVM SchemaMetaField entity namespace
 * @param properties
 *   a map of properties of this entity
 * @param required
 *   if this field is required
 * @param multiple
 *   if this field is multiple values
 * @param order
 *   this defines the processing order
 * @param isInternal
 *   if this field is internal use
 * @param customStringParser
 *   a Option[Script] entity
 * @param validators
 *   a list of Validator entities
 * @param inferrerInfos
 *   a list of InferrerInfo entities
 * @param isSensitive
 *   if the field is a PII
 * @param masking
 *   the masking algorithm if defined
 * @param checks
 *   an optinal validity check for the field
 * @param creationDate
 *   the creation date of the SchemaMetaField
 * @param creationUser
 *   the reference of the user that created the SchemaMetaField
 * @param modificationDate
 *   the modification date of the SchemaMetaField
 * @param modificationUser
 *   the reference of last user that changed the SchemaMetaField
 */
@JsonCodec
final case class SchemaMetaField(
  name: String,
  active: Boolean = true,
  module: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  `type`: String = "object",
  @JsonKey(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  @JsonKey(CLASS_NAME) className: Option[String] = None,
  properties: List[SchemaField] = Nil,
  modifiers: List[FieldModifier] = Nil,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: List[Validator] = Nil,
  inferrerInfos: List[InferrerInfo] = Nil,
  @JsonKey(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Option[Check] = None,
  @JsonKey(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(CREATION_USER) creationUser: User.Id = User.SystemID,
  @JsonKey(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @JsonKey(MODIFICATION_USER) modificationUser: User.Id = User.SystemID
) extends SchemaField {
  type Self = SchemaMetaField
  def setOrder(order: Int): SchemaMetaField = copy(order = order)

  override def dataType: String = "object"

  override def isEnum: Boolean = false

  def isRoot: Boolean = false

  def getField(name: String): Either[MissingFieldException, SchemaField] =
    properties.find(_.name == name) match {
      case Some(x) => Right(x)
      case None =>
        Left(MissingFieldException(s"Missing Field $name"))
    }

}
