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

package zio.elasticsearch.common.search

import zio._
import zio.json._
final case class PhraseSuggester(
  collate: Option[PhraseSuggestCollate] = None,
  confidence: Option[Double] = None,
  @jsonField("direct_generator") directGenerator: Option[
    Chunk[DirectGenerator]
  ] = None,
  @jsonField("force_unigrams") forceUnigrams: Option[Boolean] = None,
  @jsonField("gram_size") gramSize: Option[Int] = None,
  highlight: Option[PhraseSuggestHighlight] = None,
  @jsonField("max_errors") maxErrors: Option[Double] = None,
  @jsonField("real_word_error_likelihood") realWordErrorLikelihood: Option[
    Double
  ] = None,
  separator: Option[String] = None,
  @jsonField("shard_size") shardSize: Option[Int] = None,
  smoothing: Option[SmoothingModelContainer] = None,
  text: Option[String] = None,
  @jsonField("token_limit") tokenLimit: Option[Int] = None,
  field: String,
  analyzer: Option[String] = None,
  size: Option[Int] = None
)

object PhraseSuggester {
  implicit val jsonCodec: JsonCodec[PhraseSuggester] =
    DeriveJsonCodec.gen[PhraseSuggester]
}
