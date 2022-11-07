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

import zio.Chunk
import zio.common.TraverseUtils
import zio.exception.InvalidJsonValue
import zio.json._

import java.time.{ LocalDateTime, OffsetDateTime }

object JsonUtils {

  def resolveSingleField[T: JsonDecoder](
    json: Json,
    field: String
  ): Option[Either[String, T]] =
    resolveFieldMultiple(json, field).headOption

  def resolveFieldMultiple[T: JsonDecoder](
    json: Json,
    field: String
  ): Chunk[Either[String, T]] = {
    val tokens = field.split('.')
    var terms: Chunk[Json] = keyValues(tokens.head, json)
    tokens.tail.foreach(k => terms = terms.flatMap(j => keyValues(k, j)))
    terms.map(_.as[T])

  }

  private def keyValues(key: String, json: Json): Chunk[Json] =
    json match {
      case Json.Obj(fields)   => fields.filter(_._1 == key).map(_._2)
      case Json.Arr(elements) => elements.flatMap(o => keyValues(key, o))
      case _                  => Chunk.empty
    }

  def mergeJsonList(items: List[Json]): Json = {
    var json = Json.Obj()
    items.foreach { value =>
      json = json.deepMerge(value)
    }
    json
  }

  def mapToJson(map: Map[_, _]): Json =
    Json.Obj(map.toList.map { v =>
      v._1.toString -> anyToJson(v._2)
    }: _*)

  def anyToJson(value: Any): Json = value match {
    case v: Json    => v
    case None       => Json.Null
    case null       => Json.Null
    case Nil        => Json.Arr()
    case Some(x)    => anyToJson(x)
    case b: Boolean => Json.Bool(b)
    case i: Int     => Json.Num(i)
    case i: Long    => Json.Num(i)
    case i: Float   => Json.Num(i)
    case i: Double  => Json.Num(i)
    //    case i: BigDecimal => JsNumber(i)
    case s: String         => Json.Str(s)
    case s: OffsetDateTime => Json.Str(s.toString)
    case s: LocalDateTime  => Json.Str(s.toString)
    case x: Seq[_]         => Json.Arr(x.map(anyToJson): _*)
    case x: Set[_]         => Json.Arr(x.map(anyToJson).toList: _*)
    case x: Set[_]         => Json.Arr(x.map(anyToJson).toList: _*)
    case x: Map[_, _] =>
      Json.Obj(x.map { case (k, v) => k.toString -> anyToJson(v) }.toSeq: _*)
    case default =>
      throw InvalidJsonValue(s"$default not valid")
  }

  def jsToAny(value: Json): Any =
    value match {
      case Json.Obj(fields) =>
        fields.toMap.map {
          case (name, jval) =>
            name -> jsToAny(jval)
        }
      case Json.Arr(elements) => elements.map(p => jsToAny(p))
      case Json.Bool(value)   => value
      case Json.Str(value)    => value
      case Json.Num(value)    => value
      case Json.Null          => null
    }

  def jsClean(fields: (String, Json)*): Json =
    Json.Obj(fields.filterNot(_._2 == Json.Null): _*)

  def cleanValue(json: Json): Json =
    json match {
      case Json.Obj(fields) =>
        if (fields.isEmpty)
          Json.Null
        else
          joClean(json)

      case Json.Arr(elements) =>
        val value = elements
        if (value.isEmpty) {
          Json.Null
        } else {
          Json.Arr(
            value.map { curval =>
              cleanValue(curval)
            }.filterNot(_ == Json.Null)
          )
        }

      case Json.Str(value) =>
        val s = value
        val strim = s.trim
        if (strim.isEmpty)
          Json.Null
        else if (s == strim)
          Json.Str(value)
        else
          Json.Str(strim)

      case x => x
    }

  def joClean(json: Json): Json = json match {
    case Json.Obj(oFields) =>
      val fields = oFields.map(v => v._1 -> cleanValue(v._2)).filterNot(_._2 == Json.Null)
      if (fields.isEmpty) {
        Json.Null
      } else {
        Json.Obj(fields)
      }
    case x => x
  }

