/*
 * Copyright 2019-2023 Alberto Paro
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

import zio.Chunk

import java.time.{ LocalDate, LocalDateTime, OffsetDateTime }
import scala.collection.mutable.ListBuffer
import scala.util.control.Exception.allCatch
import zio.common.OffsetDateTimeHelper
import zio.exception.{
  FrameworkException,
  FrameworkMultipleExceptions,
  MergeSchemaException,
  MissingFieldException,
  NoTypeParserException
}
import zio.schema.elasticsearch.SchemaNames._
import zio.json.ast._
import zio.json._

/**
 * Type class for object in which we can add custom parser to go Type level
 * management
 *
 * @tparam T
 */
sealed trait SchemaFieldType[T] {
  protected var stringParsers: Chunk[String => T] = Chunk.empty[String => T]

  def addStringParser(parserFunc: String => T): Unit =
    if (!stringParsers.contains(parserFunc))
      stringParsers ++= Chunk(parserFunc)

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
  def setDescription(description: String): Self

  def metadata: Json.Obj
  def setMetadata(metadata: Json.Obj): Self

  def setMetadata[T](name: String, value: T)(implicit encoder: JsonEncoder[T]): Self =
    setMetadata(metadata.add(name, value.toJsonAST))
  def getMetadata[T](name: String)(implicit decoder: JsonDecoder[T]): Either[FrameworkException, T] =
    metadata.getEither(name).left.map(v => FrameworkException(v))

  def getMetadata[T](name: String, default: T)(implicit decoder: JsonDecoder[T]): Either[FrameworkException, T] =
    if (metadata.keys.contains(name)) {
      metadata.getEither(name).left.map(v => FrameworkException(v))
    } else Right(default)

  def active: Boolean
  def setActive(active: Boolean): Self

  def setName(name: String): Self

  def indexProperties: IndexingProperties

  /* Set the index properties of the field. */
  def setIndexProperties(indexProperties: IndexingProperties): Self

  def dataType: String

  def required: Boolean

  /* Set if the field is required. It required, it must be not nullable. */
  def setRequired(req: Boolean): Self

  def multiple: Boolean

  /* Set if the field is multiple. */
  def setMultiple(multiple: Boolean): Self

  // Serialization/inserting order
  def order: Int

  /* Set the order value */
  def setOrder(order: Int): Self

  // if the field is internal, not data related
  def isInternal: Boolean

  def isEnum: Boolean

  def modifiers: Chunk[FieldModifier]
  def setModifiers(modifiers: Chunk[FieldModifier]): Self
  def addModifier(modifier: FieldModifier): Self = {
    val newValue: Chunk[FieldModifier] = modifiers ++ Chunk(modifier)
    setModifiers(newValue)
  }

  /* a list of field validators */
  def validators: Chunk[Validator]

  def setValidators(validators: Chunk[Validator]): Self

  def addValidator(validator: Validator): Self = setValidators(validators ++ Chunk(validator))

  /* a list of inferred information when the field is reflected */
  def inferrerInfos: Chunk[InferrerInfo]

