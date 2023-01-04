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

package diffson

import jsonpatch._
import jsonpointer._
import jsonmergepatch._
import cats.{ Apply, FlatMap }
import cats.implicits._
import zio.json.ast._
import zio._
import zio.json._
package object zjson {

  implicit object jsonyCirce extends Jsony[Json] {

    def Null: Json = Json.Null

    def array(json: Json): Option[Vector[Json]] =
      json match {
        case Json.Arr(elements) => Some(elements.toVector)
        case _                  => None
      }

    def fields(json: Json): Option[Map[String, Json]] =
      json match {
        case Json.Obj(fields) => Some(fields.toList.toMap)
        case _                => None
      }

    def makeArray(values: Vector[Json]): Json =
      Json.Arr(Chunk.fromIterable(values))

    def makeObject(fields: Map[String, Json]): Json =
      Json.Obj(Chunk.fromIterable(fields.toList))

    def show(json: Json): String =
      json.toJson

    def eqv(json1: Json, json2: Json) =
      json1 == json2

  }

  implicit val pointerEncoder: JsonEncoder[Pointer] =
    JsonEncoder.string.contramap(_.show)

  implicit val pointerDecoder: JsonDecoder[Pointer] =
    JsonDecoder.string.mapOrFail(Pointer.parse[Either[Throwable, *]](_).leftMap(_.getMessage))

  implicit val operationEncoder: JsonEncoder[Operation[Json]] = Json.Obj.encoder.contramap {
    case Add(path, value) =>
      Json.Obj("op" -> Json.Str("add"), "path" -> Json.Str(path.show), "value" -> value)
    case Remove(path, Some(old)) =>
      Json.Obj("op" -> Json.Str("remove"), "path" -> Json.Str(path.show), "old" -> old)
    case Remove(path, None) =>
      Json.Obj("op" -> Json.Str("remove"), "path" -> Json.Str(path.show))
    case Replace(path, value, Some(old)) =>
      Json.Obj("op" -> Json.Str("replace"), "path" -> Json.Str(path.show), "value" -> value, "old" -> old)
    case Replace(path, value, None) =>
      Json.Obj("op" -> Json.Str("replace"), "path" -> Json.Str(path.show), "value" -> value)
    case Move(from, path) =>
      Json.Obj("op" -> Json.Str("move"), "from" -> Json.Str(from.show), "path" -> Json.Str(path.show))
    case Copy(from, path) =>
      Json.Obj("op" -> Json.Str("copy"), "from" -> Json.Str(from.show), "path" -> Json.Str(path.show))
    case Test(path, value) =>
      Json.Obj("op" -> Json.Str("test"), "path" -> Json.Str(path.show), "value" -> value)
  }

  implicit val operationDecoder: JsonDecoder[Operation[Json]] = Json.Obj.decoder.mapOrFail { c =>
    c.getOption[String]("op") match {
      case Some(op) =>
        op match {
          case "add" =>
            for {
              pointer <- c.getEither[Pointer]("path")
              value <- c.getEither[Json]("value")
            } yield Add[Json](pointer, value)
          case "remove" =>
            for {
              pointer <- c.getEither[Pointer]("path")
              old <- c.getEither[Option[Json]]("old")
            } yield Remove[Json](pointer, old)
          case "replace" =>
            for {
              pointer <- c.getEither[Pointer]("path")
              value <- c.getEither[Json]("value")
              old <- c.getEither[Option[Json]]("old")
            } yield Replace[Json](pointer, value, old)
          case "move" =>
            for {
              from <- c.getEither[Pointer]("from")
              path <- c.getEither[Pointer]("path")
            } yield Move[Json](from, path)
          case "copy" =>
            for {
              from <- c.getEither[Pointer]("from")
              path <- c.getEither[Pointer]("path")
            } yield Copy[Json](from, path)
          case "test" =>
            for {
              path <- c.getEither[Pointer]("path")
              value <- c.getEither[Json]("value")
            } yield Test[Json](path, value)
          case other =>
            Left(s"""Unknown operation "$other"""")
        }
      case None => Left("missing 'op' field")
    }
  }

  implicit val jsonPatchEncoder: JsonEncoder[JsonPatch[Json]] =
    DeriveJsonEncoder.gen[List[Json]].contramap(_.ops.map(_.asJson))

  implicit val jsonPatchDecoder: JsonDecoder[JsonPatch[Json]] = Json.Arr.decoder.mapOrFail { c =>
    val records = c.elements.map(_.as[Operation[Json]])
    records.find(_.isLeft) match {
      case Some(Left(value)) => Left(value)
      case None =>
        Right(JsonPatch(records.toList.collect { case Right(c) => c }))
    }

  }

  implicit val jsonMergePatchEncoder: JsonEncoder[JsonMergePatch[Json]] = Json.encoder.contramap(_.toJson)

  implicit val jsonMergePatchDecoder: JsonDecoder[JsonMergePatch[Json]] = Json.decoder.mapOrFail { c =>
    c match {
      case Json.Obj(fields) => Right(JsonMergePatch.Object(fields.toList.toMap))
      case value            => Right(JsonMergePatch.Value(value))
    }
  }
}
