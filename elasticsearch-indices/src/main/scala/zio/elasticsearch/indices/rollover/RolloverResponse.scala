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

package zio.elasticsearch.indices.rollover
import zio.json._
import zio.json.ast._
/*
 * Updates an alias to point to a new index when the existing index
is considered to be too large or too old.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
 *
 * @param acknowledged

 * @param conditions

 * @param dryRun

 * @param newIndex

 * @param oldIndex

 * @param rolledOver

 * @param shardsAcknowledged

 */
final case class RolloverResponse(
  acknowledged: Boolean = true,
  conditions: Map[String, Boolean] = Map.empty[String, Boolean],
  dryRun: Boolean = true,
  newIndex: String,
  oldIndex: String,
  rolledOver: Boolean = true,
  shardsAcknowledged: Boolean = true
) {}
object RolloverResponse {
  implicit val jsonCodec: JsonCodec[RolloverResponse] =
    DeriveJsonCodec.gen[RolloverResponse]
}
