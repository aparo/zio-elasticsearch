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

package zio.schema.elasticsearch.annotations

import zio.json._
@jsonEnumLowerCase
sealed trait IndexTimeInterval extends EnumLowerCase

object IndexTimeInterval {

  case object Day extends IndexTimeInterval

  case object Week extends IndexTimeInterval

  case object Month extends IndexTimeInterval

  case object Quarter extends IndexTimeInterval

  case object Year extends IndexTimeInterval

  implicit val decoder: JsonDecoder[IndexTimeInterval] = DeriveJsonDecoderEnum.gen[IndexTimeInterval]
  implicit val encoder: JsonEncoder[IndexTimeInterval] = DeriveJsonEncoderEnum.gen[IndexTimeInterval]

}
