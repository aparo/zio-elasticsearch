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

package zio.elasticsearch.ilm.migrate_to_data_tiers
import zio._
import zio.json._
import zio.json.ast._
/*
 * Migrates the indices and ILM policies away from custom node attribute allocation routing to data tiers routing
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-migrate-to-data-tiers.html
 *
 * @param dryRun

 * @param removedLegacyTemplate

 * @param migratedIlmPolicies

 * @param migratedIndices

 * @param migratedLegacyTemplates

 * @param migratedComposableTemplates

 * @param migratedComponentTemplates

 */
final case class MigrateToDataTiersResponse(
  dryRun: Boolean = true,
  removedLegacyTemplate: String,
  migratedIlmPolicies: Chunk[String] = Chunk.empty[String],
  migratedIndices: Chunk[String] = Chunk.empty[String],
  migratedLegacyTemplates: Chunk[String] = Chunk.empty[String],
  migratedComposableTemplates: Chunk[String] = Chunk.empty[String],
  migratedComponentTemplates: Chunk[String] = Chunk.empty[String]
) {}
object MigrateToDataTiersResponse {
  implicit val jsonCodec: JsonCodec[MigrateToDataTiersResponse] =
    DeriveJsonCodec.gen[MigrateToDataTiersResponse]
}
