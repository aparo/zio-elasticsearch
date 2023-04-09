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

package zio.elasticsearch.snapshot.requests
import zio._
import zio.elasticsearch.indices.IndexSettings
import zio.json._
import zio.json.ast._

final case class RestoreRequestBody(
  @jsonField("ignore_index_settings") ignoreIndexSettings: Option[
    Chunk[String]
  ] = None,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @jsonField("include_aliases") includeAliases: Option[Boolean] = None,
  @jsonField("include_global_state") includeGlobalState: Option[Boolean] = None,
  @jsonField("index_settings") indexSettings: Option[IndexSettings] = None,
  indices: Option[Chunk[String]] = None,
  partial: Option[Boolean] = None,
  @jsonField("rename_pattern") renamePattern: Option[String] = None,
  @jsonField("rename_replacement") renameReplacement: Option[String] = None
)

object RestoreRequestBody {
  implicit val jsonCodec: JsonCodec[RestoreRequestBody] =
    DeriveJsonCodec.gen[RestoreRequestBody]
}
