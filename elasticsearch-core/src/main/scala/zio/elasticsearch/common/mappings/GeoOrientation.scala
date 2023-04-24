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

package zio.elasticsearch.common.mappings

import zio.json._

sealed trait GeoOrientation

object GeoOrientation {

  case object right extends GeoOrientation

  case object RIGHT extends GeoOrientation

  case object counterclockwise extends GeoOrientation

  case object ccw extends GeoOrientation

  case object left extends GeoOrientation

  case object LEFT extends GeoOrientation

  case object clockwise extends GeoOrientation

  case object cw extends GeoOrientation

  implicit final val decoder: JsonDecoder[GeoOrientation] =
    DeriveJsonDecoderEnum.gen[GeoOrientation]
  implicit final val encoder: JsonEncoder[GeoOrientation] =
    DeriveJsonEncoderEnum.gen[GeoOrientation]
  implicit final val codec: JsonCodec[GeoOrientation] =
    JsonCodec(encoder, decoder)

}
