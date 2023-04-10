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

package zio.elasticsearch.requests.indices

import zio.elasticsearch.common.ExpandWildcards
import scala.collection.mutable
import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Provides store information for shard copies of indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shards-stores.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param status A comma-separated list of statuses used to filter on shards to get store information for
 */
final case class IndicesShardStoresRequest(
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  indices: Chunk[String] = Chunk.empty,
  status: Chunk[String] = Chunk.empty
) extends ActionRequest {
  def method: Method = Method.GET
  def urlPath: String = this.makeUrl(indices, "_shard_stores")
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
    if (status.nonEmpty) {
      queryArgs += "status" -> status.toList.mkString(",")
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object IndicesShardStoresRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesShardStoresRequest] = DeriveJsonDecoder.gen[IndicesShardStoresRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesShardStoresRequest] = DeriveJsonEncoder.gen[IndicesShardStoresRequest]
}
