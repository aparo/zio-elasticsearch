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

package zio.elasticsearch.indices
import zio._
import zio.elasticsearch.common._
import zio.json._

final case class DataStream(
  name: DataStreamName,
  @jsonField("timestamp_field") timestampField: DataStreamTimestampField,
  indices: Chunk[DataStreamIndex],
  generation: Int,
  template: String,
  hidden: Boolean,
  replicated: Option[Boolean] = None,
  system: Option[Boolean] = None,
  status: HealthStatus,
  @jsonField("ilm_policy") ilmPolicy: Option[String] = None,
  @jsonField("_meta") meta: Option[Metadata] = None,
  @jsonField("allow_custom_routing") allowCustomRouting: Option[Boolean] = None
)

object DataStream {
  implicit val jsonCodec: JsonCodec[DataStream] =
    DeriveJsonCodec.gen[DataStream]
}
