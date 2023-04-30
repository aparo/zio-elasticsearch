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
import zio._
import zio.json._
/*
 * Shows information about currently configured aliases to indices including filter and routing infos.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-alias.html
 *
 * @param Array

 */
final case class AliasesResponse(
  Array: Chunk[AliasesRecord] = Chunk.empty[AliasesRecord]
) {}
object AliasesResponse {
  implicit lazy val jsonCodec: JsonCodec[AliasesResponse] =
    DeriveJsonCodec.gen[AliasesResponse]
}
