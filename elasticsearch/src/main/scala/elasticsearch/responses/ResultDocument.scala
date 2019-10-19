/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe._
import io.circe.syntax._
import io.circe.derivation.annotations.JsonCodec
import elasticsearch.common.circe.CirceUtils
import scala.collection.mutable

@JsonCodec
final case class Explanation(
  value: Double = 0.0,
  description: String = "",
  details: List[Explanation] = Nil
) {

  def getDescriptions(): List[String] =
    List(description) ++ details.flatMap(_.getDescriptions())
}

/**
 * A single document in a search result.
 * The  `highlight` map is optional, and only present if the query asks for highlighting. It maps field names to sequences of highlighted fragments.
 */
final case class ResultDocument[T](
  id: String,
  index: String,
  docType: String,
  version: Option[Long] = None,
  score: Option[Double] = None,
  iSource: Decoder.Result[T] = ResultDocument.DecoderEmpty,
  explanation: Option[Explanation] = None,
  fields: Option[JsonObject] = None,
  highlight: Option[Map[String, Seq[String]]] = None
)(implicit encode: Encoder[T], decoder: Decoder[T]) {

  def source: T = iSource.toOption.get

  /**
   * Gets a highlight list for a field.
   * Returns the empty list if no highlights were found, or if the query did not ask for highlighting.
   */
  def highlightFor(field: String): Seq[String] =
    highlight.getOrElse(Map.empty[String, Seq[String]]).getOrElse(field, Seq.empty)

  def toJson: Json = this.asJson

  def getAllExplanationDescription(): List[String] =
    explanation.map(_.getDescriptions()).getOrElse(Nil)

}

object ResultDocument {

  val DecoderEmpty = Left(DecodingFailure("NoObject", Nil))

  def fromHit[T](
    hit: ResultDocument[JsonObject]
  )(implicit encode: Encoder[T], decoder: Decoder[T]): ResultDocument[T] =
    hit.iSource match {
      case Left(left) =>
        hit.copy(iSource = Left(DecodingFailure(left.getMessage(), Nil)))
      case Right(right) =>
        hit.copy(iSource = Json.fromJsonObject(right).as[T])
    }

  def fromGetResponse[T](
    response: GetResponse
  )(implicit encode: Encoder[T], decoder: Decoder[T]): ResultDocument[T] =
    ResultDocument(
      id = response.id,
      index = response.index,
      docType = response.docType,
      version = Option(response.version),
      iSource = Json.fromJsonObject(response.source).as[T],
      fields = Some(response.fields)
    )

  /**
   * Function to prevent Nan as value
   *
   * @param score a Float
   * @return a valid double score
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
   * @param score a Float
   * @return a valid double score
   */
  def validateScore(score: Float): Option[Double] =
    Option(score).flatMap {
      case s if s.toString == "NaN" => Some(1.0d)
      case s                        => Some(s.toDouble)
    }

  private def validateVersion(version: Long): Option[Long] = Option(version)

  implicit def decodeResultDocument[T](
    implicit encode: Encoder[T],
    decoder: Decoder[T]
  ): Decoder[ResultDocument[T]] =
    Decoder.instance { c =>
      for {
        id <- c.downField("_id").as[String]
        index <- c.downField("_index").as[String]
        typ <- c.downField("_type").as[String]
        version <- c.downField("_version").as[Option[Long]]
        score <- c.downField("_score").as[Option[Double]]
        explanation <- c.downField("_explanation").as[Option[Explanation]]
        fields <- c.downField("fields").as[Option[JsonObject]]
        highlight <- c.downField("highlight").as[Option[Map[String, Seq[String]]]]
      } yield ResultDocument(
        id = id,
        index = index,
        docType = typ,
        version = version,
        score = score,
        iSource = c.downField("_source").as[T],
        explanation = explanation,
        fields = fields,
        highlight = highlight
      )
    }

  implicit def encodeResultDocument[T](
    implicit encode: Encoder[T],
    decoder: Decoder[T]
  ): Encoder[ResultDocument[T]] =
    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("_id" -> obj.id.asJson)
      fields += ("_index" -> obj.index.asJson)
      fields += ("_type" -> obj.docType.asJson)
      obj.version.map(v => fields += ("_version" -> v.asJson))
      obj.score.map(v => fields += ("_score" -> v.asJson))
      obj.explanation.map(v => fields += ("_explanation" -> v.asJson))
      obj.fields.map(v => fields += ("fields" -> v.asJson))
      obj.highlight.map(v => fields += ("highlight" -> v.asJson))
      obj.iSource.map(v => fields += ("_source" -> v.asJson))

      Json.obj(fields: _*)
    }

  def getValues[K: Decoder](field: String, record: HitResponse): List[K] =
    field match {
      case "_id" =>
        List(record.id.asInstanceOf[K])
      case "_type" =>
        List(record.docType.asInstanceOf[K])
      case f =>
        CirceUtils.resolveFieldMultiple[K](record.source, f).flatMap(_.toOption)
    }

}
