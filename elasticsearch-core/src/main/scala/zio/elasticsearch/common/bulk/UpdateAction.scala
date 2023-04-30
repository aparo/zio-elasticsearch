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

package zio.elasticsearch.common.bulk
import zio.elasticsearch.common.{ SourceConfig, TDocument, TPartialDocument }
import zio.elasticsearch.script.Script
import zio.json._
final case class UpdateAction(
  @jsonField("detect_noop") detectNoop: Option[Boolean] = None,
  doc: Option[TPartialDocument] = None,
  @jsonField("doc_as_upsert") docAsUpsert: Option[Boolean] = None,
  script: Option[Script] = None,
  @jsonField("scripted_upsert") scriptedUpsert: Option[Boolean] = None,
  @jsonField("_source") source: Option[SourceConfig] = None,
  upsert: Option[TDocument] = None
)

object UpdateAction {
  implicit lazy val jsonCodec: JsonCodec[UpdateAction] =
    DeriveJsonCodec.gen[UpdateAction]
}
