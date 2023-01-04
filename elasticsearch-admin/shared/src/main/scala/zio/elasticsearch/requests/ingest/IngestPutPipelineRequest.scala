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

package zio.elasticsearch.requests.ingest

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Creates or updates a pipeline.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/put-pipeline-api.html
 *
 * @param id Pipeline ID
 * @param body body the body of the call
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 */
final case class IngestPutPipelineRequest(
  id: String,
  body: Json.Obj,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "PUT"
  def urlPath: String = this.makeUrl("_ingest", "pipeline", id)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    queryArgs.toMap
  }
}
object IngestPutPipelineRequest {
  implicit val jsonDecoder: JsonDecoder[IngestPutPipelineRequest] = DeriveJsonDecoder.gen[IngestPutPipelineRequest]
  implicit val jsonEncoder: JsonEncoder[IngestPutPipelineRequest] = DeriveJsonEncoder.gen[IngestPutPipelineRequest]
}
