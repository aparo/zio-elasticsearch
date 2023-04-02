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

import zio.elasticsearch.common.TDocument
import zio.json._
final case class HitMetadata(
  @jsonField("_id") id: String,
  @jsonField("_index") index: String,
  @jsonField("_primary_term") primaryTerm: Long,
  @jsonField("_routing") routing: String,
  @jsonField("_seq_no") seqNo: Int,
  @jsonField("_source") source: TDocument,
  @jsonField("_version") version: Int
)

object HitMetadata {
  implicit val jsonCodec: JsonCodec[HitMetadata] =
    DeriveJsonCodec.gen[HitMetadata]
}
