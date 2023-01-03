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

package zio.elasticsearch.responses.ingest

import zio.json._
/*
 * Allows to simulate a pipeline with example documents.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/simulate-pipeline-api.html
 *
 * @param body body the body of the call
 * @param id Pipeline ID
 * @param verbose Verbose mode. Display data output for each processor in executed pipeline
 */
final case class IngestSimulateResponse(_ok: Option[Boolean] = None)
object IngestSimulateResponse {
  implicit val jsonDecoder: JsonDecoder[IngestSimulateResponse] = DeriveJsonDecoder.gen[IngestSimulateResponse]
  implicit val jsonEncoder: JsonEncoder[IngestSimulateResponse] = DeriveJsonEncoder.gen[IngestSimulateResponse]
}
