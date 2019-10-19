/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch.orm.QueryBuilder
import elasticsearch.requests._
import elasticsearch._
import elasticsearch.script.Script
import elasticsearch.responses._
import io.circe._
import io.circe.syntax._
import elasticsearch.common.circe.CirceUtils
import com.github.mlangc.slf4zio.api._
import _root_.elasticsearch.queries.{ DefaultOperator, Query }
import elasticsearch.ZioResponse
import elasticsearch.mappings.RootDocumentMapping
import zio._

// format: off
trait ClientManager extends LoggingSupport {
  this: ElasticSearch =>

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-bulk.html
   *
   * @param body body the body of the call
   * @param index Default index for items which don't provide one
   * @param sourceInclude Default list of fields to extract and return from the _source field, can be overridden on each sub-request
   * @param source True or false to return the _source field or not, or default list of fields to return, can be overridden on each sub-request
   * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param sourceExclude Default list of fields to exclude from the returned _source field, can be overridden on each sub-request
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param fields Default comma-separated list of fields to return in the response for updates, can be overridden on each sub-request
   * @param routing Specific routing value
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the bulk operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def bulk(body: String,
           index: Option[String] = None,
           sourceInclude: Seq[String] = Nil,
           source: Seq[String] = Nil,
           refresh: Option[elasticsearch.Refresh] = None,
           sourceExclude: Seq[String] = Nil,
           pipeline: Option[String] = None,
           fields: Seq[String] = Nil,
           routing: Option[String] = None,
           timeout: Option[String] = None,
           waitForActiveShards: Option[String] = None): ZioResponse[BulkResponse] = {
    val request = BulkRequest(
      body = body,
      index = index,
      sourceInclude = sourceInclude,
      source = source,
      refresh = refresh,
      sourceExclude = sourceExclude,
      pipeline = pipeline,
      fields = fields,
      routing = routing,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    bulk(request)

  }

  def bulk(request: BulkRequest): ZioResponse[BulkResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-scroll.html
   *
   * @param scrollIds A list of scroll IDs to clear
   */
  def clearScroll(scrollIds: Seq[String]): ZioResponse[ClearScrollResponse] = {
    val request = ClearScrollRequest(scrollIds = scrollIds)
    clearScroll(request)

  }

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-scroll.html
   *
   * @param scrollId A list of scroll IDs to clear
   */
  def clearScroll(scrollId: String): ZioResponse[ClearScrollResponse] =
    clearScroll(Seq(scrollId))

  def clearScroll(request: ClearScrollRequest): ZioResponse[ClearScrollResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-count.html
   *
   * @param body body the body of the call
   * @param indices A list of indices to restrict the results
   * @param docTypes A list of types to restrict the results
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param analyzer The analyzer to use for the query string
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param minScore Include only documents with a specific `_score` value in the result
   * @param q Query in the Lucene query string syntax
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param df The field to use as default where no field prefix is given in the query string
   * @param routing Specific routing value
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def count(indices: Seq[String] = Nil,
            body: JsonObject = JsonObject.empty,
            expandWildcards: Seq[ExpandWildcards] = Nil,
            analyzer: Option[String] = None,
            preference: String = "random",
            defaultOperator: DefaultOperator = DefaultOperator.OR,
            analyzeWildcard: Boolean = false,
            minScore: Option[Double] = None,
            q: Option[String] = None,
            lenient: Option[Boolean] = None,
            df: Option[String] = None,
            routing: Option[String] = None,
            allowNoIndices: Option[Boolean] = None,
            ignoreUnavailable: Option[Boolean] = None): ZioResponse[CountResponse] = {
    val request = CountRequest(
      body = body,
      indices = indices,
      expandWildcards = expandWildcards,
      analyzer = analyzer,
      preference = preference,
      defaultOperator = defaultOperator,
      analyzeWildcard = analyzeWildcard,
      minScore = minScore,
      q = q,
      lenient = lenient,
      df = df,
      routing = routing,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable
    )

    count(request)

  }

  def count(request: CountRequest): ZioResponse[CountResponse] =
    this.execute(request)

  //
  //  def count(request:CountRequest):EitherT[Future, QDBException, CountResponse]= this.execute(request)
  //
  //  def count(query: Query, indices: Seq[String], docTypes: Seq[String]): EitherT[Future, QDBException, CountResponse] =
  //    count(query.toQueryJson, indices = indices.map { i => concreteIndex(Some(i)) }, docTypes = docTypes)

  //  def count(query: JsonObject, indices: Seq[String] = Nil, docTypes: Seq[String] = Nil,
  //            params: Map[String, String] = Map.empty[String, String]): CountResponse = {
  //    implicit val formats = DefaultFormats
  //    doCall("GET", makeUrl(indices, docTypes, "_count"), query, queryArgs = params).extract[CountResponse]
  //  }
  //

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete.html
   *
   * @param index The name of the index
   * @param docType The type of the document
   * @param id The document ID
   * @param parent ID of parent document
   * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param routing Specific routing value
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def delete(
      index: String,
      id: String,
      bulk: Boolean = false,
      parent: Option[String] = None,
      refresh: Option[elasticsearch.Refresh] = None,
      version: Option[Long] = None,
      versionType: Option[VersionType] = None,
      routing: Option[String] = None,
      timeout: Option[String] = None,
      waitForActiveShards: Option[String] = None
  )(implicit context: ESNoSqlContext): ZioResponse[DeleteResponse] = {
    //alias expansion
//    val realDocType = this.mappings.expandAliasType(concreteIndex(Some(index)))
    val ri = concreteIndex(Some(index))
    logger.debug(s"delete($ri, $id)")


    var request = DeleteRequest(
      index = concreteIndex(Some(index)),
      id = id,
      parent = parent,
      refresh = refresh,
      version = version,
      versionType = versionType,
      routing = routing,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    if (context.user.id != ESSystemUser.id) {
      val upUser=for {
        map <- this.mappings.get(concreteIndex(Some(index)))
      } yield {
        //we manage auto_owner objects
        val metaUser = map.meta.user
        if (metaUser.auto_owner) {
          request = request.copy(id = metaUser.processAutoOwnerId(id, context.user.id))
        }
      }
      context.environment.unsafeRun(upUser)
    }

    if (bulk) {
      this.addToBulk(request) *>
      ZIO.succeed(DeleteResponse(index = request.index, id = request.id))

    } else delete(request)
  }

  def delete(request: DeleteRequest): ZioResponse[DeleteResponse] =
    this.execute(request)

  /*
   * https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete-by-query.html
   *
   * @param indices A list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param docTypes A list of document types to search; leave empty to perform the operation on all types
   * @param body body the body of the call
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
   * @param refresh Should the effected indexes be refreshed?
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param stats Specific 'tag' of the request for logging and statistical purposes
   * @param analyzer The analyzer to use for the query string
   * @param size Number of hits to return (default: 10)
   * @param searchType Search operation type
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param scrollSize Size on the scroll request powering the update_by_query
   * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param conflicts What to do when the delete-by-query hits version conflicts?
   * @param version Specify whether to return document version as part of a hit
   * @param q Query in the Lucene query string syntax
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param from Starting offset (default: 0)
   * @param df The field to use as default where no field prefix is given in the query string
   * @param routing A list of specific routing values
   * @param searchTimeout Explicit timeout for each search request. Defaults to no timeout.
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param waitForCompletion Should the request should block until the delete-by-query is complete.
   * @param slices The number of slices this task should be divided into. Defaults to 1 meaning the task isn't sliced into subtasks.
   * @param sort A list of <field>:<direction> pairs
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param requestsPerSecond The throttle for this request in sub-requests per second. -1 means no throttle.
   * @param timeout Time each individual bulk request should wait for shards that are unavailable.
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete by query operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def deleteByQuery(
      indices: Seq[String] = Nil,
      body: Json,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      sourceInclude: Seq[String] = Nil,
      source: Seq[String] = Nil,
      requestCache: Option[Boolean] = None,
      refresh: Option[Boolean] = None,
      scroll: Option[String] = None,
      stats: Seq[String] = Nil,
      analyzer: Option[String] = None,
      size: Double = 10,
      searchType: Option[SearchType] = None,
      preference: String = "random",
      sourceExclude: Seq[String] = Nil,
      defaultOperator: DefaultOperator = DefaultOperator.OR,
      scrollSize: Option[Double] = None,
      terminateAfter: Option[Double] = None,
      analyzeWildcard: Boolean = false,
      conflicts: Seq[Conflicts] = Nil,
      version: Option[Boolean] = None,
      q: Option[String] = None,
      lenient: Option[Boolean] = None,
      from: Double = 0,
      df: Option[String] = None,
      routing: Seq[String] = Nil,
      searchTimeout: Option[String] = None,
      allowNoIndices: Option[Boolean] = None,
      waitForCompletion: Boolean = false,
      slices: Double = 1,
      sort: Seq[String] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      requestsPerSecond: Double = 0,
      timeout: String = "1m",
      waitForActiveShards: Option[String] = None
  ): ZioResponse[ActionByQueryResponse] = {
    val request = DeleteByQueryRequest(
      indices = indices,
      body = body,
      expandWildcards = expandWildcards,
      sourceInclude = sourceInclude,
      source = source,
      requestCache = requestCache,
      refresh = refresh,
      scroll = scroll,
      stats = stats,
      analyzer = analyzer,
      size = size,
      searchType = searchType,
      preference = preference,
      sourceExclude = sourceExclude,
      defaultOperator = defaultOperator,
      scrollSize = scrollSize,
      terminateAfter = terminateAfter,
      analyzeWildcard = analyzeWildcard,
      conflicts = conflicts,
      version = version,
      q = q,
      lenient = lenient,
      from = from,
      df = df,
      routing = routing,
      searchTimeout = searchTimeout,
      allowNoIndices = allowNoIndices,
      waitForCompletion = waitForCompletion,
      slices = slices,
      sort = sort,
      ignoreUnavailable = ignoreUnavailable,
      requestsPerSecond = requestsPerSecond,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    deleteByQuery(request)

  }

  def deleteByQuery(
                     index:String,
                     query: Query): ZioResponse[ActionByQueryResponse] =
    deleteByQuery(Seq(index), Json.obj("query" -> query.asJson))
  

  def deleteByQuery(request: DeleteByQueryRequest): ZioResponse[ActionByQueryResponse] =
    this.execute(request)


  /*
   * https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update-by-query.html
   *
   * @param indices A list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param docTypes A list of document types to search; leave empty to perform the operation on all types
   * @param body body the body of the call
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
   * @param refresh Should the effected indexes be refreshed?
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param stats Specific 'tag' of the request for logging and statistical purposes
   * @param analyzer The analyzer to use for the query string
   * @param size Number of hits to return (default: 10)
   * @param searchType Search operation type
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param scrollSize Size on the scroll request powering the update_by_query
   * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param conflicts What to do when the delete-by-query hits version conflicts?
   * @param version Specify whether to return document version as part of a hit
   * @param q Query in the Lucene query string syntax
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param from Starting offset (default: 0)
   * @param df The field to use as default where no field prefix is given in the query string
   * @param routing A list of specific routing values
   * @param searchTimeout Explicit timeout for each search request. Defaults to no timeout.
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param waitForCompletion Should the request should block until the delete-by-query is complete.
   * @param slices The number of slices this task should be divided into. Defaults to 1 meaning the task isn't sliced into subtasks.
   * @param sort A list of <field>:<direction> pairs
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param requestsPerSecond The throttle for this request in sub-requests per second. -1 means no throttle.
   * @param timeout Time each individual bulk request should wait for shards that are unavailable.
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete by query operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def updateByQuery(
                     indices: Seq[String] = Nil,
                     body: JsonObject,
                     expandWildcards: Seq[ExpandWildcards] = Nil,
                     sourceInclude: Seq[String] = Nil,
                     source: Seq[String] = Nil,
                     requestCache: Option[Boolean] = None,
                     refresh: Option[Boolean] = None,
                     scroll: Option[String] = None,
                     stats: Seq[String] = Nil,
                     analyzer: Option[String] = None,
                     size: Double = 10,
                     searchType: Option[SearchType] = None,
                     preference: String = "random",
                     sourceExclude: Seq[String] = Nil,
                     defaultOperator: DefaultOperator = DefaultOperator.OR,
                     scrollSize: Option[Double] = None,
                     terminateAfter: Option[Double] = None,
                     analyzeWildcard: Boolean = false,
                     conflicts: Seq[Conflicts] = Nil,
                     version: Option[Boolean] = None,
                     q: Option[String] = None,
                     lenient: Option[Boolean] = None,
                     from: Double = 0,
                     df: Option[String] = None,
                     routing: Seq[String] = Nil,
                     searchTimeout: Option[String] = None,
                     allowNoIndices: Option[Boolean] = None,
                     waitForCompletion: Boolean = false,
                     slices: Double = 1,
                     sort: Seq[String] = Nil,
                     ignoreUnavailable: Option[Boolean] = None,
                     requestsPerSecond: Double = 0,
                     timeout: String = "1m",
                     waitForActiveShards: Option[String] = None
                   ): ZioResponse[ActionByQueryResponse] = {
    val request = UpdateByQueryRequest(
      indices = indices,
      body = body,
      expandWildcards = expandWildcards,
      sourceInclude = sourceInclude,
      source = source,
      requestCache = requestCache,
      refresh = refresh,
      scroll = scroll,
      stats = stats,
      analyzer = analyzer,
      size = size,
      searchType = searchType,
      preference = preference,
      sourceExclude = sourceExclude,
      defaultOperator = defaultOperator,
      scrollSize = scrollSize,
      terminateAfter = terminateAfter,
      analyzeWildcard = analyzeWildcard,
      conflicts = conflicts,
      version = version,
      q = q,
      lenient = lenient,
      from = from,
      df = df,
      routing = routing,
      searchTimeout = searchTimeout,
      allowNoIndices = allowNoIndices,
      waitForCompletion = waitForCompletion,
      slices = slices,
      sort = sort,
      ignoreUnavailable = ignoreUnavailable,
      requestsPerSecond = requestsPerSecond,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    updateByQuery(request)

  }

  def updateByQuery(
                     index:String,
                     query: Query,
                     script: Script
                   ): ZioResponse[ActionByQueryResponse] =
    updateByQuery(Seq(index), JsonObject("query" -> query.asJson, "script" -> script.asJson))
  

  def updateByQuery(request: UpdateByQueryRequest): ZioResponse[ActionByQueryResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param lang Script language
   * @param id Script ID
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   */
  def deleteScript(lang: String,
                   id: Option[String] = None): ZioResponse[DeleteStoredScriptResponse] = {
    val request = DeleteStoredScriptRequest(lang = lang, id = id)

    deleteScript(request)

  }

  def deleteScript(
      request: DeleteStoredScriptRequest
  ): ZioResponse[DeleteStoredScriptResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param docType The type of the document (use `_all` to fetch the first document matching the ID across all types)
   * @param id The document ID
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param parent The ID of the parent document
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param routing Specific routing value
   * @param storedFields A list of stored fields to return in the response
   */
  def exists(index: String,
             id: String,
             sourceInclude: Seq[String] = Nil,
             parent: Option[String] = None,
             source: Seq[String] = Nil,
             refresh: Option[Boolean] = None,
             preference: String = "random",
             sourceExclude: Seq[String] = Nil,
             version: Option[Double] = None,
             versionType: Option[VersionType] = None,
             realtime: Option[Boolean] = None,
             routing: Option[String] = None,
             storedFields: Seq[String] = Nil): ZioResponse[Boolean] = {
    val request = ExistsRequest(
      index = index,
      id = id,
      sourceInclude = sourceInclude,
      parent = parent,
      source = source,
      refresh = refresh,
      preference = preference,
      sourceExclude = sourceExclude,
      version = version,
      versionType = versionType,
      realtime = realtime,
      routing = routing,
      storedFields = storedFields
    )

    exists(request)

  }

  def exists(request: ExistsRequest): ZioResponse[Boolean] =
    this.execute(request).map(_.exists())

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-explain.html
   *
   * @param index The name of the index
   * @param docType The type of the document
   * @param id The document ID
   * @param body body the body of the call
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param parent The ID of the parent document
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param analyzer The analyzer for the query string query
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param analyzeWildcard Specify whether wildcards and prefix queries in the query string query should be analyzed (default: false)
   * @param q Query in the Lucene query string syntax
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param df The default field for query string query (default: _all)
   * @param routing Specific routing value
   * @param storedFields A list of stored fields to return in the response
   */
  def explain(index: String,
              docType: String,
              id: String,
              body: Json,
              sourceInclude: Seq[String] = Nil,
              parent: Option[String] = None,
              source: Seq[String] = Nil,
              analyzer: Option[String] = None,
              preference: String = "random",
              sourceExclude: Seq[String] = Nil,
              defaultOperator: DefaultOperator = DefaultOperator.OR,
              analyzeWildcard: Boolean = false,
              q: Option[String] = None,
              lenient: Option[Boolean] = None,
              df: String = "_all",
              routing: Option[String] = None,
              storedFields: Seq[String] = Nil): ZioResponse[ExplainResponse] = {
    val request = ExplainRequest(
      index = index,
      docType = docType,
      id = id,
      body = body,
      sourceInclude = sourceInclude,
      parent = parent,
      source = source,
      analyzer = analyzer,
      preference = preference,
      sourceExclude = sourceExclude,
      defaultOperator = defaultOperator,
      analyzeWildcard = analyzeWildcard,
      q = q,
      lenient = lenient,
      df = df,
      routing = routing,
      storedFields = storedFields
    )

    explain(request)

  }

  def explain(request: ExplainRequest): ZioResponse[ExplainResponse] =
    this.execute(request)

  def getTyped[T: Encoder: Decoder](index: String, id: String)(
      implicit context: ESNoSqlContext
  ): ZioResponse[Option[ResultDocument[T]]] = 
    for {
      response <- get(concreteIndex(Some(index)), id)
    } yield {
      response.found match {
        case false => None
        case true =>
          Some(
            ResultDocument(
              id = response.id,
              index = response.index,
              docType = response.docType,
              version = if (response.version > 0) None else Some(response.version),
              iSource = Json.fromJsonObject(response.source).as[T],
              fields = Some(response.fields)
            )
          )
      }
    }
  

    /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-field-stats.html
   *
   * @param body body the body of the call
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param fields A list of fields for to get field statistics for (min value, max value, and more)
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param level Defines if field stats should be returned on a per index level or on a cluster wide level
   */
  def fieldStats(body: Json,
                 indices: Seq[String] = Nil,
                 expandWildcards: Seq[ExpandWildcards] = Nil,
                 fields: Seq[String] = Nil,
                 allowNoIndices: Option[Boolean] = None,
                 ignoreUnavailable: Option[Boolean] = None,
                 level: Level = Level.cluster): ZioResponse[FieldStatsResponse] = {
    val request = FieldStatsRequest(
      body = body,
      indices = indices,
      expandWildcards = expandWildcards,
      fields = fields,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable,
      level = level
    )

    fieldStats(request)

  }

  def fieldStats(request: FieldStatsRequest): ZioResponse[FieldStatsResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param parent The ID of the parent document
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param routing Specific routing value
   * @param storedFields A list of stored fields to return in the response
   */
  def get(
      index: String,
      id: String,
      sourceInclude: Seq[String] = Nil,
      parent: Option[String] = None,
      source: Seq[String] = Nil,
      refresh: Option[Boolean] = None,
      preference: String = "random",
      sourceExclude: Seq[String] = Nil,
      version: Option[Double] = None,
      versionType: Option[VersionType] = None,
      realtime: Option[Boolean] = None,
      routing: Option[String] = None
  )(implicit context: ESNoSqlContext): ZioResponse[GetResponse] = {
    // Custom Code On
    //alias expansion
    val ri = concreteIndex(Some(index))
    logger.debug(s"get($ri, $id)")

    var request = GetRequest(
      index = concreteIndex(Some(index)),
      id = id,
      sourceInclude = sourceInclude,
      parent = parent,
      source = source,
      refresh = refresh,
      preference = preference,
      sourceExclude = sourceExclude,
      version = version,
      versionType = versionType,
      realtime = realtime,
      routing = routing
      //,storedFields = storedFields
    )

    context.user match {
      case user if user.id == ESSystemUser.id =>
        get(request)
      case user =>
        //TODO add user to the request
        val mapping = context.environment.unsafeRun(this.mappings.get(concreteIndex(Some(index))))
        val metaUser = mapping.meta.user
        //we manage auto_owner objects
        if (metaUser.auto_owner) {
          request = request.copy(id = metaUser.processAutoOwnerId(id, user.id))
          get(request).flatMap { result =>
            if (result.found) {
              ZIO.succeed(result)
            } else {
              get(request.copy(id = id)) //TODO exception in it' missing
            }
          }

        } else {
          get(request)
        }
    }
  }

  def get(
      request: GetRequest
  )(implicit context: ESNoSqlContext): ZioResponse[GetResponse] =
    this.execute(request)

  def getLongField(index: String, id: String, field: String)(
      implicit context: ESNoSqlContext
  ): ZioResponse[Option[Long]] = for {
    resp <- get(index, id)
  } yield {
      CirceUtils
        .resolveSingleField[Long](resp.source, field)
        .toOption
    }

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param lang Script language
   * @param id Script ID
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   */
  def getScript(lang: String, id: String): ZioResponse[GetStoredScriptResponse] = {
    val request = GetStoredScriptRequest(lang, id)
    getScript(request)

  }

  def getScript(request: GetStoredScriptRequest): ZioResponse[GetStoredScriptResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param docType The type of the document; use `_all` to fetch the first document matching the ID across all types
   * @param id The document ID
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param parent The ID of the parent document
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param routing Specific routing value
   */
  def getSource(index: String,
                id: String,
                sourceInclude: Seq[String] = Nil,
                parent: Option[String] = None,
                source: Seq[String] = Nil,
                refresh: Option[Boolean] = None,
                preference: String = "random",
                sourceExclude: Seq[String] = Nil,
                version: Option[Double] = None,
                versionType: Option[VersionType] = None,
                realtime: Option[Boolean] = None,
                routing: Option[String] = None): ZioResponse[GetResponse] = {

    val request = GetRequest(
      index = concreteIndex(Some(index)),
      id = id,
      sourceInclude = sourceInclude,
      parent = parent,
      source = source,
      refresh = refresh,
      preference = preference,
      sourceExclude = sourceExclude,
      version = version,
      versionType = versionType,
      realtime = realtime,
      routing = routing
    )

    getSource(request)

  }

  def getSource(request: GetRequest): ZioResponse[GetResponse] =
    this.execute(request)

  def indexDocument(index: String, id: String, document: JsonObject)(
      implicit noSQLContextManager: ESNoSqlContext
  ): ZioResponse[IndexResponse] = {
    val currID = if (id.trim.isEmpty) None else Some(id)
    indexDocument(concreteIndex(Some(index)), currID, body = document) //.map(r => propagateLink(r, body = document))
  }

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
   *
   * @param index The name of the index
   * @param docType The type of the document
   * @param id Document ID
   * @param body body the body of the call
   * @param parent ID of the parent document
   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param timestamp Explicit timestamp for the document
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param routing Specific routing value
   * @param ttl Expiration time for the document
   * @param opType Explicit operation type
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def indexDocument(
      index2: String,
      id: Option[String] = None,
      body: JsonObject,
      parent: Option[String] = None,
      refresh: Option[elasticsearch.Refresh] = None,
      timestamp: Option[String] = None,
      pipeline: Option[String] = None,
      version: Option[Long] = None,
      versionType: Option[VersionType] = None,
      routing: Option[String] = None,
      ttl: Option[Long] = None,
      opType: OpType = OpType.index,
      timeout: Option[String] = None,
      waitForActiveShards: Option[Double] = None,
      bulk: Boolean = false
  )(implicit noSQLContextManager: ESNoSqlContext): ZioResponse[IndexResponse] = {
    //alias expansion
//    val mapping = Try(noSQLContextManager.environment.unsafeRun(this.mappings.get(concreteIndex(Some(index2))))).toOption
    //
    val request = IndexRequest(
      index = concreteIndex(Some(index2)),
      id = id,
      body = body,
      parent = parent,
      refresh = refresh,
      timestamp = timestamp,
      pipeline = pipeline,
      version = version,
      versionType = versionType,
      routing = routing,
      ttl = ttl,
      opType = opType,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    def applyMappingChanges(mapping:RootDocumentMapping, request:IndexRequest):IndexRequest=
        if (id.isDefined) {
          noSQLContextManager.user match {
            case u if u.id==ESSystemUser.id => request
            case u =>
              val metaUser = mapping.meta.user
              if (metaUser.auto_owner) {
                request.copy(id = Some(metaUser.processAutoOwnerId(id.get, u.id)))
              } else request
          }
        } else {
          noSQLContextManager.user match {
            case user if user.id == ESSystemUser.id => request
            case u =>
              val metaUser = mapping.meta.user
              if (metaUser.auto_owner) {
                request.copy(id = Some(u.id))
              } else request
          }
        }
    
    def applyReqOrBulk(request: IndexRequest, bulk:Boolean):ZioResponse[IndexResponse]=
      if (bulk) {
        this.addToBulk(request) *>
          ZIO.succeed(
            IndexResponse(
              shards = Shards.empty,
              index = request.index,
              id = request.id.getOrElse(""),
              version = 0
            )
          )

      } else
        indexDocument(request)
    
    for {
       req <- this.mappings.get(concreteIndex(Some(index2))).fold[IndexRequest](_ => request, m => applyMappingChanges(m, request))
        res <- applyReqOrBulk(req, bulk)
    } yield res


  }

  def indexDocument(
      request: IndexRequest
  )(implicit noSQLContextManager: ESNoSqlContext): ZioResponse[IndexResponse] =
    this.execute(request)

  /* QDB ON */
  def mget[T: Encoder: Decoder](index: String,
                                docType: String,
                                ids: List[String]): ZioResponse[List[ResultDocument[T]]] =
    mget(ids.map(i => (concreteIndex(Some(index)), docType, i))).map { result =>
      result.docs
        .filter(m => m.found)
        .map(r => ResultDocument.fromGetResponse[T](r))
    }

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-get.html
   *
   * @param body body the body of the call
   * @param index The name of the index
   * @param docType The type of the document
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param routing Specific routing value
   * @param storedFields A list of stored fields to return in the response
   */
  def mget(body: Seq[(String, String, String)],
           index: Option[String] = None,
           docType: Option[String] = None,
           sourceInclude: Seq[String] = Nil,
           source: Seq[String] = Nil,
           refresh: Option[Boolean] = None,
           preference: String = "random",
           sourceExclude: Seq[String] = Nil,
           realtime: Option[Boolean] = None,
           routing: Option[String] = None,
           storedFields: Seq[String] = Nil): ZioResponse[MultiGetResponse] = {

    val bodyJson = Json.obj(
      "docs" ->
        Json.fromValues(body.map(v => Json.obj("_index" -> v._1.asJson, "_type" -> v._2.asJson, "_id" -> v._3.asJson)))
    )

    val request = MultiGetRequest(
      body = bodyJson,
      index = index,
      docType = docType,
      sourceInclude = sourceInclude,
      source = source,
      refresh = refresh,
      preference = preference,
      sourceExclude = sourceExclude,
      realtime = realtime,
      routing = routing,
      storedFields = storedFields
    )
    mget(request)

  }

  /* QDB OFF */

  def mget(request: MultiGetRequest): ZioResponse[MultiGetResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-multi-search.html
   *
   * @param body body the body of the call
   * @param indices A list of index names to use as default
   * @param docTypes A list of document types to use as default
   * @param searchType Search operation type
   * @param maxConcurrentSearches Controls the maximum number of concurrent searches the multi search api will execute
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   */
  def msearch(body: Seq[String] = Nil,
              indices: Seq[String] = Nil,
              docTypes: Seq[String] = Nil,
              searchType: Option[SearchType] = None,
              maxConcurrentSearches: Option[Double] = None,
              typedKeys: Option[Boolean] = None): ZioResponse[MultiSearchResponse] = {
    val request = MultiSearchRequest(
      body = body,
      indices = indices,
      docTypes = docTypes,
      searchType = searchType,
      maxConcurrentSearches = maxConcurrentSearches,
      typedKeys = typedKeys
    )

    msearch(request)

  }

  def msearch(request: MultiSearchRequest): ZioResponse[MultiSearchResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-termvectors.html
   *
   * @param body body the body of the call
   * @param index The index in which the document resides.
   * @param docType The type of the document.
   * @param parent Parent id of documents. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param preference Specify the node or shard the operation should be performed on (default: random) .Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param payloads Specifies if term payloads should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param offsets Specifies if term offsets should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param termStatistics Specifies if total term frequency and document frequency should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param version Explicit version number for concurrency control
   * @param positions Specifies if term positions should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param versionType Specific version type
   * @param fields A list of fields to return. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param realtime Specifies if requests are real-time as opposed to near-real-time (default: true).
   * @param routing Specific routing value. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param ids A list of documents ids. You must define ids as parameter or set "ids" or "docs" in the request body
   */
  def mtermvectors(body: Json,
                   index: Option[String] = None,
                   docType: Option[String] = None,
                   parent: Option[String] = None,
                   preference: String = "random",
                   fieldStatistics: Boolean = true,
                   payloads: Boolean = true,
                   offsets: Boolean = true,
                   termStatistics: Boolean = false,
                   version: Option[Double] = None,
                   positions: Boolean = true,
                   versionType: Option[VersionType] = None,
                   fields: Seq[String] = Nil,
                   realtime: Boolean = true,
                   routing: Option[String] = None,
                   ids: Seq[String] = Nil): ZioResponse[MultiTermVectorsResponse] = {
    val request = MultiTermVectorsRequest(
      body = body,
      index = index,
      docType = docType,
      parent = parent,
      preference = preference,
      fieldStatistics = fieldStatistics,
      payloads = payloads,
      offsets = offsets,
      termStatistics = termStatistics,
      version = version,
      positions = positions,
      versionType = versionType,
      fields = fields,
      realtime = realtime,
      routing = routing,
      ids = ids
    )

    mtermvectors(request)

  }

  def mtermvectors(
      request: MultiTermVectorsRequest
  ): ZioResponse[MultiTermVectorsResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param lang Script language
   * @param id Script ID
   * @param body body the body of the call
   */
  def putScript(lang: String,
                id: String,
                body: Json): ZioResponse[PutStoredScriptResponse] = {
    val request = PutStoredScriptRequest(lang = lang, id = id, body = body)
    putScript(request)

  }

  def putScript(request: PutStoredScriptRequest): ZioResponse[PutStoredScriptResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-scroll.html
   *
   * @param body body the body of the call
   * @param scrollId The scroll ID
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   */
  def scroll(
             //              body: Json,
             scrollId: String,
             scroll: Option[String] = None): ZioResponse[SearchResponse] = {
    val request = new SearchScrollRequest(scrollId, scroll)
    this.execute(request)
  }

  def scroll(request: SearchScrollRequest): ZioResponse[SearchResponse] =
    this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-search.html
   *
   * @param body body the body of the call
   * @param indices A list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param docTypes A list of document types to search; leave empty to perform the operation on all types
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param stats Specific 'tag' of the request for logging and statistical purposes
   * @param analyzer The analyzer to use for the query string
   * @param size Number of hits to return (default: 10)
   * @param explain Specify whether to return detailed information about score computation as part of a hit
   * @param searchType Search operation type
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param fielddataFields A list of fields to return as the field data representation of a field for each hit
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param batchedReduceSize The number of shard results that should be reduced at once on the coordinating node. This value should be used as a protection mechanism to reduce the memory overhead per search request if the potential number of shards in the request can be large.
   * @param suggestMode Specify suggest mode
   * @param version Specify whether to return document version as part of a hit
   * @param suggestText The source text for which the suggestions should be returned
   * @param trackScores Whether to calculate and return scores even if they are not used for sorting
   * @param q Query in the Lucene query string syntax
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param from Starting offset (default: 0)
   * @param suggestSize How many suggestions to return in response
   * @param df The field to use as default where no field prefix is given in the query string
   * @param routing A list of specific routing values
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param lowercaseExpandedTerms Specify whether query terms should be lowercased
   * @param sort A list of <field>:<direction> pairs
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param timeout Explicit operation timeout
   * @param storedFields A comma-separated list of stored fields to return as part of a hit
   * @param suggestField Specify which field to use for suggestions
   * @param docvalueFields A comma-separated list of fields to return as the docvalue representation of a field for each hit
   */
  def searchRaw(body: Json,
                indices: Seq[String] = Nil,
                docTypes: Seq[String] = Nil,
                expandWildcards: Seq[ExpandWildcards] = Nil,
                typedKeys: Option[Boolean] = None,
                sourceInclude: Seq[String] = Nil,
                source: Seq[String] = Nil,
                requestCache: Option[Boolean] = None,
                scroll: Option[String] = None,
                stats: Seq[String] = Nil,
                analyzer: Option[String] = None,
                size: Int = 10,
                explain: Option[Boolean] = None,
                searchType: Option[SearchType] = None,
                preference: String = "random",
                fielddataFields: Seq[String] = Nil,
                sourceExclude: Seq[String] = Nil,
                defaultOperator: DefaultOperator = DefaultOperator.OR,
                terminateAfter: Option[Double] = None,
                analyzeWildcard: Boolean = false,
                batchedReduceSize: Int = 512,
                suggestMode: SuggestMode = SuggestMode.missing,
                version: Option[Boolean] = None,
                suggestText: Option[String] = None,
                trackScores: Option[Boolean] = None,
                q: Option[String] = None,
                lenient: Option[Boolean] = None,
                from: Int = 0,
                suggestSize: Option[Int] = None,
                df: Option[String] = None,
                routing: Option[String] = None,
                allowNoIndices: Option[Boolean] = None,
                sort: Seq[String] = Nil,
                ignoreUnavailable: Option[Boolean] = None,
                timeout: Option[String] = None,
                storedFields: Seq[String] = Nil,
                suggestField: Option[String] = None,
                docvalueFields: Seq[String] = Nil): ZioResponse[SearchResponse] = {
    val request = SearchRequest(
      body = body,
      indices = indices.map { i =>
        concreteIndex(Some(i))
      },
      docTypes = docTypes,
      expandWildcards = expandWildcards,
      typedKeys = typedKeys,
      sourceInclude = sourceInclude,
      source = source,
      requestCache = requestCache,
      scroll = scroll,
      stats = stats,
      analyzer = analyzer,
      size = size,
      explain = explain,
      searchType = searchType,
      preference = preference,
      fielddataFields = fielddataFields,
      sourceExclude = sourceExclude,
      defaultOperator = defaultOperator,
      terminateAfter = terminateAfter,
      analyzeWildcard = analyzeWildcard,
      batchedReduceSize = batchedReduceSize,
      suggestMode = suggestMode,
      version = version,
      suggestText = suggestText,
      trackScores = trackScores,
      q = q,
      lenient = lenient,
      from = from,
      suggestSize = suggestSize,
      df = df,
      routing = routing,
      allowNoIndices = allowNoIndices,
      sort = sort,
      ignoreUnavailable = ignoreUnavailable,
      timeout = timeout,
      storedFields = storedFields,
      suggestField = suggestField,
      docvalueFields = docvalueFields
    )

    search(request)

  }

  def search(request: SearchRequest): ZioResponse[SearchResponse] =
    this.execute(request)

  //
  //  /*
  //   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-shards.html
  //   *
  //   * @param indices A list of index names to search; use `_all` or empty string to perform the operation on all indices
  //   * @param docTypes A list of document types to search; leave empty to perform the operation on all types
  //   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
  //   * @param preference Specify the node or shard the operation should be performed on (default: random)
  //   * @param local Return local information, do not retrieve the state from master node (default: false)
  //   * @param routing Specific routing value
  //   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
  //   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
  //   */
  //  def searchShards(
  //      index: String,
  //      docType: String,
  //      expandWildcards: Seq[ExpandWildcards] = Nil,
  //      preference: String = "random",
  //      local: Boolean = false,
  //      routing: Option[String] = None,
  //      allowNoIndices: Option[Boolean] = None,
  //      ignoreUnavailable: Option[Boolean] = None): EitherT[Future, QDBException, ClusterSearchShardsResponse] = {
  //    val request = ClusterSearchShardsRequest(Seq(concreteIndex(Some(index))),
  //
  //    )
  //    request.types(docType)
  //    //    if (expandWildcards != "open") request.expandWildcards(expandWildcards)
  //    if (preference != "random") request.preference(preference)
  //    if (!local) request.local(local)
  //    if (routing.isDefined) request.routing(routing.get)
  //    //    if (allowNoIndices.isDefined) request.allowNoIndices(allowNoIndices.get)
  //    //    if (ignoreUnavailable.isDefined) request.ignoreUnavailable(ignoreUnavailable.get)
  //    searchShards(request)
  //
  //  }
  //
  //  def searchShards(request: ClusterSearchShardsRequest): EitherT[Future, QDBException, ClusterSearchShardsResponse] =
  //    this.execute(request)
  //
  //  /*
  //   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-suggesters.html
  //   *
  //   * @param body body the body of the call
  //   * @param indices A list of index names to restrict the operation; use `_all` or empty string to perform the operation on all indices
  //   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
  //   * @param preference Specify the node or shard the operation should be performed on (default: random)
  //   * @param routing Specific routing value
  //   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
  //   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
  //   */
  //  def suggest(suggest: String,
  //              indices: Seq[String] = Nil,
  //              expandWildcards: Seq[ExpandWildcards] = Nil,
  //              source: Option[String] = None,
  //              preference: String = "random",
  //              routing: List[String] = Nil,
  //              allowNoIndices: Option[Boolean] = None,
  //              ignoreUnavailable: Option[Boolean] = None): EitherT[Future, QDBException, SuggestResponse] = {
  //    val request = new SuggestRequest(indices.map { i =>
  //      concreteIndex(Some(i))
  //    }: _*).suggest(suggest)
  //    //    if (expandWildcards != "open") request.expandWildcards(expandWildcards)
  //    //    if (source.isDefined) request.source(source)
  //    if (preference != "random") request.preference(preference)
  //    if (routing.nonEmpty) request.routing(routing: _*)
  //    //    if (allowNoIndices.isDefined) request.allowNoIndices(allowNoIndices)
  //    //    if (ignoreUnavailable.isDefined) request.ignoreUnavailable(ignoreUnavailable)
  //    this.suggest(request)
  //
  //  }
  //
  //  def suggest(request: SuggestRequest): EitherT[Future, QDBException, SuggestResponse] = this.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update.html
   *
   * @param index The name of the index
   * @param docType The type of the document
   * @param id Document ID
   * @param body body the body of the call
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param parent ID of the parent document. Is is only used for routing and when for the upsert request
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param timestamp Explicit timestamp for the document
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param version Explicit version number for concurrency control
   * @param retryOnConflict Specify how many times should the operation be retried when a conflict occurs (default: 0)
   * @param versionType Specific version type
   * @param fields A list of fields to return in the response
   * @param routing Specific routing value
   * @param lang The script language (default: painless)
   * @param ttl Expiration time for the document
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the update operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def update(index: String,
             docType: String,
             id: String,
             body: JsonObject,
             sourceInclude: Seq[String] = Nil,
             parent: Option[String] = None,
             source: Seq[String] = Nil,
             refresh: elasticsearch.Refresh = elasticsearch.Refresh.`false`,
             timestamp: Option[String] = None,
             sourceExclude: Seq[String] = Nil,
             version: Option[Long] = None,
             retryOnConflict: Int = 0,
             versionType: Option[VersionType] = None,
             routing: Option[String] = None,
             timeout: Option[String] = None,
             waitForActiveShards: Int = 1,
             bulk: Boolean = false): ZioResponse[UpdateResponse] = {
    val request = UpdateRequest(
      index = index,
      docType = docType,
      id = id,
      body = body,
      sourceInclude = sourceInclude,
      parent = parent,
      source = source,
      refresh = refresh,
      timestamp = timestamp,
      sourceExclude = sourceExclude,
      version = version,
      retryOnConflict = retryOnConflict,
      versionType = versionType,
      routing = routing,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    if (bulk) {
      this.addToBulk(request) *>
      ZIO.succeed(
          UpdateResponse(index = index, id = id)
      )
    } else  update(request)

  }

  def update(request: UpdateRequest): ZioResponse[UpdateResponse] =
    //    if (request.doc != null) {
    //      val body = preprocessLink(request.index(), request.`type`(), Json.parse(request.doc.safeSource.toUtf8).as[JsonObject])
    //      this.execute(request.doc(body.toString)).map(postprocessLinkUpdate)
    //    } else {
    //      this.execute(request).map(postprocessLinkUpdate)
    //    }
    this.execute(request)

  def countAll(indices: Seq[String], types: Seq[String], filters:List[Query]=Nil)(
      implicit nosqlContext: ESNoSqlContext
  ): ZioResponse[Long] = {
    val qb = QueryBuilder(indices = indices, docTypes = types, size = 0, filters=filters)
    qb.results.map(_.total.value)
  }

  def countAll(index: String)(implicit nosqlContext: ESNoSqlContext): ZioResponse[Long] =
    countAll(indices = List(index), types = Nil)


  def countAll(index: String,
               types: Option[String],
               filters:List[Query])(implicit nosqlContext: ESNoSqlContext): ZioResponse[Long] =
    countAll(indices = List(index), types = types.toList)
}
// format: on
