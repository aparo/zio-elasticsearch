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
import zio.json.ast._
import zio.json._

/*
 * Returns information about whether a particular index exists.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-exists.html
 *
 * @param indices A comma-separated list of index names
 * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
 * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
 * @param flatSettings Return settings in flat format (default: false)
 * @param ignoreUnavailable Ignore unavailable indexes (default: false)
 * @param includeDefaults Whether to return all default setting for each of the indices.
 * @param local Return local information, do not retrieve the state from master node (default: false)
 */
final case class IndicesExistsRequest(
  indices: Seq[String] = Nil,
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @jsonField("flat_settings") flatSettings: Option[Boolean] = None,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @jsonField("include_defaults") includeDefaults: Boolean = false,
  local: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "HEAD"
  def urlPath: String = this.makeUrl(indices)
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
    flatSettings.foreach { v =>
      queryArgs += "flat_settings" -> v.toString
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += "ignore_unavailable" -> v.toString
    }
    if (includeDefaults != false) queryArgs += "include_defaults" -> includeDefaults.toString
    local.foreach { v =>
      queryArgs += "local" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object IndicesExistsRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesExistsRequest] = DeriveJsonDecoder.gen[IndicesExistsRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesExistsRequest] = DeriveJsonEncoder.gen[IndicesExistsRequest]
}
