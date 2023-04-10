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

package zio.elasticsearch.indices
import zio._
import zio.elasticsearch.common._
import zio.json._
import zio.json.ast._
final case class IndexTemplate(
  @jsonField("index_patterns") indexPatterns: Names,
  @jsonField("composed_of") composedOf: Chunk[String],
  template: Option[IndexTemplateSummary] = None,
  version: Option[Int] = None,
  priority: Option[Long] = None,
  @jsonField("_meta") meta: Option[Metadata] = None,
  @jsonField("allow_auto_create") allowAutoCreate: Option[Boolean] = None,
  @jsonField("data_stream") dataStream: Option[
    IndexTemplateDataStreamConfiguration
  ] = None
)

object IndexTemplate {
  implicit lazy val jsonCodec: JsonCodec[IndexTemplate] =
    DeriveJsonCodec.gen[IndexTemplate]
}
