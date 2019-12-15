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

package elasticsearch.requests.indices

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable

import elasticsearch.requests.ActionRequest

/*
 * Deletes an alias.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
 *
 * @param indices A comma-separated list of index names (supports wildcards); use `_all` for all indices
 * @param name A comma-separated list of aliases to delete (supports wildcards); use `_all` to delete all aliases for the specified indices.
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit timestamp for the document
 */
@JsonCodec
final case class IndicesDeleteAliasRequest(
  indices: Seq[String] = Nil,
  name: Seq[String] = Nil,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "DELETE"

  def urlPath: String = this.makeUrl(indices, "_aliases", name)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
