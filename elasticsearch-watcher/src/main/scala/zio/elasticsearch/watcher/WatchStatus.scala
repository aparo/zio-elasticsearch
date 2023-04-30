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
import java.time._

import zio.json._
final case class WatchStatus(
  actions: Actions,
  @jsonField("last_checked") lastChecked: Option[LocalDateTime] = None,
  @jsonField("last_met_condition") lastMetCondition: Option[LocalDateTime] = None,
  state: ActivationState,
  version: Int,
  @jsonField("execution_state") executionState: Option[String] = None
)

object WatchStatus {
  implicit lazy val jsonCodec: JsonCodec[WatchStatus] =
    DeriveJsonCodec.gen[WatchStatus]
}
