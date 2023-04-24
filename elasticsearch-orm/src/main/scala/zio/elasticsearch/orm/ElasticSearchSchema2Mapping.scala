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

package zio.elasticsearch.orm

import zio.Chunk
import zio.elasticsearch.common.mappings.FieldType
import zio.elasticsearch.indices.put_index_template.IndexTemplateMapping
import zio.elasticsearch.indices.requests.PutIndexTemplateRequestBody
import zio.elasticsearch.mappings._
import zio.exception.InvalidValueException
import zio.schema.elasticsearch._
object ElasticSearchSchema2Mapping {

  def toRootMapping[T](schema: ElasticSearchSchema[T]): RootDocumentMapping =
    RootDocumentMapping(properties = schema.fields.map(f => f.name.toLowerCase() -> getMapping(f)).toMap)

  def toIndexTemplate[T](schema: ElasticSearchSchema[T]): PutIndexTemplateRequestBody = {
    val documentMapping =
      RootDocumentMapping(properties = schema.fields.map(f => f.name.toLowerCase() -> getMapping(f)).toMap)

    PutIndexTemplateRequestBody(
      indexPatterns = Some(Chunk(schema.indexName + "*")),
      composedOf = None,
      template = IndexTemplateMapping(mappings = Some(toRootMapping(schema)))
    )
  }

  def getMapping(field: SchemaField): Mapping =
    field match {
      case f2: TypedSchemaField[_] =>
        f2 match {
          case s: StringSchemaField =>
            s.subType match {
              case Some(subType) =>
                subType match {
                  case StringSubType.UUID     => KeywordMapping()
                  case StringSubType.Time     => KeywordMapping()
                  case StringSubType.Text     => TextMapping()
                  case StringSubType.Email    => KeywordMapping()
                  case StringSubType.IP       => IpMapping()
                  case StringSubType.Password => KeywordMapping()
                  case StringSubType.UserId   => KeywordMapping()
                  case StringSubType.Vertex   => KeywordMapping()
                  case StringSubType.Crypted  => KeywordMapping()
                  case StringSubType.Binary   => BinaryMapping()
                }
              case None => KeywordMapping()
            }

          case _: BinarySchemaField         => BinaryMapping()
          case _: GeoPointSchemaField       => GeoPointMapping()
          case _: OffsetDateTimeSchemaField => DateMapping()
          case _: LocalDateTimeSchemaField  => DateMapping()
          case _: LocalDateSchemaField      => DateMapping()
          case _: DoubleSchemaField         => DoubleMapping()
          case _: BigIntSchemaField         => LongMapping()
          case _: BigDecimalSchemaField     => DoubleMapping()
          case _: IntSchemaField            => IntegerMapping()
          case _: BooleanSchemaField        => BooleanMapping()
          case _: LongSchemaField           => LongMapping()
          case _: ShortSchemaField          => ShortMapping()
          case _: FloatSchemaField          => FloatMapping()
          case _: ByteSchemaField           => ByteMapping()
          case _: RefSchemaField            => KeywordMapping()
        }
      case f: ListSchemaField => getMapping(f.items)
      case f: RangeSchemaField =>
        val m = getMapping(f.items)
        m.`type` match {
          case FieldType.ip            => IpRangeMapping()
          case FieldType.date          => DateRangeMapping()
          case FieldType.integer       => IntegerRangeMapping()
          case FieldType.long          => LongRangeMapping()
          case FieldType.float         => FloatRangeMapping()
          case FieldType.double        => DateRangeMapping()
          case FieldType.integer_range => IntegerRangeMapping()
          case FieldType.float_range   => FloatRangeMapping()
          case FieldType.long_range    => LongRangeMapping()
          case FieldType.double_range  => DoubleRangeMapping()
          case FieldType.date_range    => DateRangeMapping()
          case FieldType.ip_range      => IpRangeMapping()
          case v                       => throw InvalidValueException("$v is not a valid range type")
        }
      case f: ObjectSchemaField =>
        ObjectMapping(properties = f.fields.map(e => e.name.toLowerCase() -> getMapping(e)).toMap)
    }

}
