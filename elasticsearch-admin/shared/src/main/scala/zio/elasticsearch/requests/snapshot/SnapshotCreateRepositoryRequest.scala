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
 * Creates a repository.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A repository name
 * @param body body the body of the call
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 * @param verify Whether to verify the repository after creation
 */
final case class SnapshotCreateRepositoryRequest(
  repository: String,
  body: Json.Obj,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None,
  verify: Option[Boolean] = None
) extends ActionRequest {
  def method: Method = Method.PUT
  def urlPath: String = this.makeUrl("_snapshot", repository)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    verify.foreach { v =>
      queryArgs += "verify" -> v.toString
    }
    queryArgs.toMap
  }
}
object SnapshotCreateRepositoryRequest {
  implicit val jsonDecoder: JsonDecoder[SnapshotCreateRepositoryRequest] =
    DeriveJsonDecoder.gen[SnapshotCreateRepositoryRequest]
  implicit val jsonEncoder: JsonEncoder[SnapshotCreateRepositoryRequest] =
    DeriveJsonEncoder.gen[SnapshotCreateRepositoryRequest]
}
