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

package zio.elasticsearch.transform.upgrade_transforms
import zio.json._
/*
 * Upgrades all transforms.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/upgrade-transforms.html
 *
 * @param needsUpdate The number of transforms that need to be upgraded.

 * @param noAction The number of transforms that don’t require upgrading.

 * @param updated The number of transforms that have been upgraded.

 */
final case class UpgradeTransformsResponse(
  needsUpdate: Int,
  noAction: Int,
  updated: Int
) {}
object UpgradeTransformsResponse {
  implicit lazy val jsonCodec: JsonCodec[UpgradeTransformsResponse] =
    DeriveJsonCodec.gen[UpgradeTransformsResponse]
}
