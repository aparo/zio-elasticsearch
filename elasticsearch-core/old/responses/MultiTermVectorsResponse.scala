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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-termvectors.html
 *
 * @param body body the body of the call
 * @param index The index in which the document resides.
 * @param docType The type of the document.
 * @param parent Parent id of documents. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param preference Specify the node or shard the operation should be performed on (default: random) .Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param payloads Specifies if term payloads should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param offsets Specifies if term offsets should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param termStatistics Specifies if total term frequency and document frequency should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param version Explicit version number for concurrency control
 * @param positions Specifies if term positions should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param versionType Specific version type
 * @param fields A comma-separated list of fields to return. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param realtime Specifies if requests are real-time as opposed to near-real-time (default: true).
 * @param routing Specific routing value. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param ids A comma-separated list of documents ids. You must define ids as parameter or set "ids" or "docs" in the request body
 */
case class MultiTermVectorsResponse(_ok: Option[Boolean] = None)
object MultiTermVectorsResponse {
  implicit final val decoder: JsonDecoder[MultiTermVectorsResponse] =
    DeriveJsonDecoderEnum.gen[MultiTermVectorsResponse]
  implicit final val encoder: JsonEncoder[MultiTermVectorsResponse] =
    DeriveJsonEncoderEnum.gen[MultiTermVectorsResponse]
  implicit final val codec: JsonCodec[MultiTermVectorsResponse] = JsonCodec(encoder, decoder)
}
