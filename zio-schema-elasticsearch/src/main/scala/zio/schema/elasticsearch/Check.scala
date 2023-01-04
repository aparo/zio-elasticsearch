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

package zio.schema.elasticsearch

import zio.json._

/*
 * This class defines a Check entity
 * @param checkEmail if we need to check if is a email
 */
final case class Check(checkEmail: Boolean = false)
object Check {
  implicit val jsonDecoder: JsonDecoder[Check] = DeriveJsonDecoder.gen[Check]
  implicit val jsonEncoder: JsonEncoder[Check] = DeriveJsonEncoder.gen[Check]
}
