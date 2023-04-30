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

package zio.elasticsearch.common

import zio.Chunk
import zio.elasticsearch.common.get.GetResponse
import zio.json._
import zio.json.ast._
final case class Explanation(
  value: Double = 0.0d,
  description: String = "",
  details: Chunk[Explanation] = Chunk.empty
) {
  def getDescriptions(): Chunk[String] = Chunk(description) ++ details.flatMap(_.getDescriptions())
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
final case class ResultDocument(
  @jsonField("_id") id: String,
  @jsonField("_index") index: String,
  @jsonField("_type") docType: String = "_doc",
  version: Option[Long] = None,
  score: Option[Double] = None,
  @jsonField("_source") source: Option[Json.Obj] = None,
  explanation: Option[Explanation] = None,
  fields: Option[Json.Obj] = None,
  sort: Chunk[Json] = Chunk.empty[Json],
  highlight: Option[Map[String, Chunk[String]]] = None
) {

  def getTyped[T](implicit decoder: JsonDecoder[T]): Either[String, T] = source match {
    case Some(value) => value.as[T]
    case None        => Left(s"Missing source for document $index - $id")
  }

  /**
   * Gets a highlight list for a field. Returns the empty list if no highlights
   * were found, or if the query did not ask for highlighting.
   */
  def highlightFor(field: String): Chunk[String] =
    highlight.getOrElse(Map.empty[String, Chunk[String]]).getOrElse(field, Chunk.empty)

//  def toJson: Either[String, Json] = this.toJsonAST

  def getAllExplanationDescription(): Chunk[String] =
    explanation.map(_.getDescriptions()).getOrElse(Chunk.empty)

}

object ResultDocument {

  val DecoderEmpty: Left[String,Nothing] = Left("NoObject")

  def fromGetResponse(
    response: GetResponse
  ): ResultDocument =
    ResultDocument(
      id = response.id,
      index = response.index,
      docType = response.docType,
      version = Option(response.version),
      source = response.source,
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

  implicit val decodeResultDocument: JsonDecoder[ResultDocument] = DeriveJsonDecoder.gen[ResultDocument]

  implicit val encodeResultDocument: JsonEncoder[ResultDocument] = DeriveJsonEncoder.gen[ResultDocument]

  def getValues[K: JsonDecoder](field: String, record: ResultDocument): Chunk[K] =
    field match {
      case "_id" =>
        Chunk(record.id.asInstanceOf[K])
      case "_type" =>
        Chunk(record.docType.asInstanceOf[K])
      case "_index" =>
        Chunk(record.index.asInstanceOf[K])
      case f =>
        record.source match {
          case Some(value) => JsonUtils.resolveFieldMultiple[K](value, f).flatMap(_.toOption)
          case None        => Chunk.empty
        }
    }

}
