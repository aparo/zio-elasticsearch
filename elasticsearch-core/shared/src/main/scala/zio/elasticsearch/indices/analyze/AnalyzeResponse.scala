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

package zio.elasticsearch.indices.analyze
import zio._
import zio.json._
import zio.json.ast._
/*
 * Performs the analysis process on a text and return the tokens breakdown of the text.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-analyze.html
 *
 * @param detail

 * @param tokens

 */
final case class AnalyzeResponse(
  detail: AnalyzeDetail,
  tokens: Chunk[AnalyzeToken] = Chunk.empty[AnalyzeToken]
) {}
object AnalyzeResponse {
  implicit val jsonCodec: JsonCodec[AnalyzeResponse] =
    DeriveJsonCodec.gen[AnalyzeResponse]
}
