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

package zio.elasticsearch.queries

import zio.json.ast._
import zio.json._

final case class Range(from: Option[Json] = None, to: Option[Json] = None)
object Range {
  implicit val jsonDecoder: JsonDecoder[Range] = DeriveJsonDecoder.gen[Range]
  implicit val jsonEncoder: JsonEncoder[Range] = DeriveJsonEncoder.gen[Range]
}

final case class RangeString(from: Option[String] = None, to: Option[String] = None)
object RangeString {
  implicit val jsonDecoder: JsonDecoder[RangeString] = DeriveJsonDecoder.gen[RangeString]
  implicit val jsonEncoder: JsonEncoder[RangeString] = DeriveJsonEncoder.gen[RangeString]
}
