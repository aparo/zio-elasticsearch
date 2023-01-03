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

package zio.elasticsearch.requests.snapshot

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Returns information about a snapshot.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A repository name
 * @param snapshot A comma-separated list of snapshot names
 * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param verbose Whether to show verbose snapshot info or only show the basic info found in the repository index blob
 */
final case class SnapshotGetRequest(
  repository: String,
  snapshot: Seq[String] = Nil,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  verbose: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl("_snapshot", repository, snapshot)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    ignoreUnavailable.foreach { v =>
      queryArgs += "ignore_unavailable" -> v.toString
    }
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    verbose.foreach { v =>
      queryArgs += "verbose" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object SnapshotGetRequest {
  implicit val jsonDecoder: JsonDecoder[SnapshotGetRequest] = DeriveJsonDecoder.gen[SnapshotGetRequest]
  implicit val jsonEncoder: JsonEncoder[SnapshotGetRequest] = DeriveJsonEncoder.gen[SnapshotGetRequest]
}
