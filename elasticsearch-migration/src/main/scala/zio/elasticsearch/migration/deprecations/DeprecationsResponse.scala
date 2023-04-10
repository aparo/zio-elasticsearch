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

package zio.elasticsearch.migration.deprecations
import zio._
import zio.json._
import zio.json.ast._
/*
 * Retrieves information about different cluster, node, and index level settings that use deprecated features that will be removed or changed in the next major version.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/migration-api-deprecation.html
 *
 * @param clusterSettings

 * @param indexSettings

 * @param nodeSettings

 * @param mlSettings

 */
final case class DeprecationsResponse(
  clusterSettings: Chunk[Deprecation] = Chunk.empty[Deprecation],
  indexSettings: Map[String, Chunk[Deprecation]] = Map.empty[String, Chunk[Deprecation]],
  nodeSettings: Chunk[Deprecation] = Chunk.empty[Deprecation],
  mlSettings: Chunk[Deprecation] = Chunk.empty[Deprecation]
) {}
object DeprecationsResponse {
  implicit lazy val jsonCodec: JsonCodec[DeprecationsResponse] =
    DeriveJsonCodec.gen[DeprecationsResponse]
}
