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

package zio.schema.annotations

import io.circe.{ Json, JsonDecoder, JsonEncoder }
import zio.json._
import scala.annotation.StaticAnnotation

// if you add here an annotation update the MappingBuilder getField

final case class SchemaId(id: String) extends StaticAnnotation

final case class Version(version: Int) extends StaticAnnotation

final case class Description(description: String) extends StaticAnnotation

final case class Label(label: String) extends StaticAnnotation

//An autoower class has an user id used to filter by it
final case class AutoOwner() extends StaticAnnotation

sealed trait ColumnarAnnotation

sealed trait GlobalColumnarAnnotation //columanr annotation that works on MainClass

//globals
final case class NoColumnar() extends StaticAnnotation with GlobalColumnarAnnotation with ColumnarAnnotation
final case class ColumnarSingleJson() extends StaticAnnotation with GlobalColumnarAnnotation with ColumnarAnnotation
final case class SingleStorage(name: String = "default")
    extends StaticAnnotation
    with GlobalColumnarAnnotation
    with ColumnarAnnotation

final case class NamespaceName(name: String) extends StaticAnnotation with ColumnarAnnotation

final case class TableName(name: String) extends StaticAnnotation with ColumnarAnnotation

//field
final case class ColumnFamily(name: String) extends StaticAnnotation with ColumnarAnnotation

final case class ColumnQualifier(name: String) extends StaticAnnotation with ColumnarAnnotation

sealed trait ColumnVisibilityAnnotation

@JsonCodec
final case class ColumnVisibility(visibility: String)
    extends StaticAnnotation
    with ColumnarAnnotation
    with ColumnVisibilityAnnotation

@JsonCodec
final case class ColumnVisibilityScript(
  script: String,
  language: String = "scala"
) extends StaticAnnotation
    with ColumnarAnnotation
    with ColumnVisibilityAnnotation

@JsonCodec
final case class ColumnVisibilityExpression(expression: String)
    extends StaticAnnotation
    with ColumnarAnnotation
    with ColumnVisibilityAnnotation

object Visibility {

  //  case object Public extends ColumnVisibility("public")

  //  val Public:ColumnVisibility=ColumnVisibility("public")
  //  val Admin:ColumnVisibility=ColumnVisibility("admin")
  //  def owner(field:String):ColumnVisibilityExpression=ColumnVisibilityExpression("${item."+field+"}")

  //  final case class VisPublic() extends NoSqlVisibility
  //
  //  final case class VisModuleAdmin() extends NoSqlVisibility
  //
  //  final case class VisModule() extends NoSqlVisibility
  //
  //  final case class VisText(visibility: String) extends NoSqlVisibility
  //
  //  final case class VisOwner[T](visibilty: T => String) extends NoSqlVisibility
  //
  //  final case class Visibility[T](visibility: T => String) extends NoSqlVisibility
}

object ColumnVisibilityAnnotation {
  implicit final val decodeColumnVisibilityAnnotation: JsonDecoder[ColumnVisibilityAnnotation] =
    JsonDecoder.instance { c =>
      val fields = c.keys.getOrElse(Vector.empty[String]).toList
      if (fields.contains("expression")) {
        c.as[ColumnVisibilityExpression]
      } else if (fields.contains("script")) {
        c.as[ColumnVisibilityScript]
      } else {
        c.as[ColumnVisibility]
      }
    }

  implicit final val encodeColumnVisibilityAnnotation: JsonEncoder[ColumnVisibilityAnnotation] = {
    import zio.json._
    JsonEncoder.instance {
      case k: ColumnVisibility           => k.asJson
      case k: ColumnVisibilityScript     => k.asJson
      case k: ColumnVisibilityExpression => k.asJson
    }
  }
}

sealed trait SubTypeAnnotation

final case class Email() extends StaticAnnotation with SubTypeAnnotation

final case class Ip() extends StaticAnnotation with SubTypeAnnotation

final case class Password() extends StaticAnnotation with SubTypeAnnotation

final case class UserId() extends StaticAnnotation with SubTypeAnnotation

final case class Vertex() extends StaticAnnotation with SubTypeAnnotation

