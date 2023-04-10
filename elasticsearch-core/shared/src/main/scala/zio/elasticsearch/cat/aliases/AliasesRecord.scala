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

package zio.elasticsearch.cat.aliases
import zio.json._
import zio.json.ast._
final case class AliasesRecord(
  alias: Option[String] = None,
  index: Option[String] = None,
  filter: Option[String] = None,
  @jsonField("routing.index") `routing.index`: Option[String] = None,
  @jsonField("routing.search") `routing.search`: Option[String] = None,
  @jsonField("is_write_index") isWriteIndex: Option[String] = None
)

object AliasesRecord {
  implicit lazy val jsonCodec: JsonCodec[AliasesRecord] =
    DeriveJsonCodec.gen[AliasesRecord]
}
