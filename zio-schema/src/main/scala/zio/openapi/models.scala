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

package zio.openapi

import scala.collection.immutable.ListMap

import zio.schema.generic.EnumSchema
import enumeratum._
import zio.json._
import zio.json._
import io.circe.{ Json, JsonDecoder, JsonEncoder }

@JsonCodec
final case class Tag(
  name: String,
  description: String,
  externalDocs: Option[ExternalDocumentation] = None
)

@JsonCodec
final case class ExternalDocumentation(
  url: String,
  description: Option[String] = None
)

@JsonCodec
final case class Info(
  title: String,
  version: String,
  description: Option[String] = None,
  termsOfService: Option[String] = None,
  contact: Option[Contact] = None,
  license: Option[License] = None
)

@JsonCodec
final case class Contact(
  name: Option[String],
  email: Option[String],
  url: Option[String]
)

@JsonCodec
final case class License(name: String, url: Option[String])

// todo: variables
@JsonCodec
final case class Server(
  url: String,
  description: Option[String]
)

// todo: $ref
@JsonCodec
final case class PathItem(
  summary: Option[String] = None,
  description: Option[String] = None,
  get: Option[Operation] = None,
  put: Option[Operation] = None,
  post: Option[Operation] = None,
  delete: Option[Operation] = None,
  options: Option[Operation] = None,
  head: Option[Operation] = None,
  patch: Option[Operation] = None,
  trace: Option[Operation] = None,
  servers: List[Server] = Nil,
  parameters: List[ReferenceOr[Parameter]] = Nil
) {

  def mergeWith(other: PathItem): PathItem =
    PathItem(
      get = get.orElse(other.get),
      put = put.orElse(other.put),
      post = post.orElse(other.post),
      delete = delete.orElse(other.delete),
      options = options.orElse(other.options),
      head = head.orElse(other.head),
      patch = patch.orElse(other.patch),
      trace = trace.orElse(other.trace)
    )
}
@JsonCodec
final case class RequestBody(
  description: Option[String] = None,
  content: ListMap[String, MediaType],
  required: Option[Boolean] = None
)

@JsonCodec
final case class MediaType(
  schema: Option[ReferenceOr[Schema]],
  example: Option[ExampleValue] = None,
  examples: ListMap[String, ReferenceOr[Example]] = ListMap.empty,
  encoding: ListMap[String, Encoding] = ListMap.empty
)

@JsonCodec
final case class Encoding(
  contentType: Option[String],
  headers: ListMap[String, ReferenceOr[Header]],
  style: Option[ParameterStyle],
  explode: Option[Boolean],
  allowReserved: Option[Boolean]
)

sealed trait ResponsesKey

object ResponsesKey {
  implicit val encoderResponseMap: JsonEncoder[ListMap[ResponsesKey, ReferenceOr[Response]]] =
    new JsonEncoder[ListMap[ResponsesKey, ReferenceOr[Response]]] {
      override def apply(
        responses: ListMap[ResponsesKey, ReferenceOr[Response]]
      ): Json = {
        val fields = responses.map {
          case (ResponsesDefaultKey, r)    => ("default", r.asJson)
          case (ResponsesCodeKey(code), r) => (code.toString, r.asJson)
        }

        Json.Obj(fields.toSeq: _*)

      }
    }
  implicit val decoderResponseMap: JsonDecoder[ListMap[ResponsesKey, ReferenceOr[Response]]] =
    JsonDecoder.failedWithMessage[ListMap[ResponsesKey, ReferenceOr[Response]]]("unused")

}

case object ResponsesDefaultKey extends ResponsesKey

@JsonCodec
final case class ResponsesCodeKey(code: Int) extends ResponsesKey

// todo: links
@JsonCodec
final case class Response(
  description: String,
  headers: ListMap[String, ReferenceOr[Header]] = ListMap.empty,
  content: ListMap[String, MediaType] = ListMap.empty
)

@JsonCodec
final case class Example(
  summary: Option[String],
  description: Option[String],
  value: Option[ExampleValue],
  externalValue: Option[String]
)

@JsonCodec
final case class Header(
  description: Option[String],
  required: Option[Boolean],
  deprecated: Option[Boolean],
  allowEmptyValue: Option[Boolean],
  style: Option[ParameterStyle],
  explode: Option[Boolean],
  allowReserved: Option[Boolean],
  schema: Option[ReferenceOr[Schema]],
  example: Option[ExampleValue],
  examples: ListMap[String, ReferenceOr[Example]],
  content: ListMap[String, MediaType]
)

sealed trait SchemaType extends EnumEntry with EnumEntry.Lowercase

object SchemaType extends CirceEnum[SchemaType] with Enum[SchemaType] with EnumSchema[SchemaType] {
  case object Boolean extends SchemaType
  case object Object extends SchemaType
  case object Array extends SchemaType
  case object Number extends SchemaType
  case object String extends SchemaType
  case object Integer extends SchemaType

  override def values = findValues
}
@JsonCodec
final case class ExampleValue(value: String)

@JsonCodec
final case class OAuthFlows(
  `implicit`: Option[OAuthFlow] = None,
  password: Option[OAuthFlow] = None,
  clientCredentials: Option[OAuthFlow] = None,
  authorizationCode: Option[OAuthFlow] = None
)

@JsonCodec
final case class OAuthFlow(
  authorizationUrl: String,
  tokenUrl: String,
  refreshUrl: Option[String],
  scopes: ListMap[String, String]
)
