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
import zio.elasticsearch.common._
import zio.json._
final case class Watch(
  actions: Map[String, Action],
  condition: ConditionContainer,
  input: InputContainer,
  metadata: Option[Metadata] = None,
  status: Option[WatchStatus] = None,
  @jsonField("throttle_period") throttlePeriod: Option[String] = None,
  @jsonField("throttle_period_in_millis") throttlePeriodInMillis: Option[
    Long
  ] = None,
  transform: Option[TransformContainer] = None,
  trigger: TriggerContainer
)

object Watch {
  implicit lazy val jsonCodec: JsonCodec[Watch] = DeriveJsonCodec.gen[Watch]
}
