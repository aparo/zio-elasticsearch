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

package zio.elasticsearch.transform.get_transform
import zio.elasticsearch.common._
import zio.elasticsearch.ml.TransformAuthorization
import zio.elasticsearch.transform._
import zio.json._

final case class TransformSummary(
  authorization: Option[TransformAuthorization] = None,
  @jsonField("create_time") createTime: Option[Long] = None,
  description: Option[String] = None,
  dest: Destination,
  frequency: Option[String] = None,
  id: String,
  latest: Option[Latest] = None,
  pivot: Option[Pivot] = None,
  @jsonField("retention_policy") retentionPolicy: Option[
    RetentionPolicyContainer
  ] = None,
  settings: Option[Settings] = None,
  source: Source,
  sync: Option[SyncContainer] = None,
  version: Option[String] = None,
  @jsonField("_meta") meta: Option[Metadata] = None
)

object TransformSummary {
  implicit val jsonCodec: JsonCodec[TransformSummary] =
    DeriveJsonCodec.gen[TransformSummary]
}
