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
 * Allows to simulate a pipeline with example documents.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/simulate-pipeline-api.html
 *
 * @param body body the body of the call
 * @param id Pipeline ID
 * @param verbose Verbose mode. Display data output for each processor in executed pipeline
 */
@JsonCodec
final case class IngestSimulateRequest(
  body: Json.Obj,
  id: Option[String] = None,
  verbose: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_ingest", "pipeline", id, "_simulate")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (verbose != false) queryArgs += ("verbose" -> verbose.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
