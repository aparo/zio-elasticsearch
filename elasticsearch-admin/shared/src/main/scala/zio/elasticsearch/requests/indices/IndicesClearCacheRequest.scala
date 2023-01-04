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

import scala.collection.mutable

import zio.elasticsearch.ExpandWildcards
import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Clears all or specific caches for one or more indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clearcache.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param fielddata Clear field data
 * @param fields A comma-separated list of fields to clear when using the `fielddata` parameter (default: all)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param index A comma-separated list of index name to limit the operation
 * @param indices A comma-separated list of index name to limit the operation
 * @param query Clear query caches
 * @param request Clear request cache
 */
final case class IndicesClearCacheRequest(
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  fielddata: Option[Boolean] = None,
  fields: Seq[String] = Nil,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  index: Seq[String] = Nil,
  indices: Seq[String] = Nil,
  query: Option[Boolean] = None,
  request: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "POST"
  def urlPath: String = this.makeUrl(indices, "_cache", "clear")
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
    fielddata.foreach { v =>
      queryArgs += "fielddata" -> v.toString
    }
    if (fields.nonEmpty) {
      queryArgs += "fields" -> fields.toList.mkString(",")
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += "ignore_unavailable" -> v.toString
    }
    if (index.nonEmpty) {
      queryArgs += "index" -> index.toList.mkString(",")
    }
    query.foreach { v =>
      queryArgs += "query" -> v.toString
    }
    request.foreach { v =>
      queryArgs += "request" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object IndicesClearCacheRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesClearCacheRequest] = DeriveJsonDecoder.gen[IndicesClearCacheRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesClearCacheRequest] = DeriveJsonEncoder.gen[IndicesClearCacheRequest]
}
