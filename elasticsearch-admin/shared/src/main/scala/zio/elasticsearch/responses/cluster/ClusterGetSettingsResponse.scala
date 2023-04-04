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

package zio.elasticsearch.responses.cluster

import zio.json._
/*
 * Returns cluster settings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-update-settings.html
 *
 * @param flatSettings Return settings in flat format (default: false)
 * @param includeDefaults Whether to return all default clusters setting.
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 */
final case class ClusterGetSettingsResponse(_ok: Option[Boolean] = None)
object ClusterGetSettingsResponse {
  implicit val jsonDecoder: JsonDecoder[ClusterGetSettingsResponse] = DeriveJsonDecoder.gen[ClusterGetSettingsResponse]
  implicit val jsonEncoder: JsonEncoder[ClusterGetSettingsResponse] = DeriveJsonEncoder.gen[ClusterGetSettingsResponse]
}