  def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): Self

  def addInferrerInfo(inferrerInfo: InferrerInfo): Self = setInferrerInfos(inferrerInfos ++ Chunk(inferrerInfo))

  // if the field is sensitive
  def isSensitive: Boolean
  def setIsSensitive(isSensitive: Boolean): Self

  def masking: Option[String]
  def setMasking(masking: String): Self

  def checks: Chunk[Check]

  def setChecks(checks: Chunk[Check]): Self

  def addCheck(check: Check): Self = setChecks(checks ++ Chunk(check))

  def creationDate: OffsetDateTime
  def setCreationDate(creationDate: OffsetDateTime): Self

  def creationUser: User.Id
  def setCreationUser(creationUser: User.Id): Self

  def modificationDate: OffsetDateTime
  def setModificationDate(modificationDate: OffsetDateTime): Self

  def modificationUser: User.Id
  def setModificationUser(modificationUser: User.Id): Self

  def customStringParser: Option[Script]

  def getField(name: String): Either[MissingFieldException, SchemaField]

  /**
   * Return a list of fields flattened
   */
  def getFlatFields: Chunk[SchemaField]

  private def mergeIndexingProperties(
    first: IndexingProperties,
    second: IndexingProperties,
    other: SchemaField
  ): Either[MergeSchemaException, IndexingProperties] =
    if (first == second) Right(first)
    else {
      (first.es_type, second.es_type) match {
        case (EsType.none, _) => Right(second)
        case (_, EsType.none) => Right(first)
        case (EsType.long, a) =>
          a match {
            case EsType.integer | EsType.long | EsType.short | EsType.byte => Right(first)
            case EsType.none                                               => Right(second)
            case _ =>
              Left(
                MergeSchemaException(
                  s"Unable to merge ${first.es_type} - ${second.es_type}",
                  schemaFields = List(this, other)
                )
              )
          }
      }

    }

  /**
   * Merge Two schema in one.
   *
   * @param other
   *   the other schema to merge
   * @param onlyExistsInFirst
   *   merge the field if only exists in the first one
   * @return
   *   the merged schema
   */
  def merge(other: SchemaField, onlyExistsInFirst: Boolean): Either[MergeSchemaException, SchemaField] =
    for {
      indexingProperties <- mergeIndexingProperties(this.indexProperties, other.indexProperties, other)
      field <- this match {
        case field: TypedSchemaField[_] if other.isInstanceOf[TypedSchemaField[_]] =>
          field match {
            case src: BinarySchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: BinarySchemaField => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: StringSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: StringSchemaField => Right(src)
                case _: RefSchemaField    => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: RefSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: StringSchemaField => Right(other)
                case _: RefSchemaField    => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }

            case src: GeoPointSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: GeoPointSchemaField => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: OffsetDateTimeSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: OffsetDateTimeSchemaField => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: LocalDateTimeSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: LocalDateSchemaField => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: LocalDateSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: LocalDateSchemaField => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: DoubleSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: BigDecimalSchemaField => Right(other)
                case _: DoubleSchemaField     => Right(src)
                case _: FloatSchemaField      => Right(src)
                case _: BigIntSchemaField     => Right(src)
                case _: IntSchemaField        => Right(src)
                case _: LongSchemaField       => Right(src)
                case _: ShortSchemaField      => Right(src)
                case _: ByteSchemaField       => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: BigDecimalSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: DoubleSchemaField => Right(src)
                case _: FloatSchemaField  => Right(src)
                case _: BigIntSchemaField => Right(src)
                case _: IntSchemaField    => Right(src)
                case _: LongSchemaField   => Right(src)
                case _: ShortSchemaField  => Right(src)
                case _: ByteSchemaField   => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))

              }
            case src: BigIntSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: BigIntSchemaField => Right(src)
                case _: IntSchemaField    => Right(src)
                case _: LongSchemaField   => Right(src)
                case _: ShortSchemaField  => Right(src)
                case _: ByteSchemaField   => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))

              }
            case src: IntSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case other2: BigIntSchemaField => Right(other2)
                case other2: DoubleSchemaField => Right(other2)
                case _: IntSchemaField         => Right(src)
                case other2: LongSchemaField   => Right(other2)
                case _: ShortSchemaField       => Right(src)
                case other2: FloatSchemaField  => Right(other2)
                case _: ByteSchemaField        => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: BooleanSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: BooleanSchemaField => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: LongSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case other2: DoubleSchemaField => Right(other2)
                case other2: BigIntSchemaField => Right(other2)
                case _: IntSchemaField         => Right(src)
                case _: LongSchemaField        => Right(src)
                case _: ShortSchemaField       => Right(src)
                case _: FloatSchemaField       => Right(src.toDoubleField)
                case _: ByteSchemaField        => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: ShortSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case other2: DoubleSchemaField => Right(other2)
                case other2: BigIntSchemaField => Right(other2)
                case other2: IntSchemaField    => Right(other2)
                case other2: LongSchemaField   => Right(other2)
                case _: ShortSchemaField       => Right(src)
                case other2: FloatSchemaField  => Right(other2.toDoubleField)
                case _: ByteSchemaField        => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: FloatSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case _: DoubleSchemaField     => Right(src)
                case _: BigIntSchemaField     => ??? // TODO Double
                case other2: IntSchemaField   => Right(other2.toDoubleField)
                case other2: LongSchemaField  => Right(other2.toDoubleField)
                case other2: ShortSchemaField => Right(other2.toDoubleField)
                case _: FloatSchemaField      => Right(src)
                case _: ByteSchemaField       => Right(src)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
            case src: ByteSchemaField =>
              other.asInstanceOf[TypedSchemaField[_]] match {
                case other2: BigIntSchemaField => Right(other2)
                case other2: IntSchemaField    => Right(other2)
                case other2: LongSchemaField   => Right(other2)
                case other2: ShortSchemaField  => Right(other2)
                case other2: FloatSchemaField  => Right(other2)
                case other2: ByteSchemaField   => Right(other2)
                case _ =>
                  Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
              }
          }
        case field: TypedSchemaField[_] if other.isInstanceOf[ListSchemaField] =>
          val listField = other.asInstanceOf[ListSchemaField]
          if (listField.items.dataType == field.dataType) {
            Right(listField)
          } else {
            Left(MergeSchemaException(s"Unable to compare $field <> $listField", schemaFields = List(field, listField)))
          }

        case src: ListSchemaField =>
          other match {
            case other2: ListSchemaField =>
              src.items.merge(other2.items, onlyExistsInFirst).map(f => src.copy(items = f))
            case other2: ObjectSchemaField if src.items.isInstanceOf[ObjectSchemaField] =>
              src.items.merge(other2, onlyExistsInFirst).map(f => src.copy(items = f))
            case _ => src.items.merge(other, onlyExistsInFirst).map(f => src.copy(items = f))
          }

        case src: ObjectSchemaField =>
          // merge di sottoclassi
          other match {
            case other2: ObjectSchemaField =>
              val firstObjectFields = src.fields.map(_.name)
              val secondObjectFields = if (onlyExistsInFirst) Chunk.empty else other2.fields.map(_.name)
              val fieldNames = (firstObjectFields ++ secondObjectFields).distinct
              val fieldToMarkRequired = firstObjectFields.toSet.intersect(secondObjectFields.toSet)
              val finalFields = new ListBuffer[SchemaField]
              fieldNames.foreach { fieldName =>
                val srcField = src.fields.find(_.name == fieldName)
                val dstField = other2.fields.find(_.name == fieldName)
                srcField match {
                  case Some(sf) =>
                    dstField match {
                      case Some(df) =>
                        sf.merge(df, onlyExistsInFirst)
                          .toOption
                          .foreach(f => finalFields += f.setRequired(fieldToMarkRequired.contains(fieldName)))
                      case None => finalFields += sf.setRequired(false)
                    }
                  case None =>
                    dstField match {
                      case Some(value) => finalFields += value.setRequired(false)
                      case None        =>
                    }
                }
              }

              Right(src.copy(fields = Chunk.fromIterable(finalFields)))
            case _ => Left(MergeSchemaException(s"Unable to compare $src <> $other", schemaFields = List(src, other)))
          }
        case _ => Left(MergeSchemaException(s"Unable to compare $this <> $other", schemaFields = List(this, other)))
      }

    } yield field.setIndexProperties(indexingProperties)

}

sealed trait TypedSchemaField[T] extends SchemaField {

  /**
   * a optinal default value
   */
  def default: Option[T]

  /**
   * a list of value test_samples
   */
  def samples: Chunk[T]

  def `enum`: Chunk[T]

  /* Returns if a string is an enum */
  def isEnum: Boolean = enum.nonEmpty

  /* Meta management for the type: useful for implement common type manage */
  def meta: SchemaFieldType[T]

//  def parse(string: String)(implicit scriptingService: ScriptingService): Either[FrameworkException, T] =
//    customStringParser match {
//      case Some(script) =>
//        scriptingService.execute(script, context = Map("value" -> string)).map(_.asInstanceOf[T])
//      case _ =>
//        Left(Ge)
////        meta.parse(string)
//    }

