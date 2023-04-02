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

package zio.elasticsearch.ml.start_datafeed
import zio.elasticsearch.common.NodeIds
import zio.json._
import zio.json.ast._
/*
 * Starts one or more datafeeds.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-start-datafeed.html
 *
 * @param node The ID of the node that the datafeed was started on. If the datafeed is allowed to open lazily and has not yet
 * been assigned to a node, this value is an empty string.

 * @param started For a successful response, this value is always `true`. On failure, an exception is returned instead.

 */
final case class StartDatafeedResponse(
  node: NodeIds,
  started: Boolean = true
) {}
object StartDatafeedResponse {
  implicit val jsonCodec: JsonCodec[StartDatafeedResponse] =
    DeriveJsonCodec.gen[StartDatafeedResponse]
}
