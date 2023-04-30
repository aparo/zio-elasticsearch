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

package zio.elasticsearch.ml.close_job
import zio.json._
/*
 * Closes one or more anomaly detection jobs. A job can be opened and closed multiple times throughout its lifecycle.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-close-job.html
 *
 * @param closed

 */
final case class CloseJobResponse(closed: Boolean = true) {}
object CloseJobResponse {
  implicit lazy val jsonCodec: JsonCodec[CloseJobResponse] =
    DeriveJsonCodec.gen[CloseJobResponse]
}
