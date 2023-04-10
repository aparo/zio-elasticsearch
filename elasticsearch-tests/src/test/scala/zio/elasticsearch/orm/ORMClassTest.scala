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

import java.time.{ LocalDate, LocalDateTime, OffsetDateTime }

import zio.elasticsearch.geo.GeoPoint
import zio.json._
import zio.schema.elasticsearch.annotations.{ IndexName, Ip, Keyword, Nested }
import zio.schema.{ DeriveSchema, Schema }

final case class ORMClassTest(id: String, name: String, age: Int, @Keyword nick: Option[String] = None)
object ORMClassTest {
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

object ORMAllMappingTest {
  implicit final val decoder: JsonDecoder[ORMAllMappingTest] =
    DeriveJsonDecoder.gen[ORMAllMappingTest]
  implicit final val encoder: JsonEncoder[ORMAllMappingTest] =
    DeriveJsonEncoder.gen[ORMAllMappingTest]
  implicit final val codec: JsonCodec[ORMAllMappingTest] = JsonCodec(encoder, decoder)
  implicit val schema: Schema[ORMAllMappingTest] = DeriveSchema.gen[ORMAllMappingTest]

}
