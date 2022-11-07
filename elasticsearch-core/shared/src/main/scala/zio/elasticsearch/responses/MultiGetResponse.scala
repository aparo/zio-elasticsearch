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

package zio.elasticsearch.responses

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-get.html
 *
 * @param body body the body of the call
 * @param index The name of the index
 * @param docType The type of the document
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param refresh Refresh the shard containing the document before performing the operation
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param realtime Specify whether to perform the operation in realtime or search mode
 * @param routing Specific routing value
 * @param storedFields A comma-separated list of stored fields to return in the response
 */
final case class MultiGetResponse(docs: List[GetResponse])
object MultiGetResponse {
  implicit val jsonDecoder: JsonDecoder[MultiGetResponse] = DeriveJsonDecoder.gen[MultiGetResponse]
  implicit val jsonEncoder: JsonEncoder[MultiGetResponse] = DeriveJsonEncoder.gen[MultiGetResponse]
}
