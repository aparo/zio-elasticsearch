/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.mappings

import zio.json._
@jsonEnumLowerCase
sealed trait Similarity extends EnumLowerCase

object Similarity {
  implicit final val decoder: JsonDecoder[Similarity] =
    DeriveJsonDecoderEnum.gen[Similarity]
  implicit final val encoder: JsonEncoder[Similarity] =
    DeriveJsonEncoderEnum.gen[Similarity]
  implicit final val codec: JsonCodec[Similarity] = JsonCodec(encoder, decoder)
  case object Classic extends Similarity

  case object BM25 extends Similarity

}