  def getField(name: String): Either[MissingFieldException, SchemaField] =
    Left(MissingFieldException(s"Missing Field $name"))
}

object SchemaField {
  implicit final val decodeSchemaField: JsonDecoder[SchemaField] = DeriveJsonDecoder.gen[SchemaField]

  implicit final val encodeSchemaField: JsonEncoder[SchemaField] = DeriveJsonEncoder.gen[SchemaField]
}

/**
 * This class defines a StringSchemaField entity
 * @param name
 *   the name of the StringSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM StringSchemaField entity namespace
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
final case class StringSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[String] = None,
  samples: Chunk[String] = Chunk.empty,
  @jsonField(SUB_TYPE) subType: Option[StringSubType] = None,
  size: Option[Int] = None,
  `enum`: Chunk[String] = Chunk.empty[String],
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty[Check],
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[String] {
  type Self = StringSchemaField
  def setOrder(order: Int): StringSchemaField = copy(order = order)
  def dataType: String = "string"
  def meta: SchemaFieldType[String] = StringSchemaField
  override def setDescription(description: String): StringSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): StringSchemaField = copy(active = active)

  override def setName(name: String): StringSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): StringSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): StringSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): StringSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): StringSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): StringSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): StringSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): StringSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): StringSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): StringSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): StringSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): StringSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): StringSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): StringSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): StringSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)
}

object StringSchemaField extends SchemaFieldType[String] {
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
      modificationUser = other.modificationUser,
      metadata = other.metadata
    )
  implicit val jsonDecoder: JsonDecoder[StringSchemaField] = DeriveJsonDecoder.gen[StringSchemaField]
  implicit val jsonEncoder: JsonEncoder[StringSchemaField] = DeriveJsonEncoder.gen[StringSchemaField]
}

/**
 * This class defines a BinarySchemaField entity
 * @param name
 *   the name of the BinarySchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM BinarySchemaField entity namespace
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
 *   the creation date of the BinarySchemaField
 * @param creationUser
 *   the reference of the user that created the BinarySchemaField
 * @param modificationDate
 *   the modification date of the BinarySchemaField
 * @param modificationUser
 *   the reference of last user that changed the BinarySchemaField
 */
final case class BinarySchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Array[Byte]] = None,
  samples: Chunk[Array[Byte]] = Chunk.empty,
  @jsonField(SUB_TYPE) subType: Option[StringSubType] = None,
  size: Option[Int] = None,
  `enum`: Chunk[Array[Byte]] = Chunk.empty[Array[Byte]],
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty[Check],
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[Array[Byte]] {
  type Self = BinarySchemaField
  def setOrder(order: Int): BinarySchemaField = copy(order = order)
  def dataType: String = "binary"
  def meta: SchemaFieldType[Array[Byte]] = BinarySchemaField
  override def setDescription(description: String): BinarySchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): BinarySchemaField = copy(active = active)

  override def setName(name: String): BinarySchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): BinarySchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): BinarySchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): BinarySchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): BinarySchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): BinarySchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): BinarySchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): BinarySchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): BinarySchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): BinarySchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): BinarySchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): BinarySchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): BinarySchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): BinarySchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): BinarySchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)
}

object BinarySchemaField extends SchemaFieldType[Array[Byte]] {
  def fromOtherType[A](other: TypedSchemaField[A], subType: Option[StringSubType]): BinarySchemaField =
    BinarySchemaField(
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
      modificationUser = other.modificationUser,
      metadata = other.metadata
    )
  implicit val jsonDecoder: JsonDecoder[BinarySchemaField] = DeriveJsonDecoder.gen[BinarySchemaField]
  implicit val jsonEncoder: JsonEncoder[BinarySchemaField] = DeriveJsonEncoder.gen[BinarySchemaField]
}

final case class GeoPointSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField("index") indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[String] = None,
  samples: Chunk[String] = Chunk.empty,
  `enum`: Chunk[String] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[String] {
  type Self = GeoPointSchemaField
  def setOrder(order: Int): GeoPointSchemaField = copy(order = order)
  def dataType: String = "geo_point"
  def meta: SchemaFieldType[String] = GeoPointSchemaField

  /**
   * Return the reference of the schema of the record
   */
  override def setDescription(description: String): GeoPointSchemaField = copy(description = Some(description))

  override def setActive(active: Boolean): GeoPointSchemaField = copy(active = active)

  override def setName(name: String): GeoPointSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): GeoPointSchemaField = copy(metadata = metadata)

  override def setIndexProperties(indexProperties: IndexingProperties): GeoPointSchemaField =
    copy(indexProperties = indexProperties)

  override def setRequired(required: Boolean): GeoPointSchemaField = copy(required = required)

  override def setMultiple(multiple: Boolean): GeoPointSchemaField = copy(multiple = multiple)

  override def setModifiers(modifiers: Chunk[FieldModifier]): GeoPointSchemaField = copy(modifiers = modifiers)

  override def setValidators(validators: Chunk[Validator]): GeoPointSchemaField = copy(validators = validators)

  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): GeoPointSchemaField =
    copy(inferrerInfos = inferrerInfos)

  override def setIsSensitive(isSensitive: Boolean): GeoPointSchemaField = copy(isSensitive = isSensitive)

  override def setMasking(masking: String): GeoPointSchemaField = copy(masking = Some(masking))

  override def setChecks(checks: Chunk[Check]): GeoPointSchemaField = copy(checks = checks)

  override def setCreationDate(creationDate: OffsetDateTime): GeoPointSchemaField = copy(creationDate = creationDate)

  override def setCreationUser(creationUser: User.Id): GeoPointSchemaField = copy(creationUser = creationUser)

  override def setModificationDate(modificationDate: OffsetDateTime): GeoPointSchemaField =
    copy(modificationDate = modificationDate)

  override def setModificationUser(modificationUser: User.Id): GeoPointSchemaField =
    copy(modificationUser = modificationUser)
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object GeoPointSchemaField extends SchemaFieldType[String] {
  implicit final val decodeGeoPointSchemaField: JsonDecoder[GeoPointSchemaField] =
    DeriveJsonDecoder.gen[GeoPointSchemaField]
  implicit final val encodeGeoPointSchemaField: JsonEncoder[GeoPointSchemaField] =
    DeriveJsonEncoder.gen[GeoPointSchemaField]
}

