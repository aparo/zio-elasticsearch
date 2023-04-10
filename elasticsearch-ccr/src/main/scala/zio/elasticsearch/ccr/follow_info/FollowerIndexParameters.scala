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

package zio.elasticsearch.ccr.follow_info
import zio.json._
import zio.json.ast._
final case class FollowerIndexParameters(
  @jsonField("max_outstanding_read_requests") maxOutstandingReadRequests: Int,
  @jsonField(
    "max_outstanding_write_requests"
  ) maxOutstandingWriteRequests: Int,
  @jsonField(
    "max_read_request_operation_count"
  ) maxReadRequestOperationCount: Int,
  @jsonField("max_read_request_size") maxReadRequestSize: String,
  @jsonField("max_retry_delay") maxRetryDelay: String,
  @jsonField("max_write_buffer_count") maxWriteBufferCount: Int,
  @jsonField("max_write_buffer_size") maxWriteBufferSize: String,
  @jsonField(
    "max_write_request_operation_count"
  ) maxWriteRequestOperationCount: Int,
  @jsonField("max_write_request_size") maxWriteRequestSize: String,
  @jsonField("read_poll_timeout") readPollTimeout: String
)

object FollowerIndexParameters {
  implicit lazy val jsonCodec: JsonCodec[FollowerIndexParameters] =
    DeriveJsonCodec.gen[FollowerIndexParameters]
}
