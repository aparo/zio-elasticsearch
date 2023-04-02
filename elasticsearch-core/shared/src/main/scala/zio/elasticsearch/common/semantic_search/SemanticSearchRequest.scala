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

package zio.elasticsearch.common.semantic_search
import scala.collection.mutable
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Semantic search API using dense vector similarity
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/semantic-search.html
 *
 * @param indices A comma-separated list of index names to search; use `_all` to perform the operation on all indices
 * @param body body the body of the call
 * @param routing A comma-separated list of specific routing values
 */

final case class SemanticSearchRequest(
  indices: Seq[String] = Nil,
  body: Json = Json.Null,
  routing: Seq[String] = Nil
) extends ActionRequest[Json] {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_semantic_search")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (routing.nonEmpty) {
      queryArgs += ("routing" -> routing.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