/**
 * This class defines a OffsetDateTimeSchemaField entity
 * @param name
 *   the name of the OffsetDateTimeSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM OffsetDateTimeSchemaField entity namespace
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
final case class OffsetDateTimeSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[OffsetDateTime] = None,
  samples: Chunk[OffsetDateTime] = Chunk.empty,
  `enum`: Chunk[OffsetDateTime] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[OffsetDateTime] {
  type Self = OffsetDateTimeSchemaField
  def setOrder(order: Int): OffsetDateTimeSchemaField = copy(order = order)
  def dataType: String = "timestamp"
  def meta: SchemaFieldType[OffsetDateTime] = OffsetDateTimeSchemaField

  override def setDescription(description: String): OffsetDateTimeSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): OffsetDateTimeSchemaField = copy(active = active)

  override def setName(name: String): OffsetDateTimeSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): OffsetDateTimeSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): OffsetDateTimeSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): OffsetDateTimeSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): OffsetDateTimeSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): OffsetDateTimeSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): OffsetDateTimeSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): OffsetDateTimeSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): OffsetDateTimeSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): OffsetDateTimeSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): OffsetDateTimeSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): OffsetDateTimeSchemaField =
    copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): OffsetDateTimeSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): OffsetDateTimeSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): OffsetDateTimeSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)
}

object OffsetDateTimeSchemaField extends SchemaFieldType[OffsetDateTime] {
  implicit val jsonDecoder: JsonDecoder[OffsetDateTimeSchemaField] = DeriveJsonDecoder.gen[OffsetDateTimeSchemaField]
  implicit val jsonEncoder: JsonEncoder[OffsetDateTimeSchemaField] = DeriveJsonEncoder.gen[OffsetDateTimeSchemaField]
}

/**
 * This class defines a LocalDateTimeSchemaField entity
 * @param name
 *   the name of the LocalDateTimeSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM LocalDateTimeSchemaField entity namespace
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
final case class LocalDateTimeSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[LocalDateTime] = None,
  samples: Chunk[LocalDateTime] = Chunk.empty,
  `enum`: Chunk[LocalDateTime] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[LocalDateTime] {
  type Self = LocalDateTimeSchemaField
  def setOrder(order: Int): LocalDateTimeSchemaField = copy(order = order)
  def dataType: String = "datetime"
  def meta: SchemaFieldType[LocalDateTime] = LocalDateTimeSchemaField

  override def setDescription(description: String): LocalDateTimeSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): LocalDateTimeSchemaField = copy(active = active)

  override def setName(name: String): LocalDateTimeSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): LocalDateTimeSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): LocalDateTimeSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): LocalDateTimeSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): LocalDateTimeSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): LocalDateTimeSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): LocalDateTimeSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): LocalDateTimeSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): LocalDateTimeSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): LocalDateTimeSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): LocalDateTimeSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): LocalDateTimeSchemaField =
    copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): LocalDateTimeSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): LocalDateTimeSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): LocalDateTimeSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object LocalDateTimeSchemaField extends SchemaFieldType[LocalDateTime] {
  implicit val jsonDecoder: JsonDecoder[LocalDateTimeSchemaField] = DeriveJsonDecoder.gen[LocalDateTimeSchemaField]
  implicit val jsonEncoder: JsonEncoder[LocalDateTimeSchemaField] = DeriveJsonEncoder.gen[LocalDateTimeSchemaField]
}

/**
 * This class defines a LocalDateSchemaField entity
 * @param name
 *   the name of the LocalDateSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM LocalDateSchemaField entity namespace
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
final case class LocalDateSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[LocalDate] = None,
  samples: Chunk[LocalDate] = Chunk.empty,
  `enum`: Chunk[LocalDate] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[LocalDate] {
  type Self = LocalDateSchemaField
  def setOrder(order: Int): LocalDateSchemaField = copy(order = order)
  def dataType: String = "date"
  def meta: SchemaFieldType[LocalDate] = LocalDateSchemaField
  override def setDescription(description: String): LocalDateSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): LocalDateSchemaField = copy(active = active)

  override def setName(name: String): LocalDateSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): LocalDateSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): LocalDateSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): LocalDateSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): LocalDateSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): LocalDateSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): LocalDateSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): LocalDateSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): LocalDateSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): LocalDateSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): LocalDateSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): LocalDateSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): LocalDateSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): LocalDateSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): LocalDateSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object LocalDateSchemaField extends SchemaFieldType[LocalDate] {
  implicit val jsonDecoder: JsonDecoder[LocalDateSchemaField] = DeriveJsonDecoder.gen[LocalDateSchemaField]
  implicit val jsonEncoder: JsonEncoder[LocalDateSchemaField] = DeriveJsonEncoder.gen[LocalDateSchemaField]
}

/**
 * This class defines a DoubleSchemaField entity
 * @param name
 *   the name of the DoubleSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM DoubleSchemaField entity namespace
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
final case class DoubleSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Double] = None,
  samples: Chunk[Double] = Chunk.empty,
  `enum`: Chunk[Double] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[Double] {
  type Self = DoubleSchemaField
  def setOrder(order: Int): DoubleSchemaField = copy(order = order)
  def dataType: String = "double"
  def meta: SchemaFieldType[Double] = DoubleSchemaField

  override def setDescription(description: String): DoubleSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): DoubleSchemaField = copy(active = active)

  override def setName(name: String): DoubleSchemaField = copy(name = name)

  override def setIndexProperties(indexProperties: IndexingProperties): DoubleSchemaField =
    copy(indexProperties = indexProperties)
  override def setMetadata(metadata: Json.Obj): DoubleSchemaField = copy(metadata = metadata)
  override def setRequired(required: Boolean): DoubleSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): DoubleSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): DoubleSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): DoubleSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): DoubleSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): DoubleSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): DoubleSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): DoubleSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): DoubleSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): DoubleSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): DoubleSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): DoubleSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object DoubleSchemaField extends SchemaFieldType[Double] {
  implicit val jsonDecoder: JsonDecoder[DoubleSchemaField] = DeriveJsonDecoder.gen[DoubleSchemaField]
  implicit val jsonEncoder: JsonEncoder[DoubleSchemaField] = DeriveJsonEncoder.gen[DoubleSchemaField]
}

/**
 * This class defines a BigIntSchemaField entity
 * @param name
 *   the name of the BigIntSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM BigIntSchemaField entity namespace
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
final case class BigIntSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[BigInt] = None,
  samples: Chunk[BigInt] = Chunk.empty,
  `enum`: Chunk[BigInt] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[BigInt] {
  type Self = BigIntSchemaField
  def setOrder(order: Int): BigIntSchemaField = copy(order = order)
  def dataType: String = "bigint"
  def meta: SchemaFieldType[BigInt] = BigIntSchemaField

  override def setDescription(description: String): BigIntSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): BigIntSchemaField = copy(active = active)

  override def setName(name: String): BigIntSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): BigIntSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): BigIntSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): BigIntSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): BigIntSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): BigIntSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): BigIntSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): BigIntSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): BigIntSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): BigIntSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): BigIntSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): BigIntSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): BigIntSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): BigIntSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): BigIntSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object BigIntSchemaField extends SchemaFieldType[BigInt] {
  implicit val jsonDecoder: JsonDecoder[BigIntSchemaField] = DeriveJsonDecoder.gen[BigIntSchemaField]
  implicit val jsonEncoder: JsonEncoder[BigIntSchemaField] = DeriveJsonEncoder.gen[BigIntSchemaField]
}

/**
 * This class defines a BigDecimalSchemaField entity
 * @param name
 *   the name of the BigDecimalSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM BigDecimalSchemaField entity namespace
 * @param indexProperties
 *   a IndexingProperties entity
 * @param default
 *   a default value for the field
 * @param enum
 *   a list of BigDecimal entities
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
 *   the creation date of the BigDecimalSchemaField
 * @param creationUser
 *   the reference of the user that created the BigDecimalSchemaField
 * @param modificationDate
 *   the modification date of the BigDecimalSchemaField
 * @param modificationUser
 *   the reference of last user that changed the BigDecimalSchemaField
 */
