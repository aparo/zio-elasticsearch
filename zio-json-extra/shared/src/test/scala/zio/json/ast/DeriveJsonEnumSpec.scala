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

package zio.json.ast

import zio.Scope
import zio.test.Assertion._
import zio.test._
import zio.json._
import zio.test.ZIOSpecDefault
object DeriveJsonEnumSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("enum encoder")(
      test("encode simple enum value") {
        import exampleenums._

        assert(Running.asInstanceOf[Status].toJson)(equalTo(""""Running""""))
      },
      test("encode case class with enum value") {
        import exampleenums._

        assert(Entity(name = "Sample", status = Failure).toJson)(
          equalTo("""{"name":"Sample","status":"Failure"}""")
        )
      },
      test("encode simple enum lower value") {
        import examplelowerenums._

        assert(Running.asInstanceOf[Status].toJson)(equalTo(""""running""""))
      },
      test("encode case class with enum lower value") {
        import examplelowerenums._

        assert(Entity(name = "Sample", status = Failure).toJson)(
          equalTo("""{"name":"Sample","status":"failure"}""")
        )
      },
      test("encode simple enum upper value") {
        import exampleupperenums._

        assert(Running.asInstanceOf[Status].toJson)(equalTo(""""RUNNING""""))
      },
      test("encode case class with enum upper value") {
        import exampleupperenums._

        assert(Entity(name = "Sample", status = Failure).toJson)(
          equalTo("""{"name":"Sample","status":"FAILURE"}""")
        )
      }
    ) +
      suite("enum decoder")(
        test("simple enum value") {
          import exampleenums._

          assert(""""Running"""".fromJson[Status])(isRight(equalTo(Running)))
        },
        test("case class with enum value") {
          import exampleenums._

          assert("""{"name":"Sample","status":"Failure"}""".fromJson[Entity])(
            isRight(equalTo(Entity(name = "Sample", status = Failure)))
          )
        },
        test("simple enum lower value") {
          import examplelowerenums._

          assert(""""running"""".fromJson[Status])(isRight(equalTo(Running)))
        },
        test("case class with enum lower value") {
          import examplelowerenums._

          assert("""{"name":"Sample","status":"failure"}""".fromJson[Entity])(
            isRight(equalTo(Entity(name = "Sample", status = Failure)))
          )
        },
        test("simple enum upper value") {
          import exampleupperenums._

          assert(""""RUNNING"""".fromJson[Status])(isRight(equalTo(Running)))
        },
        test("case class with enum upper value") {
          import exampleupperenums._

          assert("""{"name":"Sample","status":"FAILURE"}""".fromJson[Entity])(
            isRight(equalTo(Entity(name = "Sample", status = Failure)))
          )
        }
      )
}

object exampleenums {

  sealed trait Status
  case object Running extends Status
  case object Failure extends Status

  case class Entity(name: String, status: Status)

  implicit val statusDecoder: JsonDecoder[Status] = DeriveJsonDecoderEnum.gen[Status]
  implicit val statusEncoder: JsonEncoder[Status] = DeriveJsonEncoderEnum.gen[Status]
  implicit val entityDecoder: JsonDecoder[Entity] = DeriveJsonDecoder.gen[Entity]
  implicit val entityEncoder: JsonEncoder[Entity] = DeriveJsonEncoder.gen[Entity]

}

object examplelowerenums {

  @jsonEnumLowerCase
  sealed trait Status extends EnumLowerCase
  case object Running extends Status
  case object Failure extends Status

  case class Entity(name: String, status: Status)

  implicit val statusDecoder: JsonDecoder[Status] = DeriveJsonDecoderEnum.gen[Status]
  implicit val statusEncoder: JsonEncoder[Status] = DeriveJsonEncoderEnum.gen[Status]
  implicit val entityDecoder: JsonDecoder[Entity] = DeriveJsonDecoder.gen[Entity]
  implicit val entityEncoder: JsonEncoder[Entity] = DeriveJsonEncoder.gen[Entity]

}

object exampleupperenums {

  @jsonEnumUpperCase
  sealed trait Status extends EnumUpperCase
  case object Running extends Status
  case object Failure extends Status

  case class Entity(name: String, status: Status)

  implicit val statusDecoder: JsonDecoder[Status] = DeriveJsonDecoderEnum.gen[Status]
  implicit val statusEncoder: JsonEncoder[Status] = DeriveJsonEncoderEnum.gen[Status]
  implicit val entityDecoder: JsonDecoder[Entity] = DeriveJsonDecoder.gen[Entity]
  implicit val entityEncoder: JsonEncoder[Entity] = DeriveJsonEncoder.gen[Entity]

}
