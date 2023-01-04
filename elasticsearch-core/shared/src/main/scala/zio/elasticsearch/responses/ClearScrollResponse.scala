/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.responses

import zio.elasticsearch.sort.SortMode
import zio.json._
import zio.json.ast._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-scroll.html
 *
 * @param scrollId A comma-separated list of scroll IDs to clear
 * @param body body the body of the call
 */

final case class ClearScrollResponse(_ok: Option[Boolean] = None)
object ClearScrollResponse {
  implicit final val decoder: JsonDecoder[ClearScrollResponse] =
    DeriveJsonDecoderEnum.gen[ClearScrollResponse]
  implicit final val encoder: JsonEncoder[ClearScrollResponse] =
    DeriveJsonEncoderEnum.gen[ClearScrollResponse]
  implicit final val codec: JsonCodec[ClearScrollResponse] = JsonCodec(encoder, decoder)
}
