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

package zio.elasticsearch.cat.ml_trained_models
import java.time._
import zio.json._
import zio.json.ast._
final case class TrainedModelsRecord(
  id: Option[String] = None,
  @jsonField("created_by") createdBy: Option[String] = None,
  @jsonField("heap_size") heapSize: Option[String] = None,
  operations: Option[String] = None,
  license: Option[String] = None,
  @jsonField("create_time") createTime: Option[LocalDateTime] = None,
  version: Option[String] = None,
  description: Option[String] = None,
  @jsonField("ingest.pipelines") `ingest.pipelines`: Option[String] = None,
  @jsonField("ingest.count") `ingest.count`: Option[String] = None,
  @jsonField("ingest.time") `ingest.time`: Option[String] = None,
  @jsonField("ingest.current") `ingest.current`: Option[String] = None,
  @jsonField("ingest.failed") `ingest.failed`: Option[String] = None,
  @jsonField("data_frame.id") `dataFrame.id`: Option[String] = None,
  @jsonField("data_frame.create_time") `dataFrame.createTime`: Option[
    String
  ] = None,
  @jsonField("data_frame.source_index") `dataFrame.sourceIndex`: Option[
    String
  ] = None,
  @jsonField("data_frame.analysis") `dataFrame.analysis`: Option[String] = None,
  @jsonField("type") `type`: Option[String] = None
)

object TrainedModelsRecord {
  implicit lazy val jsonCodec: JsonCodec[TrainedModelsRecord] =
    DeriveJsonCodec.gen[TrainedModelsRecord]
}
