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

package zio.elasticsearch.snapshot.get
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Returns information about a snapshot.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A repository name
 * @param snapshot A comma-separated list of snapshot names
 * @param local

 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param human When set to `true` will return statistics in a format suitable for humans.
 * For example `"exists_time": "1h"` for humans and
 * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
 * readable values will be omitted. This makes sense for responses being consumed
 * only by machines.
 * @server_default false

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param after Offset identifier to start pagination from as returned by the 'next' field in the response body.
 * @param fromSortValue Value of the current sort column at which to start retrieval.
 * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
 * @param includeRepository Whether to include the repository name in the snapshot info. Defaults to true.
 * @param indexDetails Whether to include details of each index in the snapshot, if those details are available. Defaults to false.
 * @param indexNames Whether to include the name of each index in the snapshot. Defaults to true.
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param offset Numeric offset to start pagination based on the snapshots matching the request. Defaults to 0
 * @param order Sort order
 * @param size Maximum number of snapshots to return. Defaults to 0 which means return all that match without limit.
 * @param slmPolicyFilter Filter snapshots by a comma-separated list of SLM policy names that snapshots belong to. Accepts wildcards. Use the special pattern '_none' to match snapshots without an SLM policy
 * @param sort Allows setting a sort order for the result. Defaults to start_time
 * @param verbose Whether to show verbose snapshot info or only show the basic info found in the repository index blob
 */

final case class GetRequest(
  repository: String,
  snapshot: Chunk[String] = Chunk.empty,
  local: Boolean,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  after: Option[String] = None,
  fromSortValue: Option[String] = None,
  ignoreUnavailable: Option[Boolean] = None,
  includeRepository: Option[Boolean] = None,
  indexDetails: Option[Boolean] = None,
  indexNames: Option[Boolean] = None,
  masterTimeout: Option[String] = None,
  offset: Option[Int] = None,
//  order: Order = Order.asc,
  size: Option[Int] = None,
  slmPolicyFilter: Chunk[String] = Chunk.empty,
//  sort: Sort = Sort.start_time,
  verbose: Option[Boolean] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl("_snapshot", repository, snapshot)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    after.foreach { v =>
      queryArgs += ("after" -> v)
    }
    fromSortValue.foreach { v =>
      queryArgs += ("from_sort_value" -> v)
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    includeRepository.foreach { v =>
      queryArgs += ("include_repository" -> v.toString)
    }
    indexDetails.foreach { v =>
      queryArgs += ("index_details" -> v.toString)
    }
    indexNames.foreach { v =>
      queryArgs += ("index_names" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    offset.foreach { v =>
      queryArgs += ("offset" -> v.toString)
    }
//    if (order != Order.asc)
//      queryArgs += ("order" -> order.toString)
    size.foreach { v =>
      queryArgs += ("size" -> v.toString)
    }
    if (slmPolicyFilter.nonEmpty) {
      queryArgs += ("slm_policy_filter" -> slmPolicyFilter.toList.mkString(","))
    }
//    if (sort != Sort.start_time)
//      queryArgs += ("sort" -> sort.toString)
    verbose.foreach { v =>
      queryArgs += ("verbose" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
