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

@jsonDiscriminator("type")
sealed trait Visibility

final case class VisibilityValue(visibility: String) extends Visibility
object VisibilityValue {
  implicit val jsonDecoder: JsonDecoder[VisibilityValue] = DeriveJsonDecoder.gen[VisibilityValue]
  implicit val jsonEncoder: JsonEncoder[VisibilityValue] = DeriveJsonEncoder.gen[VisibilityValue]
}

final case class VisibilityScript(script: String, language: String = "scala") extends Visibility
object VisibilityScript {
  implicit val jsonDecoder: JsonDecoder[VisibilityScript] = DeriveJsonDecoder.gen[VisibilityScript]
  implicit val jsonEncoder: JsonEncoder[VisibilityScript] = DeriveJsonEncoder.gen[VisibilityScript]
}

final case class VisibilityExpression(expression: String) extends Visibility
object VisibilityExpression {
  implicit val jsonDecoder: JsonDecoder[VisibilityExpression] = DeriveJsonDecoder.gen[VisibilityExpression]
  implicit val jsonEncoder: JsonEncoder[VisibilityExpression] = DeriveJsonEncoder.gen[VisibilityExpression]
}

object Visibility {
  implicit final val decodeVisibility: JsonDecoder[Visibility] = DeriveJsonDecoder.gen[Visibility]
  implicit final val encoderVisibility: JsonEncoder[Visibility] = DeriveJsonEncoder.gen[Visibility]
}
