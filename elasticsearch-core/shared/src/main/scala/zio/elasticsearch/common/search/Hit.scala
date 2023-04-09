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
import zio.elasticsearch.common.{ Explanation, SortResults, TDocument }
import zio.json._
import zio.json.ast._
final case class Hit(
  @jsonField("_index") index: String,
  @jsonField("_id") id: String,
  @jsonField("_score") score: Option[Double] = None,
  @jsonField("_explanation") explanation: Option[Explanation] = None,
  fields: Option[Map[String, Json]] = None,
  highlight: Option[Map[String, Chunk[String]]] = None,
  @jsonField("inner_hits") innerHits: Option[Map[String, InnerHitsResult]] = None,
  @jsonField("matched_queries") matchedQueries: Option[Chunk[String]] = None,
  @jsonField("_nested") nested: Option[NestedIdentity] = None,
  @jsonField("_ignored") ignored: Option[Chunk[String]] = None,
  @jsonField("ignored_field_values") ignoredFieldValues: Option[
    Map[String, Chunk[String]]
  ] = None,
  @jsonField("_shard") shard: Option[String] = None,
  @jsonField("_node") node: Option[String] = None,
  @jsonField("_routing") routing: Option[String] = None,
  @jsonField("_source") source: Option[TDocument] = None,
  @jsonField("_seq_no") seqNo: Option[Int] = None,
  @jsonField("_primary_term") primaryTerm: Option[Long] = None,
  @jsonField("_version") version: Option[Int] = None,
  sort: Option[SortResults] = None
)

object Hit {
  implicit val jsonCodec: JsonCodec[Hit] = DeriveJsonCodec.gen[Hit]
}
