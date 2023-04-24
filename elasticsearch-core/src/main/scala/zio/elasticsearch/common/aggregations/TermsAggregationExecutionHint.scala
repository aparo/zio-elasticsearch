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

package zio.elasticsearch.common.aggregations

import zio.json._

sealed trait TermsAggregationExecutionHint

object TermsAggregationExecutionHint {

  case object map extends TermsAggregationExecutionHint

  case object global_ordinals extends TermsAggregationExecutionHint

  case object global_ordinals_hash extends TermsAggregationExecutionHint

  case object global_ordinals_low_cardinality extends TermsAggregationExecutionHint

  implicit final val decoder: JsonDecoder[TermsAggregationExecutionHint] =
    DeriveJsonDecoderEnum.gen[TermsAggregationExecutionHint]
  implicit final val encoder: JsonEncoder[TermsAggregationExecutionHint] =
    DeriveJsonEncoderEnum.gen[TermsAggregationExecutionHint]
  implicit final val codec: JsonCodec[TermsAggregationExecutionHint] =
    JsonCodec(encoder, decoder)

}
