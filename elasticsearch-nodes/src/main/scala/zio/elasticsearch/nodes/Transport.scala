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
import zio._
import zio.json._
import zio.json.ast._
final case class Transport(
  @jsonField(
    "inbound_handling_time_histogram"
  ) inboundHandlingTimeHistogram: Option[Chunk[TransportHistogram]] = None,
  @jsonField(
    "outbound_handling_time_histogram"
  ) outboundHandlingTimeHistogram: Option[Chunk[TransportHistogram]] = None,
  @jsonField("rx_count") rxCount: Option[Long] = None,
  @jsonField("rx_size") rxSize: Option[String] = None,
  @jsonField("rx_size_in_bytes") rxSizeInBytes: Option[Long] = None,
  @jsonField("server_open") serverOpen: Option[Int] = None,
  @jsonField("tx_count") txCount: Option[Long] = None,
  @jsonField("tx_size") txSize: Option[String] = None,
  @jsonField("tx_size_in_bytes") txSizeInBytes: Option[Long] = None,
  @jsonField("total_outbound_connections") totalOutboundConnections: Option[
    Long
  ] = None
)

object Transport {
  implicit lazy val jsonCodec: JsonCodec[Transport] = DeriveJsonCodec.gen[Transport]
}
