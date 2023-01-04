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

package zio.elasticsearch.responses.nodes

import zio.json._
/*
 * Reloads secure settings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/secure-settings.html#reloadable-secure-settings
 *
 * @param nodeId A comma-separated list of node IDs to span the reload/reinit call. Should stay empty because reloading usually involves all cluster nodes.
 * @param timeout Explicit operation timeout
 */
final case class NodesReloadSecureSettingsResponse(_ok: Option[Boolean] = None)
object NodesReloadSecureSettingsResponse {
  implicit val jsonDecoder: JsonDecoder[NodesReloadSecureSettingsResponse] =
    DeriveJsonDecoder.gen[NodesReloadSecureSettingsResponse]
  implicit val jsonEncoder: JsonEncoder[NodesReloadSecureSettingsResponse] =
    DeriveJsonEncoder.gen[NodesReloadSecureSettingsResponse]
}