final case class BigDecimalSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[BigDecimal] = None,
  samples: Chunk[BigDecimal] = Chunk.empty,
  `enum`: Chunk[BigDecimal] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[BigDecimal] {
  type Self = BigDecimalSchemaField
  def setOrder(order: Int): BigDecimalSchemaField = copy(order = order)
  def dataType: String = "BigDecimal"
  def meta: SchemaFieldType[BigDecimal] = BigDecimalSchemaField

  override def setDescription(description: String): BigDecimalSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): BigDecimalSchemaField = copy(active = active)

  override def setName(name: String): BigDecimalSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): BigDecimalSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): BigDecimalSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): BigDecimalSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): BigDecimalSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): BigDecimalSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): BigDecimalSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): BigDecimalSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): BigDecimalSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): BigDecimalSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): BigDecimalSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): BigDecimalSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): BigDecimalSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): BigDecimalSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): BigDecimalSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object BigDecimalSchemaField extends SchemaFieldType[BigDecimal] {
  implicit val jsonDecoder: JsonDecoder[BigDecimalSchemaField] = DeriveJsonDecoder.gen[BigDecimalSchemaField]
  implicit val jsonEncoder: JsonEncoder[BigDecimalSchemaField] = DeriveJsonEncoder.gen[BigDecimalSchemaField]
}

