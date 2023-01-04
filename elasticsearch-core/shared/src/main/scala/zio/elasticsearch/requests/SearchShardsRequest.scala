/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.requests

import scala.collection.mutable

import zio.elasticsearch.ExpandWildcards
import zio.json._
import zio.json.ast._

/*
 * Returns information about the indices and shards that a search request would be executed against.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-shards.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param routing Specific routing value
 */
final case class SearchShardsRequest(
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  indices: Seq[String] = Nil,
  local: Option[Boolean] = None,
  preference: Option[String] = None,
  routing: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl(indices, "_search_shards")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoIndices.foreach { v =>
      queryArgs += "allow_no_indices" -> v.toString
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += "expand_wildcards" -> expandWildcards.mkString(",")
      }
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += "ignore_unavailable" -> v.toString
    }
    local.foreach { v =>
      queryArgs += "local" -> v.toString
    }
    preference.foreach { v =>
      queryArgs += "preference" -> v
    }
    routing.foreach { v =>
      queryArgs += "routing" -> v
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object SearchShardsRequest {
  implicit val jsonDecoder: JsonDecoder[SearchShardsRequest] = DeriveJsonDecoder.gen[SearchShardsRequest]
  implicit val jsonEncoder: JsonEncoder[SearchShardsRequest] = DeriveJsonEncoder.gen[SearchShardsRequest]
}
