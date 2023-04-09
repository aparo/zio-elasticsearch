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

package zio.elasticsearch.responses.indices

import zio.json._
/*
 * Returns information about ongoing index shard recoveries.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-recovery.html
 *
 * @param activeOnly Display only those recoveries that are currently on-going
 * @param detailed Whether to display detailed information about shard recovery
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 */
final case class IndicesRecoveryResponse(_ok: Option[Boolean] = None)
object IndicesRecoveryResponse {
  implicit val jsonDecoder: JsonDecoder[IndicesRecoveryResponse] = DeriveJsonDecoder.gen[IndicesRecoveryResponse]
  implicit val jsonEncoder: JsonEncoder[IndicesRecoveryResponse] = DeriveJsonEncoder.gen[IndicesRecoveryResponse]
}