/**
 * This class defines a IntSchemaField entity
 * @param name
 *   the name of the IntSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM IntSchemaField entity namespace
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
final case class IntSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Int] = None,
  samples: Chunk[Int] = Chunk.empty,
  `enum`: Chunk[Int] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[Int] {
  type Self = IntSchemaField
  def setOrder(order: Int): IntSchemaField = copy(order = order)
  def dataType: String = "integer"
  def meta: SchemaFieldType[Int] = IntSchemaField

  override def setDescription(description: String): IntSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): IntSchemaField = copy(active = active)

  override def setName(name: String): IntSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): IntSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): IntSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): IntSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): IntSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): IntSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): IntSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): IntSchemaField = copy(
    inferrerInfos = inferrerInfos
  )
  override def setIsSensitive(isSensitive: Boolean): IntSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): IntSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): IntSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): IntSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): IntSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): IntSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): IntSchemaField =
    copy(modificationUser = modificationUser)
  def toDoubleField: DoubleSchemaField = DoubleSchemaField(
    name = this.name,
    active = this.active,
    className = this.className,
    originalName = this.originalName,
    description = this.description,
    indexProperties = this.indexProperties,
    default = this.default.map(_.toDouble),
    samples = this.samples.map(_.toDouble),
    enum = this.enum.map(_.toDouble),
    modifiers = this.modifiers,
    required = this.required,
    multiple = this.multiple,
    order = this.order,
    isInternal = this.isInternal,
    customStringParser = this.customStringParser,
    validators = this.validators,
    inferrerInfos = this.inferrerInfos,
    isSensitive = this.isSensitive,
    masking = this.masking,
    checks = this.checks,
    creationDate = this.creationDate,
    creationUser = this.creationUser,
    modificationDate = this.modificationDate,
    modificationUser = this.modificationUser,
    metadata = this.metadata
  )

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object IntSchemaField extends SchemaFieldType[Int] {
  implicit val jsonDecoder: JsonDecoder[IntSchemaField] = DeriveJsonDecoder.gen[IntSchemaField]
  implicit val jsonEncoder: JsonEncoder[IntSchemaField] = DeriveJsonEncoder.gen[IntSchemaField]
}

/**
 * This class defines a BooleanSchemaField entity
 * @param name
 *   the name of the BooleanSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM BooleanSchemaField entity namespace
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
final case class BooleanSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Boolean] = None,
  samples: Chunk[Boolean] = Chunk.empty,
  `enum`: Chunk[Boolean] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[Boolean] {
  type Self = BooleanSchemaField
  def setOrder(order: Int): BooleanSchemaField = copy(order = order)
  def dataType: String = "boolean"
  def meta: SchemaFieldType[Boolean] = BooleanSchemaField

  override def setDescription(description: String): BooleanSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): BooleanSchemaField = copy(active = active)

  override def setName(name: String): BooleanSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): BooleanSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): BooleanSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): BooleanSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): BooleanSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): BooleanSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): BooleanSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): BooleanSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): BooleanSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): BooleanSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): BooleanSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): BooleanSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): BooleanSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): BooleanSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): BooleanSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object BooleanSchemaField extends SchemaFieldType[Boolean] {
  implicit val jsonDecoder: JsonDecoder[BooleanSchemaField] = DeriveJsonDecoder.gen[BooleanSchemaField]
  implicit val jsonEncoder: JsonEncoder[BooleanSchemaField] = DeriveJsonEncoder.gen[BooleanSchemaField]
}

/**
 * This class defines a LongSchemaField entity
 * @param name
 *   the name of the LongSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM LongSchemaField entity namespace
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
final case class LongSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Long] = None,
  samples: Chunk[Long] = Chunk.empty,
  `enum`: Chunk[Long] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[Long] {
  type Self = LongSchemaField
  def setOrder(order: Int): LongSchemaField = copy(order = order)
  def dataType: String = "long"
  def meta: SchemaFieldType[Long] = LongSchemaField
  override def setDescription(description: String): LongSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): LongSchemaField = copy(active = active)

  override def setName(name: String): LongSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): LongSchemaField = copy(metadata = metadata)

  override def setIndexProperties(indexProperties: IndexingProperties): LongSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): LongSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): LongSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): LongSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): LongSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): LongSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): LongSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): LongSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): LongSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): LongSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): LongSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): LongSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): LongSchemaField =
    copy(modificationUser = modificationUser)
  def toDoubleField: DoubleSchemaField = DoubleSchemaField(
    name = this.name,
    active = this.active,
    className = this.className,
    originalName = this.originalName,
    description = this.description,
    indexProperties = this.indexProperties,
    default = this.default.map(_.toDouble),
    samples = this.samples.map(_.toDouble),
    enum = this.enum.map(_.toDouble),
    modifiers = this.modifiers,
    required = this.required,
    multiple = this.multiple,
    order = this.order,
    isInternal = this.isInternal,
    customStringParser = this.customStringParser,
    validators = this.validators,
    inferrerInfos = this.inferrerInfos,
    isSensitive = this.isSensitive,
    masking = this.masking,
    checks = this.checks,
    creationDate = this.creationDate,
    creationUser = this.creationUser,
    modificationDate = this.modificationDate,
    modificationUser = this.modificationUser,
    metadata = this.metadata
  )

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object LongSchemaField extends SchemaFieldType[Long] {
  implicit val jsonDecoder: JsonDecoder[LongSchemaField] = DeriveJsonDecoder.gen[LongSchemaField]
  implicit val jsonEncoder: JsonEncoder[LongSchemaField] = DeriveJsonEncoder.gen[LongSchemaField]
}

/**
 * This class defines a ShortSchemaField entity
 * @param name
 *   the name of the ShortSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM ShortSchemaField entity namespace
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
final case class ShortSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Short] = None,
  samples: Chunk[Short] = Chunk.empty,
  `enum`: Chunk[Short] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[Short] {
  type Self = ShortSchemaField
  def setOrder(order: Int): ShortSchemaField = copy(order = order)
  def dataType: String = "integer"
  def meta: SchemaFieldType[Short] = ShortSchemaField
  override def setDescription(description: String): ShortSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): ShortSchemaField = copy(active = active)

  override def setName(name: String): ShortSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): ShortSchemaField = copy(metadata = metadata)

  override def setIndexProperties(indexProperties: IndexingProperties): ShortSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): ShortSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): ShortSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): ShortSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): ShortSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): ShortSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): ShortSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): ShortSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): ShortSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): ShortSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): ShortSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): ShortSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): ShortSchemaField =
    copy(modificationUser = modificationUser)
  def toDoubleField: DoubleSchemaField = DoubleSchemaField(
    name = this.name,
    active = this.active,
    className = this.className,
    originalName = this.originalName,
    description = this.description,
    indexProperties = this.indexProperties,
    default = this.default.map(_.toDouble),
    samples = this.samples.map(_.toDouble),
    enum = this.enum.map(_.toDouble),
    modifiers = this.modifiers,
    required = this.required,
    multiple = this.multiple,
    order = this.order,
    isInternal = this.isInternal,
    customStringParser = this.customStringParser,
    validators = this.validators,
    inferrerInfos = this.inferrerInfos,
    isSensitive = this.isSensitive,
    masking = this.masking,
    checks = this.checks,
    creationDate = this.creationDate,
    creationUser = this.creationUser,
    modificationDate = this.modificationDate,
    modificationUser = this.modificationUser,
    metadata = this.metadata
  )

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object ShortSchemaField extends SchemaFieldType[Short] {
  implicit val jsonDecoder: JsonDecoder[ShortSchemaField] = DeriveJsonDecoder.gen[ShortSchemaField]
  implicit val jsonEncoder: JsonEncoder[ShortSchemaField] = DeriveJsonEncoder.gen[ShortSchemaField]
}

/**
 * This class defines a FloatSchemaField entity
 * @param name
 *   the name of the FloatSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM FloatSchemaField entity namespace
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
final case class FloatSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Float] = None,
  samples: Chunk[Float] = Chunk.empty,
  `enum`: Chunk[Float] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  @JsonNoDefault required: Boolean = false,
  @JsonNoDefault multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[Float] {
  type Self = FloatSchemaField
  def setOrder(order: Int): FloatSchemaField = copy(order = order)
  def dataType: String = "float"
  def meta: SchemaFieldType[Float] = FloatSchemaField
  override def setDescription(description: String): FloatSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): FloatSchemaField = copy(active = active)

  override def setName(name: String): FloatSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): FloatSchemaField = copy(metadata = metadata)

  override def setIndexProperties(indexProperties: IndexingProperties): FloatSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): FloatSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): FloatSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): FloatSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): FloatSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): FloatSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): FloatSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): FloatSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): FloatSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): FloatSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): FloatSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): FloatSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): FloatSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

  def toDoubleField: DoubleSchemaField = DoubleSchemaField(
    name = this.name,
    active = this.active,
    className = this.className,
    originalName = this.originalName,
    description = this.description,
    indexProperties = this.indexProperties,
    default = this.default.map(_.toDouble),
    samples = this.samples.map(_.toDouble),
    enum = this.enum.map(_.toDouble),
    modifiers = this.modifiers,
    required = this.required,
    multiple = this.multiple,
    order = this.order,
    isInternal = this.isInternal,
    customStringParser = this.customStringParser,
    validators = this.validators,
    inferrerInfos = this.inferrerInfos,
    isSensitive = this.isSensitive,
    masking = this.masking,
    checks = this.checks,
    creationDate = this.creationDate,
    creationUser = this.creationUser,
    modificationDate = this.modificationDate,
    modificationUser = this.modificationUser,
    metadata = this.metadata
  )
}

object FloatSchemaField extends SchemaFieldType[Float] {
  implicit val jsonDecoder: JsonDecoder[FloatSchemaField] = DeriveJsonDecoder.gen[FloatSchemaField]
  implicit val jsonEncoder: JsonEncoder[FloatSchemaField] = DeriveJsonEncoder.gen[FloatSchemaField]
}

/**
 * This class defines a ByteSchemaField entity
 * @param name
 *   the name of the ByteSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM ByteSchemaField entity namespace
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
final case class ByteSchemaField(
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[Byte] = None,
  samples: Chunk[Byte] = Chunk.empty,
  `enum`: Chunk[Byte] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[Byte] {
  type Self = ByteSchemaField
  def setOrder(order: Int): ByteSchemaField = copy(order = order)
  def dataType: String = "byte"
  def meta: SchemaFieldType[Byte] = ByteSchemaField
  override def setDescription(description: String): ByteSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): ByteSchemaField = copy(active = active)

  override def setName(name: String): ByteSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): ByteSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): ByteSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): ByteSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): ByteSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): ByteSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): ByteSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): ByteSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): ByteSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): ByteSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): ByteSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): ByteSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): ByteSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): ByteSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): ByteSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

  def toDoubleField: DoubleSchemaField = DoubleSchemaField(
    name = this.name,
    active = this.active,
    className = this.className,
    originalName = this.originalName,
    description = this.description,
    indexProperties = this.indexProperties,
    default = this.default.map(_.toDouble),
    samples = this.samples.map(_.toDouble),
    enum = this.enum.map(_.toDouble),
    modifiers = this.modifiers,
    required = this.required,
    multiple = this.multiple,
    order = this.order,
    isInternal = this.isInternal,
    customStringParser = this.customStringParser,
    validators = this.validators,
    inferrerInfos = this.inferrerInfos,
    isSensitive = this.isSensitive,
    masking = this.masking,
    checks = this.checks,
    creationDate = this.creationDate,
    creationUser = this.creationUser,
    modificationDate = this.modificationDate,
    modificationUser = this.modificationUser,
    metadata = this.metadata
  )
}

object ByteSchemaField extends SchemaFieldType[Byte] {
  implicit val jsonDecoder: JsonDecoder[ByteSchemaField] = DeriveJsonDecoder.gen[ByteSchemaField]
  implicit val jsonEncoder: JsonEncoder[ByteSchemaField] = DeriveJsonEncoder.gen[ByteSchemaField]
}

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
final case class ListSchemaField(
  items: SchemaField,
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  `enum`: Chunk[SchemaField] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = true,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  subType: ListSubType = ListSubType.LIST,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends SchemaField {
  type Self = ListSchemaField
  def setOrder(order: Int): ListSchemaField = copy(order = order)
  def dataType: String = subType.entryName
  override def isEnum: Boolean = items.isEnum
  def getField(name: String): Either[MissingFieldException, SchemaField] = items.getField(name)
  override def setDescription(description: String): ListSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): ListSchemaField = copy(active = active)

  override def setName(name: String): ListSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): ListSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): ListSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): ListSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): ListSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): ListSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): ListSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): ListSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): ListSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): ListSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): ListSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): ListSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): ListSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): ListSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): ListSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = items.getFlatFields

}

object ListSchemaField {
  implicit val jsonDecoder: JsonDecoder[ListSchemaField] = DeriveJsonDecoder.gen[ListSchemaField]
  implicit val jsonEncoder: JsonEncoder[ListSchemaField] = DeriveJsonEncoder.gen[ListSchemaField]
}

/**
 * This class defines a RangeSchemaField entity
 * @param items
 *   a SchemaField entity
 * @param name
 *   the name of the RangeSchemaField entity
 * @param active
 *   if this entity is active
 * @param className
 *   a string the rappresent the JVM RangeSchemaField entity namespace
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
 *   the creation date of the RangeSchemaField
 * @param creationUser
 *   the reference of the user that created the RangeSchemaField
 * @param modificationDate
 *   the modification date of the RangeSchemaField
 * @param modificationUser
 *   the reference of last user that changed the RangeSchemaField
 */
