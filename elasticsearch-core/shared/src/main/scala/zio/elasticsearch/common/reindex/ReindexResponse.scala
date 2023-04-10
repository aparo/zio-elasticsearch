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

package zio.elasticsearch.common.reindex
import zio._
import zio.elasticsearch.common.{ BulkIndexByScrollFailure, Retries }
import zio.json._
import zio.json.ast._
/*
 * Allows to copy documents from one index to another, optionally filtering the source
documents by a query, changing the destination index settings, or fetching the
documents from a remote cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
 *
 * @param batches

 * @param created

 * @param deleted

 * @param failures

 * @param noops

 * @param retries

 * @param requestsPerSecond

 * @param sliceId

 * @param task

 * @param throttledMillis

 * @param throttledUntilMillis

 * @param timedOut

 * @param took

 * @param total

 * @param updated

 * @param versionConflicts

 */
final case class ReindexResponse(
  batches: Long,
  created: Long,
  deleted: Long,
  failures: Chunk[BulkIndexByScrollFailure] = Chunk.empty[BulkIndexByScrollFailure],
  noops: Long,
  retries: Retries,
  requestsPerSecond: Float,
  sliceId: Int,
  task: String,
  throttledMillis: Long,
  throttledUntilMillis: Long,
  timedOut: Boolean = true,
  took: Long,
  total: Long,
  updated: Long,
  versionConflicts: Long
) {}
object ReindexResponse {
  implicit lazy val jsonCodec: JsonCodec[ReindexResponse] =
    DeriveJsonCodec.gen[ReindexResponse]
}
