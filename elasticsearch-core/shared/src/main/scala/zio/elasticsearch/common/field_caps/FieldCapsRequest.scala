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

package zio.elasticsearch.common.field_caps
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.common.requests.FieldCapsRequestBody
/*
 * Returns the information about the capabilities of fields among multiple indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-field-caps.html
 *
 * @param index Comma-separated list of data streams, indices, and aliases used to limit the request. Supports wildcards (*). To target all data streams and indices, omit this parameter or use * or _all.

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

 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param body body the body of the call
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param fields A comma-separated list of field names
 * @param filters An optional set of filters: can include +metadata,-metadata,-nested,-multifield,-parent
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param includeUnmapped Indicates whether unmapped fields should be included in the response.
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param types Only return results for fields that have one of the types in the list
 */

final case class FieldCapsRequest(
  indices: Seq[String] = Nil,
  body: FieldCapsRequestBody = FieldCapsRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  allowNoIndices: Option[Boolean] = None,
  expandWildcards: Seq[ExpandWildcards] = Nil,
  fields: Seq[String] = Nil,
  filters: Seq[String] = Nil,
  ignoreUnavailable: Option[Boolean] = None,
  includeUnmapped: Boolean = false,
  types: Seq[String] = Nil
) extends ActionRequest[FieldCapsRequestBody]
    with RequestBase {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_field_caps")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoIndices.foreach { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    if (fields.nonEmpty) {
      queryArgs += ("fields" -> fields.toList.mkString(","))
    }
    if (filters.nonEmpty) {
      queryArgs += ("filters" -> filters.toList.mkString(","))
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    if (includeUnmapped != false)
      queryArgs += ("include_unmapped" -> includeUnmapped.toString)
    if (types.nonEmpty) {
      queryArgs += ("types" -> types.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
