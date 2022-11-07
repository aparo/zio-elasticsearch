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

package zio.elasticsearch.requests
import scala.collection.mutable

import zio.json._
import io.circe.derivation.annotations.{ JsonKey, jsonDerive }

/*
 * Changes the number of requests per second for a particular Reindex operation.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
 *
 * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
 * @param taskId The task id to rethrottle
 */
final case class ReindexRethrottleRequest(
  @jsonField("requests_per_second") requestsPerSecond: Int,
  @jsonField("task_id") taskId: String
) extends ActionRequest {
  def method: String = "POST"
  def urlPath: String = this.makeUrl("_reindex", taskId, "_rethrottle")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    queryArgs += "requests_per_second" -> requestsPerSecond.toString
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object ReindexRethrottleRequest {
  implicit val jsonDecoder: JsonDecoder[ReindexRethrottleRequest] = DeriveJsonDecoder.gen[ReindexRethrottleRequest]
  implicit val jsonEncoder: JsonEncoder[ReindexRethrottleRequest] = DeriveJsonEncoder.gen[ReindexRethrottleRequest]
}
