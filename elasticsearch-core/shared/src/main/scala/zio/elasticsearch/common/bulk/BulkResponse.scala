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

package zio.elasticsearch.common.bulk
import zio._
import zio.elasticsearch.common.OperationType
import zio.json._
import zio.json.ast._
/*
 * Allows to perform multiple index/update/delete operations in a single request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-bulk.html
 *
 * @param errors

 * @param items

 * @param took

 * @param ingestTook

 */
final case class BulkResponse(
  errors: Boolean = true,
  items: Chunk[Map[OperationType, ResponseItem]] = Chunk.empty[Map[OperationType, ResponseItem]],
  took: Long,
  ingestTook: Long
) {}
object BulkResponse {
  implicit val jsonCodec: JsonCodec[BulkResponse] =
    DeriveJsonCodec.gen[BulkResponse]
}