//special field annotations
final case class Unique() extends StaticAnnotation

final case class Created() extends StaticAnnotation

final case class Modified() extends StaticAnnotation

//final case class Fk[T](name: String) extends StaticAnnotation

sealed trait IndexAnnotation extends StaticAnnotation

final case class IndexRequireType() extends IndexAnnotation

final case class IndexName(name: String) extends IndexAnnotation

final case class IndexPrefix(name: String) extends IndexAnnotation

final case class TimeSerieIndex(
  interval: IndexTimeInterval = IndexTimeInterval.Month,
  name: Option[String] = None
) extends IndexAnnotation

final case class TimeSerieField() extends IndexAnnotation

final case class Embedded() extends IndexAnnotation

final case class Nested() extends IndexAnnotation

final case class Parent(parent: String) extends IndexAnnotation

final case class Index() extends IndexAnnotation

final case class NoIndex() extends IndexAnnotation

final case class Store(store: Boolean = false) extends IndexAnnotation

final case class Binary() extends StaticAnnotation with SubTypeAnnotation

final case class Keyword() extends IndexAnnotation

final case class Text() extends IndexAnnotation

final case class NLP() extends IndexAnnotation

final case class Suggest() extends IndexAnnotation

final case class Stem(language: String = "en") extends IndexAnnotation

final case class HeatMap() extends IndexAnnotation

sealed trait PKAnnotation extends StaticAnnotation

final case class PK() extends PKAnnotation
final case class PKLowercase() extends PKAnnotation
final case class PKHash() extends PKAnnotation
final case class PKLowercaseHash() extends PKAnnotation

final case class PKSeparator(text: String) extends PKAnnotation

//final case class Editable() extends IndexAnnotation

//final case class NoEditable() extends IndexAnnotation
//final case class Attachment() extends StaticAnnotation

@JsonCodec
final case class KeyPostProcessing(language: String, script: String)

object KeyPostProcessing {
  lazy val LowerCase = KeyPostProcessing("native", "lowercase")
  lazy val UpperCase = KeyPostProcessing("native", "uppercase")
  lazy val Hash = KeyPostProcessing("native", "hash")
  lazy val Slug = KeyPostProcessing("native", "slug")
}

sealed trait KeyPart

@JsonCodec
final case class KeyField(
  field: String,
  postProcessing: List[KeyPostProcessing] = Nil,
  format: Option[String] = None
) extends KeyPart

object KeyPart {
  implicit final val decodeKeyPart: JsonDecoder[KeyPart] =
    JsonDecoder.instance { c =>
      c.as[KeyField]
    }

  implicit final val encodeKeyPart: JsonEncoder[KeyPart] = {
    import zio.json._
    JsonEncoder.instance {
      case k: KeyField => k.asJson
    }
  }
}

final case class KeyManagement(
  parts: List[KeyPart],
  separator: Option[String] = None,
  postProcessing: List[KeyPostProcessing] = Nil
) extends StaticAnnotation

object KeyManagement {

  lazy val empty: KeyManagement = KeyManagement(Nil)

  implicit final val decodeKeyManagement: JsonDecoder[KeyManagement] =
    JsonDecoder.instance { c =>
      for {
        parts <- c.downField("parts").as[Option[List[KeyPart]]]
        separator <- c.downField("separator").as[Option[String]]
        postProcessing <- c.downField("postProcessing").as[Option[List[KeyPostProcessing]]]
      } yield KeyManagement(
        parts = parts.getOrElse(Nil),
        separator = separator,
        postProcessing = postProcessing.getOrElse(Nil)
      )
    }

  implicit final val encodeKeyManagement: JsonEncoder[KeyManagement] = {
    import zio.json._
    JsonEncoder.instance { obj =>
      var fields: List[(String, Json)] = List(
        "parts" -> obj.parts.asJson
      )
      obj.separator.foreach(v => fields ::= "separator" -> Json.Str(v))
      if (obj.postProcessing.nonEmpty) {
        fields ::= "postProcessing" -> Json.Arr(
          obj.postProcessing.map(_.asJson)
        )
      }

      Json.Obj(fields: _*)
    }
  }
}
