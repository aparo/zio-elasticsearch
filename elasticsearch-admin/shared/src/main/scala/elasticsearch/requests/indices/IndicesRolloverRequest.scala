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
 * Updates an alias to point to a new index when the existing index
is considered to be too large or too old.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
 *
 * @param alias The name of the alias to rollover
 * @param body body the body of the call
 * @param dryRun If set to true the rollover action will only be validated but not actually performed even if a condition matches. The default is false
 * @param includeTypeName Whether a type should be included in the body of the mappings.
 * @param masterTimeout Specify timeout for connection to master
 * @param newIndex The name of the rollover index
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Set the number of active shards to wait for on the newly created rollover index before the operation returns.
 */
@JsonCodec
final case class IndicesRolloverRequest(
  alias: String,
  body: Option[JsonObject] = None,
  @JsonKey("dry_run") dryRun: Option[Boolean] = None,
  @JsonKey("include_type_name") includeTypeName: Option[Boolean] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  @JsonKey("new_index") newIndex: Option[String] = None,
  timeout: Option[String] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(alias, "_rollover", newIndex)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    body.foreach { v =>
      queryArgs += ("body" -> v.toString)
    }
    dryRun.foreach { v =>
      queryArgs += ("dry_run" -> v.toString)
    }
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
      queryArgs += ("wait_for_active_shards" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
