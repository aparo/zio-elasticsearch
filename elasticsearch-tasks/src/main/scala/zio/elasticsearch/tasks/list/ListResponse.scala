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

package zio.elasticsearch.tasks.list
import zio._
import zio.json._
import zio.elasticsearch.tasks._
import zio.elasticsearch.common._

/*4
 * Returns a list of tasks.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param nodeFailures

 * @param taskFailures

 * @param nodes Task information grouped by node, if `group_by` was set to `node` (the default).

 * @param tasks Either a flat list of tasks if `group_by` was set to `none`, or grouped by parents if
 * `group_by` was set to `parents`.

 */
final case class ListResponse(
  nodeFailures: Chunk[ErrorCause] = Chunk.empty[ErrorCause],
  taskFailures: Chunk[TaskFailure] = Chunk.empty[TaskFailure],
  nodes: Map[String, NodeTasks] = Map.empty[String, NodeTasks],
  tasks: Chunk[TaskInfo] = Chunk.empty
) {}
object ListResponse {
  implicit lazy val jsonCodec: JsonCodec[ListResponse] =
    DeriveJsonCodec.gen[ListResponse]
}
