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

package elasticsearch

import elasticsearch.ElasticSearchService.ElasticSearchService
import elasticsearch.client.IndicesActionResolver
import elasticsearch.mappings.RootDocumentMapping
import elasticsearch.requests.indices._
import elasticsearch.responses.indices._
import io.circe._
import io.circe.syntax._
import zio._
import zio.circe.CirceUtils
import zio.exception.FrameworkException
import zio.logging.Logging

object IndicesService {
  type IndicesService = Has[Service]

  trait Service extends IndicesActionResolver {
    def loggingService: Logging.Service
    def client: ElasticSearchService.Service
    var defaultSettings = Settings.ElasticSearchBase
    var defaultTestSettings = Settings.ElasticSearchTestBase
    //default settings to build index
    //  var defaultIndex = "default"
    var alias = Set.empty[String]

    def refreshA(): ZioResponse[Unit] =
      for {
        _ <- flushBulk(false)
        _ <- refresh(Nil)
        dir <- client.dirty
        _ <- dir.set(false)
      } yield ()

    def exists(
      indices: String*
    ): ZioResponse[IndicesExistsResponse] =
      exists(indices)

    def flush(
      indices: String*
    ): ZioResponse[IndicesFlushResponse] =
      flush(indices)

    def refresh(): ZioResponse[IndicesRefreshResponse] =
      refresh(Nil)

    def refresh(
      index: String,
      indices: String*
    ): ZioResponse[IndicesRefreshResponse] =
      refresh(index +: indices)

    def flushBulk(
      async: Boolean = false
    ): ZioResponse[IndicesFlushResponse] =
      for {
        blkr <- client.bulker
        _ <- blkr.flushBulk()
      } yield IndicesFlushResponse()

    /*
     * Performs the analysis process on a text and return the tokens breakdown of the text.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-analyze.html
     *
     * @param body body the body of the call
     * @param index The name of the index to scope the operation
     */
    def analyze(body: JsonObject, index: Option[String] = None): ZioResponse[IndicesAnalyzeResponse] = {
      val request = IndicesAnalyzeRequest(body = body, index = index)

      analyze(request)

    }

    def analyze(request: IndicesAnalyzeRequest): ZioResponse[IndicesAnalyzeResponse] =
      execute(request)

    /*
     * Clears all or specific caches for one or more indices.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clearcache.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param fielddata Clear field data
     * @param fields A comma-separated list of fields to clear when using the `fielddata` parameter (default: all)
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param index A comma-separated list of index name to limit the operation
     * @param indices A comma-separated list of index name to limit the operation
     * @param query Clear query caches
     * @param request Clear request cache
     */
    def clearCache(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      fielddata: Option[Boolean] = None,
      fields: Seq[String] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      index: Seq[String] = Nil,
      indices: Seq[String] = Nil,
      query: Option[Boolean] = None,
      request: Option[Boolean] = None
    ): ZioResponse[IndicesClearCacheResponse] = {
      val requestI = IndicesClearCacheRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        fielddata = fielddata,
        fields = fields,
        ignoreUnavailable = ignoreUnavailable,
        index = index,
        indices = indices,
        query = query,
        request = request
      )

      clearCache(requestI)

    }

    def clearCache(request: IndicesClearCacheRequest): ZioResponse[IndicesClearCacheResponse] = execute(request)

    /*
     * Clones an index
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clone-index.html
     *
     * @param index The name of the source index to clone
     * @param target The name of the target index to clone into
     * @param body body the body of the call
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit operation timeout
     * @param waitForActiveShards Set the number of active shards to wait for on the cloned index before the operation returns.
     */
    def clone(
      index: String,
      target: String,
      body: Option[JsonObject] = None,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None,
      waitForActiveShards: Option[String] = None
    ): ZioResponse[IndicesCloneResponse] = {
      val request = IndicesCloneRequest(
        index = index,
        target = target,
        body = body,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )

      clone(request)

    }

    def clone(request: IndicesCloneRequest): ZioResponse[IndicesCloneResponse] =
      execute(request)

    /*
     * Closes an index.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
     *
     * @param index A comma separated list of indices to close
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit operation timeout
     * @param waitForActiveShards Sets the number of active shards to wait for before the operation returns.
     */
    def close(
      index: String,
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None,
      waitForActiveShards: Option[String] = None
    ): ZioResponse[IndicesCloseResponse] = {
      val request = IndicesCloseRequest(
        index = index,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )

      close(request)

    }

    def close(request: IndicesCloseRequest): ZioResponse[IndicesCloseResponse] =
      execute(request)

    /*
     * Creates an index with optional settings and mappings.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-create-index.html
     *
     * @param index The name of the index
     * @param body body the body of the call
     * @param includeTypeName Whether a type should be expected in the body of the mappings.
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit operation timeout
     * @param waitForActiveShards Set the number of active shards to wait for before the operation returns.
     */
    def createIfNotExists(
      index: String,
      body: JsonObject = JsonObject.empty,
      waitForActiveShards: Option[Int] = None,
      timeout: Option[String] = None,
      masterTimeout: Option[String] = None
    ): ZioResponse[Unit] =
      for {
        existsRes <- exists(index)
        _ <- create(
          IndicesCreateRequest(
            index = index,
            body = CirceUtils.cleanValue(body), // we remove null
            waitForActiveShards = waitForActiveShards,
            timeout = timeout,
            masterTimeout = masterTimeout
          )
        ).when(existsRes.exists)
      } yield ()

    def create(
      index: String,
      body: JsonObject = JsonObject.empty,
      includeTypeName: Option[Boolean] = None,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None,
      waitForActiveShards: Option[Int] = None
    ): ZioResponse[IndicesCreateResponse] = {
      val request = IndicesCreateRequest(
        index = index,
        body = body,
        includeTypeName = includeTypeName,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )

      create(request)

    }

    def create(request: IndicesCreateRequest): ZioResponse[IndicesCreateResponse] =
      execute(request)

    /*
     * Deletes an index.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-delete-index.html
     *
     * @param indices A comma-separated list of indices to delete; use `_all` or `*` string to delete all indices
     * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
     * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
     * @param ignoreUnavailable Ignore unavailable indexes (default: false)
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit operation timeout
     */
    def delete(
      indices: Seq[String] = Nil,
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None
    ): ZioResponse[IndicesDeleteResponse] = {
      val request = IndicesDeleteRequest(
        indices = indices,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        timeout = timeout
      )

      delete(request)

    }

    def delete(request: IndicesDeleteRequest): ZioResponse[IndicesDeleteResponse] =
      execute(request)

    /*
     * Deletes an alias.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
     *
     * @param indices A comma-separated list of index names (supports wildcards); use `_all` for all indices
     * @param name A comma-separated list of aliases to delete (supports wildcards); use `_all` to delete all aliases for the specified indices.
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit timestamp for the document
     */
    def deleteAlias(
      names: Seq[String] = Nil,
      indices: Seq[String] = Nil,
      timeout: Option[String] = None,
      masterTimeout: Option[String] = None
    ): ZioResponse[IndicesDeleteAliasResponse] = {

      val request =
        IndicesDeleteAliasRequest(
          name = names,
          indices = indices,
          timeout = timeout,
          masterTimeout = masterTimeout
        )
      deleteAlias(request)

    }

    def deleteAlias(
      request: IndicesDeleteAliasRequest
    ): ZioResponse[IndicesDeleteAliasResponse] =
      execute(request)

    def addAlias(
      alias: String,
      indices: List[String]
    ): ZioResponse[IndicesPutAliasResponse] =
      putAlias(indices = indices, name = alias)

