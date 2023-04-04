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
 * The _upgrade API is no longer useful and will be removed.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-upgrade.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param onlyAncientSegments If true, only ancient (an older Lucene major release) segments will be upgraded
 * @param waitForCompletion Specify whether the request should block until the all segments are upgraded (default: false)
 */
final case class IndicesUpgradeRequest(
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  indices: Seq[String] = Nil,
  @jsonField("only_ancient_segments") onlyAncientSegments: Option[Boolean] = None,
  @jsonField("wait_for_completion") waitForCompletion: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "POST"
  def urlPath: String = this.makeUrl(indices, "_upgrade")
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
    onlyAncientSegments.foreach { v =>
      queryArgs += "only_ancient_segments" -> v.toString
    }
    waitForCompletion.foreach { v =>
      queryArgs += "wait_for_completion" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object IndicesUpgradeRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesUpgradeRequest] = DeriveJsonDecoder.gen[IndicesUpgradeRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesUpgradeRequest] = DeriveJsonEncoder.gen[IndicesUpgradeRequest]
}
