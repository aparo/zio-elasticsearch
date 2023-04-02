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

package zio.elasticsearch.snapshot.get
import zio._
import zio.elasticsearch.snapshot.SnapshotInfo
import zio.json._
import zio.json.ast._
/*
 * Returns information about a snapshot.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param responses

 * @param snapshots

 * @param total The total number of snapshots that match the request when ignoring size limit or after query parameter.
 * @since 7.15.0

 * @param remaining The number of remaining snapshots that were not returned due to size limits and that can be fetched by additional requests using the next field value.
 * @since 7.15.0

 */
final case class GetResponse(
  responses: Chunk[SnapshotResponseItem] = Chunk.empty[SnapshotResponseItem],
  snapshots: Chunk[SnapshotInfo] = Chunk.empty[SnapshotInfo],
  total: Int,
  remaining: Int
) {}
object GetResponse {
  implicit val jsonCodec: JsonCodec[GetResponse] =
    DeriveJsonCodec.gen[GetResponse]
}
