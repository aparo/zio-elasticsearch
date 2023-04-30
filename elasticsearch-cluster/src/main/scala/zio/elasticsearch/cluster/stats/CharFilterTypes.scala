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

package zio.elasticsearch.cluster.stats
import zio._
import zio.json._
final case class CharFilterTypes(
  @jsonField("char_filter_types") charFilterTypes: Chunk[FieldTypes],
  @jsonField("tokenizer_types") tokenizerTypes: Chunk[FieldTypes],
  @jsonField("filter_types") filterTypes: Chunk[FieldTypes],
  @jsonField("analyzer_types") analyzerTypes: Chunk[FieldTypes],
  @jsonField("built_in_char_filters") builtInCharFilters: Chunk[FieldTypes],
  @jsonField("built_in_tokenizers") builtInTokenizers: Chunk[FieldTypes],
  @jsonField("built_in_filters") builtInFilters: Chunk[FieldTypes],
  @jsonField("built_in_analyzers") builtInAnalyzers: Chunk[FieldTypes]
)

object CharFilterTypes {
  implicit lazy val jsonCodec: JsonCodec[CharFilterTypes] =
    DeriveJsonCodec.gen[CharFilterTypes]
}
