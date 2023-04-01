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

package zio.elasticsearch.requests.indices

import zio.elasticsearch.common.ExpandWildcards
import scala.collection.mutable
import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Returns mapping for one or more fields.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-field-mapping.html
 *
 * @param fields A comma-separated list of fields
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param includeDefaults Whether the default mapping values should be returned as well
 * @param includeTypeName Whether a type should be returned in the body of the mappings.
 * @param indices A comma-separated list of index names
 * @param local Return local information, do not retrieve the state from master node (default: false)
 */
final case class IndicesGetFieldMappingRequest(
  fields: Seq[String] = Nil,
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @jsonField("include_defaults") includeDefaults: Option[Boolean] = None,
  @jsonField("include_type_name") includeTypeName: Option[Boolean] = None,
  indices: Seq[String] = Nil,
  local: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl(indices, "_mapping", "field", fields)
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
    includeDefaults.foreach { v =>
      queryArgs += "include_defaults" -> v.toString
    }
    includeTypeName.foreach { v =>
      queryArgs += "include_type_name" -> v.toString
    }
    local.foreach { v =>
      queryArgs += "local" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object IndicesGetFieldMappingRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesGetFieldMappingRequest] =
    DeriveJsonDecoder.gen[IndicesGetFieldMappingRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesGetFieldMappingRequest] =
    DeriveJsonEncoder.gen[IndicesGetFieldMappingRequest]
}
