/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.common.circe

import io.circe._
import java.time.{LocalDateTime, OffsetDateTime}

import elasticsearch.exception.InvalidJsonValue

import scala.collection.Seq

object CirceUtils {

  // Default printer of Circe encoded json to string
  lazy val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)
  lazy val printer2: Printer = Printer.spaces2.copy(dropNullValues = true)

  def resolveSingleField[T: Decoder](
      json: Json,
      field: String
  ): Decoder.Result[T] =
    resolveFieldMultiple(json, field).head

  def resolveSingleField[T: Decoder](
      jsonObject: JsonObject,
      field: String
  ): Decoder.Result[T] =
    resolveFieldMultiple(Json.fromJsonObject(jsonObject), field).head

  def resolveFieldMultiple[T: Decoder](
      json: JsonObject,
      field: String
  ): List[Decoder.Result[T]] =
    resolveFieldMultiple(Json.fromJsonObject(json), field)

  def resolveFieldMultiple[T: Decoder](
      json: Json,
      field: String
  ): List[Decoder.Result[T]] = {
    val tokens = field.split('.')
    var terms: List[Json] = keyValues(tokens.head, json)
    tokens.tail.foreach(k => terms = terms.flatMap(j => keyValues(k, j)))
    terms.map(_.as[T])

  }

  private def keyValues(key: String, json: Json): List[Json] = json match {
    case json if json.isObject =>
      json.asObject.get.toList.filter { _._1 == key }.map(_._2)
    case json if json.isArray =>
      json.asArray.get.toList.flatMap(o => keyValues(key, o))
    case _ => Nil
  }

  def mergeJsonList(items: List[Json]): Json = {
    var json = Json.obj()
    items.foreach { value =>
      json = json.deepMerge(value)
    }
    json
  }

  def mapToJson(map: Map[_, _]): Json =
    Json.obj(map.map { case (k, v) => k.toString -> anyToJson(v) }.toSeq: _*)

  def anyToJson(value: Any): Json = value match {
    case v: Json => v
    case v: JsonObject => Json.fromJsonObject(v)
    case None => Json.Null
    case null => Json.Null
    case Nil => Json.fromValues(Nil)
    case Some(x) => anyToJson(x)
    case b: Boolean => Json.fromBoolean(b)
    case i: Int => Json.fromInt(i)
    case i: Long => Json.fromLong(i)
    case i: Float => Json.fromFloat(i).get
    case i: Double => Json.fromDouble(i).get
    //    case i: BigDecimal => JsNumber(i)
    case s: String => Json.fromString(s)
    case s: OffsetDateTime => Json.fromString(s.toString)
    case s: LocalDateTime => Json.fromString(s.toString)
    case x: Seq[_] => Json.fromValues(x.map(anyToJson))
    case x: Set[_] => Json.fromValues(x.map(anyToJson))
    case x: Map[_, _] =>
      Json.obj(x.map { case (k, v) => k.toString -> anyToJson(v) }.toSeq: _*)
    case default =>
      throw InvalidJsonValue(s"$default not valid")
  }

  def jsToAny(value: Json): Any = value match {
    case Json.Null => null
    case js: Json if js.isArray => value.asArray.get.map(p => jsToAny(p))
    case js: Json if js.isString => value.asString.getOrElse("")
    case js: Json if js.isNumber => js.asNumber.get.toDouble
    case js: Json if js.isBoolean => js.asBoolean.getOrElse(true)
    case js: Json if js.isObject =>
      js.asObject.get.toMap.map {
        case (name, jval) => name -> jsToAny(jval)
      }
    case default =>
      throw InvalidJsonValue(s"$value")
  }

  def jsClean(fields: (String, Json)*): Json =
    Json.obj(fields.filterNot(_._2.isNull): _*)

  def cleanValue(json: JsonObject): JsonObject =
    json.mapValues(cleanValue)

  def cleanValue(json: Json): Json =
    json match {
      case js: Json if js.isString =>
        val s = js.asString.getOrElse("")
        val strim = s.trim
        if (strim.isEmpty)
          Json.Null
        else if (s == strim)
          js
        else
          Json.fromString(strim)

      case js: Json if js.isArray =>
        val value = js.asArray.get
        if (value.isEmpty) {
          Json.Null
        } else {
          val newarr = Json.fromValues(
            value
              .map { curval =>
                cleanValue(curval)
              }
              .filterNot(_ == Json.Null)
          )
          newarr
        }

      case js: Json if js.isObject =>
        val fields = js.asObject.get.keys
        if (fields.isEmpty)
          Json.Null
        else
          joClean(js)
      case x => x
    }

  def joClean(json: Json): Json = json match {
    case js: Json if js.isObject =>
      val fields = js.asObject.get.toList
        .map(v => v._1 -> cleanValue(v._2))
        .filterNot(_._2.isNull)
      if (fields.isEmpty) {
        Json.Null
      } else {
        Json.obj(fields: _*)
      }
    case x => x
  }

  /**
    * These functions are used to create Json objects from filtered sequences of (String, Json) tuples.
    * When the Json in a tuple is Json.Null or JsUndefined or an empty JsonObject, that tuple is considered not valid, and will be filtered out.
    */
  /**
    * Create a Json from `value`, which is valid if the `isValid` function applied to `value` is true.
    */
  def toJsonIfValid[T](value: T, isValid: T => Boolean)(
      implicit enc: Encoder[T]
  ): Json =
    if (isValid(value)) enc.apply(value) else Json.Null

  def toJsonIfFull[T](value: Seq[T])(implicit enc: Encoder[T]): Json =
    if (value.nonEmpty) Json.fromValues(value.map(enc.apply)) else Json.Null

  def toJsonIfFull[T, S](value: Seq[T], xform: T => S)(
      implicit enc: Encoder[S]
  ): Json =
    if (value.nonEmpty) Json.fromValues(value.map(xform.andThen(enc.apply)))
    else Json.Null

  /**
    * Create a Json from `xform` applied to `value`, which is valid if the `isValid` function applied to `value` is true.
    */
  def toJsonIfValid[T, S](value: T, isValid: T => Boolean, xform: T => S)(
      implicit enc: Encoder[S]
  ): Json =
    if (isValid(value)) enc.apply(xform(value)) else Json.Null

  /**
    * Create a Json from `value`, which is valid if `value` is not equal to `default`.
    */
  def toJsonIfNot[T](value: T, default: T)(implicit enc: Encoder[T]): Json =
    if (value != default) enc.apply(value) else Json.Null

  /**
    * Create a Json from `xform` applied to `value`, which is valid if `value` is not equal to `default`.
    */
  def toJsonIfNot[T, S](value: T, default: T, xform: T => S)(
      implicit enc: Encoder[S]
  ): Json =
    if (value != default) enc.apply(xform(value)) else Json.Null

  /**
    * Determines if a property (String, Json) is valid, by testing the Json in the second item.
    */
  def isValidJsonProperty(property: (String, Json)): Boolean =
    property._2 match {
      case obj: Json if obj.isArray => obj.asArray.get.nonEmpty
      case obj: Json if obj.isObject => !obj.asObject.get.isEmpty
      case v if v.isNull => false
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
    Json.obj(filterValid(properties: _*): _*)

}
