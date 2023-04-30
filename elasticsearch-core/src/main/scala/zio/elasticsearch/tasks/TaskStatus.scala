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

package zio.elasticsearch.tasks
import zio._
import zio.elasticsearch.common._
import zio.json._
final case class TaskStatus(
  batches: Long,
  canceled: Option[String] = None,
  created: Long,
  deleted: Long,
  noops: Long,
  failures: Option[Chunk[String]] = None,
  @jsonField("requests_per_second") requestsPerSecond: Float,
  retries: Retries,
  throttled: Option[String] = None,
  @jsonField("throttled_millis") throttledMillis: Long,
  @jsonField("throttled_until") throttledUntil: Option[String] = None,
  @jsonField("throttled_until_millis") throttledUntilMillis: Long,
  @jsonField("timed_out") timedOut: Option[Boolean] = None,
  took: Option[Long] = None,
  total: Long,
  updated: Long,
  @jsonField("version_conflicts") versionConflicts: Long
)

object TaskStatus {
  implicit lazy val jsonCodec: JsonCodec[TaskStatus] =
    DeriveJsonCodec.gen[TaskStatus]
}
