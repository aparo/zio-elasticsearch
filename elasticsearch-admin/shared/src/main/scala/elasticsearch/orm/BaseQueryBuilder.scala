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

package elasticsearch.orm

import java.time.{LocalDateTime, OffsetDateTime}

import _root_.elasticsearch.nosql.suggestion.{
  DirectGenerator,
  PhraseSuggestion,
  PhraseSuggestionOptions,
  Suggestion
}
import _root_.elasticsearch.{ZioResponse, _}
import elasticsearch.aggregations.Aggregation
import elasticsearch.highlight.Highlight
import elasticsearch.mappings.RootDocumentMapping
import elasticsearch.queries.Query
import elasticsearch.requests.{ActionRequest, SearchRequest}
import elasticsearch.search.QueryUtils
import elasticsearch.sort.Sort._
import io.circe._
import io.circe.syntax._
import zio.auth.AuthContext
import zio.circe.CirceUtils
import zio.logging.{LogLevel, Logging}
import zio.{UIO, ZIO}

import scala.collection.mutable.ListBuffer

trait BaseQueryBuilder extends ActionRequest {
  implicit def clusterService: ClusterService.Service
  def loggingService: Logging.Service = clusterService.loggingService
  val defaultScrollTime = "1m"

  def authContext: AuthContext

  def queries: List[Query]

  def filters: List[Query]

  def postFilters: List[Query]

  def fields: Seq[String]

  def indices: Seq[String]

  def docTypes: Seq[String]

  def from: Int

  def size: Int

  def highlight: Highlight

  def explain: Boolean

  def bulkRead: Int

  def sort: Sort

  def searchType: Option[String]

  def scrollTime: Option[String]

  def timeout: Long

  def version: Boolean

  def trackScore: Boolean

  def suggestions: Map[String, Suggestion]

  def aggregations: Map[String, Aggregation]

  def source: SourceSelector

  def searchAfter: Array[AnyRef]

  def method: String = "POST"

  def isScroll: Boolean = scrollTime.isDefined

  def queryArgs: Map[String, String] = {
    var parameters = Map.empty[String, String]
    if (isScan) {
      val scroll: String = this.scrollTime match {
        case None => this.defaultScrollTime
        case Some(s) => s
      }
      return Map("scroll" -> scroll) //"search_type" -> "scan",
    }
    if (searchType.isDefined)
      parameters += ("search_type" -> searchType.get)
    if (scrollTime.isDefined)
      parameters += ("scroll" -> scrollTime.get)
    parameters
  }

  def toRequest: UIO[SearchRequest] = {
    val ri = getRealIndices(indices)

    var request =
      SearchRequest(indices = ri, body = toJson)
    if (isScan) {
      request = request.copy(scroll = Some(scrollTime.getOrElse("5m")))

    }
    val body = CirceUtils.printer2.print(toJson)
    loggingService.logger.log(LogLevel.Info)(
      s"indices: $ri docTypes: $docTypes query:\n$body"
    ) *>
      ZIO.succeed(request)
  }

  def isScan: Boolean = this.isScroll

  def toJson: Json = {
    val fields = new ListBuffer[(String, Json)]
    if (from > 0) fields += "from" -> Json.fromInt(from)
    if (size > -1) fields += "size" -> Json.fromInt(size)
    if (trackScore) fields += "track_score" -> Json.fromBoolean(trackScore)
    if (explain) fields += "explain" -> Json.fromBoolean(explain)
    if (highlight.fields.nonEmpty) fields += "highlight" -> highlight.asJson
    if (version) fields += "version" -> Json.fromBoolean(version)
    if (sort.nonEmpty) fields += "sort" -> sort.asJson
    if (suggestions.nonEmpty) fields += "suggest" -> suggestions.asJson
    if (aggregations.nonEmpty) fields += "aggs" -> aggregations.asJson
    if (source.nonEmpty) fields += "_source" -> source.asJson

    val query = buildQuery(Nil)
    fields += "query" -> query.asJson
    CirceUtils.joClean(Json.obj(fields: _*))
  }

  def buildQuery(extraFilters: List[Query]): Query =
    QueryUtils.generateOptimizedQuery(
      this.queries,
      this.filters ++ this.postFilters ++ extraFilters
    )

  def getRealIndices(indices: Seq[String]): Seq[String] =
    indices.map { index =>
      clusterService.baseElasticSearchService.concreteIndex(index)
    }

  def internalPhraseSuggester(
      field: String,
      text: String,
      gramSize: Int = 2
  ): PhraseSuggestion =
    PhraseSuggestion(
      text + ".bigram",
      phrase = PhraseSuggestionOptions(
        field,
        gramSize = gramSize,
        confidence = 2.0,
        directGenerators = List(
          DirectGenerator(
            field = field + ".tkl",
            suggestMode = Some("always"),
            minWordLength = Some(1)
          ),
          DirectGenerator(
            field = field + ".reverse",
            suggestMode = Some("always"),
            minWordLength = Some(1),
            preFilter = Some("reverse"),
            postFilter = Some("reverse")
          )
        )
      )
    )

  def getMappings(): ZioResponse[Seq[RootDocumentMapping]] =
    for {
      mappings <- ZIO.foreach(this.getRealIndices(indices)) { index =>
        clusterService.mappings.get(index)
      }
    } yield mappings

  /**
    * Returns the last update value from a query
    *
    * @param field the field that contains the updated datetime value
    * @return the field value otherwise now!!
    */
  def getLastUpdate[T: Decoder](
      field: String
  ): ZioResponse[Option[T]]

  def getLastUpdateASOffsetDateTime(
      field: String
  ): ZioResponse[Option[OffsetDateTime]] =
    getLastUpdate[OffsetDateTime](field)

  def getLastUpdateAsLocalDateTime(
      field: String
  ): ZioResponse[Option[LocalDateTime]] =
    getLastUpdate[LocalDateTime](field)

}
