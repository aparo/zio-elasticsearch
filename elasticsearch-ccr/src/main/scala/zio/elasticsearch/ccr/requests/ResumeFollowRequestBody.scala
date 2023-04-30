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

package zio.elasticsearch.ccr.requests
import zio.json._

final case class ResumeFollowRequestBody(
  @jsonField(
    "max_outstanding_read_requests"
  ) maxOutstandingReadRequests: Option[Long] = None,
  @jsonField(
    "max_outstanding_write_requests"
  ) maxOutstandingWriteRequests: Option[Long] = None,
  @jsonField(
    "max_read_request_operation_count"
  ) maxReadRequestOperationCount: Option[Long] = None,
  @jsonField("max_read_request_size") maxReadRequestSize: Option[String] = None,
  @jsonField("max_retry_delay") maxRetryDelay: Option[String] = None,
  @jsonField("max_write_buffer_count") maxWriteBufferCount: Option[Long] = None,
  @jsonField("max_write_buffer_size") maxWriteBufferSize: Option[String] = None,
  @jsonField(
    "max_write_request_operation_count"
  ) maxWriteRequestOperationCount: Option[Long] = None,
  @jsonField("max_write_request_size") maxWriteRequestSize: Option[String] = None,
  @jsonField("read_poll_timeout") readPollTimeout: Option[String] = None
)

object ResumeFollowRequestBody {
  implicit lazy val jsonCodec: JsonCodec[ResumeFollowRequestBody] =
    DeriveJsonCodec.gen[ResumeFollowRequestBody]
}
