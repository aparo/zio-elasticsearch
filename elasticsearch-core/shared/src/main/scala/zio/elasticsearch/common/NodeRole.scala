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

package zio.elasticsearch.common

import zio.json._

sealed trait NodeRole

object NodeRole {

  case object master extends NodeRole

  case object data extends NodeRole

  case object data_cold extends NodeRole

  case object data_content extends NodeRole

  case object data_frozen extends NodeRole

  case object data_hot extends NodeRole

  case object data_warm extends NodeRole

  case object client extends NodeRole

  case object ingest extends NodeRole

  case object ml extends NodeRole

  case object voting_only extends NodeRole

  case object transform extends NodeRole

  case object remote_cluster_client extends NodeRole

  case object coordinating_only extends NodeRole

  implicit final val decoder: JsonDecoder[NodeRole] =
    DeriveJsonDecoderEnum.gen[NodeRole]
  implicit final val encoder: JsonEncoder[NodeRole] =
    DeriveJsonEncoderEnum.gen[NodeRole]
  implicit final val codec: JsonCodec[NodeRole] = JsonCodec(encoder, decoder)

}
