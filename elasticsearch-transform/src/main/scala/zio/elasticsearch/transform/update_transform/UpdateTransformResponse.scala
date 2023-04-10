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

package zio.elasticsearch.transform.update_transform
import zio.elasticsearch.common.Metadata
import zio.elasticsearch.ml.TransformAuthorization
import zio.elasticsearch.transform._
import zio.json._

/*
 * Updates certain properties of a transform.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/update-transform.html
 *
 * @param authorization

 * @param createTime

 * @param description //  create_time_date_time?: DateTime


 * @param dest

 * @param frequency

 * @param id

 * @param latest

 * @param pivot

 * @param retentionPolicy

 * @param settings

 * @param source

 * @param sync

 * @param version

 * @param meta

 */
final case class UpdateTransformResponse(
  authorization: TransformAuthorization,
  createTime: Long,
  description: String,
  dest: Destination,
  frequency: String,
  id: String,
  latest: Latest,
  pivot: Pivot,
  retentionPolicy: RetentionPolicyContainer,
  settings: Settings,
  source: Source,
  sync: SyncContainer,
  version: String,
  meta: Metadata
) {}
object UpdateTransformResponse {
  implicit lazy val jsonCodec: JsonCodec[UpdateTransformResponse] =
    DeriveJsonCodec.gen[UpdateTransformResponse]
}
