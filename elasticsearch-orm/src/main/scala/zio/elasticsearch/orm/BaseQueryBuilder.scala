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

package zio.elasticsearch.orm

import java.time.{ LocalDateTime, OffsetDateTime }

import _root_.zio.elasticsearch._

import zio.auth.AuthContext
import zio.elasticsearch.aggregations.Aggregation
import zio.elasticsearch.common.search.{ Highlight, SearchRequest, SearchRequestBody }
import zio.elasticsearch.common.{ ActionRequest, Method, SourceConfig }
import zio.elasticsearch.mappings.RootDocumentMapping
import zio.elasticsearch.queries.Query
import zio.elasticsearch.search.QueryUtils
import zio.elasticsearch.sort.Sort._
import zio.elasticsearch.suggestion._
import zio.exception.FrameworkException
import zio.json._
import zio.json.ast._
import zio.{ Chunk, ZIO }

trait BaseQueryBuilder extends ActionRequest[SearchRequestBody] {
  implicit def ormManager: OrmManager
  val defaultScrollTime = "1m"

  def authContext: AuthContext

  def queries: Chunk[Query]

  def filters: Chunk[Query]

  def postFilters: Chunk[Query]

  def fields: Chunk[String]

  def indices: Chunk[String]

  def docTypes: Chunk[String]

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

  def aggregations: Aggregation.Aggregations

  def source: SourceConfig

  def searchAfter: Chunk[Json]

  // default search method
  def method: Method = Method.POST

  def isSingleIndex: Boolean

  def isScroll: Boolean = scrollTime.isDefined

  def queryArgs: Map[String, String] = {
    var parameters = Map.empty[String, String]
    if (isScan) {
      val scroll: String = this.scrollTime match {
        case None    => this.defaultScrollTime
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

  def toRequest: SearchRequest = {
    val ri = getRealIndices(indices)

    var request =
      SearchRequest(indices = ri, body = toJson)
    if (isScan) {
      request = request.copy(scroll = Some(scrollTime.getOrElse("5m")))

    }
    request
  }

  def isScan: Boolean = this.isScroll

  def toJson: SearchRequestBody =
    SearchRequestBody(
      from = if (from > 0) from else 0,
      size = if (size > -1) size else 10,
      trackScores = if (this.trackScore) Some(true) else None,
      explain = if (this.explain) Some(true) else None,
      highlight = if (this.highlight.fields.nonEmpty) Some(this.highlight) else None,
      version = if (this.version) Some(true) else None,
      sort = if (this.sort.nonEmpty) Some(this.sort) else None,
      suggest = if (this.suggestions.nonEmpty) Some(this.suggestions) else None,
      aggregations = if (this.aggregations.nonEmpty) Some(this.aggregations) else None,
      _source = if (this.source.nonEmpty) Some(this.source) else None
    )

  def buildQuery(extraFilters: Chunk[Query]): Query =
    QueryUtils.generateOptimizedQuery(
      this.queries,
      this.filters ++ this.postFilters ++ extraFilters
    )

  def getRealIndices(indices: Chunk[String]): Chunk[String] =
    indices.map { index =>
      ormManager.elasticSearchService.concreteIndex(index)
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

  def mappings: ZIO[Any, FrameworkException, Seq[RootDocumentMapping]] =
    for {
      mappings <- ZIO.foreach(this.getRealIndices(indices)) { index =>
        ormManager.indicesManager.getMapping(Chunk(index))
      }
    } yield mappings.flatMap(_.values)

  /**
   * Returns the last update value from a query
   *
   * @param field
   *   the field that contains the updated datetime value
   * @return
   *   the field value otherwise now!!
   */
  def getLastUpdate[T: JsonDecoder](
    field: String
  ): ZIO[Any, FrameworkException, Option[T]]

  def getLastUpdateASOffsetDateTime(
    field: String
  ): ZIO[Any, FrameworkException, Option[OffsetDateTime]] =
    getLastUpdate[OffsetDateTime](field)

  def getLastUpdateAsLocalDateTime(
    field: String
  ): ZIO[Any, FrameworkException, Option[LocalDateTime]] =
    getLastUpdate[LocalDateTime](field)

  def resolveId(name: String, id: String): String =
    if (isSingleIndex) id else s"$name${ElasticSearchConstants.SINGLE_STORAGE_SEPARATOR}$id"

}
