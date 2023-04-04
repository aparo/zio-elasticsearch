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
 * Updates the index settings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-update-settings.html
 *
 * @param body body the body of the call
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param flatSettings Return settings in flat format (default: false)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param masterTimeout Specify timeout for connection to master
 * @param preserveExisting Whether to update existing settings. If set to `true` existing settings on an index remain unchanged, the default is `false`
 * @param timeout Explicit operation timeout
 */
final case class IndicesPutSettingsRequest(
  body: Json.Obj,
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @jsonField("flat_settings") flatSettings: Option[Boolean] = None,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  indices: Seq[String] = Nil,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  @jsonField("preserve_existing") preserveExisting: Option[Boolean] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "PUT"
  def urlPath: String = this.makeUrl(indices, "_settings")
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
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    preserveExisting.foreach { v =>
      queryArgs += "preserve_existing" -> v.toString
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    queryArgs.toMap
  }
}
object IndicesPutSettingsRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesPutSettingsRequest] = DeriveJsonDecoder.gen[IndicesPutSettingsRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesPutSettingsRequest] = DeriveJsonEncoder.gen[IndicesPutSettingsRequest]
}
