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
final case class IngestTotal(
  count: Option[Long] = None,
  current: Option[Long] = None,
  failed: Option[Long] = None,
  processors: Option[Chunk[Map[String, KeyedProcessor]]] = None,
  @jsonField("time_in_millis") timeInMillis: Option[Long] = None
)

object IngestTotal {
  implicit val jsonCodec: JsonCodec[IngestTotal] =
    DeriveJsonCodec.gen[IngestTotal]
}
