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

package zio.elasticsearch.sql.requests
import zio.elasticsearch.common.RuntimeFields
import zio.elasticsearch.queries.Query
import zio.json._
import zio.json.ast._

final case class QueryRequestBody(
  catalog: Option[String] = None,
  columnar: Option[Boolean] = None,
  cursor: Option[String] = None,
  @jsonField("fetch_size") fetchSize: Option[Int] = None,
  filter: Option[Query] = None,
  query: Option[String] = None,
  @jsonField("request_timeout") requestTimeout: Option[String] = None,
  @jsonField("page_timeout") pageTimeout: Option[String] = None,
  @jsonField("time_zone") timeZone: Option[Json] = None,
  @jsonField("field_multi_value_leniency") fieldMultiValueLeniency: Option[
    Boolean
  ] = None,
  @jsonField("runtime_mappings") runtimeMappings: Option[RuntimeFields] = None,
  @jsonField("wait_for_completion_timeout") waitForCompletionTimeout: Option[
    String
  ] = None,
  params: Option[Map[String, Json]] = None,
  @jsonField("keep_alive") keepAlive: Option[String] = None,
  @jsonField("keep_on_completion") keepOnCompletion: Option[Boolean] = None,
  @jsonField("index_using_frozen") indexUsingFrozen: Option[Boolean] = None
)

object QueryRequestBody {
  implicit lazy val jsonCodec: JsonCodec[QueryRequestBody] =
    DeriveJsonCodec.gen[QueryRequestBody]
}
