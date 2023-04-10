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

package zio.elasticsearch.xpack.usage
import zio._
import zio.json._
import zio.json.ast._
final case class RuntimeFieldsType(
  @jsonField("chars_max") charsMax: Long,
  @jsonField("chars_total") charsTotal: Long,
  count: Long,
  @jsonField("doc_max") docMax: Long,
  @jsonField("doc_total") docTotal: Long,
  @jsonField("index_count") indexCount: Long,
  lang: Chunk[String],
  @jsonField("lines_max") linesMax: Long,
  @jsonField("lines_total") linesTotal: Long,
  name: String,
  @jsonField("scriptless_count") scriptlessCount: Long,
  @jsonField("shadowed_count") shadowedCount: Long,
  @jsonField("source_max") sourceMax: Long,
  @jsonField("source_total") sourceTotal: Long
)

object RuntimeFieldsType {
  implicit lazy val jsonCodec: JsonCodec[RuntimeFieldsType] =
    DeriveJsonCodec.gen[RuntimeFieldsType]
}
