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

package zio.elasticsearch.requests.snapshot

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Removes stale data from repository.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A repository name
 * @param body body the body of the call
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 */
final case class SnapshotCleanupRepositoryRequest(
  repository: String,
  body: Option[Json.Obj] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: Method = Method.POST
  def urlPath: String = this.makeUrl("_snapshot", repository, "_cleanup")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    body.foreach { v =>
      queryArgs += "body" -> v.toString
    }
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    queryArgs.toMap
  }
}
object SnapshotCleanupRepositoryRequest {
  implicit val jsonDecoder: JsonDecoder[SnapshotCleanupRepositoryRequest] =
    DeriveJsonDecoder.gen[SnapshotCleanupRepositoryRequest]
  implicit val jsonEncoder: JsonEncoder[SnapshotCleanupRepositoryRequest] =
    DeriveJsonEncoder.gen[SnapshotCleanupRepositoryRequest]
}
