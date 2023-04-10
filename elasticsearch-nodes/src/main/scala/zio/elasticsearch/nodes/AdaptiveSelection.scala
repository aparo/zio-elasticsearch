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

package zio.elasticsearch.nodes
import zio.json._
import zio.json.ast._
final case class AdaptiveSelection(
  @jsonField("avg_queue_size") avgQueueSize: Option[Long] = None,
  @jsonField("avg_response_time") avgResponseTime: Option[String] = None,
  @jsonField("avg_response_time_ns") avgResponseTimeNs: Option[Long] = None,
  @jsonField("avg_service_time") avgServiceTime: Option[String] = None,
  @jsonField("avg_service_time_ns") avgServiceTimeNs: Option[Long] = None,
  @jsonField("outgoing_searches") outgoingSearches: Option[Long] = None,
  rank: Option[String] = None
)

object AdaptiveSelection {
  implicit lazy val jsonCodec: JsonCodec[AdaptiveSelection] =
    DeriveJsonCodec.gen[AdaptiveSelection]
}
