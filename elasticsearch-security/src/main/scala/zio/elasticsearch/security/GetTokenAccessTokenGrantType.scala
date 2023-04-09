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

package zio.elasticsearch.security

import zio.json._

sealed trait GetTokenAccessTokenGrantType

object GetTokenAccessTokenGrantType {

  case object password extends GetTokenAccessTokenGrantType

  case object client_credentials extends GetTokenAccessTokenGrantType

  case object _kerberos extends GetTokenAccessTokenGrantType

  case object refresh_token extends GetTokenAccessTokenGrantType

  implicit final val decoder: JsonDecoder[GetTokenAccessTokenGrantType] =
    DeriveJsonDecoderEnum.gen[GetTokenAccessTokenGrantType]
  implicit final val encoder: JsonEncoder[GetTokenAccessTokenGrantType] =
    DeriveJsonEncoderEnum.gen[GetTokenAccessTokenGrantType]
  implicit final val codec: JsonCodec[GetTokenAccessTokenGrantType] =
    JsonCodec(encoder, decoder)

}
