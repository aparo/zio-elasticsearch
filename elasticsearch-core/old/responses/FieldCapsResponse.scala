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

package zio.elasticsearch.responses

import zio.json.{ DeriveJsonDecoderEnum, DeriveJsonEncoderEnum, JsonCodec, JsonDecoder, JsonEncoder }
import zio.json.ast._
/*
 * Returns the information about the capabilities of fields among multiple indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-field-caps.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param fields A comma-separated list of field names
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param includeUnmapped Indicates whether unmapped fields should be included in the response.
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 */
final case class FieldCapsResponse(_ok: Option[Boolean] = None)
object FieldCapsResponse {
  implicit final val decoder: JsonDecoder[FieldCapsResponse] =
    DeriveJsonDecoderEnum.gen[FieldCapsResponse]
  implicit final val encoder: JsonEncoder[FieldCapsResponse] =
    DeriveJsonEncoderEnum.gen[FieldCapsResponse]
  implicit final val codec: JsonCodec[FieldCapsResponse] = JsonCodec(encoder, decoder)
}
