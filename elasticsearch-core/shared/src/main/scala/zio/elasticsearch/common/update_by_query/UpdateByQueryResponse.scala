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

package zio.elasticsearch.common.update_by_query
import zio._
import zio.elasticsearch.common.{ BulkIndexByScrollFailure, Retries }
import zio.json._
import zio.json.ast._
/*
 * Performs an update on every document in the index without changing the source,
for example to pick up a mapping change.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update-by-query.html
 *
 * @param batches

 * @param failures

 * @param noops

 * @param deleted

 * @param requestsPerSecond

 * @param retries

 * @param task

 * @param timedOut

 * @param took

 * @param total

 * @param updated

 * @param versionConflicts

 * @param throttled

 * @param throttledMillis

 * @param throttledUntil

 * @param throttledUntilMillis

 */
@jsonMemberNames(SnakeCase)
final case class UpdateByQueryResponse(
  batches: Long = 0,
  failures: Chunk[BulkIndexByScrollFailure] = Chunk.empty[BulkIndexByScrollFailure],
  noops: Long = 0,
  deleted: Long = 0,
  requestsPerSecond: Float = 0.0f,
  retries: Retries = Retries(),
  task: Option[String] = None,
  timedOut: Boolean = true,
  took: Long = 0,
  total: Long = 0,
  updated: Long = 0,
  versionConflicts: Long = 0,
  throttled: Option[String] = None,
  throttledMillis: Long = 0,
  throttledUntil: Option[String] = None,
  throttledUntilMillis: Long = 0
) {}
object UpdateByQueryResponse {
  implicit lazy val jsonCodec: JsonCodec[UpdateByQueryResponse] =
    DeriveJsonCodec.gen[UpdateByQueryResponse]
}
