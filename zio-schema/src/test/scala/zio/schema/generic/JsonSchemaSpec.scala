/*
 * Copyright 2019-2020 Alberto Paro
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

/**
package zio.schema.generic

import java.time._

import zio.schema.Schema
import enumeratum._
import io.circe._
import io.circe.derivation.annotations.JsonKey
import org.scalatest.FreeSpec
import org.scalatest.Matchers._

import scala.reflect.runtime.{universe => u}

object JsonSchemaSpec {
  val ref: String = "$ref"

  def id[T: u.WeakTypeTag] =
    getClass.getCanonicalName.replace('$', '.') +
      implicitly[u.WeakTypeTag[T]].tpe.typeSymbol.name

  sealed abstract class E extends EnumEntry

  case class AllTypes(
    x: Int,
    y: String,
    long: Long,
    short: Short,
    bigint: BigInt,
    double: Double,
    float: Float,
    byte: Byte,
    boo: Boolean,
    datetimeOffset: OffsetDateTime,
    localDate: LocalDate,
    localDatetime: LocalDateTime,
    z: Option[String] = None
  )

  case class AllMultipleTypes(
    ints: List[Int] = Nil,
    sets: Set[String] = Set.empty[String],
    seqs: Seq[String] = Nil,
    vectors: Vector[String] = Vector.empty[String]
  )

  case class Foo(@JsonKey("x1") x: Int, y: String, z: Option[String]) {
    val otherVal = "not in schema"
  }

  case class Nested(name: String, foo: Foo)

  case class NestedOpt(name: String, fooOpt: Option[Foo])

  case class X(e: E, f: F)

  object E extends Enum[E] with EnumSchema[E] {
    override val values = findValues

    case object E1 extends E

    case object E2 extends E

  }

  sealed trait F extends EnumEntry

  object F extends Enum[F] with EnumSchema[F] {
    override val values = findValues

    case object F1 extends F

    case object F2 extends F

  }

  sealed trait TheEnum

  case object Enum1 extends TheEnum

  case object Enum2 extends TheEnum

  sealed trait TheADT

  case class A(a: Int, b: Char, c: Option[Double]) extends TheADT

  case class B(d: Symbol, e: Long) extends TheADT

  case class C(foo: Foo, g: Long) extends TheADT

  case class D(enum: TheEnum) extends TheADT

}

class JsonSchemaSpec extends FreeSpec {

  import JsonSchemaSpec._

  val fooSchema = JsonSchema.deriveFor[Foo]
  val allTypeSchema = JsonSchema.deriveFor[AllTypes]

  val allMultipleTypeSchema = JsonSchema.deriveFor[AllMultipleTypes]

  "automatic derivation" - {
    "handles plain case classes" in {
      parser.parse(
        """
          |{
          |  "type" : "object",
          |  "name" : "foo",
          |  "module" : "schema",
          |  "class_name" : "zio.schema.generic.JsonSchemaSpec.Foo",
          |  "is_root" : true,
          |  "properties" : [
          |    {
          |      "type" : "integer",
          |      "format" : "int32",
          |      "class_name" : "x",
          |      "name" : "x1"
          |    },
          |    {
          |      "type" : "string",
          |      "name" : "y"
          |    },
          |    {
          |      "type" : "string",
          |      "required" : false,
          |      "multiple" : false,
          |      "name" : "z"
          |    }
          |  ],
          |  "id" : "zio.schema.generic.JsonSchemaSpec.Foo"
          |}
        """.stripMargin
      ) should ===(Right(fooSchema.asJson))
      fooSchema.id should ===(id[Foo])
      fooSchema.asSchema === Schema(
        name = "foo",
        module = "schema",
        className = Some("zio.schema.generic.JsonSchemaSpec.Foo")
      )
      fooSchema.asSchema.isRoot should ===(true)
    }

    "handles all types" in {
      implicit val fs: JsonSchema[AllTypes] = allTypeSchema

      val schema = JsonSchema.deriveFor[AllTypes]
      parser.parse(s"""
           |{
           |  "type" : "object",
           |  "name" : "all_types",
           |  "module" : "schema",
           |  "class_name" : "zio.schema.generic.JsonSchemaSpec.AllTypes",
           |  "is_root" : true,
           |  "properties" : [
           |    {
           |      "type" : "integer",
           |      "format" : "int32",
           |      "name" : "x"
           |    },
           |    {
           |      "type" : "number",
           |      "format" : "float",
           |      "name" : "float"
           |    },
           |    {
           |      "type" : "string",
           |      "name" : "y"
           |    },
           |    {
           |      "type" : "string",
           |      "format" : "date-time",
           |      "format_options" : "offset",
           |      "name" : "datetimeOffset"
           |    },
           |    {
           |      "type" : "integer",
           |      "format" : "int16",
           |      "name" : "short"
           |    },
           |    {
           |      "type" : "boolean",
           |      "name" : "boo"
           |    },
           |    {
           |      "type" : "number",
           |      "format" : "double",
           |      "name" : "double"
           |    },
           |    {
           |      "type" : "integer",
           |      "format" : "int64",
           |      "name" : "long"
           |    },
           |    {
           |      "type" : "string",
           |      "format" : "date-time",
           |      "name" : "localDatetime"
           |    },
           |    {
           |      "type" : "integer",
           |      "format" : "big",
           |      "name" : "bigint"
           |    },
           |    {
           |      "type" : "string",
           |      "format" : "byte",
           |      "name" : "byte"
           |    },
           |    {
           |      "type" : "string",
           |      "required" : false,
           |      "multiple" : false,
           |      "name" : "z"
           |    },
           |    {
           |      "type" : "string",
           |      "format" : "date",
           |      "name" : "localDate"
           |    }
           |  ],
           |  "id" : "zio.schema.generic.JsonSchemaSpec.AllTypes"
           |}
              """.stripMargin) should ===(Right(schema.asJson))

      schema.id should ===(id[AllTypes])
      schema.asSchema.isRoot should ===(true)
    }

    "handles all Multiple types" in {

      implicit val fs: JsonSchema[AllMultipleTypes] = allMultipleTypeSchema

      val schema = JsonSchema.deriveFor[AllMultipleTypes]
      parser.parse(s"""
           |{
           |  "type" : "object",
           |  "name" : "all_multiple_types",
           |  "module" : "schema",
           |  "class_name" : "zio.schema.generic.JsonSchemaSpec.AllMultipleTypes",
           |  "is_root" : true,
           |  "properties" : [
           |    {
           |      "format" : "list",
           |      "items" : {
           |        "type" : "integer",
           |        "format" : "int32",
           |        "name" : "ints"
           |      },
           |      "multiple" : true,
           |      "type" : "array",
           |      "required" : false,
           |      "default" : [
           |      ],
           |      "name" : "ints"
           |    },
           |    {
           |      "format" : "set",
           |      "items" : {
           |        "type" : "string",
           |        "name" : "sets"
           |      },
           |      "multiple" : true,
           |      "type" : "array",
           |      "required" : false,
           |      "default" : [
           |      ],
           |      "name" : "sets"
           |    },
           |    {
           |      "format" : "seq",
           |      "items" : {
           |        "type" : "string",
           |        "name" : "seqs"
           |      },
           |      "multiple" : true,
           |      "type" : "array",
           |      "required" : false,
           |      "default" : [
           |      ],
           |      "name" : "seqs"
           |    },
           |    {
           |      "format" : "vector",
           |      "items" : {
           |        "type" : "string",
           |        "name" : "vectors"
           |      },
           |      "multiple" : true,
           |      "type" : "array",
           |      "required" : false,
           |      "default" : [
           |      ],
           |      "name" : "vectors"
           |    }
           |  ],
           |  "id" : "zio.schema.generic.JsonSchemaSpec.AllMultipleTypes"
           |}
              """.stripMargin) should ===(Right(schema.asJson))

      schema.id should ===(id[AllMultipleTypes])
      schema.asSchema.isRoot should ===(true)
    }

    "handles non primitive types" in {
      implicit val fs: JsonSchema[Foo] = fooSchema

      val schema = JsonSchema.deriveFor[Nested]
      parser.parse(s"""
           |{
           |  "id" : "zio.schema.generic.JsonSchemaSpec.Nested",
           |  "is_root" : true,
           |  "module": "schema",
           |  "name": "nested",
           |  "type": "object",
           |  "class_name" : "zio.schema.generic.JsonSchemaSpec.Nested",
           |  "properties" : [
           |    {
           |      "type" : "string",
           |      "name" : "name"
           |    },
           |    {
           |      "type" : "ref",
           |      "$ref" : "#/definitions/${id[Foo]}",
           |      "name" : "foo"
           |    }
           |  ]
           |}
           |
              """.stripMargin) should ===(Right(schema.asJsonWithRef))

      parser.parse(s"""
           |{
           |  "type" : "object",
           |  "name" : "nested",
           |  "module" : "schema",
           |  "class_name" : "zio.schema.generic.JsonSchemaSpec.Nested",
           |  "is_root" : true,
           |  "properties" : [
           |    {
           |      "type" : "string",
           |      "name" : "name"
           |    },
           |    {
           |      "type" : "object",
           |      "name" : "foo",
           |      "module" : "schema",
           |      "class_name" : "zio.schema.generic.JsonSchemaSpec.Foo",
           |      "is_root" : true,
           |      "properties" : [
           |        {
           |          "type" : "integer",
           |          "format" : "int32",
           |          "class_name" : "x",
           |          "name" : "x1"
           |        },
           |        {
           |          "type" : "string",
           |          "name" : "y"
           |        },
           |        {
           |          "type" : "string",
           |          "required" : false,
           |          "multiple" : false,
           |          "name" : "z"
           |        }
           |      ],
           |      "id" : "zio.schema.generic.JsonSchemaSpec.Foo"
           |    }
           |  ],
           |  "id" : "zio.schema.generic.JsonSchemaSpec.Nested"
           |}
              """.stripMargin) should ===(Right(schema.asJson))

      schema.id should ===(id[Nested])
      schema.asSchema.isRoot should ===(true)
      //      schema.relatedDefinitions should ===(Set(fs.NamedDefinition("foo")))
      //      SchemaManager.getSchema(schema.id).isDefined should ===(true)
      //      schema.relatedDefinitions.foreach{
      //        d =>
      //          SchemaManager.getSchema(d.id).isDefined should ===(true)
      //      }
    }

    "handles non-primitive types as options" in {
      implicit val fs: JsonSchema[Foo] = fooSchema

      val schema = JsonSchema.deriveFor[NestedOpt]
      parser.parse(s"""
                      |{
                      |  "type" : "object",
                      |  "name" : "nested_opt",
                      |  "module" : "schema",
                      |  "class_name" : "zio.schema.generic.JsonSchemaSpec.NestedOpt",
                      |  "is_root" : true,
                      |  "properties" : [
                      |    {
                      |      "type" : "string",
                      |      "name" : "name"
                      |    },
                      |    {
                      |      "type" : "object",
                      |      "name" : "fooOpt",
                      |      "module" : "schema",
                      |      "class_name" : "zio.schema.generic.JsonSchemaSpec.Foo",
                      |      "is_root" : true,
                      |      "properties" : [
                      |        {
                      |          "type" : "integer",
                      |          "format" : "int32",
                      |          "class_name" : "x",
                      |          "name" : "x1"
                      |        },
                      |        {
                      |          "type" : "string",
                      |          "name" : "y"
                      |        },
                      |        {
                      |          "type" : "string",
                      |          "required" : false,
                      |          "multiple" : false,
                      |          "name" : "z"
                      |        }
                      |      ],
                      |      "required" : false,
                      |      "multiple" : false,
                      |      "id" : "zio.schema.generic.JsonSchemaSpec.Foo"
                      |    }
                      |  ],
                      |  "id" : "zio.schema.generic.JsonSchemaSpec.NestedOpt"
                      |}
              """.stripMargin) should ===(Right(schema.asJson))

      schema.id should ===(id[NestedOpt])
      schema.asSchema.isRoot should ===(true)
      //          schema.relatedDefinitions should ===(Set(fs.NamedDefinition("fooOpt")))
    }

    "with types extending enumeratum.EnumEntry" - {
//      "does not derive automatically" in {
//        """
//              sealed trait WontCompile extends EnumEntry
//              object WontCompile extends Enum[WontCompile] {
//                case object A extends WontCompile
//                case object B extends WontCompile
//                override def values = findValues
//
//                val schema = JsonSchema.deriveFor[WontCompile]
//              }
//
//            """.stripMargin shouldNot typeCheck
//      }
      "derives an enum when the EnumSchema[T] trait is extended" in {
        val schema = JsonSchema.deriveFor[X]

        parser.parse(
          """
            |{
            |  "type" : "object",
            |  "name" : "x",
            |  "module" : "schema",
            |  "class_name" : "zio.schema.generic.JsonSchemaSpec.X",
            |  "is_root" : true,
            |  "properties" : [
            |    {
            |      "type" : "string",
            |      "enum" : [
            |        "E1",
            |        "E2"
            |      ],
            |      "name" : "e"
            |    },
            |    {
            |      "type" : "string",
            |      "enum" : [
            |        "F1",
            |        "F2"
            |      ],
            |      "name" : "f"
            |    }
            |  ],
            |  "id" : "zio.schema.generic.JsonSchemaSpec.X"
            |}
              """.stripMargin
        ) should ===(Right(schema.asJson))
        schema.asSchema.isRoot should ===(true)
      }
    }
//    "with sealed traits of case objects" - {
//      "generates an enumerable" in {
//        val schema = JsonSchema.deriveEnum[TheEnum]
//
//        schema.id should ===(id[TheEnum])
//        parser.parse(
//          """
//            |{
//            |  "enum" : ["Enum1", "Enum2"]
//            |}
//          """.stripMargin) should ===(Right(schema.asJson))
//      }
//    }
//
//    "with ADTs" - {
//      "generates a schema using the allOf keyword" in {
//        val schema = JsonSchema.deriveFor[TheADT]
//        parser.parse(
//          s"""
//              {
//                "type" : "object",
//                "allOf" : [
//                  {
//                    "$ref": "#/definitions/${id[A]}"
//                  },
//                  {
//                    "$ref": "#/definitions/${id[B]}"
//                  },
//                  {
//                    "$ref": "#/definitions/${id[C]}"
//                  },
//                  {
//                    "$ref": "#/definitions/${id[D]}"
//                  }
//                ]
//              }
//            """.stripMargin) should ===(Right(schema.asJson))
//      }
//      "generates a schema using the oneOf keyword" in {
//        implicit val conf = Config(Combinator.OneOf)
//        val schema = JsonSchema.deriveFor[TheADT]
//        parser.parse(
//          s"""
//              {
//                "type" : "object",
//                "oneOf" : [
//                  {
//                    "$ref": "#/definitions/${id[A]}"
//                  },
//                  {
//                    "$ref": "#/definitions/${id[B]}"
//                  },
//                  {
//                    "$ref": "#/definitions/${id[C]}"
//                  },
//                  {
//                    "$ref": "#/definitions/${id[D]}"
//                  }
//                ]
//              }
//            """.stripMargin) should ===(Right(schema.asJson))
//      }
//
//      "provides JSON definitions of the coproduct" in {
//        implicit val fs: JsonSchema[Foo] = fooSchema
//        implicit val theEnumSchema: JsonSchema[TheEnum] = JsonSchema.deriveEnum[TheEnum]
//
//        val schema = JsonSchema.deriveFor[TheADT]
//        val aSchema = JsonSchema.deriveFor[A]
//        val bSchema = JsonSchema.deriveFor[B]
//        val cSchema = JsonSchema.deriveFor[C]
//        val dSchema = JsonSchema.deriveFor[D]
//
//        schema.relatedDefinitions should ===(Set(
//          aSchema.definition,
//          bSchema.definition,
//          cSchema.definition,
//          dSchema.definition))
//      }
//    }
  }
}
 */
