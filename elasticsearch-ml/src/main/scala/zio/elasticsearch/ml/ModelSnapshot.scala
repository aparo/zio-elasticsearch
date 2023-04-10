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

package zio.elasticsearch.ml
import zio.json._
import zio.json.ast._
final case class ModelSnapshot(
  description: Option[String] = None,
  @jsonField("job_id") jobId: String,
  @jsonField("latest_record_time_stamp") latestRecordTimeStamp: Option[Int] = None,
  @jsonField("latest_result_time_stamp") latestResultTimeStamp: Option[Int] = None,
  @jsonField("min_version") minVersion: String,
  @jsonField("model_size_stats") modelSizeStats: Option[ModelSizeStats] = None,
  retain: Boolean,
  @jsonField("snapshot_doc_count") snapshotDocCount: Long,
  @jsonField("snapshot_id") snapshotId: String,
  timestamp: Long
)

object ModelSnapshot {
  implicit lazy val jsonCodec: JsonCodec[ModelSnapshot] =
    DeriveJsonCodec.gen[ModelSnapshot]
}
