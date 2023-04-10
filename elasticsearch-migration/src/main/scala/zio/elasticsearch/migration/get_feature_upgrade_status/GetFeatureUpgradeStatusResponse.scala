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

package zio.elasticsearch.migration.get_feature_upgrade_status
import zio._
import zio.elasticsearch.migration.MigrationStatus
import zio.json._
import zio.json.ast._
/*
 * Find out whether system features need to be upgraded or not
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/migration-api-feature-upgrade.html
 *
 * @param features

 * @param migrationStatus

 */
final case class GetFeatureUpgradeStatusResponse(
  features: Chunk[MigrationFeature] = Chunk.empty[MigrationFeature],
  migrationStatus: MigrationStatus
) {}
object GetFeatureUpgradeStatusResponse {
  implicit lazy val jsonCodec: JsonCodec[GetFeatureUpgradeStatusResponse] =
    DeriveJsonCodec.gen[GetFeatureUpgradeStatusResponse]
}
