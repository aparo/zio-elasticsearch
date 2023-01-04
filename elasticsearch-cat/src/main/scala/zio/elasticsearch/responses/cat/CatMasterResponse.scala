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

package zio.elasticsearch.responses.cat

import zio.json._
/*
 * Returns information about the master node.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-master.html
 *
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param v Verbose mode. Display column headers
 */
final case class CatMasterResponse(_ok: Option[Boolean] = None)
object CatMasterResponse {
  implicit val jsonDecoder: JsonDecoder[CatMasterResponse] = DeriveJsonDecoder.gen[CatMasterResponse]
  implicit val jsonEncoder: JsonEncoder[CatMasterResponse] = DeriveJsonEncoder.gen[CatMasterResponse]
}
