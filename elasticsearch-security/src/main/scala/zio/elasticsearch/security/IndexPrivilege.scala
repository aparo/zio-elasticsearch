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

sealed trait IndexPrivilege

object IndexPrivilege {

  case object none extends IndexPrivilege

  case object all extends IndexPrivilege

  case object auto_configure extends IndexPrivilege

  case object create extends IndexPrivilege

  case object create_doc extends IndexPrivilege

  case object create_index extends IndexPrivilege

  case object delete extends IndexPrivilege

  case object delete_index extends IndexPrivilege

  case object index extends IndexPrivilege

  case object maintenance extends IndexPrivilege

  case object manage extends IndexPrivilege

  case object manage_follow_index extends IndexPrivilege

  case object manage_ilm extends IndexPrivilege

  case object manage_leader_index extends IndexPrivilege

  case object monitor extends IndexPrivilege

  case object read extends IndexPrivilege

  case object read_cross_cluster extends IndexPrivilege

  case object view_index_metadata extends IndexPrivilege

  case object write extends IndexPrivilege

  implicit final val decoder: JsonDecoder[IndexPrivilege] =
    DeriveJsonDecoderEnum.gen[IndexPrivilege]
  implicit final val encoder: JsonEncoder[IndexPrivilege] =
    DeriveJsonEncoderEnum.gen[IndexPrivilege]
  implicit final val codec: JsonCodec[IndexPrivilege] =
    JsonCodec(encoder, decoder)

}
