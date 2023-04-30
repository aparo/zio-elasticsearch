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

package zio.elasticsearch.cat.tasks
import zio._
import zio.json._
/*
 * Returns information about the tasks currently executing on one or more nodes in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param Array

 */
final case class TasksResponse(
  Array: Chunk[TasksRecord] = Chunk.empty[TasksRecord]
) {}
object TasksResponse {
  implicit lazy val jsonCodec: JsonCodec[TasksResponse] =
    DeriveJsonCodec.gen[TasksResponse]
}
