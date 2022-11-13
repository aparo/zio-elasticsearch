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

package zio.elasticsearch.sort

import scala.collection.mutable

import zio.elasticsearch.geo.{ DistanceType, GeoPoint }
import zio.elasticsearch.queries.Query
import zio.elasticsearch.script.{ InlineScript, Script }
import zio.json._
import zio.json._
import zio.json._

object Sort {
  type Sort = List[Sorter]
  val EmptySort: List[Sorter] = Nil
}

sealed trait Sorter

object Sorter {

  def apply(field: String, order: Boolean): FieldSort = {
    val orderName = order match {
      case true  => SortOrder.Asc
      case false => SortOrder.Desc
    }
    FieldSort(field, orderName)
  }

  def apply(orderName: String): FieldSort = {
    var field = orderName
    val ordering = orderName match {
      case "asc"  => SortOrder.Asc
      case "desc" => SortOrder.Desc
      case _ =>
        orderName.charAt(0) match {
          case '+' =>
            field = field.substring(1)
            SortOrder.Asc
          case '-' =>
            field = field.substring(1)
            SortOrder.Desc
          case _ =>
            SortOrder.Asc
        }
    }
    FieldSort(field, ordering)
  }

  def random(): ScriptSort = ScriptSort(script = InlineScript("Math.random()"))

  implicit final val decodeSorter: JsonDecoder[Sorter] =
    JsonDecoder.instance { c =>
      c.focus.get match {
        case o: Json if o.isString =>
          Right(new FieldSort(o.asString.get))
        case o: Json if o.isObject =>
          o.asObject.get.toList.head match {
            case ("_geo_distance", jn) =>
              //TODO fix safety
              jn.as[GeoDistanceSort]
            case ("_script", jn) =>
              //TODO fix safety
              jn.as[ScriptSort]
            case (field, jn) =>
              //TODO fix safety
              o.as[FieldSort]

          }
      }
    }

  implicit final val encodeSort: JsonEncoder[Sorter] =
    JsonEncoder.instance {
      case o: ScriptSort      => Json.Obj("_script" -> o.asJson)
      case o: GeoDistanceSort => Json.Obj("_geo_distance" -> o.asJson)
      case o: FieldSort       => o.asJson
    }

}

final case class ScriptSort(
  script: Script,
  `type`: String = "number",
  order: SortOrder = SortOrder.Asc,
  missing: Option[Json] = None,
  mode: Option[SortMode] = None,
  nestedPath: Option[String] = None
) extends Sorter

object ScriptSort {
  implicit final val decodeScriptSort: JsonDecoder[ScriptSort] =
    JsonDecoder.instance { c =>
      c.downField("script").as[Script] match {
        case Left(left) =>
          Left(left)
        case Right(script) =>
          Right(
            new ScriptSort(
              script = script,
              order = c.downField("order").as[SortOrder].toOption.getOrElse(SortOrder.Asc),
              nestedPath = c.downField("nested_path").as[String].toOption,
              `type` = c.downField("type").as[String].toOption.getOrElse("number"),
              mode = c.downField("mode").as[SortMode].toOption,
              missing = c.downField("missing").as[Json].toOption
            )
          )
      }

    }

  implicit final val encodeScriptSort: JsonEncoder[ScriptSort] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("script" -> obj.script.asJson)
      fields += ("order" -> obj.order.asJson)
      fields += ("type" -> obj.`type`.asJson)
      obj.nestedPath.map(v => fields += ("nested_path" -> v.asJson))
      obj.mode.map(v => fields += ("mode" -> v.asJson))
      obj.missing.map(v => fields += ("missing" -> v.asJson))

      Json.fromFields(fields)
    }
}

final case class GeoDistanceSort(
  field: String,
  points: List[GeoPoint],
  order: SortOrder = SortOrder.Asc,
  ignore_unmapped: Boolean = true,
  missing: Option[Json] = None,
  unit: Option[String] = None,
  mode: Option[SortMode] = None,
  @jsonField("nested_path") nestedPath: Option[String] = None,
  @jsonField("distance_type") distanceType: Option[DistanceType] = None
) extends Sorter

object GeoDistanceSort {
  private lazy val otherFields =
    List(
      "order",
      "ignore_unmapped",
      "missing",
      "unit",
      "mode",
      "nested_path",
      "distance_type"
    )

