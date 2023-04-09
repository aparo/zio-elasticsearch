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

package zio.elasticsearch.eql.requests
import zio._
import zio.elasticsearch.common.{ FieldAndFormat, RuntimeFields }
import zio.elasticsearch.eql.ResultPosition
import zio.elasticsearch.queries.Query
import zio.json._
import zio.json.ast._

final case class SearchRequestBody(
  query: String,
  @jsonField("case_sensitive") caseSensitive: Option[Boolean] = None,
  @jsonField("event_category_field") eventCategoryField: Option[String] = None,
  @jsonField("tiebreaker_field") tiebreakerField: Option[String] = None,
  @jsonField("timestamp_field") timestampField: Option[String] = None,
  @jsonField("fetch_size") fetchSize: Option[Long] = None,
  filter: Option[Chunk[Query]] = None,
  @jsonField("keep_alive") keepAlive: Option[String] = None,
  @jsonField("keep_on_completion") keepOnCompletion: Option[Boolean] = None,
  @jsonField("wait_for_completion_timeout") waitForCompletionTimeout: Option[
    String
  ] = None,
  size: Option[Long] = None,
  fields: Option[Chunk[FieldAndFormat]] = None,
  @jsonField("result_position") resultPosition: Option[ResultPosition] = None,
  @jsonField("runtime_mappings") runtimeMappings: Option[RuntimeFields] = None
)

object SearchRequestBody {
  implicit val jsonCodec: JsonCodec[SearchRequestBody] =
    DeriveJsonCodec.gen[SearchRequestBody]
}
