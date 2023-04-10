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

package zio.elasticsearch.indices.requests
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.indices.DataStreamVisibility
import zio.elasticsearch.indices.put_index_template.IndexTemplateMapping
import zio.json._
import zio.json.ast._

final case class PutIndexTemplateRequestBody(
  @jsonField("index_patterns") indexPatterns: Option[Chunk[String]] = None,
  @jsonField("composed_of") composedOf: Option[Chunk[String]] = None,
  template: Option[IndexTemplateMapping] = None,
  @jsonField("data_stream") dataStream: Option[DataStreamVisibility] = None,
  priority: Option[Int] = None,
  version: Option[Int] = None,
  @jsonField("_meta") meta: Option[Metadata] = None
)

object PutIndexTemplateRequestBody {
  implicit lazy val jsonCodec: JsonCodec[PutIndexTemplateRequestBody] =
    DeriveJsonCodec.gen[PutIndexTemplateRequestBody]
}
