/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

package zio.elasticsearch.orm

import java.time.{ LocalDate, LocalDateTime, OffsetDateTime }

import fixures.models.Person
import zio.elasticsearch.geo.GeoPoint
import zio.json._
import zio.schema.elasticsearch.annotations.{ IndexName, Ip, Keyword, Nested }
import zio.schema.{ DeriveSchema, Schema }

final case class ORMClassTest(id: String, name: String, age: Int, @Keyword nick: Option[String] = None)
object ORMClassTest extends ElasticSearchMeta[ORMClassTest] {
  implicit final val decoder: JsonDecoder[ORMClassTest] =
    DeriveJsonDecoder.gen[ORMClassTest]
  implicit final val encoder: JsonEncoder[ORMClassTest] =
    DeriveJsonEncoder.gen[ORMClassTest]
  implicit final val codec: JsonCodec[ORMClassTest] = JsonCodec(encoder, decoder)
  implicit val schema: Schema[ORMClassTest] = DeriveSchema.gen[ORMClassTest]

  val empty: ORMClassTest = ORMClassTest("", "", 0)
}

@IndexName("allmappingtest")
final case class ORMAllMappingTest(
  name: String = "",
  @Keyword nick: Option[String] = None,
  i: Int = 1,
  l: Long = 1L,
  d: Double = 1.0,
  f: Float = 1.0f,
  ld: LocalDate,
  ldt: LocalDateTime,
  odt: OffsetDateTime,
  @Ip ip: Option[String] = None,
  b: Boolean,
  @Nested nested: ORMClassTest = ORMClassTest.empty,
  obj: ORMClassTest = ORMClassTest.empty,
  point: GeoPoint = GeoPoint(0, 0)
)

object ORMAllMappingTest extends ElasticSearchMeta[ORMAllMappingTest] {
  implicit final val decoder: JsonDecoder[ORMAllMappingTest] =
    DeriveJsonDecoder.gen[ORMAllMappingTest]
  implicit final val encoder: JsonEncoder[ORMAllMappingTest] =
    DeriveJsonEncoder.gen[ORMAllMappingTest]
  implicit final val codec: JsonCodec[ORMAllMappingTest] = JsonCodec(encoder, decoder)
  implicit val schema: Schema[ORMAllMappingTest] = DeriveSchema.gen[ORMAllMappingTest]

}
