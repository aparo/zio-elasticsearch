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

package zio.elasticsearch.common.requests
import zio._
import zio.elasticsearch.common.{ FieldAndFormat, SourceConfig }
import zio.elasticsearch.queries.Query
import zio.json._
import zio.json.ast._

final case class KnnSearchRequestBody(
  @jsonField("_source") source: Option[SourceConfig] = None,
  @jsonField("docvalue_fields") docvalueFields: Option[
    Chunk[FieldAndFormat]
  ] = None,
  @jsonField("stored_fields") storedFields: Option[Chunk[String]] = None,
  fields: Option[Chunk[String]] = None,
  filter: Option[Chunk[Query]] = None,
  knn: Query
)

object KnnSearchRequestBody {
  implicit lazy val jsonCodec: JsonCodec[KnnSearchRequestBody] =
    DeriveJsonCodec.gen[KnnSearchRequestBody]
}
