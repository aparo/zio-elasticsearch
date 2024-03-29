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

package zio.elasticsearch.requests

import scala.collection.mutable

import zio.elasticsearch.common.search.SearchType
import zio.json._

/*
 * Allows to execute several search operations in one request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-multi-search.html
 *
 * @param body body the body of the call
 * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
 * @param indices A comma-separated list of index names to use as default
 * @param maxConcurrentSearches Controls the maximum number of concurrent searches the multi search api will execute
 * @param maxConcurrentShardRequests The number of concurrent shard requests each sub search executes concurrently per node. This value should be used to limit the impact of the search on the cluster in order to limit the number of concurrent shard requests
 * @param preFilterShardSize A threshold that enforces a pre-filter roundtrip to prefilter search shards based on query rewriting if the number of shards the search request expands to exceeds the threshold. This filter roundtrip can limit the number of shards significantly if for instance a shard can not match any documents based on it's rewrite method ie. if date filters are mandatory to match but the shard bounds and the query are disjoint.
 * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
 * @param searchType Search operation type
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 */
final case class MultiSearchRequest(
  body: Seq[String] = Nil,
  @jsonField("ccs_minimize_roundtrips") ccsMinimizeRoundtrips: Boolean = true,
  indices: Seq[String] = Nil,
  @jsonField("max_concurrent_searches") maxConcurrentSearches: Option[Double] = None,
  @jsonField("max_concurrent_shard_requests") maxConcurrentShardRequests: Double = 5,
  @jsonField("pre_filter_shard_size") preFilterShardSize: Double = 128,
  @jsonField("rest_total_hits_as_int") restTotalHitsAsInt: Boolean = false,
  @jsonField("search_type") searchType: Option[SearchType] = None,
  @jsonField("typed_keys") typedKeys: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl(indices, "_msearch")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    if (ccsMinimizeRoundtrips != true) queryArgs += "ccs_minimize_roundtrips" -> ccsMinimizeRoundtrips.toString
    maxConcurrentSearches.foreach { v =>
      queryArgs += "max_concurrent_searches" -> v.toString
    }
    if (maxConcurrentShardRequests != 5)
      queryArgs += "max_concurrent_shard_requests" -> maxConcurrentShardRequests.toString
    if (preFilterShardSize != 128) queryArgs += "pre_filter_shard_size" -> preFilterShardSize.toString
    if (restTotalHitsAsInt != false) queryArgs += "rest_total_hits_as_int" -> restTotalHitsAsInt.toString
    searchType.foreach { v =>
      queryArgs += "search_type" -> v.toString
    }
    typedKeys.foreach { v =>
      queryArgs += "typed_keys" -> v.toString
    }
    queryArgs.toMap
  }
}
object MultiSearchRequest {
  implicit val jsonDecoder: JsonDecoder[MultiSearchRequest] = DeriveJsonDecoder.gen[MultiSearchRequest]
  implicit val jsonEncoder: JsonEncoder[MultiSearchRequest] = DeriveJsonEncoder.gen[MultiSearchRequest]
}