final case class RangeSchemaField(
  items: SchemaField,
  name: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  `enum`: Chunk[SchemaField] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = true,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  subType: ListSubType = ListSubType.LIST,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends SchemaField {
  type Self = RangeSchemaField
  def setOrder(order: Int): RangeSchemaField = copy(order = order)
  def dataType: String = subType.entryName
  override def isEnum: Boolean = items.isEnum
  def getField(name: String): Either[MissingFieldException, SchemaField] = items.getField(name)
  override def setDescription(description: String): RangeSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): RangeSchemaField = copy(active = active)

  override def setName(name: String): RangeSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): RangeSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): RangeSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): RangeSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): RangeSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): RangeSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): RangeSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): RangeSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): RangeSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): RangeSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): RangeSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): RangeSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): RangeSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): RangeSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): RangeSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object RangeSchemaField {
  implicit val jsonDecoder: JsonDecoder[RangeSchemaField] = DeriveJsonDecoder.gen[RangeSchemaField]
  implicit val jsonEncoder: JsonEncoder[RangeSchemaField] = DeriveJsonEncoder.gen[RangeSchemaField]
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
final case class RefSchemaField(
  name: String,
  @jsonField(s"$$ref") ref: String,
  active: Boolean = true,
  className: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  default: Option[String] = None,
  samples: Chunk[String] = Chunk.empty,
  `enum`: Chunk[String] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  @jsonField(SUB_TYPE) subType: Option[StringSubType] = None,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends TypedSchemaField[String] {
  type Self = RefSchemaField
  def setOrder(order: Int): RefSchemaField = copy(order = order)
  def dataType: String = "ref"
  def meta: SchemaFieldType[String] = RefSchemaField
  override def setDescription(description: String): RefSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): RefSchemaField = copy(active = active)

  override def setName(name: String): RefSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): RefSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): RefSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): RefSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): RefSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): RefSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): RefSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): RefSchemaField = copy(
    inferrerInfos = inferrerInfos
  )
  override def setIsSensitive(isSensitive: Boolean): RefSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): RefSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): RefSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): RefSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): RefSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): RefSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): RefSchemaField =
    copy(modificationUser = modificationUser)

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] = Chunk(this)

}

