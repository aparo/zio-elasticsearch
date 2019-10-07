/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import com.github.mlangc.slf4zio.api._
import elasticsearch.requests.indices._
import elasticsearch.responses.cluster.ClusterIndexSetting
import elasticsearch.responses.indices._
import elasticsearch.ElasticSearch
import elasticsearch._
import io.circe._
import io.circe.syntax._
import elasticsearch.mappings._
import _root_.elasticsearch.queries.DefaultOperator
import elasticsearch.responses.{ GetIndexTemplatesResponse, GetMappingsResponse }
import elasticsearch.ZioResponse
import elasticsearch.common.circe.CirceUtils
import zio.ZIO

class IndicesManager(client: ElasticSearch) extends LoggingSupport {

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-analyze.html
   *
   * @param body body the body of the call
   * @param index The name of the index to scope the operation
   * @param preferLocal With `true`, specify that a local shard should be used if available, with `false`, use a random shard (default: true)
   * @param format Format of the output
   */
  def analyze(
    body: Json,
    index: Option[String] = None,
    preferLocal: Boolean = true,
    format: OutputFormat = OutputFormat.detailed
  ): ZioResponse[AnalyzeResponse] = {
    val request =
      AnalyzeRequest(
        body = body,
        index = index,
        preferLocal = preferLocal,
        format = format
      )

    analyze(request)

  }