  /**
   * These functions are used to create Json objects from filtered sequences of
   * (String, Json) tuples. When the Json in a tuple is Json.Null or JsUndefined
   * or an empty Json.Obj, that tuple is considered not valid, and will be
   * filtered out. Create a Json from `value`, which is valid if the `isValid`
   * function applied to `value` is true.
   */
  def toJsonIfValid[T](value: T, isValid: T => Boolean)(
    implicit
    enc: JsonEncoder[T]
  ): Either[String, Json] =
    if (isValid(value)) enc.toJsonAST(value) else Right(Json.Null)

  def toJsonIfFull[T](value: Seq[T])(implicit enc: JsonEncoder[T]): Either[String, Json] =
    if (value.nonEmpty) TraverseUtils.traverse(value.map(enc.toJsonAST)).map(r => Json.Arr(r: _*)) else Right(Json.Null)

  def toJsonIfFull[T, S](value: Seq[T], xform: T => S)(
    implicit
    enc: JsonEncoder[S]
  ): Either[String, Json] =
    if (value.nonEmpty) {
      TraverseUtils.traverse(value.map(xform.andThen(enc.toJsonAST))).map(r => Json.Arr(r: _*))
    } else Right(Json.Null)

  /**
   * Create a Json from `xform` applied to `value`, which is valid if the
   * `isValid` function applied to `value` is true.
   */
  def toJsonIfValid[T, S](value: T, isValid: T => Boolean, xform: T => S)(
    implicit
    enc: JsonEncoder[S]
  ): Either[String, Json] =
    if (isValid(value)) enc.toJsonAST(xform(value)) else Right(Json.Null)

  /**
   * Create a Json from `value`, which is valid if `value` is not equal to
   * `default`.
   */
  def toJsonIfNot[T](value: T, default: T)(implicit enc: JsonEncoder[T]): Either[String, Json] =
    if (value != default) enc.toJsonAST(value) else Right(Json.Null)

  /**
   * Create a Json from `xform` applied to `value`, which is valid if `value` is
   * not equal to `default`.
   */
  def toJsonIfNot[T, S](value: T, default: T, xform: T => S)(
    implicit
    enc: JsonEncoder[S]
  ): Either[String, Json] =
    if (value != default) enc.toJsonAST(xform(value)) else Right(Json.Null)

  /**
   * Determines if a property (String, Json) is valid, by testing the Json in
   * the second item.
   */
  def isValidJsonProperty(property: (String, Json)): Boolean =
    property._2 match {
      case Json.Obj(fields)   => fields.nonEmpty
      case Json.Arr(elements) => elements.nonEmpty
      case Json.Bool(value)   => value
      case Json.Null          => false
      case _                  => true
    }

  /**
   * Filters a series of properties, keeping only the valid ones.
   */
  def filterValid(properties: (String, Json)*) =
    properties.filter(isValidJsonProperty)

  /**
   * Create a Json object by filtering a series of properties.
   */
  def toJsonObject(properties: (String, Json)*): Json =
    Json.Obj(filterValid(properties: _*): _*)

  /**
   * Drop the entries with a null value if this is an object or array.
   */
//  def deepDropEmptyValues(src: Json): Json = {
//    val folder = new Json.Folder[Json] {
//      def onNull: Json = Json.Null
//      def onBoolean(value: Boolean): Json = Json.Bool(value)
//      def onNumber(value: JsonNumber): Json = Json.fromJsonNumber(value)
//      def onString(value: String): Json = Json.Str(value)
//      def onArray(value: Vector[Json]): Json = {
//        val values = value.collect {
//          case v if !v.isNull => v.foldWith(this)
//        }
//        if (values.nonEmpty)
//          Json.Arr(values)
//        else
//          Json.Null
//      }
//
//      def onObject(value: Json.Obj): Json = {
//        val jsonObject = value.filter { case (_, v) => !v.isNull }.mapValues(_.foldWith(this))
//        if (jsonObject.nonEmpty)
//          Json.fromJsonObject(jsonObject)
//        else
//          Json.Null
//      }
//    }
//
//    src.foldWith(folder).deepDropNullValues
//  }

}
