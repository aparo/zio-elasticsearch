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
import zio.elasticsearch.common._
import zio.elasticsearch.sort.Sort.Sort
import zio.json._
final case class InnerHits(
  name: Option[String] = None,
  size: Option[Int] = None,
  from: Option[Int] = None,
//  collapse: Option[FieldCollapse] = None, // Removed to reduce cycling JSON
  @jsonField("docvalue_fields") docvalueFields: Option[
    Chunk[FieldAndFormat]
  ] = None,
  explain: Option[Boolean] = None,
  highlight: Option[Highlight] = None,
  @jsonField("ignore_unmapped") ignoreUnmapped: Option[Boolean] = None,
  @jsonField("script_fields") scriptFields: Option[Map[String, ScriptField]] = None,
  @jsonField("seq_no_primary_term") seqNoPrimaryTerm: Option[Boolean] = None,
  fields: Option[Chunk[String]] = None,
  sort: Option[Sort] = None,
  @jsonField("_source") source: Option[SourceConfig] = None,
  @jsonField("stored_field") storedField: Option[Chunk[String]] = None,
  @jsonField("track_scores") trackScores: Option[Boolean] = None,
  version: Option[Boolean] = None
)

object InnerHits {
  implicit lazy val jsonCodec: JsonCodec[InnerHits] = DeriveJsonCodec.gen[InnerHits]
}
