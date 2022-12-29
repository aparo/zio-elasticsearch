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

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Creates an index with optional settings and mappings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-create-index.html
 *
 * @param index The name of the index
 * @param body body the body of the call
 * @param includeTypeName Whether a type should be expected in the body of the mappings.
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Set the number of active shards to wait for before the operation returns.
 */
@JsonCodec
final case class IndicesCreateRequest(
  index: String,
  body: Json.Obj = Json.Obj(),
  @jsonField("include_type_name") includeTypeName: Option[Boolean] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None,
  @jsonField("wait_for_active_shards") waitForActiveShards: Option[Int] = None
) extends ActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl(index)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    includeTypeName.foreach { v =>
      queryArgs += ("include_type_name" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    waitForActiveShards.foreach { v =>
      queryArgs += ("wait_for_active_shards" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
