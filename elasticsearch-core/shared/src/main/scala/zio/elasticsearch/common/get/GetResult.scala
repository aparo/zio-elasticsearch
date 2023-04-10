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

package zio.elasticsearch.common.get
import zio.elasticsearch.common.TDocument
import zio.json._
import zio.json.ast._
final case class GetResult(
  @jsonField("_index") index: String,
  fields: Option[Map[String, Json]] = None,
  found: Boolean,
  @jsonField("_id") id: String,
  @jsonField("_primary_term") primaryTerm: Option[Long] = None,
  @jsonField("_routing") routing: Option[String] = None,
  @jsonField("_seq_no") seqNo: Option[Int] = None,
  @jsonField("_source") source: Option[TDocument] = None,
  @jsonField("_version") version: Option[Int] = None
)

object GetResult {
  implicit lazy val jsonCodec: JsonCodec[GetResult] = DeriveJsonCodec.gen[GetResult]
}
