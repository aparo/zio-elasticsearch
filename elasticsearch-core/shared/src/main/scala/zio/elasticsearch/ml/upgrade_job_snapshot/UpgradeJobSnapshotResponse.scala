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

package zio.elasticsearch.ml.upgrade_job_snapshot
import zio.json._
import zio.json.ast._
/*
 * Upgrades a given job snapshot to the current major version.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-upgrade-job-model-snapshot.html
 *
 * @param node The ID of the assigned node for the upgrade task if it is still running.

 * @param completed When true, this means the task is complete. When false, it is still running.

 */
final case class UpgradeJobSnapshotResponse(
  node: String,
  completed: Boolean = true
) {}
object UpgradeJobSnapshotResponse {
  implicit val jsonCodec: JsonCodec[UpgradeJobSnapshotResponse] =
    DeriveJsonCodec.gen[UpgradeJobSnapshotResponse]
}
