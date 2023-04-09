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

package zio.elasticsearch.ingest

import zio.json._

sealed trait UserAgentProperty

object UserAgentProperty {

  case object NAME extends UserAgentProperty

  case object MAJOR extends UserAgentProperty

  case object MINOR extends UserAgentProperty

  case object PATCH extends UserAgentProperty

  case object OS extends UserAgentProperty

  case object OS_NAME extends UserAgentProperty

  case object OS_MAJOR extends UserAgentProperty

  case object OS_MINOR extends UserAgentProperty

  case object DEVICE extends UserAgentProperty

  case object BUILD extends UserAgentProperty

  implicit final val decoder: JsonDecoder[UserAgentProperty] =
    DeriveJsonDecoderEnum.gen[UserAgentProperty]
  implicit final val encoder: JsonEncoder[UserAgentProperty] =
    DeriveJsonEncoderEnum.gen[UserAgentProperty]
  implicit final val codec: JsonCodec[UserAgentProperty] =
    JsonCodec(encoder, decoder)

}
