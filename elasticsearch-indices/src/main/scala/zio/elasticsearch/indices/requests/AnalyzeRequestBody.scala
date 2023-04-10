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

package zio.elasticsearch.indices.requests
import zio._
import zio.elasticsearch.common.analysis._
import zio.elasticsearch.indices.{ TextToAnalyze, TokenFilter }
import zio.json._
import zio.json.ast._

final case class AnalyzeRequestBody(
  analyzer: Option[String] = None,
  attributes: Option[Chunk[String]] = None,
  @jsonField("char_filter") charFilter: Option[Chunk[CharFilter]] = None,
  explain: Option[Boolean] = None,
  field: Option[String] = None,
  filter: Option[Chunk[TokenFilter]] = None,
  normalizer: Option[String] = None,
  text: Option[TextToAnalyze] = None,
  tokenizer: Option[Tokenizer] = None
)

object AnalyzeRequestBody {
  implicit lazy val jsonCodec: JsonCodec[AnalyzeRequestBody] =
    DeriveJsonCodec.gen[AnalyzeRequestBody]
}
