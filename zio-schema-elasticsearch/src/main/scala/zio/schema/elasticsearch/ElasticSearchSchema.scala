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

import java.time.OffsetDateTime
import zio.schema.elasticsearch.annotations.{ IndexName, KeyField, KeyManagement, KeyPostProcessing }
import zio.common.{ OffsetDateTimeHelper, StringUtils, UUID }
import zio.exception.{ FrameworkException, MergeSchemaException, MissingFieldException, SchemaValidationException }
import zio.schema.elasticsearch.SchemaNames._
import zio.json.ast.Json
import zio.json._
import zio.schema.{ Schema, StandardType, TypeId }

import scala.collection.mutable.ListBuffer

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
 * @param fields
 *   the sub fields
 */
final case class ElasticSearchSchema[T](
  name: String,
  module: String,
  version: Int = 1,
  `type`: String = "object",
  description: String = "",
  @jsonField(AUTO_OWNER) autoOwner: Boolean = false,
  active: Boolean = true,
  labels: Chunk[String] = Chunk.empty[String],
  @jsonField(CREATION_DATE) creationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(CREATION_USER) creationUser: User.Id = User.SystemID,
  @jsonField(MODIFICATION_DATE) modificationDate: OffsetDateTime = OffsetDateTimeHelper.utcNow,
  @jsonField(MODIFICATION_USER) modificationUser: User.Id = User.SystemID,
  key: KeyManagement = KeyManagement.empty,
  index: GlobalIndexProperties = GlobalIndexProperties(),
  @jsonField(IS_ROOT) isRoot: Boolean = false,
  @jsonField(CLASS_NAME) className: Option[String] = None,
  delta: Chunk[DeltaRule] = Chunk.empty[DeltaRule],
  fields: Chunk[SchemaField] = Chunk.empty[SchemaField],
  schema: Schema[T]
)(implicit decoder: JsonDecoder[T], encoder: JsonEncoder[T])
    extends EditingTrait {
  def id: String = s"$module.$name"
  def idVersioned: String = s"$module.$name.$version"
  def required: Chunk[String] = fields.filter(_.required).map(_.name)
  def indexRequireType: Boolean = index.requireType
  def indexRequireTypePrefix: String = name + SchemaNames.SINGLE_STORAGE_SEPARATOR

  def indexName: String = KebabCase.apply(this.index.indexName.getOrElse(name))
  def ownerField: Option[SchemaField] = fields.find { field =>
    field
      .isInstanceOf[StringSchemaField] && field.asInstanceOf[StringSchemaField].subType.contains(StringSubType.UserId)
  }
  def isOwnerFiltrable: Boolean = autoOwner && ownerField.isDefined
  private def extractKey(json: Json.Obj): Option[String] = {
    val keyResult = if (key == KeyManagement.empty) {
      None
    } else {
      val components = key.parts.flatMap({
        case k: KeyField =>
          for (jValue <- json.getOption[Json](k.field))
            yield postProcessScripts(jValue.toJson.stripPrefix("\"").stripSuffix("\""), k.postProcessing)
      })
      val keyValue = components.mkString(key.separator.getOrElse(""))
      Some(postProcessScripts(keyValue, key.postProcessing))
    }
    keyResult.map(validateId)
  }
  def cleanId(value: String): String = if (this.indexRequireType && value.startsWith(indexRequireTypePrefix)) {
    value.substring(indexRequireTypePrefix.length)
  } else {
    value
  }
  def validateId(value: String): String = if (this.indexRequireType && !value.startsWith(indexRequireTypePrefix)) {
    indexRequireTypePrefix + value
  } else {
    value
  }
  private def postProcessScripts(keyValue: String, postprocessing: List[KeyPostProcessing]): String = {
    var result = keyValue
    import StringUtils._
    postprocessing.foreach({
      case KeyPostProcessing.LowerCase =>
        result = result.toLowerCase
      case KeyPostProcessing.UpperCase =>
        result = result.toUpperCase
      case KeyPostProcessing.Slug =>
        result = result.slug
      case KeyPostProcessing.Hash =>
        result = result.sha256Hash
      case KeyPostProcessing(_, _) =>
    })
    result
  }
  def resolveId(json: Json.Obj, optionalID: Option[String]): Option[String] =
    optionalID.orElse(extractKey(json)).map { rId =>
      if (indexRequireType && !rId.startsWith(indexRequireTypePrefix)) {
        indexRequireTypePrefix + rId
      } else rId
    }
  def getField(name: String): Either[MissingFieldException, SchemaField] = if (name.contains(".")) {
    val tokens = name.split('.')
    var result = getField(tokens.head)
    var i = 1
    while (i < tokens.length - 1 && result.isRight) {
      result = result.flatMap(_.getField(tokens(i)))
      i = i + 1
    }
    result
  } else {
    fields.find(_.name == name) match {
      case Some(x) =>
        Right(x)
      case None =>
        Left(MissingFieldException(s"Missing Field $name"))
    }
  }
  def validate(): Either[FrameworkException, ElasticSearchSchema[T]] = {
    val iterator = delta.iterator
    var result: Either[FrameworkException, ElasticSearchSchema[T]] = Right(this)
    while (iterator.hasNext && result.isRight) {
      val elem = iterator.next()
      val res = getField(elem.field)
      if (res.isLeft) {
        result = Left(MissingFieldException(s"delta is missing ${elem.field}"))
      }
    }
    result
  }
  def extractNameConversions: Map[String, String] =
    fields.flatMap(f => f.originalName.map(fo => fo -> f.name)).toMap

  /**
   * Merge Two schema in one.
   *
   * @param other
   * the other schema to merge
   * @param onlyExistsInFirst
   * merge the field if only exists in the first one
   * @return
   * the merged schema
   */
  def merge(
    otherSchema: ElasticSearchSchema[T],
    onlyExistsInFirst: Boolean = false
  ): Either[MergeSchemaException, ElasticSearchSchema[T]] = {
    val firstObjectFields = this.fields.map(_.name)
    val secondObjectFields = if (onlyExistsInFirst) Nil else otherSchema.fields.map(_.name)
    val fieldNames = (firstObjectFields ++ secondObjectFields).distinct
    val fieldToMarkRequired = firstObjectFields.toSet.intersect(secondObjectFields.toSet)
    val finalFields = new ListBuffer[SchemaField]
    val exceptions = new ListBuffer[MergeSchemaException]
    fieldNames.foreach { fieldName =>
      val srcField = this.fields.find(_.name == fieldName)
      val dstField = otherSchema.fields.find(_.name == fieldName)
      srcField match {
        case Some(sf) =>
          dstField match {
            case Some(df) =>
              sf.merge(df, onlyExistsInFirst) match {
                case Left(value) =>
                  exceptions += value
                case Right(value) =>
                  // check if the field must be marked required
                  finalFields += value.setRequired(fieldToMarkRequired.contains(fieldName))
              }
            case None =>
              finalFields += sf.setRequired(false)
          }
        case None =>
          dstField match {
            case Some(value) =>
              finalFields += value.setRequired(false)
            case None =>
          }
      }
    }
    if (exceptions.nonEmpty) {
      Left(
        MergeSchemaException(
          exceptions.map(_.error).mkString("\n"),
          schemaFields = exceptions.flatMap(_.schemaFields).toList
        )
      )
    } else Right(this.copy(fields = Chunk.fromIterable(finalFields)))
  }

  /**
   * Return a list of fields flattened
   */
  def getFlattenFields: Chunk[SchemaField] =
    fields.flatMap(_.getFlatFields)

}