  def analyze(
    request: AnalyzeRequest
  ): ZioResponse[AnalyzeResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clearcache.html
   *
   * @param indices A list of index name to limit the operation
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param fieldData Clear field data
   * @param request Clear request cache
   * @param recycler Clear the recycler cache
   * @param query Clear query caches
   * @param fields A list of fields to clear when using the `field_data` parameter (default: all)
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param fielddata Clear field data
   */
  def clearCache(
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    fieldData: Option[Boolean] = None,
    requestCache: Option[Boolean] = None,
    recycler: Option[Boolean] = None,
    query: Option[Boolean] = None,
    fields: Seq[String] = Nil,
    allowNoIndices: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    fielddata: Option[Boolean] = None
  ): ZioResponse[ClearIndicesCacheResponse] = {
    val request = ClearIndicesCacheRequest(
      indices = indices,
      expandWildcards = expandWildcards,
      fieldData = fieldData,
      request = requestCache,
      recycler = recycler,
      query = query,
      fields = fields,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable,
      fielddata = fielddata
    )

    clearCache(request)

  }

  def clearCache(
    request: ClearIndicesCacheRequest
  ): ZioResponse[ClearIndicesCacheResponse] =
    client.execute(request)

  /*
   *  Close an index.
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
   *
   * @param index The name of the index
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param masterTimeout Specify timeout for connection to master
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param timeout Explicit operation timeout
   */
  def close(
    index: String,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    allowNoIndices: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    ignoreUnavailable: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZioResponse[CloseIndexResponse] = {
    val request = CloseIndexRequest(
      index = index,
      expandWildcards = expandWildcards,
      allowNoIndices = allowNoIndices,
      masterTimeout = masterTimeout,
      ignoreUnavailable = ignoreUnavailable,
      timeout = timeout
    )

    close(request)

  }

  def close(
    request: CloseIndexRequest
  ): ZioResponse[CloseIndexResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-create-index.html
   *
   * @param index The name of the index
   * @param body body the body of the call
   * @param waitForActiveShards Set the number of active shards to wait for before the operation returns.
   * @param timeout Explicit operation timeout
   * @param masterTimeout Specify timeout for connection to master
   * @param updateAllTypes Whether to update the mapping for all fields with the same name across all types or not
   */
  def create(
    index: String,
    body: JsonObject = JsonObject.empty,
    waitForActiveShards: Option[Int] = None,
    timeout: Option[String] = None,
    masterTimeout: Option[String] = None,
    updateAllTypes: Option[Boolean] = None
  ): ZioResponse[CreateIndexResponse] = {
    val request = CreateIndexRequest(
      index = index,
      body = CirceUtils.cleanValue(body),
      waitForActiveShards = waitForActiveShards,
      timeout = timeout,
      masterTimeout = masterTimeout,
      updateAllTypes = updateAllTypes
    )

    create(request)

  }

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-create-index.html
   *
   * @param index The name of the index
   * @param body body the body of the call
   * @param waitForActiveShards Set the number of active shards to wait for before the operation returns.
   * @param timeout Explicit operation timeout
   * @param masterTimeout Specify timeout for connection to master
   * @param updateAllTypes Whether to update the mapping for all fields with the same name across all types or not
   */
  def createIfNotExists(
    index: String,
    body: JsonObject = JsonObject.empty,
    waitForActiveShards: Option[Int] = None,
    timeout: Option[String] = None,
    masterTimeout: Option[String] = None,
    updateAllTypes: Option[Boolean] = None
  ): ZioResponse[Unit] =
    for {
      existsRes <- exists(index)
      _ <- create(
        CreateIndexRequest(
          index = index,
          body = CirceUtils.cleanValue(body),
          waitForActiveShards = waitForActiveShards,
          timeout = timeout,
          masterTimeout = masterTimeout,
          updateAllTypes = updateAllTypes
        )
      ).when(existsRes.exists)
    } yield ()

  def create(
    request: CreateIndexRequest
  ): ZioResponse[CreateIndexResponse] =
    client.execute(request)

  def delete(
    index: String
  ): ZioResponse[DeleteIndexResponse] =
    this.delete(indices = List(index))

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-delete-index.html
   *
   * @param indices A list of indices to delete; use `_all` or `*` string to delete all indices
   * @param timeout Explicit operation timeout
   * @param masterTimeout Specify timeout for connection to master
   */
  def delete(
    indices: Seq[String] = Nil,
    timeout: Option[String] = None,
    masterTimeout: Option[String] = None
  ): ZioResponse[DeleteIndexResponse] = {
    val request =
      DeleteIndexRequest(
        indices = indices,
        timeout = timeout,
        masterTimeout = masterTimeout
      )

    delete(request)

  }

  def delete(
    request: DeleteIndexRequest
  ): ZioResponse[DeleteIndexResponse] =
    client.execute(request)

  def deleteAlias(
    alias: String,
    toDelete: Seq[String]
  ): ZioResponse[IndicesAliasesResponse] =
    deleteAlias(List(alias), toDelete)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param indices A list of index names (supports wildcards); use `_all` for all indices
   * @param aliases A list of aliases to delete (supports wildcards); use `_all` to delete all aliases for the specified indices.
   * @param timeout Explicit timestamp for the document
   * @param masterTimeout Specify timeout for connection to master
   */
  def deleteAlias(
    aliases: Seq[String] = Nil,
    indices: Seq[String] = Nil,
    timeout: Option[String] = None,
    masterTimeout: Option[String] = None
  ): ZioResponse[IndicesAliasesResponse] = {
    val actions = for {
      index <- indices
      alias <- aliases
    } yield new AliasAction(AliasActionType.Remove, index, alias)

    val body = JsonObject.fromMap(Map("actions" -> actions.asJson))
    val request =
      IndicesAliasesRequest(
        body = body,
        timeout = timeout,
        masterTimeout = masterTimeout
      )
    deleteAlias(request)

  }

  def deleteAlias(
    request: IndicesAliasesRequest
  ): ZioResponse[IndicesAliasesResponse] =
    client.execute(request)

  def addAlias(
    alias: String,
    indices: List[String]
  ): ZioResponse[IndicesAliasesResponse] =
    /*
          Add an alias to point to a set of indices.
          (See :ref:`qdb-guide-reference-api-admin-indices-aliases`)

          :param alias: the name of an alias
          :param indices: a list of indices

     */
    putAlias(indices = indices, name = alias)

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
  ): ZioResponse[IndicesAliasesResponse] = {
    val request = IndicesAliasesRequest(
      indices = indices,
      name = Seq(name),
      body = body,
      timeout = timeout,
      masterTimeout = masterTimeout
    )

    putAlias(request)

  }

  def putAlias(
    request: IndicesAliasesRequest
  ): ZioResponse[IndicesAliasesResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the template
   * @param timeout Explicit operation timeout
   * @param masterTimeout Specify timeout for connection to master
   */
  def deleteTemplate(
    name: String,
    timeout: Option[String] = None,
    masterTimeout: Option[String] = None
  ): ZioResponse[DeleteIndexTemplateResponse] = {
    val request =
      DeleteIndexTemplateRequest(
        name = name,
        timeout = timeout,
        masterTimeout = masterTimeout
      )

    deleteTemplate(request)

  }

  def deleteTemplate(
    request: DeleteIndexTemplateRequest
  ): ZioResponse[DeleteIndexTemplateResponse] =
    client.execute(request)

  def exists(
    index: String
  ): ZioResponse[IndicesExistsResponse] =
    exists(Seq(index))

  def existsAsBoolean(
    index: String
  ): ZioResponse[Boolean] =
    exists(Seq(index)).map(_.exists)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-exists.html
   *
   * @param indices A list of index names
   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param includeDefaults Whether to return all default setting for each of the indices.
   * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   * @param flatSettings Return settings in flat format (default: false)
   */
  def exists(
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    local: Boolean = false,
    includeDefaults: Boolean = false,
    allowNoIndices: Boolean = false,
    ignoreUnavailable: Boolean = false,
    flatSettings: Boolean = false
  ): ZioResponse[IndicesExistsResponse] = {
    val request = IndicesExistsRequest(
      indices = indices,
      expandWildcards = expandWildcards,
      local = local,
      includeDefaults = includeDefaults,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable,
      flatSettings = flatSettings
    )

    exists(request)

  }

  def exists(
    request: IndicesExistsRequest
  ): ZioResponse[IndicesExistsResponse] =
    client.execute(request)

  def deleteIndexIfExists(index: String) = {
    /*
        Deletes an index if it exists.

        :param index: the name of the index

     */
    //TODO
    //    if (!exists(Seq(index)))
    //      delete(Seq(index))

  }

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param indices A list of index names to filter aliases
   * @param name A list of alias names to return
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def existsAlias(
    indices: Seq[String] = Nil,
    name: Seq[String] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    local: Boolean = false
  ): ZioResponse[GetAliasesResponse] = {
    val request = AliasesExistRequest(
      indices = indices,
      name = name,
      ignoreUnavailable = ignoreUnavailable,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      local = local
    )

    existsAlias(request)

  }

  def existsAlias(
    request: AliasesExistRequest
  ): ZioResponse[GetAliasesResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The comma separated names of the index templates
   * @param flatSettings Return settings in flat format (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def existsTemplate(
    name: String,
    flatSettings: Boolean = false,
    masterTimeout: Option[String] = None,
    local: Boolean = false
  ): ZioResponse[Boolean] = {
    val request =
      GetIndexTemplatesRequest(
        name = Some(name),
        flatSettings = flatSettings,
        masterTimeout = masterTimeout,
        local = local
      )

    existsTemplate(request).map(_.contains(name))

  }

  def existsTemplate(
    request: GetIndexTemplatesRequest
  ): ZioResponse[GetIndexTemplatesResponse] =
    client.execute(request)

  def existsType(
    index: String,
    docType: String
  ): ZioResponse[TypesExistsResponse] =
    existsType(List(index), List(docType))

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-types-exists.html
   *
   * @param indices A list of index names; use `_all` to check the types across all indices
   * @param docTypes A list of document types to check
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def existsType(
    indices: Seq[String] = Nil,
    docTypes: Seq[String] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    local: Boolean = false
  ): ZioResponse[TypesExistsResponse] = {
    val request = TypesExistsRequest(
      indices = indices,
      docTypes = docTypes,
      ignoreUnavailable = ignoreUnavailable,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      local = local
    )

    existsType(request)

  }

  def existsType(
    request: TypesExistsRequest
  ): ZioResponse[TypesExistsResponse] =
    client.execute(request)

  /*
   * Flushes one or more indices (clear memory)
   * If a bulk is full, it sends it.
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-flush.html
   *
   * @param indices A list of index names; use `_all` or empty string for all indices
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param force Whether a flush should be forced even if it is not necessarily needed ie. if no changes will be committed to the index. This is useful if transaction log IDs should be incremented even if no uncommitted changes are present. (This setting can be considered as internal)
   * @param waitIfOngoing If set to true the flush operation will block until the flush can be executed if another flush operation is already executing. The default is true. If set to false the flush will be skipped iff if another flush operation is already running.
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def flush(
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    force: Option[Boolean] = None,
    waitIfOngoing: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZioResponse[FlushResponse] = {
    val request = FlushRequest(
      indices = indices,
      expandWildcards = expandWildcards,
      force = force,
      waitIfOngoing = waitIfOngoing,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable
    )

    flush(request)

  }

  def flush(
    request: FlushRequest
  ): ZioResponse[FlushResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-synced-flush.html
   *
   * @param indices A list of index names; use `_all` or empty string for all indices
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   */
  def flushSynced(
    indices: Seq[String] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil
  ): ZioResponse[SyncedFlushResponse] = {
    val request = SyncedFlushRequest(
      indices = indices,
      ignoreUnavailable = ignoreUnavailable,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards
    )

    flushSynced(request)

  }

  def flushSynced(
    request: SyncedFlushRequest
  ): ZioResponse[SyncedFlushResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-forcemerge.html
   *
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param onlyExpungeDeletes Specify whether the operation should only expunge deleted documents
   * @param operationThreading TODO: ?
   * @param waitForMerge Specify whether the request should block until the merge process is finished (default: true)
   * @param flush Specify whether the index should be flushed after performing the operation (default: true)
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param maxNumSegments The number of segments the index should be merged into (default: dynamic)
   */
  def forcemerge(
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    onlyExpungeDeletes: Option[Boolean] = None,
    operationThreading: Option[String] = None,
    waitForMerge: Boolean = true,
    flush: Boolean = true,
    allowNoIndices: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    maxNumSegments: Option[Double] = None
  ): ZioResponse[ForceMergeResponse] = {
    val request = ForceMergeRequest(
      indices = indices,
      expandWildcards = expandWildcards,
      onlyExpungeDeletes = onlyExpungeDeletes,
      operationThreading = operationThreading,
      waitForMerge = waitForMerge,
      flush = flush,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable,
      maxNumSegments = maxNumSegments
    )

    forcemerge(request)

  }

  def forcemerge(
    request: ForceMergeRequest
  ): ZioResponse[ForceMergeResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-index.html
   *
   * @param indices A list of index names
   * @param feature A list of features
   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param includeDefaults Whether to return all default setting for each of the indices.
   * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   * @param flatSettings Return settings in flat format (default: false)
   */
  def get(
    indices: Seq[String] = Nil,
    feature: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    local: Boolean = false,
    includeDefaults: Boolean = false,
    allowNoIndices: Boolean = false,
    ignoreUnavailable: Boolean = false,
    flatSettings: Boolean = false
  ): ZioResponse[GetIndexResponse] = {
    val request = GetIndexRequest(
      indices = indices,
      feature = feature,
      expandWildcards = expandWildcards,
      local = local,
      includeDefaults = includeDefaults,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable,
      flatSettings = flatSettings
    )

    get(request)

  }

  def get(
    request: GetIndexRequest
  ): ZioResponse[GetIndexResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param indices A list of index names to filter aliases
   * @param names A list of alias names to return
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def getAlias(
    indices: Seq[String] = Nil,
    name: Seq[String] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    local: Boolean = false
  ): ZioResponse[GetAliasesResponse] = {
    val request = GetAliasesRequest(
      indices = indices,
      name = name,
      ignoreUnavailable = ignoreUnavailable,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      local = local
    )

    getAlias(request)

  }

  def getAlias(
    request: GetAliasesRequest
  ): ZioResponse[GetAliasesResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-field-mapping.html
   *
   * @param indices A list of index names
   * @param docTypes A list of document types
   * @param fields A list of fields
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param includeDefaults Whether the default mapping values should be returned as well
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def getFieldMapping(
    indices: Seq[String] = Nil,
    docTypes: Seq[String] = Nil,
    fields: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    local: Boolean = false,
    includeDefaults: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZioResponse[GetFieldMappingsResponse] = {
    val request = GetFieldMappingsRequest(
      indices = indices,
      docTypes = docTypes,
      fields = fields,
      expandWildcards = expandWildcards,
      local = local,
      includeDefaults = includeDefaults,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable
    )

    getFieldMapping(request)

  }

  def getFieldMapping(
    request: GetFieldMappingsRequest
  ): ZioResponse[GetFieldMappingsResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-mapping.html
   *
   * @param indices A list of index names
   * @param docTypes A list of document types
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def getMapping(
    indices: Seq[String] = Nil,
    docTypes: Seq[String] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    local: Boolean = false
  ): ZioResponse[GetMappingsResponse] = {
    val request = GetMappingsRequest(
      indices = indices,
      docTypes = docTypes,
      ignoreUnavailable = ignoreUnavailable,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      local = local
    )

    getMapping(request)

  }

  def getMapping(
    request: GetMappingsRequest
  ): ZioResponse[GetMappingsResponse] =
    client.execute(request)

  /*
   * Returns the current settings for an index.
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-settings.html
   *
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param name The name of the settings that should be included
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param includeDefaults Whether to return all default setting for each of the indices.
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param flatSettings Return settings in flat format (default: false)
   */
  def getSettings(
    indices: Seq[String] = Nil,
    name: Option[String] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    local: Boolean = false,
    includeDefaults: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    flatSettings: Boolean = false
  ): ZioResponse[GetSettingsResponse] = {
    val request = GetSettingsRequest(
      indices = indices,
      name = name,
      expandWildcards = expandWildcards,
      local = local,
      includeDefaults = includeDefaults,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable,
      flatSettings = flatSettings
    )

    getSettings(request)

  }

  def getSettings(
    request: GetSettingsRequest
  ): ZioResponse[GetSettingsResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The comma separated names of the index templates
   * @param flatSettings Return settings in flat format (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def getTemplate(
    name: String,
    flatSettings: Boolean = false,
    masterTimeout: Option[String] = None,
    local: Boolean = false
  ): ZioResponse[GetIndexTemplatesResponse] = {
    val request =
      GetIndexTemplatesRequest(
        name = Some(name),
        flatSettings = flatSettings,
        masterTimeout = masterTimeout,
        local = local
      )

    getTemplate(request)

  }

  /*
   * Shortcut to return all the templates
   */
  def getTemplates(): ZioResponse[GetIndexTemplatesResponse] = {
    val request =
      GetIndexTemplatesRequest(
        name = None,
        flatSettings = false,
        masterTimeout = None,
        local = true
      )

    getTemplate(request)

  }

  def getTemplate(
    request: GetIndexTemplatesRequest
  ): ZioResponse[GetIndexTemplatesResponse] =
    client.execute(request)

  /*
   * Open an index.
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
   *
   * @param index A comma separated list of indices to open
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param masterTimeout Specify timeout for connection to master
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param timeout Explicit operation timeout
   */
  def open(
    indices: Seq[String],
    expandWildcards: Seq[ExpandWildcards] = Nil,
    allowNoIndices: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    ignoreUnavailable: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZioResponse[OpenIndexResponse] = {
    val request = OpenIndexRequest(
      indices = indices,
      expandWildcards = expandWildcards,
      allowNoIndices = allowNoIndices,
      masterTimeout = masterTimeout,
      ignoreUnavailable = ignoreUnavailable,
      timeout = timeout
    )

    open(request)

  }

  def open(
    request: OpenIndexRequest
  ): ZioResponse[OpenIndexResponse] =
    client.execute(request)

  def open(
    index: String
  ): ZioResponse[OpenIndexResponse] =
    client.execute(new OpenIndexRequest(Seq(index)))

  def putMapping(
    index: String,
    docType: String,
    mapping: RootDocumentMapping
  ): ZioResponse[PutMappingResponse] =
    putMapping(indices = Seq(index), docType = docType, body = mapping.asJson)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-put-mapping.html
   *
   * @param indices A list of index names the mapping should be added to (supports wildcards); use `_all` or omit to add the mapping on all indices.
   * @param docType The name of the document type
   * @param body body the body of the call
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param updateAllTypes Whether to update the mapping for all fields with the same name across all types or not
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param masterTimeout Specify timeout for connection to master
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param timeout Explicit operation timeout
   */
  def putMapping(
    indices: Seq[String] = Nil,
    docType: String,
    body: Json,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    updateAllTypes: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    ignoreUnavailable: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZioResponse[PutMappingResponse] = {
    val request = PutMappingRequest(
      indices = indices,
      docType = docType,
      body = CirceUtils.cleanValue(body),
      expandWildcards = expandWildcards,
      updateAllTypes = updateAllTypes,
      allowNoIndices = allowNoIndices,
      masterTimeout = masterTimeout,
      ignoreUnavailable = ignoreUnavailable,
      timeout = timeout
    )

    putMapping(request)

  }

  def putMapping(
    request: PutMappingRequest
  ): ZioResponse[PutMappingResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-update-settings.html
   *
   * @param body body the body of the call
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param masterTimeout Specify timeout for connection to master
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param flatSettings Return settings in flat format (default: false)
   * @param preserveExisting Whether to update existing settings. If set to `true` existing settings on an index remain unchanged, the default is `false`
   */
  def putSettings(
    body: Json,
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    allowNoIndices: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    ignoreUnavailable: Option[Boolean] = None,
    flatSettings: Boolean = false,
    preserveExisting: Option[Boolean] = None
  ): ZioResponse[UpdateSettingsResponse] = {
    val request = UpdateSettingsRequest(
      body = body,
      indices = indices,
      expandWildcards = expandWildcards,
      allowNoIndices = allowNoIndices,
      masterTimeout = masterTimeout,
      ignoreUnavailable = ignoreUnavailable,
      flatSettings = flatSettings,
      preserveExisting = preserveExisting
    )

    putSettings(request)

  }

  def putSettings(
    request: UpdateSettingsRequest
  ): ZioResponse[UpdateSettingsResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the template
   * @param body body the body of the call
   * @param create Whether the index template should only be added if new or can also replace an existing one
   * @param order The order for this template when merging multiple matching ones (higher numbers are merged later, overriding the lower numbers)
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param flatSettings Return settings in flat format (default: false)
   */
  def putTemplate(
    name: String,
    body: Json,
    create: Boolean = false,
    order: Option[Double] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    flatSettings: Boolean = false
  ): ZioResponse[PutIndexTemplateResponse] = {
    val request = PutIndexTemplateRequest(
      name = name,
      body = CirceUtils.cleanValue(body),
      create = create,
      order = order,
      masterTimeout = masterTimeout,
      timeout = timeout,
      flatSettings = flatSettings
    )

    putTemplate(request)

    putTemplate(request)
  }

  def putTemplate(
    request: PutIndexTemplateRequest
  ): ZioResponse[PutIndexTemplateResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-recovery.html
   *
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param detailed Whether to display detailed information about shard recovery
   * @param activeOnly Display only those recoveries that are currently on-going
   */
  def recovery(
    indices: Seq[String] = Nil,
    detailed: Boolean = false,
    activeOnly: Boolean = false
  ): ZioResponse[RecoveryResponse] = {
    val request = RecoveryRequest(
      indices = indices,
      detailed = detailed,
      activeOnly = activeOnly
    )

    recovery(request)

  }

  def recovery(
    request: RecoveryRequest
  ): ZioResponse[RecoveryResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-refresh.html
   *
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param force Force a refresh even if not required
   * @param operationThreading TODO: ?
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def refresh(
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    force: Boolean = false,
    operationThreading: Option[String] = None,
    allowNoIndices: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZioResponse[RefreshResponse] = {
    // Custom Code On
    client.flushBulk()

    val request = RefreshRequest(
      indices = indices,
      expandWildcards = expandWildcards,
      force = force,
      operationThreading = operationThreading,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable
    )
    refresh(request)

  }

  def refresh(
    request: RefreshRequest
  ): ZioResponse[RefreshResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
   *
   * @param alias The name of the alias to rollover
   * @param newIndex The name of the rollover index
   * @param body body the body of the call
   * @param timeout Explicit operation timeout
   * @param dryRun If set to true the rollover action will only be validated but not actually performed even if a condition matches. The default is false
   * @param masterTimeout Specify timeout for connection to master
   * @param waitForActiveShards Set the number of active shards to wait for on the newly created rollover index before the operation returns.
   */
  def rollover(
    alias: String,
    newIndex: Option[String] = None,
    body: Json,
    timeout: Option[String] = None,
    dryRun: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZioResponse[RolloverResponse] = {
    val request = RolloverRequest(
      alias = alias,
      newIndex = newIndex,
      body = body,
      timeout = timeout,
      dryRun = dryRun,
      masterTimeout = masterTimeout,
      waitForActiveShards = waitForActiveShards
    )

    rollover(request)

  }

  def rollover(
    request: RolloverRequest
  ): ZioResponse[RolloverResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-segments.html
   *
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param verbose Includes detailed memory usage by Lucene.
   * @param operationThreading TODO: ?
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def segments(
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    verbose: Boolean = false,
    operationThreading: Option[String] = None,
    allowNoIndices: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZioResponse[IndicesSegmentResponse] = {
    val request = IndicesSegmentsRequest(
      indices = indices,
      expandWildcards = expandWildcards,
      verbose = verbose,
      operationThreading = operationThreading,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable
    )

    segments(request)

  }

  def segments(
    request: IndicesSegmentsRequest
  ): ZioResponse[IndicesSegmentResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shards-stores.html
   *
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param operationThreading TODO: ?
   * @param status A list of statuses used to filter on shards to get store information for
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def shardStores(
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    operationThreading: Option[String] = None,
    status: Seq[String] = Nil,
    allowNoIndices: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZioResponse[IndicesShardStoresResponse] = {
    val request = IndicesShardStoresRequest(
      indices = indices,
      expandWildcards = expandWildcards,
      operationThreading = operationThreading,
      status = status,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable
    )

    shardStores(request)

  }

  def shardStores(
    request: IndicesShardStoresRequest
  ): ZioResponse[IndicesShardStoresResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shrink-index.html
   *
   * @param index The name of the source index to shrink
   * @param target The name of the target index to shrink into
   * @param body body the body of the call
   * @param timeout Explicit operation timeout
   * @param masterTimeout Specify timeout for connection to master
   * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
   */
  def shrink(
    index: String,
    target: String,
    body: Json,
    timeout: Option[String] = None,
    masterTimeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZioResponse[ShrinkResponse] = {
    val request = ShrinkRequest(
      index = index,
      target = target,
      body = body,
      timeout = timeout,
      masterTimeout = masterTimeout,
      waitForActiveShards = waitForActiveShards
    )

    shrink(request)

  }

  def shrink(
    request: ShrinkRequest
  ): ZioResponse[ShrinkResponse] =
    client.execute(request)

  /*
   * Retrieve the statistic of one or more indices
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-stats.html
   *
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param metric Limit the information returned the specific metrics.
   * @param fielddataFields A list of fields for `fielddata` index metric (supports wildcards)
   * @param groups A list of search groups for `search` index metric
   * @param completionFields A list of fields for `fielddata` and `suggest` index metric (supports wildcards)
   * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
   * @param fields A list of fields for `fielddata` and `completion` index metric (supports wildcards)
   * @param types A list of document types for the `indexing` index metric
   * @param level Return stats aggregated at cluster, index or shard level
   */
  def stats(
    indices: Seq[String] = Nil,
    metric: Option[String] = None,
    fielddataFields: Seq[String] = Nil,
    groups: Seq[String] = Nil,
    completionFields: Seq[String] = Nil,
    includeSegmentFileSizes: Boolean = false,
    fields: Seq[String] = Nil,
    types: Seq[String] = Nil,
    level: Level = Level.indices
  ): ZioResponse[IndicesStatsResponse] = {
    val request = IndicesStatsRequest(
      indices = indices,
      metric = metric,
      fielddataFields = fielddataFields,
      groups = groups,
      completionFields = completionFields,
      includeSegmentFileSizes = includeSegmentFileSizes,
      fields = fields,
      types = types,
      level = level
    )

    stats(request)

  }

  def stats(
    request: IndicesStatsRequest
  ): ZioResponse[IndicesStatsResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-upgrade.html
   *
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param onlyAncientSegments If true, only ancient (an older Lucene major release) segments will be upgraded
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param waitForCompletion Specify whether the request should block until the all segments are upgraded (default: false)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def upgrade(
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    onlyAncientSegments: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    waitForCompletion: Boolean = false,
    ignoreUnavailable: Option[Boolean] = None
  ): ZioResponse[UpgradeStatusResponse] = {
    val request = UpgradeStatusRequest(
      indices = indices,
      expandWildcards = expandWildcards,
      onlyAncientSegments = onlyAncientSegments,
      allowNoIndices = allowNoIndices,
      waitForCompletion = waitForCompletion,
      ignoreUnavailable = ignoreUnavailable
    )

    upgrade(request)

  }

  def upgrade(
    request: UpgradeStatusRequest
  ): ZioResponse[UpgradeStatusResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-validate.html
   *
   * @param body body the body of the call
   * @param indices A list of index names to restrict the operation; use `_all` or empty string to perform the operation on all indices
   * @param docTypes A list of document types to restrict the operation; leave empty to perform the operation on all types
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param analyzer The analyzer to use for the query string
   * @param explain Return detailed information about the error
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param operationThreading TODO: ?
   * @param q Query in the Lucene query string syntax
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param rewrite Provide a more detailed explanation showing the actual Lucene query that will be executed.
   * @param df The field to use as default where no field prefix is given in the query string
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param lowercaseExpandedTerms Specify whether query terms should be lowercased
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def validateQuery(
    body: Json,
    indices: Seq[String] = Nil,
    docTypes: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    analyzer: Option[String] = None,
    explain: Option[Boolean] = None,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    analyzeWildcard: Boolean = false,
    operationThreading: Option[String] = None,
    q: Option[String] = None,
    lenient: Option[Boolean] = None,
    rewrite: Option[Boolean] = None,
    df: Option[String] = None,
    allowNoIndices: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZioResponse[ValidateQueryResponse] = {
    val request = ValidateQueryRequest(
      body = body,
      indices = indices,
      docTypes = docTypes,
      expandWildcards = expandWildcards,
      analyzer = analyzer,
      explain = explain,
      defaultOperator = defaultOperator,
      analyzeWildcard = analyzeWildcard,
      operationThreading = operationThreading,
      q = q,
      lenient = lenient,
      rewrite = rewrite,
      df = df,
      allowNoIndices = allowNoIndices,
      ignoreUnavailable = ignoreUnavailable
    )

    validateQuery(request)

  }

  def validateQuery(
    request: ValidateQueryRequest
  ): ZioResponse[ValidateQueryResponse] =
    client.execute(request)

  def createWithSettings(
    index: String,
    settings: String
  ): ZioResponse[CreateIndexResponse] = {
    /*
        Creates an index with optional settings.
        :ref:`qdb-guide-reference-api-admin-indices-create-index`

        :param index: the name of the index
        :keyword settings: a settings object or a dict containing settings
     */
    import io.circe.parser._

    val request = CreateIndexRequest(
      index,
      body = CirceUtils.cleanValue(
        JsonObject.fromMap(Map("settings" -> parse(settings).right.get))
      )
    )
    client.execute(request)
    // TODO add waiting creation
  }

  def ensureIndex(
    index: String,
    settings: ClusterIndexSetting,
    mappings: Map[String, RootDocumentMapping]
  ): Unit =
    ensureIndex(index, Settings.fromCluster(settings), mappings)

  def ensureIndex(index: String, settings: String): ZioResponse[Unit] =
    for {
      existResult <- this.exists(Seq(index))
      _ <- if (existResult.exists) ZIO.unit else createWithSettings(index, settings)
    } yield ()

  def ensureIndex(
    index: String,
    settings: Settings = Settings.ElasticSearchBase,
    mappings: Map[String, RootDocumentMapping] = Map.empty[String, RootDocumentMapping]
  ): ZioResponse[Unit] = {

    def createIfNotExistPriv(): ZioResponse[Unit] =
      for {
        _ <- createWithSettingsAndMappings(index, settings, mappings)
      } yield ()

    //      while (!client.awaitResult(this.exists(Seq(index)).value).map(_.isExists).getOrElse(false)) {
    //        logger.warn(s"Waiting for creation of $index")
    //        Thread.sleep(client.creationSleep.toLong)
    //        //        val res = client.cluster.health(waitForStatus=Some(WaitForStatus.yellow)) //TODO restore
    //        val res = client.cluster.health()
    //        client.awaitResult(res.value)
    //      }

    def createIfExist(): ZioResponse[Unit] =
      //TODO add custom mappings
//      val currMappings = client.mappings.get(index)
//      val toAdd = mappings.keys.toSet -- currMappings.keys.toSet
//      //TODO aparo: implement diff for mappings
//      //      currMappings.head.getDiff(...)
//      toAdd.foreach { name =>
//        mappings.get(name).foreach { mapping =>
//          logger.info(s"$index $name ${mapping.asJson}")
//          val res = client.indices.putMapping(Seq(index), name, mapping)
//          client.awaitResult(res.value)
//        }
//      }
//
//      //TODO aparo: implement diff for mappings
//      //      val toDiff =  currMappings.map(_.field).toSet -- mappings.map(_.field).toSet
//      //
//      //      toDiff.foreach{
//      //        mappingName=>
//      //          for( oldMapping <- currMappings.find(_.field==mappingName);
//      //            newMapping <- mappings.find(_.field==mappingName);
//      //          ) {
//      //            oldMapping.
//      //          }
//      //      }
//
//      //      val res = client.cluster.health(waitForStatus=Some(WaitForStatus.yellow)) //TODO restore
//      val res = client.cluster.health()
//      client.awaitResult(res.value)
      ZIO.unit

    for {
      existResult <- this.exists(Seq(index))
      _ <- if (existResult.exists) createIfExist() else createIfNotExistPriv()
    } yield ()

  }

  def createWithSettingsAndMappings(
    index: String,
    settings: Settings = Settings(),
    mappings: Map[String, RootDocumentMapping] = Map.empty[String, RootDocumentMapping]
  ): ZioResponse[CreateIndexResponse] = {
    /*
        Creates an index with optional settings.
        :ref:`qdb-guide-reference-api-admin-indices-create-index`

        :param index: the name of the index
        :keyword settings: a settings object or a dict containing settings
     */

    val request = CreateIndexRequest(
      index,
      body = CirceUtils.cleanValue(
        JsonObject.fromMap(
          Map("settings" -> settings.asJson, "mappings" -> mappings.asJson)
        )
      )
    )

    client.execute(request)
  }

  def putMapping(
    indices: Seq[String],
    docType: String,
    mapping: RootDocumentMapping
  ): ZioResponse[PutMappingResponse] =
    putMapping(
      indices = indices,
      docType = docType,
      body = CirceUtils.cleanValue(mapping.asJson)
    )

  //  def ensureIndex(index: String,
  //                  settings: org.qdb.common.settings.Settings,
  //                  mappings: Map[String, MappingMetaData]): Unit = {
  //    val existsValue = client.awaitResult(this.exists(Seq(index)))
  //    if (!existsValue.isExists) {
  //      val request = new CreateIndexRequest(index).settings(settings)
  //      mappings.foreach {
  //        case (name, mapping) =>
  //          request.mapping(name, mapping.getSourceAsMap)
  //      }
  //      client.awaitResult(client.indices.create(request))
  //    } else {
  //      val currMappings = client.mappings.get(index)
  //      val toAdd = mappings.keySet -- currMappings.map(_.field).toSet
  //      //TODO aparo: implement diff for mappings
  //      toAdd.foreach { name =>
  //        mappings.find(_._1 == name).foreach { mapping =>
  //          val request = new PutMappingRequest(index).`type`(name).source(mapping._2.getSourceAsMap)
  //          client.indices.putMapping(request)
  //        }
  //      }
  //      //      val res = client.cluster.health(waitForStatus=Some(WaitForStatus.yellow)) //TODO restore
  //      val res = client.cluster.health()
  //      client.awaitResult(res)
  //
  //    }
  //  }

}
