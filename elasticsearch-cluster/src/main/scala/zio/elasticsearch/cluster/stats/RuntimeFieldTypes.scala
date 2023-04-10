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
import zio.json.ast._
final case class RuntimeFieldTypes(
  name: String,
  count: Int,
  @jsonField("index_count") indexCount: Int,
  @jsonField("scriptless_count") scriptlessCount: Int,
  @jsonField("shadowed_count") shadowedCount: Int,
  lang: Chunk[String],
  @jsonField("lines_max") linesMax: Int,
  @jsonField("lines_total") linesTotal: Int,
  @jsonField("chars_max") charsMax: Int,
  @jsonField("chars_total") charsTotal: Int,
  @jsonField("source_max") sourceMax: Int,
  @jsonField("source_total") sourceTotal: Int,
  @jsonField("doc_max") docMax: Int,
  @jsonField("doc_total") docTotal: Int
)

object RuntimeFieldTypes {
  implicit lazy val jsonCodec: JsonCodec[RuntimeFieldTypes] =
    DeriveJsonCodec.gen[RuntimeFieldTypes]
}
