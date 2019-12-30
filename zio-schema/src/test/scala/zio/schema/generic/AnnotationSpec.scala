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

package zio.schema.generic

import java.time.OffsetDateTime

import io.circe._
import zio.schema.annotations._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._

object AnnotationSpec {

  @ColumnarStorage
  @ColumnVisibility("admin") case class ColumnarFoo(
    @ColumnFamily("cf") @ColumnQualifier("cq") @ColumnVisibility(
      "admin|foo-admin"
    ) x: Int,
    y: String,
    z: Option[String]
  )

  @ColumnarStorage
  case class TypedFoo(
    @Email email: String,
    @Ip ip: String,
    @Password password: String,
    @UserId user: String,
    @Vertex vertex: String
  )

  @ColumnarStorage
  case class SpecialFieldFoo(
    @Unique unique: String,
    @Created created: OffsetDateTime,
    @Modified modified: OffsetDateTime
  )

  @ElasticSearchStorage
  case class IndexFieldFoo(
    @Keyword @Text @NLP @Stem("it") @Suggest unique: String,
    @HeatMap @Created created: OffsetDateTime
  )

  @NoIndex
  @NoColumnar
  @IgniteStorage
  case class NoColumnarNoIndex(name: String)

  case class MyClass(name: String)

  case class EmbeddedFieldFoo(
    @Nested nested: MyClass,
    @Embedded embedded: MyClass,
    default: MyClass,
    lists: List[MyClass]
  )

  @SchemaId("myID")
  @Version(2)
  @Description("My Description")
  @Label("Global Annotation Foo")
  @SingleStorage("myStorage")
  @IgniteStorage
  case class GlobalAnnotationFoo(
    @Description("Name of the Annotation") @Label("Name") name: String
  )

}

class AnnotationSpec extends FreeSpec {

  import AnnotationSpec._

  implicit val myclass = JsonSchema.deriveFor[MyClass]

  val fooSchema = JsonSchema.deriveFor[ColumnarFoo]
  val typedSchema = JsonSchema.deriveFor[TypedFoo]
  val specialFieldSchema = JsonSchema.deriveFor[SpecialFieldFoo]
  val indexFieldSchema = JsonSchema.deriveFor[IndexFieldFoo]
  val nestingFieldSchema = JsonSchema.deriveFor[EmbeddedFieldFoo]
  val globalAnnotationSchema = JsonSchema.deriveFor[GlobalAnnotationFoo]
  val noColumnarNoIndex = JsonSchema.deriveFor[NoColumnarNoIndex]

