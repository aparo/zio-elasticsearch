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

package elasticsearch.orm

import java.time.{LocalDate, LocalDateTime, OffsetDateTime}

import elasticsearch.geo.GeoPoint
import io.circe.derivation.annotations.JsonCodec
import zio.schema.SchemaDocumentCodec
import zio.schema.annotations._

@ElasticSearchStorage
@SchemaDocumentCodec
@JsonCodec
final case class ORMClassTest(id: String,
                              name: String,
                              age: Int,
                              @Keyword nick: Option[String] = None)

object ORMClassTest {
  val empty: ORMClassTest = ORMClassTest("", "", 0)
}

@IndexName("allmappingtest")
@ElasticSearchStorage
@SchemaDocumentCodec
@JsonCodec
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

object ORMAllMappingTest
