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

package zio.elasticsearch.sql.translate
import zio._
import zio.elasticsearch.aggregations.Aggregation
import zio.elasticsearch.common.{ FieldAndFormat, SourceConfig }
import zio.elasticsearch.queries.Query
import zio.elasticsearch.sort.Sort.Sort
import zio.json._
import zio.json.ast._
/*
 * Translates SQL into Elasticsearch queries
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/sql-translate-api.html
 *
 * @param aggregations

 * @param size

 * @param source

 * @param fields

 * @param query

 * @param sort

 */
final case class TranslateResponse(
  aggregations: Map[String, Aggregation] = Map.empty[String, Aggregation],
  size: Long,
  source: SourceConfig,
  fields: Chunk[FieldAndFormat] = Chunk.empty[FieldAndFormat],
  query: Query,
  sort: Sort
) {}
object TranslateResponse {
  implicit lazy val jsonCodec: JsonCodec[TranslateResponse] =
    DeriveJsonCodec.gen[TranslateResponse]
}
