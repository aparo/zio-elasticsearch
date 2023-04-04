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

sealed trait LifecycleOperationMode

object LifecycleOperationMode {

  case object RUNNING extends LifecycleOperationMode

  case object STOPPING extends LifecycleOperationMode

  case object STOPPED extends LifecycleOperationMode

  implicit final val decoder: JsonDecoder[LifecycleOperationMode] =
    DeriveJsonDecoderEnum.gen[LifecycleOperationMode]
  implicit final val encoder: JsonEncoder[LifecycleOperationMode] =
    DeriveJsonEncoderEnum.gen[LifecycleOperationMode]
  implicit final val codec: JsonCodec[LifecycleOperationMode] =
    JsonCodec(encoder, decoder)

}