object ElasticSearchSchema {

  def extractFieldValue(name: String, schema: Schema[_], anns: Chunk[Any]): Option[SchemaField] =
    schema match {
      case enum: Schema.Enum[_] => None
      case record: Schema.Record[_] =>
        Some(ObjectSchemaField(name = name, fields = record.fields.flatMap(f => extractField(f))))

      case collection: Schema.Collection[_, _] =>
        collection match {
          case Schema.Sequence(elementSchema, _, _, annotations, _) =>
            for {
              elem <- extractFieldValue(name, elementSchema, anns ++ annotations)
            } yield ListSchemaField(items = elem, name = name)
          case Schema.Map(_, _, _) => None //TODO
          case Schema.Set(elementSchema, annotations) =>
            for {
              elem <- extractFieldValue(name, elementSchema, anns ++ annotations)
            } yield ListSchemaField(items = elem, name = name)

        }
      case Schema.Transform(schema, _, _, annotations, _) => extractFieldValue(name, schema, anns ++ annotations)
      case Schema.Primitive(standardType, annotations) =>
        standardType match {
          case StandardType.UnitType           => None
          case StandardType.StringType         => Some(StringSchemaField(name = name))
          case StandardType.BoolType           => Some(BooleanSchemaField(name = name))
          case StandardType.ByteType           => Some(ByteSchemaField(name = name))
          case StandardType.ShortType          => Some(ShortSchemaField(name = name))
          case StandardType.IntType            => Some(IntSchemaField(name = name))
          case StandardType.LongType           => Some(LongSchemaField(name = name))
          case StandardType.FloatType          => Some(FloatSchemaField(name = name))
          case StandardType.DoubleType         => Some(DoubleSchemaField(name = name))
          case StandardType.BinaryType         => Some(BinarySchemaField(name = name))
          case StandardType.CharType           => Some(StringSchemaField(name = name))
          case StandardType.UUIDType           => Some(StringSchemaField(name = name, subType = Some(StringSubType.UUID)))
          case StandardType.BigDecimalType     => Some(BigDecimalSchemaField(name = name))
          case StandardType.BigIntegerType     => Some(BigIntSchemaField(name = name))
          case StandardType.DayOfWeekType      => Some(IntSchemaField(name = name))
          case StandardType.MonthType          => Some(IntSchemaField(name = name))
          case StandardType.MonthDayType       => Some(IntSchemaField(name = name))
          case StandardType.PeriodType         => Some(IntSchemaField(name = name))
          case StandardType.YearType           => Some(IntSchemaField(name = name))
          case StandardType.YearMonthType      => Some(IntSchemaField(name = name))
          case StandardType.ZoneIdType         => Some(StringSchemaField(name = name))
          case StandardType.ZoneOffsetType     => Some(StringSchemaField(name = name))
          case StandardType.DurationType       => Some(StringSchemaField(name = name))
          case StandardType.InstantType        => Some(LongSchemaField(name = name))
          case StandardType.LocalDateType      => Some(LocalDateSchemaField(name = name))
          case StandardType.LocalTimeType      => Some(StringSchemaField(name = name))
          case StandardType.LocalDateTimeType  => Some(LocalDateTimeSchemaField(name = name))
          case StandardType.OffsetTimeType     => Some(StringSchemaField(name = name))
          case StandardType.OffsetDateTimeType => Some(OffsetDateTimeSchemaField(name = name))
          case StandardType.ZonedDateTimeType  => Some(OffsetDateTimeSchemaField(name = name))
        }
      case Schema.Optional(schema, annotations) => extractFieldValue(name, schema, anns ++ annotations)
      case Schema.Fail(_, _)                    => None
      case Schema.Tuple2(left, right, annotations) =>
        for {
          l <- extractFieldValue(name, left, anns ++ annotations)
          r <- extractFieldValue(name, right, anns ++ annotations)
          result <- l.merge(r, false).toOption
        } yield result

      case Schema.Either(left, right, annotations) =>
        for {
          l <- extractFieldValue(name, left, anns ++ annotations)
          r <- extractFieldValue(name, right, anns ++ annotations)
          result <- l.merge(r, false).toOption
        } yield result

      case Schema.Lazy(schema0) => extractFieldValue(name, schema0.apply(), anns)
      case Schema.Dynamic(_)    => None
    }

