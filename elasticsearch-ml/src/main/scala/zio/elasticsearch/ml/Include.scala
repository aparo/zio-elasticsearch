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

package zio.elasticsearch.ml

import zio.json._

sealed trait Include

object Include {

  case object definition extends Include

  case object feature_importance_baseline extends Include

  case object hyperparameters extends Include

  case object total_feature_importance extends Include

  implicit final val decoder: JsonDecoder[Include] =
    DeriveJsonDecoderEnum.gen[Include]
  implicit final val encoder: JsonEncoder[Include] =
    DeriveJsonEncoderEnum.gen[Include]
  implicit final val codec: JsonCodec[Include] = JsonCodec(encoder, decoder)

}
