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

sealed trait GetRoleTemplateFormat

object GetRoleTemplateFormat {

  case object string extends GetRoleTemplateFormat

  case object json extends GetRoleTemplateFormat

  implicit final val decoder: JsonDecoder[GetRoleTemplateFormat] =
    DeriveJsonDecoderEnum.gen[GetRoleTemplateFormat]
  implicit final val encoder: JsonEncoder[GetRoleTemplateFormat] =
    DeriveJsonEncoderEnum.gen[GetRoleTemplateFormat]
  implicit final val codec: JsonCodec[GetRoleTemplateFormat] =
    JsonCodec(encoder, decoder)

}
