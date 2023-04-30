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

package zio.elasticsearch.tasks
import zio._
import zio.elasticsearch.common._
import zio.json._
final case class NodeTasks(
  name: Option[String] = None,
  @jsonField("transport_address") transportAddress: Option[TransportAddress] = None,
  host: Option[String] = None,
  ip: Option[String] = None,
  roles: Option[Chunk[String]] = None,
  attributes: Option[Map[String, String]] = None,
  tasks: Map[String, TaskInfo]
)

object NodeTasks {
  implicit lazy val jsonCodec: JsonCodec[NodeTasks] = DeriveJsonCodec.gen[NodeTasks]
}
