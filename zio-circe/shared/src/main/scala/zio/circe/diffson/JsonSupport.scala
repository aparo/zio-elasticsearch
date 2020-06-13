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

package zio.circe.diffson

import scala.language.higherKinds

trait JsonSupport[JsValue] { this: DiffsonInstance[JsValue] =>

  /** The Json provider acts as an abstraction layer over the Json library.
   *  It exposes all methods and types used by diffson when manipulating Json values directly.
   *
   *  @author Lucas Satabin
   */
  abstract class JsonProvider {

    type Marshaller[T]

    type Unmarshaller[T]

    implicit val patchMarshaller: Marshaller[JsonPatch]

    implicit val patchUnmarshaller: Unmarshaller[JsonPatch]

    def parseJson(s: String): JsValue

    def unapplyArray(value: JsValue): Option[Vector[JsValue]]

    def applyArray(elems: Vector[JsValue]): JsValue

    def unapplyObject(value: JsValue): Option[Map[String, JsValue]]

    def applyObject(fields: Map[String, JsValue]): JsValue

    val JsNull: JsValue

    def marshall[T: Marshaller](value: T): JsValue

    def unmarshall[T: Unmarshaller](value: JsValue): T

    def prettyPrint(value: JsValue): String

    def compactPrint(value: JsValue): String

    object JsArray {

      @inline
      def apply(elems: Vector[JsValue]): JsValue =
        applyArray(elems)

      @inline
      def unapply(value: JsValue): Option[Vector[JsValue]] =
        unapplyArray(value)

    }

    object JsObject {

      @inline
      def apply(fields: Map[String, JsValue]): JsValue =
        applyObject(fields)

      @inline
      def unapply(value: JsValue): Option[Map[String, JsValue]] =
        unapplyObject(value)

    }

  }
}
