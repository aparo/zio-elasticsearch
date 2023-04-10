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

package zio.elasticsearch.watcher
import zio.elasticsearch.common.ErrorCause
import zio.json._
import zio.json.ast._
final case class ExecutionResultAction(
  email: Option[EmailResult] = None,
  id: String,
  index: Option[IndexResult] = None,
  logging: Option[LoggingResult] = None,
  pagerduty: Option[PagerDutyResult] = None,
  reason: Option[String] = None,
  slack: Option[SlackResult] = None,
  status: ActionStatusOptions,
  @jsonField("type") `type`: ActionType,
  webhook: Option[WebhookResult] = None,
  error: Option[ErrorCause] = None
)

object ExecutionResultAction {
  implicit lazy val jsonCodec: JsonCodec[ExecutionResultAction] =
    DeriveJsonCodec.gen[ExecutionResultAction]
}
