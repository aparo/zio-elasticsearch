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

package zio.elasticsearch.responses

import scala.collection.mutable
import zio.json._
import zio.json.ast._
import zio.json.ast.JsonUtils
import zio.exception.{ FrameworkException, JsonDecodingException }
final case class Explanation(value: Double = 0.0d, description: String = "", details: List[Explanation] = Nil) {
  def getDescriptions(): List[String] = List(description) ++ details.flatMap(_.getDescriptions())
}
object Explanation {
  implicit val jsonDecoder: JsonDecoder[Explanation] = DeriveJsonDecoder.gen[Explanation]
  implicit val jsonEncoder: JsonEncoder[Explanation] = DeriveJsonEncoder.gen[Explanation]
}

/**
 * A single document in a search result. The `highlight` map is optional, and
 * only present if the query asks for highlighting. It maps field names to
 * sequences of highlighted fragments.
 */
final case class ResultDocument[T](
  @jsonField("_id") id: String,
  @jsonField("_index") index: String,
  @jsonField("_type") docType: String = "_doc",
  version: Option[Long] = None,
  score: Option[Double] = None,
  source: Either[String, T] = ResultDocument.DecoderEmpty,
  explanation: Option[Explanation] = None,
  fields: Option[Json.Obj] = None,
  sort: List[Json] = List.empty[Json],
  highlight: Option[Map[String, Seq[String]]] = None
)(implicit encode: JsonEncoder[T], decoder: JsonDecoder[T]) {

//  def source: T = iSource.toOption.get

  /**
   * Gets a highlight list for a field. Returns the empty list if no highlights
   * were found, or if the query did not ask for highlighting.
   */
  def highlightFor(field: String): Seq[String] =
    highlight.getOrElse(Map.empty[String, Seq[String]]).getOrElse(field, Seq.empty)

  def toJson: Either[String, Json] = this.toJsonAST

  def getAllExplanationDescription(): List[String] =
    explanation.map(_.getDescriptions()).getOrElse(Nil)

}

object ResultDocument {

  val DecoderEmpty = Left("NoObject")

  def fromHit[T](
    hit: ResultDocument[Json.Obj]
  )(implicit encode: JsonEncoder[T], decoder: JsonDecoder[T]): ResultDocument[T] =
    hit.source match {
      case Left(left) =>
        hit.copy(source = Left(left))
      case Right(right) =>
        hit.copy(source = right.as[T])
    }

  def fromGetResponse[T](
    response: GetResponse
  )(implicit encode: JsonEncoder[T], decoder: JsonDecoder[T]): ResultDocument[T] =
    ResultDocument(
      id = response.id,
      index = response.index,
      docType = response.docType,
      version = Option(response.version),
      source = response.source.as[T],
      fields = Some(response.fields)
    )

  /**
   * Function to prevent Nan as value
   *
   * @param score
   *   a Float
   * @return
   *   a valid double score
   */
  def validateScore(score: Option[Double]): Option[Double] =
    score match {
      case Some(value) =>
        if (value.toString == "NaN")
          Some(1.0d)
        else
          score
      case None =>
        Some(1.0d)
    }

  /**
   * Function to prevent Nan as value
   *
   * @param score
   *   a Float
   * @return
   *   a valid double score
   */
  def validateScore(score: Float): Option[Double] =
    Option(score).flatMap {
      case s if s.toString == "NaN" => Some(1.0d)
      case s                        => Some(s.toDouble)
    }

  private def validateVersion(version: Long): Option[Long] = Option(version)

  implicit def decodeResultDocument[T](
    implicit
    encode: JsonEncoder[T],
    decoder: JsonDecoder[T]
  ): JsonDecoder[ResultDocument[T]] = DeriveJsonDecoder.gen[ResultDocument[T]]
//    JsonDecoder.instance { c =>
//      for {
//        id <- jObj.get[String]("_id")
//        index <- jObj.get[String]("_index")
//        typ <- jObj.get[Option[String]]("_type")
//        version <- jObj.get[Option[Long]]("_version")
//        score <- jObj.get[Option[Double]]("_score")
//        explanation <- jObj.get[Option[Explanation]]("_explanation")
//        fields <- jObj.get[Option[Json.Obj]]("fields")
//        sort <- jObj.get[Option[List[Json]]]("sort")
//        highlight <- jObj.get[Option[Map[String, Seq[String]]]]("highlight")
//      } yield ResultDocument(
//        id = id,
//        index = index,
//        docType = typ.getOrElse("_doc"),
//        version = version,
//        score = score,
//        iSource = jObj.get[T]("_source"),
//        explanation = explanation,
//        fields = fields,
//        sort = sort.getOrElse(List.empty[Json]),
//        highlight = highlight
//      )
//    }

  implicit def encodeResultDocument[T](
    implicit
    encode: JsonEncoder[T],
    decoder: JsonDecoder[T]
  ): JsonEncoder[ResultDocument[T]] = DeriveJsonEncoder.gen[ResultDocument[T]]
//    JsonEncoder.instance { obj =>
//      val fields = new mutable.ListBuffer[(String, Json)]()
//      fields += ("_id" -> obj.id.asJson)
//      fields += ("_index" -> obj.index.asJson)
//      fields += ("_type" -> obj.docType.asJson)
//      obj.version.map(v => fields += ("_version" -> v.asJson))
//      obj.score.map(v => fields += ("_score" -> v.asJson))
//      obj.explanation.map(v => fields += ("_explanation" -> v.asJson))
//      obj.fields.map(v => fields += ("fields" -> v.asJson))
//      obj.highlight.map(v => fields += ("highlight" -> v.asJson))
//      obj.iSource.map(v => fields += ("_source" -> v.asJson))
//      if (obj.sort.nonEmpty) fields += ("sort" -> obj.sort.asJson)
//
//      Json.Obj(Chunk.fromIterable(fields))
//    }

  def getValues[K: JsonDecoder](field: String, record: HitResponse): List[K] =
    field match {
      case "_id" =>
        List(record.id.asInstanceOf[K])
      case "_type" =>
        List(record.docType.asInstanceOf[K])
      case f =>
        record.source match {
          case Left(_) => Nil
          case Right(value) =>
            JsonUtils.resolveFieldMultiple[K](value, f).flatMap(_.toOption).toList
        }
    }

}
