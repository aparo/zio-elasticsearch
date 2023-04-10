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

package zio.elasticsearch.watcher.requests
import zio.elasticsearch.watcher.{ ActionExecutionMode, ScheduleTriggerEvent, SimulatedActions, Watch }
import zio.json._
import zio.json.ast._

final case class ExecuteWatchRequestBody(
  @jsonField("action_modes") actionModes: Option[
    Map[String, ActionExecutionMode]
  ] = None,
  @jsonField("alternative_input") alternativeInput: Option[
    Map[String, Json]
  ] = None,
  @jsonField("ignore_condition") ignoreCondition: Option[Boolean] = None,
  @jsonField("record_execution") recordExecution: Option[Boolean] = None,
  @jsonField("simulated_actions") simulatedActions: Option[SimulatedActions] = None,
  @jsonField("trigger_data") triggerData: Option[ScheduleTriggerEvent] = None,
  watch: Option[Watch] = None
)

object ExecuteWatchRequestBody {
  implicit lazy val jsonCodec: JsonCodec[ExecuteWatchRequestBody] =
    DeriveJsonCodec.gen[ExecuteWatchRequestBody]
}
