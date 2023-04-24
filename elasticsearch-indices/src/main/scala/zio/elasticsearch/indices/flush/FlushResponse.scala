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

package zio.elasticsearch.indices.flush
import zio.elasticsearch.common._
import zio.json._
import zio.json.ast._
/*
 * Performs the flush operation on one or more indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-flush.html
 *
 * @param shards

 */
final case class FlushResponse(@jsonField("_shards") shards: ShardStatistics) {}
object FlushResponse {
  implicit lazy val jsonCodec: JsonCodec[FlushResponse] =
    DeriveJsonCodec.gen[FlushResponse]
}
