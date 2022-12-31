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

import zio.json.{ DeriveJsonDecoderEnum, DeriveJsonEncoderEnum, JsonCodec, JsonDecoder, JsonEncoder }
import zio.json.ast._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-termvectors.html
 *
 * @param index The index in which the document resides.
 * @param docType The type of the document.
 * @param id The id of the document, when not specified a doc param should be supplied.
 * @param body body the body of the call
 * @param parent Parent id of documents.
 * @param preference Specify the node or shard the operation should be performed on (default: random).
 * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned.
 * @param payloads Specifies if term payloads should be returned.
 * @param offsets Specifies if term offsets should be returned.
 * @param termStatistics Specifies if total term frequency and document frequency should be returned.
 * @param version Explicit version number for concurrency control
 * @param positions Specifies if term positions should be returned.
 * @param versionType Specific version type
 * @param fields A comma-separated list of fields to return.
 * @param realtime Specifies if request is real-time as opposed to near-real-time (default: true).
 * @param routing Specific routing value.
 */
final case class TermVectorsResponse(_ok: Option[Boolean] = None)
object TermVectorsResponse {
  implicit final val decoder: JsonDecoder[TermVectorsResponse] =
    DeriveJsonDecoderEnum.gen[TermVectorsResponse]
  implicit final val encoder: JsonEncoder[TermVectorsResponse] =
    DeriveJsonEncoderEnum.gen[TermVectorsResponse]
  implicit final val codec: JsonCodec[TermVectorsResponse] = JsonCodec(encoder, decoder)
}
