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

package zio.elasticsearch.indices.analyze
import zio.json._
final case class AnalyzeToken(
  @jsonField("end_offset") endOffset: Long,
  position: Long,
  positionLength: Option[Long] = None,
  @jsonField("start_offset") startOffset: Long,
  token: String,
  @jsonField("type") `type`: String
)

object AnalyzeToken {
  implicit lazy val jsonCodec: JsonCodec[AnalyzeToken] =
    DeriveJsonCodec.gen[AnalyzeToken]
}
