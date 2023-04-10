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

package zio.elasticsearch.cat.thread_pool
import zio.json._
import zio.json.ast._
final case class ThreadPoolRecord(
  @jsonField("node_name") nodeName: Option[String] = None,
  @jsonField("node_id") nodeId: Option[String] = None,
  @jsonField("ephemeral_node_id") ephemeralNodeId: Option[String] = None,
  pid: Option[String] = None,
  host: Option[String] = None,
  ip: Option[String] = None,
  port: Option[String] = None,
  name: Option[String] = None,
  @jsonField("type") `type`: Option[String] = None,
  active: Option[String] = None,
  @jsonField("pool_size") poolSize: Option[String] = None,
  queue: Option[String] = None,
  @jsonField("queue_size") queueSize: Option[String] = None,
  rejected: Option[String] = None,
  largest: Option[String] = None,
  completed: Option[String] = None,
  core: Option[String] = None,
  max: Option[String] = None,
  size: Option[String] = None,
  @jsonField("keep_alive") keepAlive: Option[String] = None
)

object ThreadPoolRecord {
  implicit lazy val jsonCodec: JsonCodec[ThreadPoolRecord] =
    DeriveJsonCodec.gen[ThreadPoolRecord]
}