object RefSchemaField extends SchemaFieldType[String] {
  implicit val jsonDecoder: JsonDecoder[RefSchemaField] = DeriveJsonDecoder.gen[RefSchemaField]
  implicit val jsonEncoder: JsonEncoder[RefSchemaField] = DeriveJsonEncoder.gen[RefSchemaField]
}

/**
 * This class defines a ObjectSchemaField entity
 * @param name
 *   the name of the ObjectSchemaField entity
 * @param active
 *   if this entity is active
 * @param module
 *   the module associated to the ObjectSchemaField entity
 * @param type
 *   the type of the ObjectSchemaField entity
 * @param indexProperties
 *   a IndexingProperties entity
 * @param className
 *   a string the rappresent the JVM ObjectSchemaField entity namespace
 * @param fields
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
 *   the creation date of the ObjectSchemaField
 * @param creationUser
 *   the reference of the user that created the ObjectSchemaField
 * @param modificationDate
 *   the modification date of the ObjectSchemaField
 * @param modificationUser
 *   the reference of last user that changed the ObjectSchemaField
 */
final case class ObjectSchemaField(
  name: String,
  active: Boolean = true,
  module: Option[String] = None,
  originalName: Option[String] = None,
  description: Option[String] = None,
  `type`: String = "object",
  @jsonField(INDEX) indexProperties: IndexingProperties = IndexingProperties.empty,
  @jsonField(CLASS_NAME) className: Option[String] = None,
  fields: Chunk[SchemaField] = Chunk.empty,
  modifiers: Chunk[FieldModifier] = Chunk.empty,
  required: Boolean = false,
  multiple: Boolean = false,
  order: Int = -1,
  isInternal: Boolean = false,
  customStringParser: Option[Script] = None,
  validators: Chunk[Validator] = Chunk.empty,
  inferrerInfos: Chunk[InferrerInfo] = Chunk.empty,
  @jsonField(IS_SENSITIVE) isSensitive: Boolean = false,
  masking: Option[String] = None,
  checks: Chunk[Check] = Chunk.empty,
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  metadata: Json.Obj = Json.Obj()
) extends SchemaField {
  type Self = ObjectSchemaField
  def setOrder(order: Int): ObjectSchemaField = copy(order = order)
  override def dataType: String = "object"
  override def isEnum: Boolean = false
  def isRoot: Boolean = false
  def getField(name: String): Either[MissingFieldException, SchemaField] = fields.find(_.name == name) match {
    case Some(x) =>
      Right(x)
    case None =>
      Left(MissingFieldException(s"Missing Field $name"))
  }

  /**
   * Return a list of fields flattened
   */
  override def getFlatFields: Chunk[SchemaField] =
    this.fields.flatMap(_.getFlatFields).map(f => f.setName(s"${this.name}.${f.name}"))

  override def setDescription(description: String): ObjectSchemaField = copy(description = Some(description))
  override def setActive(active: Boolean): ObjectSchemaField = copy(active = active)

  override def setName(name: String): ObjectSchemaField = copy(name = name)

  override def setMetadata(metadata: Json.Obj): ObjectSchemaField = copy(metadata = metadata)
  override def setIndexProperties(indexProperties: IndexingProperties): ObjectSchemaField =
    copy(indexProperties = indexProperties)
  override def setRequired(required: Boolean): ObjectSchemaField = copy(required = required)
  override def setMultiple(multiple: Boolean): ObjectSchemaField = copy(multiple = multiple)
  override def setModifiers(modifiers: Chunk[FieldModifier]): ObjectSchemaField = copy(modifiers = modifiers)
  override def setValidators(validators: Chunk[Validator]): ObjectSchemaField = copy(validators = validators)
  override def setInferrerInfos(inferrerInfos: Chunk[InferrerInfo]): ObjectSchemaField =
    copy(inferrerInfos = inferrerInfos)
  override def setIsSensitive(isSensitive: Boolean): ObjectSchemaField = copy(isSensitive = isSensitive)
  override def setMasking(masking: String): ObjectSchemaField = copy(masking = Some(masking))
  override def setChecks(checks: Chunk[Check]): ObjectSchemaField = copy(checks = checks)
  override def setCreationDate(creationDate: OffsetDateTime): ObjectSchemaField = copy(creationDate = creationDate)
  override def setCreationUser(creationUser: User.Id): ObjectSchemaField = copy(creationUser = creationUser)
  override def setModificationDate(modificationDate: OffsetDateTime): ObjectSchemaField =
    copy(modificationDate = modificationDate)
  override def setModificationUser(modificationUser: User.Id): ObjectSchemaField =
    copy(modificationUser = modificationUser)
}
object ObjectSchemaField {
  implicit val jsonDecoder: JsonDecoder[ObjectSchemaField] = DeriveJsonDecoder.gen[ObjectSchemaField]
  implicit val jsonEncoder: JsonEncoder[ObjectSchemaField] = DeriveJsonEncoder.gen[ObjectSchemaField]
}