  "annotation derivation" - {
    "handles columnar annotations" in {

      parser.parse(
        """
        |{
        |  "type" : "object",
        |  "name" : "columnar_foo",
        |  "module" : "schema",
        |  "class_name" : "zio.schema.generic.AnnotationSpec.ColumnarFoo",
        |  "is_root" : true,
        |  "properties" : [
        |    {
        |      "type" : "integer",
        |      "format" : "int32",
        |      "columnar" : {
        |        "visibility" : [
        |          {
        |            "visibility" : "admin|foo-admin"
        |          },
        |          {
        |            "visibility" : "admin"
        |          }
        |        ],
        |        "qualifier" : "cq",
        |        "family" : "cf"
        |      },
        |      "name" : "x"
        |    },
        |    {
        |      "type" : "string",
        |      "columnar" : {
        |        "visibility" : [
        |          {
        |            "visibility" : "admin"
        |          }
        |        ]
        |      },
        |      "name" : "y"
        |    },
        |    {
        |      "type" : "string",
        |      "required" : false,
        |      "multiple" : false,
        |      "columnar" : {
        |        "visibility" : [
        |          {
        |            "visibility" : "admin"
        |          }
        |        ]
        |      },
        |      "name" : "z"
        |    }
        |  ],
        |  "columnar" : {
        |    "visibility" : [
        |      {
        |        "visibility" : "admin"
        |      }
        |    ]
        |  },
        |  "storages" : [
        |    "columnar"
        |  ],
        |  "id" : "zio.schema.generic.AnnotationSpec.ColumnarFoo"
        |}
        """.stripMargin
      ) should ===(Right(fooSchema.asJson))

    }

    "handles typed annotations" in {
      parser.parse(
        """
                     |{
                     |  "type" : "object",
                     |  "name" : "typed_foo",
                     |  "module" : "schema",
                     |  "class_name" : "zio.schema.generic.AnnotationSpec.TypedFoo",
                     |  "is_root" : true,
                     |  "properties" : [
                     |    {
                     |      "type" : "string",
                     |      "sub_type" : "ip",
                     |      "name" : "ip"
                     |    },
                     |    {
                     |      "type" : "string",
                     |      "sub_type" : "vertex",
                     |      "name" : "vertex"
                     |    },
                     |    {
                     |      "type" : "string",
                     |      "sub_type" : "email",
                     |      "name" : "email"
                     |    },
                     |    {
                     |      "type" : "string",
                     |      "sub_type" : "userid",
                     |      "name" : "user"
                     |    },
                     |    {
                     |      "type" : "string",
                     |      "sub_type" : "password",
                     |      "name" : "password"
                     |    }
                     |  ],
                     |  "storages" : [
                     |    "columnar"
                     |  ],
                     |  "id" : "zio.schema.generic.AnnotationSpec.TypedFoo"
                     |}
        """.stripMargin
      ) should ===(Right(typedSchema.asJson))
    }

    "handles special field annotations" in {
      parser.parse(
        """
                     |{
                     |  "type" : "object",
                     |  "name" : "special_field_foo",
                     |  "module" : "schema",
                     |  "class_name" : "zio.schema.generic.AnnotationSpec.SpecialFieldFoo",
                     |  "is_root" : true,
                     |  "properties" : [
                     |    {
                     |      "type" : "string",
                     |      "unique" : true,
                     |      "name" : "unique"
                     |    },
                     |    {
                     |      "type" : "string",
                     |      "format" : "date-time",
                     |      "format_options" : "offset",
                     |      "created" : true,
                     |      "name" : "created"
                     |    },
                     |    {
                     |      "type" : "string",
                     |      "format" : "date-time",
                     |      "format_options" : "offset",
                     |      "modified" : true,
                     |      "name" : "modified"
                     |    }
                     |  ],
                     |  "storages" : [
                     |    "columnar"
                     |  ],
                     |  "id" : "zio.schema.generic.AnnotationSpec.SpecialFieldFoo"
                     |}
        """.stripMargin
      ) should ===(Right(specialFieldSchema.asJson))
    }

    "handles index field annotations" in {
      parser.parse(
        """
                     |{
                     |  "type" : "object",
                     |  "name" : "index_field_foo",
                     |  "module" : "schema",
                     |  "class_name" : "zio.schema.generic.AnnotationSpec.IndexFieldFoo",
                     |  "is_root" : true,
                     |  "properties" : [
                     |    {
                     |      "type" : "string",
                     |      "index" : {
                     |        "analyzers" : [
                     |          "suggest",
                     |          "stem|it",
                     |          "nlp",
                     |          "text",
                     |          "keyword"
                     |        ]
                     |      },
                     |      "name" : "unique"
                     |    },
                     |    {
                     |      "type" : "string",
                     |      "format" : "date-time",
                     |      "format_options" : "offset",
                     |      "index" : {
                     |        "analyzers" : [
                     |          "heatmap"
                     |        ]
                     |      },
                     |      "created" : true,
                     |      "name" : "created"
                     |    }
                     |  ],
                     |  "storages" : [
                     |    "elasticsearch"
                     |  ],
                     |  "id" : "zio.schema.generic.AnnotationSpec.IndexFieldFoo"
                     |}
        """.stripMargin
      ) should ===(Right(indexFieldSchema.asJson))
    }

    "handles nesting field annotations" in {
      parser.parse(
        """
          |{
          |  "type" : "object",
          |  "name" : "embedded_field_foo",
          |  "module" : "schema",
          |  "class_name" : "zio.schema.generic.AnnotationSpec.EmbeddedFieldFoo",
          |  "is_root" : true,
          |  "properties" : [
          |    {
          |      "type" : "object",
          |      "name" : "nested",
          |      "module" : "schema",
          |      "class_name" : "zio.schema.generic.AnnotationSpec.MyClass",
          |      "is_root" : true,
          |      "properties" : [
          |        {
          |          "type" : "string",
          |          "name" : "name"
          |        }
          |      ],
          |      "id" : "zio.schema.generic.AnnotationSpec.MyClass",
          |      "index" : {
          |        "nesting" : "nested"
          |      }
          |    },
          |    {
          |      "type" : "object",
          |      "name" : "embedded",
          |      "module" : "schema",
          |      "class_name" : "zio.schema.generic.AnnotationSpec.MyClass",
          |      "is_root" : true,
          |      "properties" : [
          |        {
          |          "type" : "string",
          |          "name" : "name"
          |        }
          |      ],
          |      "id" : "zio.schema.generic.AnnotationSpec.MyClass",
          |      "index" : {
          |        "nesting" : "embedded"
          |      }
          |    },
          |    {
          |      "type" : "object",
          |      "name" : "default",
          |      "module" : "schema",
          |      "class_name" : "zio.schema.generic.AnnotationSpec.MyClass",
          |      "is_root" : true,
          |      "properties" : [
          |        {
          |          "type" : "string",
          |          "name" : "name"
          |        }
          |      ],
          |      "id" : "zio.schema.generic.AnnotationSpec.MyClass"
          |    },
          |    {
          |      "format" : "list",
          |      "items" : {
          |        "type" : "object",
          |        "name" : "lists",
          |        "module" : "schema",
          |        "class_name" : "zio.schema.generic.AnnotationSpec.MyClass",
          |        "is_root" : true,
          |        "properties" : [
          |          {
          |            "type" : "string",
          |            "name" : "name"
          |          }
          |        ],
          |        "id" : "zio.schema.generic.AnnotationSpec.MyClass"
          |      },
          |      "multiple" : true,
          |      "type" : "array",
          |      "required" : false,
          |      "name" : "lists"
          |    }
          |  ],
          |  "id" : "zio.schema.generic.AnnotationSpec.EmbeddedFieldFoo"
          |}
        """.stripMargin
      ) should ===(Right(nestingFieldSchema.asJson))
    }

    "handles global class annotations" in {
      parser.parse(
        """
                     |{
                     |  "type" : "object",
                     |  "name" : "global_annotation_foo",
                     |  "module" : "schema",
                     |  "class_name" : "zio.schema.generic.AnnotationSpec.GlobalAnnotationFoo",
                     |  "is_root" : true,
                     |  "properties" : [
                     |    {
                     |      "type" : "string",
                     |      "label" : "Name",
                     |      "description" : "Name of the Annotation",
                     |      "name" : "name"
                     |    }
                     |  ],
                     |  "columnar" : {
                     |    "single_storage" : "myStorage"
                     |  },
                     |  "storages" : [
                     |    "ignite"
                     |  ],
                     |  "label" : "Global Annotation Foo",
                     |  "description" : "My Description",
                     |  "id" : "myID",
                     |  "version" : 2
                     |}
                   """.stripMargin
      ) should ===(Right(globalAnnotationSchema.asJson))

      globalAnnotationSchema.id should ===("myID")
      globalAnnotationSchema.asSchema.isSingleStorage should ===(true)
    }

    "handles no columnar and no index class annotations" in {
      parser.parse(
        """
                     |{
                     |   "type" : "object",
                     |   "name" : "no_columnar_no_index",
                     |   "module" : "schema",
                     |   "class_name" : "zio.schema.generic.AnnotationSpec.NoColumnarNoIndex",
                     |   "is_root" : true,
                     |   "properties" : [
                     |     {
                     |       "type" : "string",
                     |       "name" : "name"
                     |     }
                     |   ],
                     |   "index" : {
                     |     "active" : false
                     |   },
                     |   "columnar" : {
                     |     "active" : false
                     |   },
                     |   "storages" : [
                     |     "ignite"
                     |   ],
                     |   "id" : "zio.schema.generic.AnnotationSpec.NoColumnarNoIndex"
                     | }
                   """.stripMargin
      ) should ===(Right(noColumnarNoIndex.asJson))

      noColumnarNoIndex.id should ===(
        "zio.schema.generic.AnnotationSpec.NoColumnarNoIndex"
      )
      val schema = noColumnarNoIndex.asSchema
      schema.index.active should ===(false)
      schema.columnar.active should ===(false)
    }

  }
}
