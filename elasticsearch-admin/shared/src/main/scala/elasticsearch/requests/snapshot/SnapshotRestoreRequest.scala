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

package elasticsearch.requests.snapshot

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable

import elasticsearch.requests.ActionRequest

/*
 * Restores a snapshot.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A repository name
 * @param snapshot A snapshot name
 * @param body body the body of the call
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param waitForCompletion Should this request wait until the operation has completed before returning
 */
@JsonCodec
final case class SnapshotRestoreRequest(
    repository: String,
    snapshot: String,
    body: Option[JsonObject] = None,
    @JsonKey("master_timeout") masterTimeout: Option[String] = None,
    @JsonKey("wait_for_completion") waitForCompletion: Boolean = false
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String =
    this.makeUrl("_snapshot", repository, snapshot, "_restore")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    body.foreach { v =>
      queryArgs += ("body" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    if (waitForCompletion != false)
      queryArgs += ("wait_for_completion" -> waitForCompletion.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
