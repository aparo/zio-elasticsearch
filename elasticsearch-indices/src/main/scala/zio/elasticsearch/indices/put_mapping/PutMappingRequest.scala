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

package zio.elasticsearch.indices.put_mapping
import scala.collection.mutable

import zio.Chunk
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Updates the index mappings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-put-mapping.html
 *
 * @param indices A comma-separated list of index names the mapping should be added to (supports wildcards); use `_all` or omit to add the mapping on all indices.
 * @param body body the body of the call
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 * @param writeIndexOnly When true, applies mappings only to the write index of an alias or data stream
 */

final case class PutMappingRequest(
  indices: Chunk[String] = Chunk.empty,
  body: Json,
  allowNoIndices: Option[Boolean] = None,
  expandWildcards: Seq[ExpandWildcards] = Nil,
  ignoreUnavailable: Option[Boolean] = None,
  masterTimeout: Option[String] = None,
  timeout: Option[String] = None,
  writeIndexOnly: Boolean = false
) extends ActionRequest[Json] {
  def method: Method = Method.PUT

  def urlPath: String = this.makeUrl(indices, "_mapping")

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
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    if (writeIndexOnly != false)
      queryArgs += ("write_index_only" -> writeIndexOnly.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