    /*
     * Deletes an index template.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
     *
     * @param name The name of the template
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit operation timeout
     */
    def deleteTemplate(
      name: String,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None
    ): ZioResponse[IndicesDeleteTemplateResponse] = {
      val request = IndicesDeleteTemplateRequest(name = name, masterTimeout = masterTimeout, timeout = timeout)

      deleteTemplate(request)

    }

    def deleteTemplate(request: IndicesDeleteTemplateRequest): ZioResponse[IndicesDeleteTemplateResponse] =
      execute(request)

    def exists(
      index: String
    ): ZioResponse[IndicesExistsResponse] =
      exists(Seq(index))

    def existsAsBoolean(
      index: String
    ): ZioResponse[Boolean] =
      exists(Seq(index)).map(_.exists)

    /*
     * Returns information about whether a particular index exists.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-exists.html
     *
     * @param indices A comma-separated list of index names
     * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
     * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
     * @param flatSettings Return settings in flat format (default: false)
     * @param ignoreUnavailable Ignore unavailable indexes (default: false)
     * @param includeDefaults Whether to return all default setting for each of the indices.
     * @param local Return local information, do not retrieve the state from master node (default: false)
     */
    def exists(
      indices: Seq[String] = Nil,
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      flatSettings: Option[Boolean] = None,
      ignoreUnavailable: Option[Boolean] = None,
      includeDefaults: Boolean = false,
      local: Option[Boolean] = None
    ): ZioResponse[IndicesExistsResponse] = {
      val request = IndicesExistsRequest(
        indices = indices,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flatSettings = flatSettings,
        ignoreUnavailable = ignoreUnavailable,
        includeDefaults = includeDefaults,
        local = local
      )

      exists(request)

    }

    def exists(request: IndicesExistsRequest): ZioResponse[IndicesExistsResponse] =
      execute(request)

    /*
     * Returns information about whether a particular alias exists.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
     *
     * @param name A comma-separated list of alias names to return
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names to filter aliases
     * @param local Return local information, do not retrieve the state from master node (default: false)
     */
    def existsAlias(
      name: Seq[String] = Nil,
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      local: Option[Boolean] = None
    ): ZioResponse[IndicesExistsAliasResponse] = {
      val request = IndicesExistsAliasRequest(
        name = name,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        local = local
      )

      existsAlias(request)

    }

    def existsAlias(request: IndicesExistsAliasRequest): ZioResponse[IndicesExistsAliasResponse] =
      execute(request)

    /*
     * Returns information about whether a particular index template exists.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
     *
     * @param name The comma separated names of the index templates
     * @param flatSettings Return settings in flat format (default: false)
     * @param local Return local information, do not retrieve the state from master node (default: false)
     * @param masterTimeout Explicit operation timeout for connection to master node
     */
    def existsTemplate(
      name: String,
      flatSettings: Option[Boolean] = None,
      local: Option[Boolean] = None,
      masterTimeout: Option[String] = None
    ): ZioResponse[Boolean] = {
      val request = IndicesExistsTemplateRequest(
        name = name,
        flatSettings = flatSettings,
        local = local,
        masterTimeout = masterTimeout
      )

      existsTemplate(request).map(_.contains(name))

    }

    def existsTemplate(
      request: IndicesExistsTemplateRequest
    ): ZioResponse[IndicesExistsTemplateResponse] =
      execute(request)

    /*
     * Performs the flush operation on one or more indices.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-flush.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param force Whether a flush should be forced even if it is not necessarily needed ie. if no changes will be committed to the index. This is useful if transaction log IDs should be incremented even if no uncommitted changes are present. (This setting can be considered as internal)
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names; use `_all` or empty string for all indices
     * @param waitIfOngoing If set to true the flush operation will block until the flush can be executed if another flush operation is already executing. The default is true. If set to false the flush will be skipped iff if another flush operation is already running.
     */
    def flush(
      indices: Seq[String] = Nil,
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      force: Option[Boolean] = None,
      ignoreUnavailable: Option[Boolean] = None,
      waitIfOngoing: Option[Boolean] = None
    ): ZioResponse[IndicesFlushResponse] = {
      val request = IndicesFlushRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        force = force,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        waitIfOngoing = waitIfOngoing
      )

