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

sealed trait FieldType

object FieldType {

  case object none extends FieldType

  case object geo_point extends FieldType

  case object geo_shape extends FieldType

  case object ip extends FieldType

  case object binary extends FieldType

  case object keyword extends FieldType

  case object text extends FieldType

  case object search_as_you_type extends FieldType

  case object date extends FieldType

  case object date_nanos extends FieldType

  case object boolean extends FieldType

  case object completion extends FieldType

  case object nested extends FieldType

  case object `object` extends FieldType

  case object murmur3 extends FieldType

  case object token_count extends FieldType

  case object percolator extends FieldType

  case object integer extends FieldType

  case object long extends FieldType

  case object short extends FieldType

  case object byte extends FieldType

  case object float extends FieldType

  case object half_float extends FieldType

  case object scaled_float extends FieldType

  case object double extends FieldType

  case object integer_range extends FieldType

  case object float_range extends FieldType

  case object long_range extends FieldType

  case object double_range extends FieldType

  case object date_range extends FieldType

  case object ip_range extends FieldType

  case object alias extends FieldType

  case object join extends FieldType

  case object rank_feature extends FieldType

  case object rank_features extends FieldType

  case object flattened extends FieldType

  case object shape extends FieldType

  case object histogram extends FieldType

  case object constant_keyword extends FieldType

  case object aggregate_metric_double extends FieldType

  case object dense_vector extends FieldType

  case object match_only_text extends FieldType

  implicit final val decoder: JsonDecoder[FieldType] =
    DeriveJsonDecoderEnum.gen[FieldType]
  implicit final val encoder: JsonEncoder[FieldType] =
    DeriveJsonEncoderEnum.gen[FieldType]
  implicit final val codec: JsonCodec[FieldType] = JsonCodec(encoder, decoder)

}
