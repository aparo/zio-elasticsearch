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

package zio.elasticsearch.common.requests
import zio.elasticsearch.common.Conflicts
import zio.elasticsearch.common.reindex.{ Destination, Source }
import zio.elasticsearch.script.Script
import zio.json._

final case class ReindexRequestBody(
  conflicts: Option[Conflicts] = None,
  dest: Destination,
  @jsonField("max_docs") maxDocs: Option[Long] = None,
  script: Option[Script] = None,
  size: Option[Long] = None,
  source: Source
)

object ReindexRequestBody {
  implicit lazy val jsonCodec: JsonCodec[ReindexRequestBody] =
    DeriveJsonCodec.gen[ReindexRequestBody]
}