      flush(request)

    }

    def flush(
      request: IndicesFlushRequest
    ): ZioResponse[IndicesFlushResponse] =
      execute(request)

    /*
     * Performs a synced flush operation on one or more indices.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-synced-flush-api.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names; use `_all` or empty string for all indices
     */
    def flushSynced(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil
    ): ZioResponse[IndicesFlushSyncedResponse] = {
      val request = IndicesFlushSyncedRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices
      )

      flushSynced(request)

    }

    def flushSynced(request: IndicesFlushSyncedRequest): ZioResponse[IndicesFlushSyncedResponse] =
      execute(request)

    /*
     * Performs the force merge operation on one or more indices.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-forcemerge.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param flush Specify whether the index should be flushed after performing the operation (default: true)
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     * @param maxNumSegments The number of segments the index should be merged into (default: dynamic)
     * @param onlyExpungeDeletes Specify whether the operation should only expunge deleted documents
     */
    def forcemerge(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      flush: Option[Boolean] = None,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      maxNumSegments: Option[Double] = None,
      onlyExpungeDeletes: Option[Boolean] = None
    ): ZioResponse[IndicesForcemergeResponse] = {
      val request = IndicesForcemergeRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flush = flush,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        maxNumSegments = maxNumSegments,
        onlyExpungeDeletes = onlyExpungeDeletes
      )

      forcemerge(request)

    }

    def forcemerge(
      request: IndicesForcemergeRequest
    ): ZioResponse[IndicesForcemergeResponse] = execute(request)

    /*
     * Returns information about one or more indices.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-index.html
     *
     * @param indices A comma-separated list of index names
     * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
     * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
     * @param flatSettings Return settings in flat format (default: false)
     * @param ignoreUnavailable Ignore unavailable indexes (default: false)
     * @param includeDefaults Whether to return all default setting for each of the indices.
     * @param includeTypeName Whether to add the type name to the response (default: false)
     * @param local Return local information, do not retrieve the state from master node (default: false)
     * @param masterTimeout Specify timeout for connection to master
     */
    def get(
      indices: Seq[String] = Nil,
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      flatSettings: Option[Boolean] = None,
      ignoreUnavailable: Option[Boolean] = None,
      includeDefaults: Boolean = false,
      includeTypeName: Option[Boolean] = None,
      local: Option[Boolean] = None,
      masterTimeout: Option[String] = None
    ): ZioResponse[IndicesGetResponse] = {
      val request = IndicesGetRequest(
        indices = indices,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flatSettings = flatSettings,
        ignoreUnavailable = ignoreUnavailable,
        includeDefaults = includeDefaults,
        includeTypeName = includeTypeName,
        local = local,
        masterTimeout = masterTimeout
      )

      get(request)

    }

    def get(
      request: IndicesGetRequest
    ): ZioResponse[IndicesGetResponse] =
      execute(request)

    /*
     * Returns an alias.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names to filter aliases
     * @param local Return local information, do not retrieve the state from master node (default: false)
     * @param name A comma-separated list of alias names to return
     */
    def getAlias(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      local: Option[Boolean] = None,
      name: Seq[String] = Nil
    ): ZioResponse[IndicesGetAliasResponse] = {
      val request = IndicesGetAliasRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        local = local,
        name = name
      )

      getAlias(request)

    }

    def getAlias(request: IndicesGetAliasRequest): ZioResponse[IndicesGetAliasResponse] =
      execute(request)

    /*
     * Returns mapping for one or more fields.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-field-mapping.html
     *
     * @param fields A comma-separated list of fields
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param includeDefaults Whether the default mapping values should be returned as well
     * @param includeTypeName Whether a type should be returned in the body of the mappings.
     * @param indices A comma-separated list of index names
     * @param local Return local information, do not retrieve the state from master node (default: false)
     */
    def getFieldMapping(
      fields: Seq[String] = Nil,
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      includeDefaults: Option[Boolean] = None,
      includeTypeName: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      local: Option[Boolean] = None
    ): ZioResponse[IndicesGetFieldMappingResponse] = {
      val request = IndicesGetFieldMappingRequest(
        fields = fields,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        includeDefaults = includeDefaults,
        includeTypeName = includeTypeName,
        indices = indices,
        local = local
      )

      getFieldMapping(request)

    }

    def getFieldMapping(
      request: IndicesGetFieldMappingRequest
    ): ZioResponse[IndicesGetFieldMappingResponse] =
      execute(request)

    /*
     * Returns mappings for one or more indices.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-mapping.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names
     * @param local Return local information, do not retrieve the state from master node (default: false)
     * @param masterTimeout Specify timeout for connection to master
     */
    def getMapping(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      local: Option[Boolean] = None,
      masterTimeout: Option[String] = None
    ): ZioResponse[IndicesGetMappingResponse] = {
      val request = IndicesGetMappingRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        local = local,
        masterTimeout = masterTimeout
      )

      getMapping(request)

    }

    def getMapping(request: IndicesGetMappingRequest): ZioResponse[IndicesGetMappingResponse] = execute(request)

    /*
     * Returns settings for one or more indices.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-settings.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param flatSettings Return settings in flat format (default: false)
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param includeDefaults Whether to return all default setting for each of the indices.
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     * @param local Return local information, do not retrieve the state from master node (default: false)
     * @param masterTimeout Specify timeout for connection to master
     * @param name The name of the settings that should be included
     */
    def getSettings(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      flatSettings: Option[Boolean] = None,
      ignoreUnavailable: Option[Boolean] = None,
      includeDefaults: Boolean = false,
      indices: Seq[String] = Nil,
      local: Option[Boolean] = None,
      masterTimeout: Option[String] = None,
      name: Option[String] = None
    ): ZioResponse[IndicesGetSettingsResponse] = {
      val request = IndicesGetSettingsRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flatSettings = flatSettings,
        ignoreUnavailable = ignoreUnavailable,
        includeDefaults = includeDefaults,
        indices = indices,
        local = local,
        masterTimeout = masterTimeout,
        name = name
      )

      getSettings(request)

    }

    def getSettings(request: IndicesGetSettingsRequest): ZioResponse[IndicesGetSettingsResponse] =
      execute(request)

    /*
     * Returns an index template.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
     *
     * @param flatSettings Return settings in flat format (default: false)
     * @param includeTypeName Whether a type should be returned in the body of the mappings.
     * @param local Return local information, do not retrieve the state from master node (default: false)
     * @param masterTimeout Explicit operation timeout for connection to master node
     * @param name The comma separated names of the index templates
     */
    def getTemplate(
      flatSettings: Option[Boolean] = None,
      includeTypeName: Option[Boolean] = None,
      local: Option[Boolean] = None,
      masterTimeout: Option[String] = None,
      name: Option[String] = None
    ): ZioResponse[IndicesGetTemplateResponse] = {
      val request = IndicesGetTemplateRequest(
        flatSettings = flatSettings,
        includeTypeName = includeTypeName,
        local = local,
        masterTimeout = masterTimeout,
        name = name
      )

      getTemplate(request)

    }

    def getTemplate(request: IndicesGetTemplateRequest): ZioResponse[IndicesGetTemplateResponse] =
      execute(request)

    /*
     * Shortcut to return all the templates
     */
    def getTemplates(): ZioResponse[IndicesGetTemplateResponse] = {
      val request =
        IndicesGetTemplateRequest(
          name = None,
          flatSettings = Some(false),
          masterTimeout = None,
          local = Some(true)
        )

      getTemplate(request)

    }

    /*
     * The _upgrade API is no longer useful and will be removed.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-upgrade.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     */
    def getUpgrade(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil
    ): ZioResponse[IndicesGetUpgradeResponse] = {
      val request = IndicesGetUpgradeRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices
      )

      getUpgrade(request)

    }

    def getUpgrade(request: IndicesGetUpgradeRequest): ZioResponse[IndicesGetUpgradeResponse] = execute(request)

    /*
     * Opens an index.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
     *
     * @param index A comma separated list of indices to open
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit operation timeout
     * @param waitForActiveShards Sets the number of active shards to wait for before the operation returns.
     */
    def open(
      indices: Seq[String],
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None,
      waitForActiveShards: Option[String] = None
    ): ZioResponse[IndicesOpenResponse] = {
      val request = IndicesOpenRequest(
        indices = indices,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )

      open(request)

    }

    def open(request: IndicesOpenRequest): ZioResponse[IndicesOpenResponse] =
      execute(request)

    def open(
      index: String
    ): ZioResponse[IndicesOpenResponse] =
      execute(new IndicesOpenRequest(Seq(index)))

    /*
     * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
     *
     * @param indices A list of index names the alias should point to (supports wildcards); use `_all` or omit to perform the operation on all indices.
     * @param name The name of the alias to be created or updated
     * @param body body the body of the call
     * @param timeout Explicit timestamp for the document
     * @param masterTimeout Specify timeout for connection to master
     */
    def putAlias(
      indices: Seq[String] = Nil,
      name: String,
      body: JsonObject = JsonObject.empty,
      timeout: Option[String] = None,
      masterTimeout: Option[String] = None
    ): ZioResponse[IndicesPutAliasResponse] = {
      val request = IndicesPutAliasRequest(
        indices = indices,
        name = name,
        timeout = timeout,
        masterTimeout = masterTimeout
      )

      putAlias(request)

    }

    def putAlias(
      request: IndicesPutAliasRequest
    ): ZioResponse[IndicesPutAliasResponse] =
      execute(request)

    def putMapping(
      index: String,
      docType: String,
      mapping: RootDocumentMapping
    ): ZioResponse[IndicesPutMappingResponse] =
      putMapping(indices = Seq(index), body = mapping.asJsonObject)

    /*
     * Updates the index mappings.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-put-mapping.html
     *
     * @param indices A comma-separated list of index names the mapping should be added to (supports wildcards); use `_all` or omit to add the mapping on all indices.
     * @param body body the body of the call
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit operation timeout
     */
    def putMapping(
      indices: Seq[String] = Nil,
      body: JsonObject,
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None
    ): ZioResponse[IndicesPutMappingResponse] = {
      val request = IndicesPutMappingRequest(
        indices = indices,
        body = body,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        timeout = timeout
      )

      putMapping(request)

    }

    def putMapping(
      request: IndicesPutMappingRequest
    ): ZioResponse[IndicesPutMappingResponse] =
      execute(request)

    /*
     * Updates the index settings.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-update-settings.html
     *
     * @param body body the body of the call
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param flatSettings Return settings in flat format (default: false)
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     * @param masterTimeout Specify timeout for connection to master
     * @param preserveExisting Whether to update existing settings. If set to `true` existing settings on an index remain unchanged, the default is `false`
     * @param timeout Explicit operation timeout
     */
    def putSettings(
      body: JsonObject,
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      flatSettings: Option[Boolean] = None,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      masterTimeout: Option[String] = None,
      preserveExisting: Option[Boolean] = None,
      timeout: Option[String] = None
    ): ZioResponse[IndicesPutSettingsResponse] = {
      val request = IndicesPutSettingsRequest(
        body = body,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flatSettings = flatSettings,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        masterTimeout = masterTimeout,
        preserveExisting = preserveExisting,
        timeout = timeout
      )

      putSettings(request)

    }

    def putSettings(
      request: IndicesPutSettingsRequest
    ): ZioResponse[IndicesPutSettingsResponse] =
      execute(request)

    /*
     * Creates or updates an index template.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
     *
     * @param name The name of the template
     * @param body body the body of the call
     * @param create Whether the index template should only be added if new or can also replace an existing one
     * @param flatSettings Return settings in flat format (default: false)
     * @param includeTypeName Whether a type should be returned in the body of the mappings.
     * @param masterTimeout Specify timeout for connection to master
     * @param order The order for this template when merging multiple matching ones (higher numbers are merged later, overriding the lower numbers)
     * @param timeout Explicit operation timeout
     */
    def putTemplate(
      name: String,
      body: JsonObject,
      create: Boolean = false,
      flatSettings: Option[Boolean] = None,
      includeTypeName: Option[Boolean] = None,
      masterTimeout: Option[String] = None,
      order: Option[Double] = None,
      timeout: Option[String] = None
    ): ZioResponse[IndicesPutTemplateResponse] = {
      val request = IndicesPutTemplateRequest(
        name = name,
        body = body,
        create = create,
        flatSettings = flatSettings,
        includeTypeName = includeTypeName,
        masterTimeout = masterTimeout,
        order = order,
        timeout = timeout
      )

      putTemplate(request)

      putTemplate(request)
    }

    def putTemplate(
      request: IndicesPutTemplateRequest
    ): ZioResponse[IndicesPutTemplateResponse] =
      execute(request)

    /*
     * Returns information about ongoing index shard recoveries.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-recovery.html
     *
     * @param activeOnly Display only those recoveries that are currently on-going
     * @param detailed Whether to display detailed information about shard recovery
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     */
    def recovery(
      activeOnly: Boolean = false,
      detailed: Boolean = false,
      indices: Seq[String] = Nil
    ): ZioResponse[IndicesRecoveryResponse] = {
      val request = IndicesRecoveryRequest(activeOnly = activeOnly, detailed = detailed, indices = indices)

      recovery(request)

    }

    def recovery(
      request: IndicesRecoveryRequest
    ): ZioResponse[IndicesRecoveryResponse] =
      execute(request)

    /*
     * Performs the refresh operation in one or more indices.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-refresh.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     */
    def refresh(
      indices: Seq[String],
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None
    ): ZioResponse[IndicesRefreshResponse] = {
      val request = IndicesRefreshRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices
      )

      refresh(request)

    }

    def refresh(request: IndicesRefreshRequest): ZioResponse[IndicesRefreshResponse] =
      execute(request)

    /*
     * Updates an alias to point to a new index when the existing index
  is considered to be too large or too old.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
     *
     * @param alias The name of the alias to rollover
     * @param body body the body of the call
     * @param dryRun If set to true the rollover action will only be validated but not actually performed even if a condition matches. The default is false
     * @param includeTypeName Whether a type should be included in the body of the mappings.
     * @param masterTimeout Specify timeout for connection to master
     * @param newIndex The name of the rollover index
     * @param timeout Explicit operation timeout
     * @param waitForActiveShards Set the number of active shards to wait for on the newly created rollover index before the operation returns.
     */
    def rollover(
      alias: String,
      body: Option[JsonObject] = None,
      dryRun: Option[Boolean] = None,
      includeTypeName: Option[Boolean] = None,
      masterTimeout: Option[String] = None,
      newIndex: Option[String] = None,
      timeout: Option[String] = None,
      waitForActiveShards: Option[String] = None
    ): ZioResponse[IndicesRolloverResponse] = {
      val request = IndicesRolloverRequest(
        alias = alias,
        body = body,
        dryRun = dryRun,
        includeTypeName = includeTypeName,
        masterTimeout = masterTimeout,
        newIndex = newIndex,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )

      rollover(request)

    }

    def rollover(
      request: IndicesRolloverRequest
    ): ZioResponse[IndicesRolloverResponse] =
      execute(request)

    /*
     * Provides low-level information about segments in a Lucene index.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-segments.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     * @param verbose Includes detailed memory usage by Lucene.
     */
    def segments(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      verbose: Boolean = false
    ): ZioResponse[IndicesSegmentsResponse] = {
      val request = IndicesSegmentsRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        verbose = verbose
      )

      segments(request)

    }

    def segments(request: IndicesSegmentsRequest): ZioResponse[IndicesSegmentsResponse] =
      execute(request)

    /*
     * Provides store information for shard copies of indices.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shards-stores.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     * @param status A comma-separated list of statuses used to filter on shards to get store information for
     */
    def shardStores(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      status: Seq[String] = Nil
    ): ZioResponse[IndicesShardStoresResponse] = {
      val request = IndicesShardStoresRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        status = status
      )

      shardStores(request)

    }

    def shardStores(request: IndicesShardStoresRequest): ZioResponse[IndicesShardStoresResponse] =
      execute(request)

    /*
     * Allow to shrink an existing index into a new index with fewer primary shards.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shrink-index.html
     *
     * @param index The name of the source index to shrink
     * @param target The name of the target index to shrink into
     * @param body body the body of the call
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit operation timeout
     * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
     */
    def shrink(
      index: String,
      target: String,
      body: Option[JsonObject] = None,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None,
      waitForActiveShards: Option[String] = None
    ): ZioResponse[IndicesShrinkResponse] = {
      val request = IndicesShrinkRequest(
        index = index,
        target = target,
        body = body,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )

      shrink(request)

    }

    def shrink(request: IndicesShrinkRequest): ZioResponse[IndicesShrinkResponse] =
      execute(request)

    /*
     * Allows you to split an existing index into a new index with more primary shards.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-split-index.html
     *
     * @param index The name of the source index to split
     * @param target The name of the target index to split into
     * @param body body the body of the call
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Explicit operation timeout
     * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
     */
    def split(
      index: String,
      target: String,
      body: Option[JsonObject] = None,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None,
      waitForActiveShards: Option[String] = None
    ): ZioResponse[IndicesSplitResponse] = {
      val request = IndicesSplitRequest(
        index = index,
        target = target,
        body = body,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )

      split(request)

    }

    def split(request: IndicesSplitRequest): ZioResponse[IndicesSplitResponse] =
      execute(request)

    /*
     * Provides statistics on operations happening in an index.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-stats.html
     *
     * @param completionFields A comma-separated list of fields for `fielddata` and `suggest` index metric (supports wildcards)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param fielddataFields A comma-separated list of fields for `fielddata` index metric (supports wildcards)
     * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
     * @param forbidClosedIndices If set to false stats will also collected from closed indices if explicitly specified or if expand_wildcards expands to closed indices
     * @param groups A comma-separated list of search groups for `search` index metric
     * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
     * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     * @param level Return stats aggregated at cluster, index or shard level
     * @param metric Limit the information returned the specific metrics.
     * @param types A comma-separated list of document types for the `indexing` index metric
     */
    def stats(
      indices: Seq[String] = Nil,
      completionFields: Seq[String] = Nil,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      fielddataFields: Seq[String] = Nil,
      fields: Seq[String] = Nil,
      forbidClosedIndices: Boolean = true,
      groups: Seq[String] = Nil,
      includeSegmentFileSizes: Boolean = false,
      includeUnloadedSegments: Boolean = false,
      level: Level = Level.indices,
      metric: Option[String] = None,
      types: Seq[String] = Nil
    ): ZioResponse[IndicesStatsResponse] = {
      val request = IndicesStatsRequest(
        completionFields = completionFields,
        expandWildcards = expandWildcards,
        fielddataFields = fielddataFields,
        fields = fields,
        forbidClosedIndices = forbidClosedIndices,
        groups = groups,
        includeSegmentFileSizes = includeSegmentFileSizes,
        includeUnloadedSegments = includeUnloadedSegments,
        indices = indices,
        level = level,
        metric = metric,
        types = types
      )

      stats(request)

    }

    def stats(request: IndicesStatsRequest): ZioResponse[IndicesStatsResponse] =
      execute(request)

    /*
     * Updates index aliases.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
     *
     * @param body body the body of the call
     * @param masterTimeout Specify timeout for connection to master
     * @param timeout Request timeout
     */
    def updateAliases(
      body: JsonObject,
      masterTimeout: Option[String] = None,
      timeout: Option[String] = None
    ): ZioResponse[IndicesUpdateAliasesResponse] = {
      val request = IndicesUpdateAliasesRequest(body = body, masterTimeout = masterTimeout, timeout = timeout)

      updateAliases(request)

    }

    def updateAliases(request: IndicesUpdateAliasesRequest): ZioResponse[IndicesUpdateAliasesResponse] =
      execute(request)

    /*
     * The _upgrade API is no longer useful and will be removed.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-upgrade.html
     *
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
     * @param onlyAncientSegments If true, only ancient (an older Lucene major release) segments will be upgraded
     * @param waitForCompletion Specify whether the request should block until the all segments are upgraded (default: false)
     */
    def upgrade(
      allowNoIndices: Option[Boolean] = None,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      onlyAncientSegments: Option[Boolean] = None,
      waitForCompletion: Option[Boolean] = None
    ): ZioResponse[IndicesUpgradeResponse] = {
      val request = IndicesUpgradeRequest(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        onlyAncientSegments = onlyAncientSegments,
        waitForCompletion = waitForCompletion
      )

      upgrade(request)

    }

    def upgrade(request: IndicesUpgradeRequest): ZioResponse[IndicesUpgradeResponse] =
      execute(request)

    /*
     * Allows a user to validate a potentially expensive query without executing it.
     * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-validate.html
     *
     * @param allShards Execute validation on all shards instead of one random shard per index
     * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
     * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
     * @param analyzer The analyzer to use for the query string
     * @param body body the body of the call
     * @param defaultOperator The default operator for query string query (AND or OR)
     * @param df The field to use as default where no field prefix is given in the query string
     * @param docTypes A comma-separated list of document types to restrict the operation; leave empty to perform the operation on all types
     * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
     * @param explain Return detailed information about the error
     * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
     * @param indices A comma-separated list of index names to restrict the operation; use `_all` or empty string to perform the operation on all indices
     * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
     * @param q Query in the Lucene query string syntax
     * @param rewrite Provide a more detailed explanation showing the actual Lucene query that will be executed.
     */
    def validateQuery(
      body: JsonObject,
      allShards: Option[Boolean] = None,
      allowNoIndices: Option[Boolean] = None,
      analyzeWildcard: Option[Boolean] = None,
      analyzer: Option[String] = None,
      defaultOperator: DefaultOperator = DefaultOperator.OR,
      df: Option[String] = None,
      docTypes: Seq[String] = Nil,
      expandWildcards: Seq[ExpandWildcards] = Nil,
      explain: Option[Boolean] = None,
      ignoreUnavailable: Option[Boolean] = None,
      indices: Seq[String] = Nil,
      lenient: Option[Boolean] = None,
      q: Option[String] = None,
      rewrite: Option[Boolean] = None
    ): ZioResponse[IndicesValidateQueryResponse] = {
      val request = IndicesValidateQueryRequest(
        allShards = allShards,
        allowNoIndices = allowNoIndices,
        analyzeWildcard = analyzeWildcard,
        analyzer = analyzer,
        body = body,
        defaultOperator = defaultOperator,
        df = df,
        expandWildcards = expandWildcards,
        explain = explain,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        lenient = lenient,
        q = q,
        rewrite = rewrite
      )

      validateQuery(request)
    }

    def validateQuery(request: IndicesValidateQueryRequest): ZioResponse[IndicesValidateQueryResponse] =
      execute(request)

    def createWithSettingsAndMappings(
      index: String,
      settings: Settings = Settings(),
      mappings: Option[RootDocumentMapping] = None
    ): ZioResponse[IndicesCreateResponse] = {
      /*
          Creates an index with optional settings.
          :ref:`qdb-guide-reference-api-admin-indices-create-index`

          :param index: the name of the index
          :keyword settings: a settings object or a dict containing settings
       */

      val request = IndicesCreateRequest(
        index,
        body = CirceUtils.cleanValue(
          JsonObject.fromMap(
            Map("settings" -> settings.asJson, "mappings" -> mappings.map(_.asJson).getOrElse(Json.obj()))
          )
        )
      )

      execute(request)
    }

    def putMapping(
      indices: Seq[String],
      docType: String,
      mapping: RootDocumentMapping
    ): ZioResponse[IndicesPutMappingResponse] =
      putMapping(
        indices = indices,
        body = CirceUtils.cleanValue(mapping.asJsonObject)
      )
  }

  // services
  private case class Live(
    loggingService: Logging.Service,
    client: ElasticSearchService.Service,
    httpService: HTTPService.Service
  ) extends Service

  val live: ZLayer[ElasticSearchService, Nothing, Has[Service]] =
    ZLayer.fromService[ElasticSearchService.Service, Service] { baseElasticSearchService =>
      Live(baseElasticSearchService.loggingService, baseElasticSearchService, baseElasticSearchService.httpService)
    }

  // access methods

  /*
   * Performs the analysis process on a text and return the tokens breakdown of the text.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-analyze.html
   *
   * @param body body the body of the call
   * @param index The name of the index to scope the operation
   */
  def analyze(
    body: JsonObject,
    index: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesAnalyzeResponse] =
    ZIO.accessM[IndicesService](_.get.analyze(body = body, index = index))

  def analyze(request: IndicesAnalyzeRequest): ZIO[IndicesService, FrameworkException, IndicesAnalyzeResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Clears all or specific caches for one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clearcache.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param fielddata Clear field data
   * @param fields A comma-separated list of fields to clear when using the `fielddata` parameter (default: all)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param index A comma-separated list of index name to limit the operation
   * @param indices A comma-separated list of index name to limit the operation
   * @param query Clear query caches
   * @param request Clear request cache
   */
  def clearCache(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    fielddata: Option[Boolean] = None,
    fields: Seq[String] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    index: Seq[String] = Nil,
    indices: Seq[String] = Nil,
    query: Option[Boolean] = None,
    request: Option[Boolean] = None
  ): ZIO[IndicesService, FrameworkException, IndicesClearCacheResponse] =
    ZIO.accessM[IndicesService](
      _.get.clearCache(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        fielddata = fielddata,
        fields = fields,
        ignoreUnavailable = ignoreUnavailable,
        index = index,
        indices = indices,
        query = query,
        request = request
      )
    )

  def clearCache(
    request: IndicesClearCacheRequest
  ): ZIO[IndicesService, FrameworkException, IndicesClearCacheResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Clones an index
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clone-index.html
   *
   * @param index The name of the source index to clone
   * @param target The name of the target index to clone into
   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for on the cloned index before the operation returns.
   */
  def clone(
    index: String,
    target: String,
    body: Option[JsonObject] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesCloneResponse] =
    ZIO.accessM[IndicesService](
      _.get.clone(
        index = index,
        target = target,
        body = body,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )
    )

  def clone(request: IndicesCloneRequest): ZIO[IndicesService, FrameworkException, IndicesCloneResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Closes an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
   *
   * @param index A comma separated list of indices to close
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Sets the number of active shards to wait for before the operation returns.
   */
  def close(
    index: String,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesCloseResponse] =
    ZIO.accessM[IndicesService](
      _.get.close(
        index = index,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )
    )

  def close(request: IndicesCloseRequest): ZIO[IndicesService, FrameworkException, IndicesCloseResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Creates an index with optional settings and mappings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-create-index.html
   *
   * @param index The name of the index
   * @param body body the body of the call
   * @param includeTypeName Whether a type should be expected in the body of the mappings.
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for before the operation returns.
   */
  def create(
    index: String,
    body: JsonObject,
    includeTypeName: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[Int] = None
  ): ZIO[IndicesService, FrameworkException, IndicesCreateResponse] =
    ZIO.accessM[IndicesService](
      _.get.create(
        index = index,
        body = body,
        includeTypeName = includeTypeName,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )
    )

  def create(request: IndicesCreateRequest): ZIO[IndicesService, FrameworkException, IndicesCreateResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Deletes an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-delete-index.html
   *
   * @param indices A comma-separated list of indices to delete; use `_all` or `*` string to delete all indices
   * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def delete(
    indices: Seq[String] = Nil,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesDeleteResponse] =
    ZIO.accessM[IndicesService](
      _.get.delete(
        indices = indices,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        timeout = timeout
      )
    )

  def delete(request: IndicesDeleteRequest): ZIO[IndicesService, FrameworkException, IndicesDeleteResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Deletes an alias.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param indices A comma-separated list of index names (supports wildcards); use `_all` for all indices
   * @param name A comma-separated list of aliases to delete (supports wildcards); use `_all` to delete all aliases for the specified indices.
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit timestamp for the document
   */
  def deleteAlias(
    indices: Seq[String] = Nil,
    names: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesDeleteAliasResponse] =
    ZIO.accessM[IndicesService](
      _.get.deleteAlias(indices = indices, names = names, masterTimeout = masterTimeout, timeout = timeout)
    )

  def deleteAlias(
    request: IndicesDeleteAliasRequest
  ): ZIO[IndicesService, FrameworkException, IndicesDeleteAliasResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Deletes an index template.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the template
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def deleteTemplate(
    name: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesDeleteTemplateResponse] =
    ZIO.accessM[IndicesService](_.get.deleteTemplate(name = name, masterTimeout = masterTimeout, timeout = timeout))

  def deleteTemplate(
    request: IndicesDeleteTemplateRequest
  ): ZIO[IndicesService, FrameworkException, IndicesDeleteTemplateResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns information about whether a particular index exists.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-exists.html
   *
   * @param indices A comma-separated list of index names
   * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   * @param flatSettings Return settings in flat format (default: false)
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   * @param includeDefaults Whether to return all default setting for each of the indices.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def exists(
    indices: Seq[String] = Nil,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    includeDefaults: Boolean = false,
    local: Option[Boolean] = None
  ): ZIO[IndicesService, FrameworkException, IndicesExistsResponse] =
    ZIO.accessM[IndicesService](
      _.get.exists(
        indices = indices,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flatSettings = flatSettings,
        ignoreUnavailable = ignoreUnavailable,
        includeDefaults = includeDefaults,
        local = local
      )
    )

  def exists(request: IndicesExistsRequest): ZIO[IndicesService, FrameworkException, IndicesExistsResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns information about whether a particular alias exists.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param name A comma-separated list of alias names to return
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to filter aliases
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def existsAlias(
    name: Seq[String] = Nil,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    local: Option[Boolean] = None
  ): ZIO[IndicesService, FrameworkException, IndicesExistsAliasResponse] =
    ZIO.accessM[IndicesService](
      _.get.existsAlias(
        name = name,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        local = local
      )
    )

  def existsAlias(
    request: IndicesExistsAliasRequest
  ): ZIO[IndicesService, FrameworkException, IndicesExistsAliasResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns information about whether a particular index template exists.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The comma separated names of the index templates
   * @param flatSettings Return settings in flat format (default: false)
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def existsTemplate(
    name: String,
    flatSettings: Option[Boolean] = None,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, Boolean] =
    ZIO.accessM[IndicesService](
      _.get.existsTemplate(name = name, flatSettings = flatSettings, local = local, masterTimeout = masterTimeout)
    )

  def existsTemplate(
    request: IndicesExistsTemplateRequest
  ): ZIO[IndicesService, FrameworkException, IndicesExistsTemplateResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Performs the flush operation on one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-flush.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param force Whether a flush should be forced even if it is not necessarily needed ie. if no changes will be committed to the index. This is useful if transaction log IDs should be incremented even if no uncommitted changes are present. (This setting can be considered as internal)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string for all indices
   * @param waitIfOngoing If set to true the flush operation will block until the flush can be executed if another flush operation is already executing. The default is true. If set to false the flush will be skipped iff if another flush operation is already running.
   */
  def flush(
    indices: Seq[String] = Nil,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    force: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    waitIfOngoing: Option[Boolean] = None
  ): ZIO[IndicesService, FrameworkException, IndicesFlushResponse] =
    ZIO.accessM[IndicesService](
      _.get.flush(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        force = force,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        waitIfOngoing = waitIfOngoing
      )
    )

  def flush(index: String): ZIO[IndicesService, FrameworkException, IndicesFlushResponse] =
    flush(Seq(index))

  def flush(request: IndicesFlushRequest): ZIO[IndicesService, FrameworkException, IndicesFlushResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Performs a synced flush operation on one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-synced-flush-api.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string for all indices
   */
  def flushSynced(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil
  ): ZIO[IndicesService, FrameworkException, IndicesFlushSyncedResponse] =
    ZIO.accessM[IndicesService](
      _.get.flushSynced(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices
      )
    )

  def flushSynced(
    request: IndicesFlushSyncedRequest
  ): ZIO[IndicesService, FrameworkException, IndicesFlushSyncedResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Performs the force merge operation on one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-forcemerge.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param flush Specify whether the index should be flushed after performing the operation (default: true)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param maxNumSegments The number of segments the index should be merged into (default: dynamic)
   * @param onlyExpungeDeletes Specify whether the operation should only expunge deleted documents
   */
  def forcemerge(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flush: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    maxNumSegments: Option[Double] = None,
    onlyExpungeDeletes: Option[Boolean] = None
  ): ZIO[IndicesService, FrameworkException, IndicesForcemergeResponse] =
    ZIO.accessM[IndicesService](
      _.get.forcemerge(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flush = flush,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        maxNumSegments = maxNumSegments,
        onlyExpungeDeletes = onlyExpungeDeletes
      )
    )

  def forcemerge(
    request: IndicesForcemergeRequest
  ): ZIO[IndicesService, FrameworkException, IndicesForcemergeResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns information about one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-index.html
   *
   * @param indices A comma-separated list of index names
   * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   * @param flatSettings Return settings in flat format (default: false)
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   * @param includeDefaults Whether to return all default setting for each of the indices.
   * @param includeTypeName Whether to add the type name to the response (default: false)
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Specify timeout for connection to master
   */
  def get(
    indices: Seq[String] = Nil,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    includeDefaults: Boolean = false,
    includeTypeName: Option[Boolean] = None,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesGetResponse] =
    ZIO.accessM[IndicesService](
      _.get.get(
        indices = indices,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flatSettings = flatSettings,
        ignoreUnavailable = ignoreUnavailable,
        includeDefaults = includeDefaults,
        includeTypeName = includeTypeName,
        local = local,
        masterTimeout = masterTimeout
      )
    )

  def get(request: IndicesGetRequest): ZIO[IndicesService, FrameworkException, IndicesGetResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns an alias.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to filter aliases
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param name A comma-separated list of alias names to return
   */
  def getAlias(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    local: Option[Boolean] = None,
    name: Seq[String] = Nil
  ): ZIO[IndicesService, FrameworkException, IndicesGetAliasResponse] =
    ZIO.accessM[IndicesService](
      _.get.getAlias(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        local = local,
        name = name
      )
    )

  def getAlias(request: IndicesGetAliasRequest): ZIO[IndicesService, FrameworkException, IndicesGetAliasResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns mapping for one or more fields.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-field-mapping.html
   *
   * @param fields A comma-separated list of fields
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param includeDefaults Whether the default mapping values should be returned as well
   * @param includeTypeName Whether a type should be returned in the body of the mappings.
   * @param indices A comma-separated list of index names
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def getFieldMapping(
    fields: Seq[String] = Nil,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    includeDefaults: Option[Boolean] = None,
    includeTypeName: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    local: Option[Boolean] = None
  ): ZIO[IndicesService, FrameworkException, IndicesGetFieldMappingResponse] =
    ZIO.accessM[IndicesService](
      _.get.getFieldMapping(
        fields = fields,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        includeDefaults = includeDefaults,
        includeTypeName = includeTypeName,
        indices = indices,
        local = local
      )
    )

  def getFieldMapping(
    request: IndicesGetFieldMappingRequest
  ): ZIO[IndicesService, FrameworkException, IndicesGetFieldMappingResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns mappings for one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-mapping.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Specify timeout for connection to master
   */
  def getMapping(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesGetMappingResponse] =
    ZIO.accessM[IndicesService](
      _.get.getMapping(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        local = local,
        masterTimeout = masterTimeout
      )
    )

  def getMapping(
    request: IndicesGetMappingRequest
  ): ZIO[IndicesService, FrameworkException, IndicesGetMappingResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns settings for one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-settings.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param flatSettings Return settings in flat format (default: false)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param includeDefaults Whether to return all default setting for each of the indices.
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Specify timeout for connection to master
   * @param name The name of the settings that should be included
   */
  def getSettings(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    includeDefaults: Boolean = false,
    indices: Seq[String] = Nil,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesGetSettingsResponse] =
    ZIO.accessM[IndicesService](
      _.get.getSettings(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flatSettings = flatSettings,
        ignoreUnavailable = ignoreUnavailable,
        includeDefaults = includeDefaults,
        indices = indices,
        local = local,
        masterTimeout = masterTimeout,
        name = name
      )
    )

  def getSettings(
    request: IndicesGetSettingsRequest
  ): ZIO[IndicesService, FrameworkException, IndicesGetSettingsResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns an index template.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param flatSettings Return settings in flat format (default: false)
   * @param includeTypeName Whether a type should be returned in the body of the mappings.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param name The comma separated names of the index templates
   */
  def getTemplate(
    flatSettings: Option[Boolean] = None,
    includeTypeName: Option[Boolean] = None,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesGetTemplateResponse] =
    ZIO.accessM[IndicesService](
      _.get.getTemplate(
        flatSettings = flatSettings,
        includeTypeName = includeTypeName,
        local = local,
        masterTimeout = masterTimeout,
        name = name
      )
    )

  def getTemplate(
    request: IndicesGetTemplateRequest
  ): ZIO[IndicesService, FrameworkException, IndicesGetTemplateResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * The _upgrade API is no longer useful and will be removed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-upgrade.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   */
  def getUpgrade(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil
  ): ZIO[IndicesService, FrameworkException, IndicesGetUpgradeResponse] =
    ZIO.accessM[IndicesService](
      _.get.getUpgrade(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices
      )
    )

  def getUpgrade(
    request: IndicesGetUpgradeRequest
  ): ZIO[IndicesService, FrameworkException, IndicesGetUpgradeResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Opens an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
   *
   * @param index A comma separated list of indices to open
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Sets the number of active shards to wait for before the operation returns.
   */
  def open(
    indices: Seq[String],
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesOpenResponse] =
    ZIO.accessM[IndicesService](
      _.get.open(
        indices = indices,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )
    )

  def open(request: IndicesOpenRequest): ZIO[IndicesService, FrameworkException, IndicesOpenResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Creates or updates an alias.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param indices A comma-separated list of index names the alias should point to (supports wildcards); use `_all` to perform the operation on all indices.
   * @param name The name of the alias to be created or updated
   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit timestamp for the document
   */
  def putAlias(
    indices: Seq[String] = Nil,
    name: String,
    body: JsonObject,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesPutAliasResponse] =
    ZIO.accessM[IndicesService](
      _.get.putAlias(indices = indices, name = name, body = body, masterTimeout = masterTimeout, timeout = timeout)
    )

  def putAlias(request: IndicesPutAliasRequest): ZIO[IndicesService, FrameworkException, IndicesPutAliasResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Updates the index mappings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-put-mapping.html
   *
   * @param indices A comma-separated list of index names the mapping should be added to (supports wildcards); use `_all` or omit to add the mapping on all indices.
   * @param body body the body of the call
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def putMapping(
    indices: Seq[String] = Nil,
    body: JsonObject,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesPutMappingResponse] =
    ZIO.accessM[IndicesService](
      _.get.putMapping(
        indices = indices,
        body = body,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        timeout = timeout
      )
    )

  def putMapping(
    request: IndicesPutMappingRequest
  ): ZIO[IndicesService, FrameworkException, IndicesPutMappingResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Updates the index settings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-update-settings.html
   *
   * @param body body the body of the call
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param flatSettings Return settings in flat format (default: false)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param masterTimeout Specify timeout for connection to master
   * @param preserveExisting Whether to update existing settings. If set to `true` existing settings on an index remain unchanged, the default is `false`
   * @param timeout Explicit operation timeout
   */
  def putSettings(
    body: JsonObject,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    preserveExisting: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesPutSettingsResponse] =
    ZIO.accessM[IndicesService](
      _.get.putSettings(
        body = body,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        flatSettings = flatSettings,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        masterTimeout = masterTimeout,
        preserveExisting = preserveExisting,
        timeout = timeout
      )
    )

  def putSettings(
    request: IndicesPutSettingsRequest
  ): ZIO[IndicesService, FrameworkException, IndicesPutSettingsResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Creates or updates an index template.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the template
   * @param body body the body of the call
   * @param create Whether the index template should only be added if new or can also replace an existing one
   * @param flatSettings Return settings in flat format (default: false)
   * @param includeTypeName Whether a type should be returned in the body of the mappings.
   * @param masterTimeout Specify timeout for connection to master
   * @param order The order for this template when merging multiple matching ones (higher numbers are merged later, overriding the lower numbers)
   * @param timeout Explicit operation timeout
   */
  def putTemplate(
    name: String,
    body: JsonObject,
    create: Boolean = false,
    flatSettings: Option[Boolean] = None,
    includeTypeName: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    order: Option[Double] = None,
    timeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesPutTemplateResponse] =
    ZIO.accessM[IndicesService](
      _.get.putTemplate(
        name = name,
        body = body,
        create = create,
        flatSettings = flatSettings,
        includeTypeName = includeTypeName,
        masterTimeout = masterTimeout,
        order = order,
        timeout = timeout
      )
    )

  def putTemplate(
    request: IndicesPutTemplateRequest
  ): ZIO[IndicesService, FrameworkException, IndicesPutTemplateResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Returns information about ongoing index shard recoveries.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-recovery.html
   *
   * @param activeOnly Display only those recoveries that are currently on-going
   * @param detailed Whether to display detailed information about shard recovery
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   */
  def recovery(
    activeOnly: Boolean = false,
    detailed: Boolean = false,
    indices: Seq[String] = Nil
  ): ZIO[IndicesService, FrameworkException, IndicesRecoveryResponse] =
    ZIO.accessM[IndicesService](_.get.recovery(activeOnly = activeOnly, detailed = detailed, indices = indices))

  def recovery(request: IndicesRecoveryRequest): ZIO[IndicesService, FrameworkException, IndicesRecoveryResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Performs the refresh operation in one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-refresh.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   */
  def refresh(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil
  ): ZIO[IndicesService, FrameworkException, IndicesRefreshResponse] =
    ZIO.accessM[IndicesService](
      _.get.refresh(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices
      )
    )

  def refresh(request: IndicesRefreshRequest): ZIO[IndicesService, FrameworkException, IndicesRefreshResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Updates an alias to point to a new index when the existing index
is considered to be too large or too old.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
   *
   * @param alias The name of the alias to rollover
   * @param body body the body of the call
   * @param dryRun If set to true the rollover action will only be validated but not actually performed even if a condition matches. The default is false
   * @param includeTypeName Whether a type should be included in the body of the mappings.
   * @param masterTimeout Specify timeout for connection to master
   * @param newIndex The name of the rollover index
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for on the newly created rollover index before the operation returns.
   */
  def rollover(
    alias: String,
    body: Option[JsonObject] = None,
    dryRun: Option[Boolean] = None,
    includeTypeName: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    newIndex: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesRolloverResponse] =
    ZIO.accessM[IndicesService](
      _.get.rollover(
        alias = alias,
        body = body,
        dryRun = dryRun,
        includeTypeName = includeTypeName,
        masterTimeout = masterTimeout,
        newIndex = newIndex,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )
    )

  def rollover(request: IndicesRolloverRequest): ZIO[IndicesService, FrameworkException, IndicesRolloverResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Provides low-level information about segments in a Lucene index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-segments.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param verbose Includes detailed memory usage by Lucene.
   */
  def segments(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    verbose: Boolean = false
  ): ZIO[IndicesService, FrameworkException, IndicesSegmentsResponse] =
    ZIO.accessM[IndicesService](
      _.get.segments(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        verbose = verbose
      )
    )

  def segments(request: IndicesSegmentsRequest): ZIO[IndicesService, FrameworkException, IndicesSegmentsResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Provides store information for shard copies of indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shards-stores.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param status A comma-separated list of statuses used to filter on shards to get store information for
   */
  def shardStores(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    status: Seq[String] = Nil
  ): ZIO[IndicesService, FrameworkException, IndicesShardStoresResponse] =
    ZIO.accessM[IndicesService](
      _.get.shardStores(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        status = status
      )
    )

  def shardStores(
    request: IndicesShardStoresRequest
  ): ZIO[IndicesService, FrameworkException, IndicesShardStoresResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Allow to shrink an existing index into a new index with fewer primary shards.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shrink-index.html
   *
   * @param index The name of the source index to shrink
   * @param target The name of the target index to shrink into
   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
   */
  def shrink(
    index: String,
    target: String,
    body: Option[JsonObject] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesShrinkResponse] =
    ZIO.accessM[IndicesService](
      _.get.shrink(
        index = index,
        target = target,
        body = body,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )
    )

  def shrink(request: IndicesShrinkRequest): ZIO[IndicesService, FrameworkException, IndicesShrinkResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Allows you to split an existing index into a new index with more primary shards.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-split-index.html
   *
   * @param index The name of the source index to split
   * @param target The name of the target index to split into
   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
   */
  def split(
    index: String,
    target: String,
    body: Option[JsonObject] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesSplitResponse] =
    ZIO.accessM[IndicesService](
      _.get.split(
        index = index,
        target = target,
        body = body,
        masterTimeout = masterTimeout,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards
      )
    )

  def split(request: IndicesSplitRequest): ZIO[IndicesService, FrameworkException, IndicesSplitResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Provides statistics on operations happening in an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-stats.html
   *
   * @param completionFields A comma-separated list of fields for `fielddata` and `suggest` index metric (supports wildcards)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param fielddataFields A comma-separated list of fields for `fielddata` index metric (supports wildcards)
   * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
   * @param forbidClosedIndices If set to false stats will also collected from closed indices if explicitly specified or if expand_wildcards expands to closed indices
   * @param groups A comma-separated list of search groups for `search` index metric
   * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
   * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param level Return stats aggregated at cluster, index or shard level
   * @param metric Limit the information returned the specific metrics.
   * @param types A comma-separated list of document types for the `indexing` index metric
   */
  def stats(
    completionFields: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    fielddataFields: Seq[String] = Nil,
    fields: Seq[String] = Nil,
    forbidClosedIndices: Boolean = true,
    groups: Seq[String] = Nil,
    includeSegmentFileSizes: Boolean = false,
    includeUnloadedSegments: Boolean = false,
    indices: Seq[String] = Nil,
    level: Level = Level.indices,
    metric: Option[String] = None,
    types: Seq[String] = Nil
  ): ZIO[IndicesService, FrameworkException, IndicesStatsResponse] =
    ZIO.accessM[IndicesService](
      _.get.stats(
        completionFields = completionFields,
        expandWildcards = expandWildcards,
        fielddataFields = fielddataFields,
        fields = fields,
        forbidClosedIndices = forbidClosedIndices,
        groups = groups,
        includeSegmentFileSizes = includeSegmentFileSizes,
        includeUnloadedSegments = includeUnloadedSegments,
        indices = indices,
        level = level,
        metric = metric,
        types = types
      )
    )

  def stats(request: IndicesStatsRequest): ZIO[IndicesService, FrameworkException, IndicesStatsResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Updates index aliases.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Request timeout
   */
  def updateAliases(
    body: JsonObject,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[IndicesService, FrameworkException, IndicesUpdateAliasesResponse] =
    ZIO.accessM[IndicesService](_.get.updateAliases(body = body, masterTimeout = masterTimeout, timeout = timeout))

  def updateAliases(
    request: IndicesUpdateAliasesRequest
  ): ZIO[IndicesService, FrameworkException, IndicesUpdateAliasesResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * The _upgrade API is no longer useful and will be removed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-upgrade.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param onlyAncientSegments If true, only ancient (an older Lucene major release) segments will be upgraded
   * @param waitForCompletion Specify whether the request should block until the all segments are upgraded (default: false)
   */
  def upgrade(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    onlyAncientSegments: Option[Boolean] = None,
    waitForCompletion: Option[Boolean] = None
  ): ZIO[IndicesService, FrameworkException, IndicesUpgradeResponse] =
    ZIO.accessM[IndicesService](
      _.get.upgrade(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        onlyAncientSegments = onlyAncientSegments,
        waitForCompletion = waitForCompletion
      )
    )

  def upgrade(request: IndicesUpgradeRequest): ZIO[IndicesService, FrameworkException, IndicesUpgradeResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

  /*
   * Allows a user to validate a potentially expensive query without executing it.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-validate.html
   *
   * @param allShards Execute validation on all shards instead of one random shard per index
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param analyzer The analyzer to use for the query string
   * @param body body the body of the call
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param df The field to use as default where no field prefix is given in the query string
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param explain Return detailed information about the error
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to restrict the operation; use `_all` or empty string to perform the operation on all indices
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param q Query in the Lucene query string syntax
   * @param rewrite Provide a more detailed explanation showing the actual Lucene query that will be executed.
   */
  def validateQuery(
    body: JsonObject,
    allShards: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    explain: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    lenient: Option[Boolean] = None,
    q: Option[String] = None,
    rewrite: Option[Boolean] = None
  ): ZIO[IndicesService, FrameworkException, IndicesValidateQueryResponse] =
    ZIO.accessM[IndicesService](
      _.get.validateQuery(
        allShards = allShards,
        allowNoIndices = allowNoIndices,
        analyzeWildcard = analyzeWildcard,
        analyzer = analyzer,
        body = body,
        defaultOperator = defaultOperator,
        df = df,
        expandWildcards = expandWildcards,
        explain = explain,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        lenient = lenient,
        q = q,
        rewrite = rewrite
      )
    )

  def validateQuery(
    request: IndicesValidateQueryRequest
  ): ZIO[IndicesService, FrameworkException, IndicesValidateQueryResponse] =
    ZIO.accessM[IndicesService](_.get.execute(request))

}
