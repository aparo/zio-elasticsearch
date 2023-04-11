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

package zio.schema.elasticsearch

import zio.json._

@jsonEnumLowerCase sealed trait EsType extends EnumLowerCase

object EsType {
  implicit final val decoder: JsonDecoder[EsType] =
    DeriveJsonDecoderEnum.gen[EsType]
  implicit final val encoder: JsonEncoder[EsType] =
    DeriveJsonEncoderEnum.gen[EsType]
  implicit final val codec: JsonCodec[EsType] = JsonCodec(encoder, decoder)

  case object none extends EsType

  case object geo_point extends EsType
  case object geo_shape extends EsType
  case object ip extends EsType

  case object binary extends EsType

  case object keyword extends EsType

  case object text extends EsType

  case object search_as_you_type extends EsType

  case object date extends EsType

  case object date_nanos extends EsType

  case object boolean extends EsType

  case object completion extends EsType

  case object nested extends EsType

  case object `object` extends EsType

  case object murmur3 extends EsType

  case object token_count extends EsType

  case object percolator extends EsType

  case object integer extends EsType

  case object long extends EsType

  case object short extends EsType

  case object byte extends EsType

  case object float extends EsType

  case object half_float extends EsType

  case object scaled_float extends EsType

  case object double extends EsType

  case object integer_range extends EsType

  case object float_range extends EsType

  case object long_range extends EsType

  case object double_range extends EsType

  case object date_range extends EsType

  case object ip_range extends EsType

  case object alias extends EsType

  case object join extends EsType

  case object rank_feature extends EsType

  case object rank_features extends EsType

  case object flattened extends EsType

  case object shape extends EsType

  case object histogram extends EsType

  case object constant_keyword extends EsType

  case object aggregate_metric_double extends EsType

  case object dense_vector extends EsType

  case object match_only_text extends EsType
}
