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
import zio.json._
final case class Action(
  @jsonField("action_type") actionType: Option[ActionType] = None,
  condition: Option[ConditionContainer] = None,
  foreach: Option[String] = None,
  @jsonField("max_iterations") maxIterations: Option[Int] = None,
  name: Option[String] = None,
  @jsonField("throttle_period") throttlePeriod: Option[String] = None,
  @jsonField("throttle_period_in_millis") throttlePeriodInMillis: Option[
    Long
  ] = None,
  transform: Option[TransformContainer] = None,
  index: Option[IndexAction] = None,
  logging: Option[LoggingAction] = None,
  email: Option[EmailAction] = None,
  pagerduty: Option[PagerDutyAction] = None,
  slack: Option[SlackAction] = None,
  webhook: Option[WebhookAction] = None
)

object Action {
  implicit lazy val jsonCodec: JsonCodec[Action] = DeriveJsonCodec.gen[Action]
}
