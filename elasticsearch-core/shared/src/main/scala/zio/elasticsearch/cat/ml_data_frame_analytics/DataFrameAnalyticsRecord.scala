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

package zio.elasticsearch.cat.ml_data_frame_analytics
import zio.json._
import zio.json.ast._
final case class DataFrameAnalyticsRecord(
  id: Option[String] = None,
  @jsonField("type") `type`: Option[String] = None,
  @jsonField("create_time") createTime: Option[String] = None,
  version: Option[String] = None,
  @jsonField("source_index") sourceIndex: Option[String] = None,
  @jsonField("dest_index") destIndex: Option[String] = None,
  description: Option[String] = None,
  @jsonField("model_memory_limit") modelMemoryLimit: Option[String] = None,
  state: Option[String] = None,
  @jsonField("failure_reason") failureReason: Option[String] = None,
  progress: Option[String] = None,
  @jsonField("assignment_explanation") assignmentExplanation: Option[String] = None,
  @jsonField("node.id") `node.id`: Option[String] = None,
  @jsonField("node.name") `node.name`: Option[String] = None,
  @jsonField("node.ephemeral_id") `node.ephemeralId`: Option[String] = None,
  @jsonField("node.address") `node.address`: Option[String] = None
)

object DataFrameAnalyticsRecord {
  implicit val jsonCodec: JsonCodec[DataFrameAnalyticsRecord] =
    DeriveJsonCodec.gen[DataFrameAnalyticsRecord]
}
