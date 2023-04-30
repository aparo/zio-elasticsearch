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

package zio.schema.elasticsearch.annotations

import scala.annotation.StaticAnnotation

import zio.json._

// if you add here an annotation update the MappingBuilder getField

final case class SchemaId(id: String) extends StaticAnnotation

final case class Version(version: Int) extends StaticAnnotation

final case class Description(description: String) extends StaticAnnotation

final case class Label(label: String) extends StaticAnnotation

//An autoower class has an user id used to filter by it
final case class AutoOwner() extends StaticAnnotation

//globals

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

final case class KeyPostProcessing(language: String, script: String)

object KeyPostProcessing {
  lazy val LowerCase: KeyPostProcessing = KeyPostProcessing("native", "lowercase")
  lazy val UpperCase: KeyPostProcessing = KeyPostProcessing("native", "uppercase")
  lazy val Hash: KeyPostProcessing = KeyPostProcessing("native", "hash")
  lazy val Slug: KeyPostProcessing = KeyPostProcessing("native", "slug")
  implicit val jsonDecoder: JsonDecoder[KeyPostProcessing] = DeriveJsonDecoder.gen[KeyPostProcessing]
  implicit val jsonEncoder: JsonEncoder[KeyPostProcessing] = DeriveJsonEncoder.gen[KeyPostProcessing]
}

@jsonDiscriminator("type")
sealed trait KeyPart

final case class KeyField(field: String, postProcessing: List[KeyPostProcessing] = Nil, format: Option[String] = None)
    extends KeyPart
object KeyField {
  implicit val jsonDecoder: JsonDecoder[KeyField] = DeriveJsonDecoder.gen[KeyField]
  implicit val jsonEncoder: JsonEncoder[KeyField] = DeriveJsonEncoder.gen[KeyField]
}

object KeyPart {
  implicit final val decodeKeyPart: JsonDecoder[KeyPart] = DeriveJsonDecoder.gen[KeyPart]

  implicit final val encodeKeyPart: JsonEncoder[KeyPart] = DeriveJsonEncoder.gen[KeyPart]
}

final case class KeyManagement(
  parts: List[KeyPart],
  separator: Option[String] = None,
  postProcessing: List[KeyPostProcessing] = Nil
) extends StaticAnnotation

object KeyManagement {

  lazy val empty: KeyManagement = KeyManagement(Nil)

  implicit final val decodeKeyManagement: JsonDecoder[KeyManagement] = DeriveJsonDecoder.gen[KeyManagement]

  implicit final val encodeKeyManagement: JsonEncoder[KeyManagement] = DeriveJsonEncoder.gen[KeyManagement]
}