  implicit final val decodeScriptSort: JsonDecoder[GeoDistanceSort] =
    JsonDecoder.instance { c =>
      c.keys.get.toList.diff(otherFields).headOption match {
        case None =>
          Left(DecodingFailure("Unable to detect the geo field", Nil))
        case Some(field) =>
          val points: List[GeoPoint] = c.downField(field).focus.get match {
            case jo: Json if jo.isArray && jo.asArray.get.isEmpty => Nil
            case jo: Json if jo.isArray && jo.asArray.get.head.isNumber =>
              jo.as[GeoPoint].toOption.toList
            case jo: Json if jo.isArray =>
              jo.as[List[GeoPoint]].toOption.getOrElse(Nil)
            case jo: Json => jo.as[GeoPoint].toOption.toList
          }

          Right(
            new GeoDistanceSort(
              field = field,
              points = points,
              order = c.downField("order").as[SortOrder].toOption.getOrElse(SortOrder.Asc),
              ignore_unmapped = c.downField("ignore_unmapped").as[Boolean].toOption.getOrElse(true),
              missing = c.downField("missing").as[Json].toOption,
              unit = c.downField("unit").as[String].toOption,
              mode = c.downField("mode").as[SortMode].toOption,
              nestedPath = c.downField("nested_path").as[String].toOption,
              distanceType = c.downField("distance_type").as[DistanceType].toOption
            )
          )

      }
    }

  implicit final val encodeScriptSort: JsonEncoder[GeoDistanceSort] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      if (obj.points.length == 1) {
        fields += (obj.field -> obj.points.head.asJson)
      } else {
        fields += (obj.field -> obj.points.asJson)

      }
      fields += ("order" -> obj.order.asJson)
      if (obj.ignore_unmapped)
        fields += ("ignore_unmapped" -> obj.ignore_unmapped.asJson)
      obj.missing.map(v => fields += ("missing" -> v.asJson))
      obj.unit.map(v => fields += ("unit" -> v.asJson))
      obj.mode.map(v => fields += ("mode" -> v.asJson))
      obj.nestedPath.map(v => fields += ("nested_path" -> v.asJson))
      obj.distanceType.map(v => fields += ("distance_type" -> v.asJson))

      Json.fromFields(fields)
    }
}

final case class FieldSort(
  field: String = "",
  order: SortOrder = SortOrder.Asc,
  @jsonField("unmapped_type") unmappedType: Option[String] = None,
  @jsonField("nested_path") nestedPath: Option[String] = None,
  @jsonField("nested_filter") nestedFilter: Option[Query] = None,
  mode: Option[SortMode] = None,
  missing: Option[Json] = None
) extends Sorter

object FieldSort {

  def apply(field: String, asc: Boolean): FieldSort =
    FieldSort(field, if (asc) SortOrder.Asc else SortOrder.Desc)

  implicit final val decodeFieldSort: JsonDecoder[FieldSort] =
    JsonDecoder.instance { c =>
      c.keys.getOrElse(Vector.empty[String]).headOption match {
        case None => Left(DecodingFailure("Empty sort object", Nil))
        case Some(field) =>
          c.downField(field).focus.get match {
            case json: Json if json.isString =>
              Right(
                new FieldSort(
                  field = field,
                  json.as[SortOrder].toOption.getOrElse(SortOrder.Asc)
                )
              )
            case json: Json if json.isObject =>
              val valueJson = c.downField(field)
              Right(
                new FieldSort(
                  field = field,
                  order = valueJson.downField("order").as[SortOrder].toOption.getOrElse(SortOrder.Asc),
                  unmappedType = valueJson.downField("unmapped_type").as[String].toOption,
                  nestedPath = valueJson.downField("nested_path").as[String].toOption,
                  nestedFilter = valueJson.downField("nested_filter").as[Query].toOption,
                  mode = valueJson.downField("mode").as[SortMode].toOption,
                  missing = valueJson.downField("missing").as[Json].toOption
                )
              )
          }
      }
    }

  implicit final val encodeFieldSort: JsonEncoder[FieldSort] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("order" -> obj.order.asJson)
      obj.unmappedType.map(v => fields += ("unmapped_type" -> v.asJson))
      obj.nestedPath.map(v => fields += ("nested_path" -> v.asJson))
      obj.nestedFilter.map(v => fields += ("nested_filter" -> v.asJson))
      obj.mode.map(v => fields += ("mode" -> v.asJson))
      obj.missing.map(v => fields += ("missing" -> v.asJson))

      Json.Obj(obj.field -> Json.fromFields(fields))
    }

}
