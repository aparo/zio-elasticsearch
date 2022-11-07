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

package zio

import scala.collection.immutable
import scala.collection.immutable.ListMap

import cats.implicits._
import io.circe.JsonDecoder.Result
import zio.json.ast.Json
import zio.json._
import zio.json._

package object openapi {
  implicit class IterableToListMap[A](xs: Iterable[A]) {
    def toListMap[T, U](implicit ev: A <:< (T, U)): immutable.ListMap[T, U] = {
      val b = immutable.ListMap.newBuilder[T, U]
      for (x <- xs)
        b += x

      b.result()
    }
  }

  type ReferenceOr[T] = Either[Reference, T]
  implicit def referenceEncoder[T](
    implicit
    encoder: JsonEncoder[T]
  ): JsonEncoder[Either[Reference, T]] =
    new JsonEncoder[Either[Reference, T]] {
      override def apply(a: Either[Reference, T]): Json = a match {
        case Left(value)  => value.asJson
        case Right(value) => encoder.apply(value)
      }
    }

  implicit def referenceCodec[T](
    implicit
    encoder: JsonEncoder[T],
    decoder: JsonDecoder[T]
  ): Codec[Either[Reference, T]] =
    new Codec[Either[Reference, T]] {
      override def apply(a: Either[Reference, T]): Json = a match {
        case Left(value)  => value.asJson
        case Right(value) => encoder.apply(value)
      }

      override def apply(c: HCursor): Result[Either[Reference, T]] =
        decoder(c) match {
          case Right(b) =>
            b.asRight[Reference].asRight[DecodingFailure]
          case _ =>
            c.as[Reference] match {
              case Left(ex) =>
                DecodingFailure(s"unable to decode: $ex", Nil).asLeft[Either[Reference, T]]
              case Right(b) =>
                b.asLeft[T].asRight[DecodingFailure]
            }
        }
//      c.as[Reference].map(v => Left(v))
    }

  implicit def referenceorSchemaCodec: Codec[ListMap[String, ReferenceOr[Schema]]] =
    new Codec[ListMap[String, ReferenceOr[Schema]]] {

      override def apply(a: ListMap[String, ReferenceOr[Schema]]): Json =
        Json.fromFields(a.map(v => v._1 -> v._2.asJson))

      override def apply(
        c: HCursor
      ): Result[ListMap[String, ReferenceOr[Schema]]] =
        c.as[Map[String, ReferenceOr[Schema]]] match {
          case Right(r) => Right(ListMap(r.toList: _*))
          case Left(x)  => Left(x)
        }
    }

//  // using a Vector instead of a List, as empty Lists are always encoded as nulls
//  // here, we need them encoded as an empty array
//  type SecurityRequirement = ListMap[String, Vector[String]]
}