  def extractField(value: Schema.Field[_, _]): Option[SchemaField] = {
    val name = value.name.toString
    extractFieldValue(name, value.schema, value.annotations)
  }

  def gen[T](implicit schema: Schema[T], decoder: JsonDecoder[T], encoder: JsonEncoder[T]): ElasticSearchSchema[T] =
    schemaFromRecord(schema.asInstanceOf[Schema.Record[T]])

  private def schemaFromRecord[T](
    record: Schema.Record[T]
  )(implicit schema: Schema[T], decoder: JsonDecoder[T], encoder: JsonEncoder[T]): ElasticSearchSchema[T] = {
    var name = "empty"
    var module = "empty"
    record.id match {
      case TypeId.Nominal(packageName, objectNames, typeName) =>
        module = packageName.mkString(".")
        name = typeName
      case TypeId.Structural =>
    }
    var indexProperties = GlobalIndexProperties()
    record.annotations.foreach {
      case IndexName(n) => indexProperties = indexProperties.copy(indexName = Some(n))
      case _            =>
    }
    ElasticSearchSchema[T](
      name = name,
      module = module,
      index = indexProperties,
      fields = record.fields.flatMap(f => extractField(f)),
      schema = schema
    )
  }

  def extractSchema[T](
    zschema: Schema[T],
    anns: Chunk[Any] = Chunk.empty
  )(implicit decoder: JsonDecoder[T], encoder: JsonEncoder[T]): Either[FrameworkException, ElasticSearchSchema[T]] =
    zschema match {
      case enum: Schema.Enum[_] => Left(SchemaValidationException(s"Invalid schema format $enum"))
      case record: Schema.Record[_] =>
        Right(schemaFromRecord(record.asInstanceOf[Schema.Record[T]])(zschema, decoder, encoder))
      case Schema.Transform(schema, _, _, annotations, _) =>
        extractSchema(schema.asInstanceOf[Schema[T]], anns ++ annotations)
      case s: Schema.Optional[_]        => extractSchema(s.schema.asInstanceOf[Schema[T]], anns ++ s.annotations)
      case sch: Schema.Fail[_]          => Left(SchemaValidationException(s"Invalid schema format $sch"))
      case s: Schema.Lazy[_]            => extractSchema(s.schema.asInstanceOf[Schema[T]], anns)
      case sch: Schema.Primitive[_]     => Left(SchemaValidationException(s"Invalid schema format $sch"))
      case sch: Schema.Dynamic          => Left(SchemaValidationException(s"Invalid schema format $sch"))
      case sch: Schema.Collection[_, _] => Left(SchemaValidationException(s"Invalid schema format $sch"))
      case sch                          => Left(SchemaValidationException(s"Invalid schema format $sch"))
//      case Schema.Tuple2(left, right, annotations) =>
//        for {
//          l <- extractSchema(left, anns ++ annotations)
//          r <- extractSchema(right, anns ++ annotations)
//          res <- l.merge(r)
//        } yield res
//      case Schema.Either(left, right, annotations) =>
//        for {
//          l <- extractSchema(left, anns ++ annotations)
//          r <- extractSchema(right.asInstanceOf[Schema[T]], anns ++ annotations)
//          res <- l.merge(r)
//        } yield res
    }
//  implicit val jsonDecoder: JsonDecoder[ElasticSearchSchema] = DeriveJsonDecoder.gen[ElasticSearchSchema]
//  implicit val jsonEncoder: JsonEncoder[ElasticSearchSchema] = DeriveJsonEncoder.gen[ElasticSearchSchema]
}
