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

package zio.elasticsearch.cluster
import zio.elasticsearch.common._
import zio.elasticsearch.indices.{ AliasDefinition, IndexSettings }
import zio.elasticsearch.mappings.TypeMapping
import zio.json._
import zio.json.ast._
final case class ComponentTemplateSummary(
  @jsonField("_meta") meta: Option[Metadata] = None,
  version: Option[Int] = None,
  settings: Option[Map[String, IndexSettings]] = None,
  mappings: Option[TypeMapping] = None,
  aliases: Option[Map[String, AliasDefinition]] = None
)

object ComponentTemplateSummary {
  implicit val jsonCodec: JsonCodec[ComponentTemplateSummary] =
    DeriveJsonCodec.gen[ComponentTemplateSummary]
}