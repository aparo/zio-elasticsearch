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

package zio.elasticsearch.transform.get_transform_stats
import zio.json._
import zio.json.ast._
final case class TransformProgress(
  @jsonField("docs_indexed") docsIndexed: Long,
  @jsonField("docs_processed") docsProcessed: Long,
  @jsonField("docs_remaining") docsRemaining: Long,
  @jsonField("percent_complete") percentComplete: Double,
  @jsonField("total_docs") totalDocs: Long
)

object TransformProgress {
  implicit lazy val jsonCodec: JsonCodec[TransformProgress] =
    DeriveJsonCodec.gen[TransformProgress]
}
