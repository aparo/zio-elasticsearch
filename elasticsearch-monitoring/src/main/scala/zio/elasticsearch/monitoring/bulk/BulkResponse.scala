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

package zio.elasticsearch.monitoring.bulk
import zio.elasticsearch.common.ErrorCause
import zio.json._
/*
 * Used by the monitoring features to send monitoring data.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/monitor-elasticsearch-cluster.html
 *
 * @param error

 * @param errors True if there is was an error

 * @param ignored Was collection disabled?

 * @param took

 */
final case class BulkResponse(
  error: ErrorCause,
  errors: Boolean = true,
  ignored: Boolean = true,
  took: Long
) {}
object BulkResponse {
  implicit lazy val jsonCodec: JsonCodec[BulkResponse] =
    DeriveJsonCodec.gen[BulkResponse]
}
