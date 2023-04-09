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

sealed trait StatsWatcherMetric

object StatsWatcherMetric {

  case object _all extends StatsWatcherMetric

  case object all extends StatsWatcherMetric

  case object queued_watches extends StatsWatcherMetric

  case object current_watches extends StatsWatcherMetric

  case object pending_watches extends StatsWatcherMetric

  implicit final val decoder: JsonDecoder[StatsWatcherMetric] =
    DeriveJsonDecoderEnum.gen[StatsWatcherMetric]
  implicit final val encoder: JsonEncoder[StatsWatcherMetric] =
    DeriveJsonEncoderEnum.gen[StatsWatcherMetric]
  implicit final val codec: JsonCodec[StatsWatcherMetric] =
    JsonCodec(encoder, decoder)

}
