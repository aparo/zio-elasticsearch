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

import zio.json.ast.Json
import zio.json._
import zio.json._
import zio.json._

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
  implicit final val decodeVisibility: JsonDecoder[Visibility] =
    JsonDecoder.instance { c =>
      val fields = c.keys.getOrElse(Nil).toSet
      if (fields.contains("visibility")) {
        c.as[VisibilityValue]
      } else if (fields.contains("script")) {
        c.as[VisibilityScript]
      } else if (fields.contains("expression")) {
        c.as[VisibilityExpression]
      } else {
        //TODO check because this one is impossible
        c.as[VisibilityValue]
      }
    }
  implicit final val encoderVisibility: JsonEncoder[Visibility] = {
    JsonEncoder.instance {
      case o: VisibilityValue      => o.asJson
      case o: VisibilityScript     => o.asJson
      case o: VisibilityExpression => o.asJson
    }
  }
}
