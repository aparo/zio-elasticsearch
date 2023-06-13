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

package zio.elasticsearch.indices

import zio.elasticsearch._
import zio.elasticsearch.common._
import zio.elasticsearch.indices.add_block.{ AddBlockRequest, AddBlockResponse }
import zio.elasticsearch.indices.analyze.{ AnalyzeRequest, AnalyzeResponse }
import zio.elasticsearch.indices.clear_cache.{ ClearCacheRequest, ClearCacheResponse }
import zio.elasticsearch.indices.clone.{ CloneRequest, CloneResponse }
import zio.elasticsearch.indices.close.{ CloseRequest, CloseResponse }
import zio.elasticsearch.indices.create.{ CreateRequest, CreateResponse }
import zio.elasticsearch.indices.create_data_stream.{ CreateDataStreamRequest, CreateDataStreamResponse }
import zio.elasticsearch.indices.data_streams_stats.{ DataStreamsStatsRequest, DataStreamsStatsResponse }
import zio.elasticsearch.indices.delete.{ DeleteRequest, DeleteResponse }
import zio.elasticsearch.indices.delete_alias.{ DeleteAliasRequest, DeleteAliasResponse }
import zio.elasticsearch.indices.delete_data_stream.{ DeleteDataStreamRequest, DeleteDataStreamResponse }
import zio.elasticsearch.indices.delete_index_template.{ DeleteIndexTemplateRequest, DeleteIndexTemplateResponse }
import zio.elasticsearch.indices.delete_template.{ DeleteTemplateRequest, DeleteTemplateResponse }
import zio.elasticsearch.indices.disk_usage.{ DiskUsageRequest, DiskUsageResponse }
import zio.elasticsearch.indices.downsample.{ DownsampleRequest, DownsampleResponse }
import zio.elasticsearch.indices.exists.ExistsRequest
import zio.elasticsearch.indices.exists_alias.ExistsAliasRequest
import zio.elasticsearch.indices.exists_index_template.{ ExistsIndexTemplateRequest, ExistsIndexTemplateResponse }
import zio.elasticsearch.indices.exists_template.{ ExistsTemplateRequest, ExistsTemplateResponse }
import zio.elasticsearch.indices.field_usage_stats.{ FieldUsageStatsRequest, FieldUsageStatsResponse }
import zio.elasticsearch.indices.flush.{ FlushRequest, FlushResponse }
import zio.elasticsearch.indices.forcemerge.{ ForcemergeRequest, ForcemergeResponse }
import zio.elasticsearch.indices.get.{ GetRequest, GetResponse }
import zio.elasticsearch.indices.get_alias.{ GetAliasRequest, GetAliasResponse }
import zio.elasticsearch.indices.get_data_stream.{ GetDataStreamRequest, GetDataStreamResponse }
import zio.elasticsearch.indices.get_field_mapping.{ GetFieldMappingRequest, GetFieldMappingResponse }
import zio.elasticsearch.indices.get_index_template.{ GetIndexTemplateRequest, GetIndexTemplateResponse }
import zio.elasticsearch.indices.get_mapping.{ GetMappingRequest, GetMappingResponse }
import zio.elasticsearch.indices.get_settings.{ GetSettingsRequest, GetSettingsResponse }
import zio.elasticsearch.indices.get_template.{ GetTemplateRequest, GetTemplateResponse }
import zio.elasticsearch.indices.migrate_to_data_stream.{ MigrateToDataStreamRequest, MigrateToDataStreamResponse }
import zio.elasticsearch.indices.modify_data_stream.{ ModifyDataStreamRequest, ModifyDataStreamResponse }
import zio.elasticsearch.indices.open.{ OpenRequest, OpenResponse }
import zio.elasticsearch.indices.promote_data_stream.{ PromoteDataStreamRequest, PromoteDataStreamResponse }
import zio.elasticsearch.indices.put_alias.{ PutAliasRequest, PutAliasResponse }
import zio.elasticsearch.indices.put_index_template.{ PutIndexTemplateRequest, PutIndexTemplateResponse }
import zio.elasticsearch.indices.put_mapping.{ PutMappingRequest, PutMappingResponse }
import zio.elasticsearch.indices.put_settings.{ PutSettingsRequest, PutSettingsResponse }
import zio.elasticsearch.indices.put_template.{ PutTemplateRequest, PutTemplateResponse }
import zio.elasticsearch.indices.recovery.RecoveryRequest
import zio.elasticsearch.indices.refresh.{ RefreshRequest, RefreshResponse }
import zio.elasticsearch.indices.reload_search_analyzers.{ ReloadSearchAnalyzersRequest, ReloadSearchAnalyzersResponse }
import zio.elasticsearch.indices.requests._
import zio.elasticsearch.indices.resolve_index.{ ResolveIndexRequest, ResolveIndexResponse }
import zio.elasticsearch.indices.rollover.{ RolloverRequest, RolloverResponse }
import zio.elasticsearch.indices.segments.{ SegmentsRequest, SegmentsResponse }
import zio.elasticsearch.indices.shard_stores.{ ShardStoresRequest, ShardStoresResponse }
import zio.elasticsearch.indices.shrink.{ ShrinkRequest, ShrinkResponse }
import zio.elasticsearch.indices.simulate_index_template.{ SimulateIndexTemplateRequest, SimulateIndexTemplateResponse }
import zio.elasticsearch.indices.simulate_template.{ SimulateTemplateRequest, SimulateTemplateResponse }
import zio.elasticsearch.indices.split.{ SplitRequest, SplitResponse }
import zio.elasticsearch.indices.stats.{ StatsRequest, StatsResponse }
import zio.elasticsearch.indices.update_aliases.{ UpdateAliasesRequest, UpdateAliasesResponse }
import zio.elasticsearch.indices.validate_query.{ ValidateQueryRequest, ValidateQueryResponse }
import zio.exception._
import zio.json.ast._
import zio.{ ZIO, _ }

object IndicesManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, IndicesManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new IndicesManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait IndicesManager {
  def httpService: ElasticSearchHttpService

  /*
   * Adds a block to an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/index-modules-blocks.html
   *
   * @param index A comma separated list of indices to add a block to
   * @param block The block to add (one of read, write, read_only or metadata)
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def addBlock(
    index: String,
    block: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, AddBlockResponse] = {
    val request = AddBlockRequest(
      index = index,
      block = block,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    addBlock(request)

  }

  def addBlock(
    request: AddBlockRequest
  ): ZIO[Any, FrameworkException, AddBlockResponse] =
    httpService.execute[Json, AddBlockResponse](request)

  /*
   * Performs the analysis process on a text and return the tokens breakdown of the text.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-analyze.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param index The name of the index to scope the operation
   */
  def analyze(
    body: AnalyzeRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    index: Option[String] = None
  ): ZIO[Any, FrameworkException, AnalyzeResponse] = {
    val request = AnalyzeRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      index = index
    )

    analyze(request)

  }

  def analyze(
    request: AnalyzeRequest
  ): ZIO[Any, FrameworkException, AnalyzeResponse] =
    httpService.execute[AnalyzeRequestBody, AnalyzeResponse](request)

  /*
   * Clears all or specific caches for one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clearcache.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    fielddata: Option[Boolean] = None,
    fields: Chunk[String] = Chunk.empty,
    ignoreUnavailable: Option[Boolean] = None,
    index: Chunk[String] = Chunk.empty,
    indices: Chunk[String] = Chunk.empty,
    query: Option[Boolean] = None,
    request: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, ClearCacheResponse] = {
    val req = ClearCacheRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

    clearCache(req)

  }

  def clearCache(
    request: ClearCacheRequest
  ): ZIO[Any, FrameworkException, ClearCacheResponse] =
    httpService.execute[Json, ClearCacheResponse](request)

  /*
   * Clones an index
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clone-index.html
   *
   * @param index The name of the source index to clone
   * @param target The name of the target index to clone into
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for on the cloned index before the operation returns.
   */
  def clone(
    index: String,
    target: String,
    body: CloneRequestBody = CloneRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, CloneResponse] = {
    val request = CloneRequest(
      index = index,
      target = target,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      masterTimeout = masterTimeout,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    clone(request)

  }

  def clone(
    request: CloneRequest
  ): ZIO[Any, FrameworkException, CloneResponse] =
    httpService.execute[CloneRequestBody, CloneResponse](request)

  /*
   * Closes an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
   *
   * @param index A comma separated list of indices to close
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Sets the number of active shards to wait for before the operation returns.
   */
  def close(
    index: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, CloseResponse] = {
    val request = CloseRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    close(request)

  }

  def close(request: CloseRequest): ZIO[Any, FrameworkException, CloseResponse] =
    httpService.execute[Json, CloseResponse](request)

  /*
   * Creates an index with optional settings and mappings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-create-index.html
   *
   * @param index The name of the index
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for before the operation returns.
   */
  def create(
    index: String,
    body: CreateRequestBody = CreateRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, CreateResponse] = {
    val request = CreateRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      masterTimeout = masterTimeout,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    create(request)

  }

  def create(
    request: CreateRequest
  ): ZIO[Any, FrameworkException, CreateResponse] =
    httpService.execute[CreateRequestBody, CreateResponse](request)

  /*
   * Creates a data stream
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/data-streams.html
   *
   * @param name The name of the data stream
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def createDataStream(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, CreateDataStreamResponse] = {
    val request = CreateDataStreamRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    createDataStream(request)

  }

  def createDataStream(
    request: CreateDataStreamRequest
  ): ZIO[Any, FrameworkException, CreateDataStreamResponse] =
    httpService.execute[Json, CreateDataStreamResponse](request)

  /*
   * Provides statistics on operations happening in a data stream.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/data-streams.html
   *
   * @param expandWildcards

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param name A comma-separated list of data stream names; use `_all` or empty string to perform the operation on all data streams
   */
  def dataStreamsStats(
    expandWildcards: ExpandWildcards,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    name: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, DataStreamsStatsResponse] = {
    val request = DataStreamsStatsRequest(
      expandWildcards = expandWildcards,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      name = name
    )

    dataStreamsStats(request)

  }

  def dataStreamsStats(
    request: DataStreamsStatsRequest
  ): ZIO[Any, FrameworkException, DataStreamsStatsResponse] =
    httpService.execute[Json, DataStreamsStatsResponse](request)

  /*
   * Deletes an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-delete-index.html
   *
   * @param indices A comma-separated list of indices to delete; use `_all` or `*` string to delete all indices
   * @param name

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
   * @param expandWildcards Whether wildcard expressions should get expanded to open, closed, or hidden indices
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def delete(
    indices: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteResponse] = {
    val request = DeleteRequest(
      indices = indices,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    delete(request)

  }

  def delete(
    request: DeleteRequest
  ): ZIO[Any, FrameworkException, DeleteResponse] =
    httpService.execute[Json, DeleteResponse](request)

  /*
   * Deletes an alias.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param indices A comma-separated list of index names (supports wildcards); use `_all` for all indices
   * @param name A comma-separated list of aliases to delete (supports wildcards); use `_all` to delete all aliases for the specified indices.
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit timestamp for the document
   */
  def deleteAlias(
    indices: Chunk[String] = Chunk.empty,
    name: Chunk[String] = Chunk.empty,
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteAliasResponse] = {
    val request = DeleteAliasRequest(
      indices = indices,
      name = name,
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteAlias(request)

  }

  def deleteAlias(
    request: DeleteAliasRequest
  ): ZIO[Any, FrameworkException, DeleteAliasResponse] =
    httpService.execute[Json, DeleteAliasResponse](request)

  /*
   * Deletes a data stream.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/data-streams.html
   *
   * @param name A comma-separated list of data streams to delete; use `*` to delete all data streams
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   */
  def deleteDataStream(
    name: Chunk[String] = Chunk.empty,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    expandWildcards: Seq[ExpandWildcards] = Nil
  ): ZIO[Any, FrameworkException, DeleteDataStreamResponse] = {
    val request = DeleteDataStreamRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      expandWildcards = expandWildcards
    )

    deleteDataStream(request)

  }

  def deleteDataStream(
    request: DeleteDataStreamRequest
  ): ZIO[Any, FrameworkException, DeleteDataStreamResponse] =
    httpService.execute[Json, DeleteDataStreamResponse](request)

  /*
   * Deletes an index template.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the template
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def deleteIndexTemplate(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteIndexTemplateResponse] = {
    val request = DeleteIndexTemplateRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteIndexTemplate(request)

  }

  def deleteIndexTemplate(
    request: DeleteIndexTemplateRequest
  ): ZIO[Any, FrameworkException, DeleteIndexTemplateResponse] =
    httpService.execute[Json, DeleteIndexTemplateResponse](request)

  /*
   * Deletes an index template.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the template
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def deleteTemplate(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteTemplateResponse] = {
    val request = DeleteTemplateRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteTemplate(request)

  }

  def deleteTemplate(
    request: DeleteTemplateRequest
  ): ZIO[Any, FrameworkException, DeleteTemplateResponse] =
    httpService.execute[Json, DeleteTemplateResponse](request)

  /*
   * Analyzes the disk usage of each field of an index or data stream
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-disk-usage.html
   *
   * @param index Comma-separated list of indices or data streams to analyze the disk usage
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param flush Whether flush or not before analyzing the index disk usage. Defaults to true
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param runExpensiveTasks Must be set to [true] in order for the task to be performed. Defaults to false.
   */
  def diskUsage(
    index: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flush: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    runExpensiveTasks: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, DiskUsageResponse] = {
    val request = DiskUsageRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      flush = flush,
      ignoreUnavailable = ignoreUnavailable,
      runExpensiveTasks = runExpensiveTasks
    )

    diskUsage(request)

  }

  def diskUsage(
    request: DiskUsageRequest
  ): ZIO[Any, FrameworkException, DiskUsageResponse] =
    httpService.execute[Json, DiskUsageResponse](request)

  /*
   * Downsample an index
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/xpack-rollup.html
   *
   * @param index The index to downsample
   * @param targetIndex The name of the target index to store downsampled data
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def downsample(
    index: String,
    targetIndex: String,
    body: DownsampleConfig,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DownsampleResponse] = {
    val request = DownsampleRequest(
      index = index,
      targetIndex = targetIndex,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    downsample(request)

  }

  def downsample(
    request: DownsampleRequest
  ): ZIO[Any, FrameworkException, DownsampleResponse] =
    httpService.execute[DownsampleConfig, DownsampleResponse](request)

  /*
   * Returns information about whether a particular index exists.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-exists.html
   *
   * @param indices A comma-separated list of index names
   * @param name Comma-separated list of index template names used to limit the request. Wildcard (*) expressions are supported.

   * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   * @param flatSettings Return settings in flat format (default: false)
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   * @param includeDefaults Whether to return all default setting for each of the indices.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def exists(
    indices: Chunk[String],
    name: Option[String] = None,
    masterTimeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    includeDefaults: Boolean = false,
    local: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, Boolean] = {
    val request = ExistsRequest(
      indices = indices,
      name = name,
      masterTimeout = masterTimeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      flatSettings = flatSettings,
      ignoreUnavailable = ignoreUnavailable,
      includeDefaults = includeDefaults,
      local = local
    )

    exists(request)

  }

  def exists(
    request: ExistsRequest
  ): ZIO[Any, FrameworkException, Boolean] =
    httpService.execute[Json, Boolean](request)

  /*
   * Returns information about whether a particular alias exists.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param name A comma-separated list of alias names to return
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to filter aliases
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def existsAlias(
    name: Chunk[String] = Chunk.empty,
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    local: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, Boolean] = {
    val request = ExistsAliasRequest(
      name = name,
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      local = local
    )

    existsAlias(request)

  }

  def existsAlias(
    request: ExistsAliasRequest
  ): ZIO[Any, FrameworkException, Boolean] =
    httpService.execute[Json, Boolean](request)

  /*
   * Returns information about whether a particular index template exists.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the template
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param flatSettings Return settings in flat format (default: false)
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def existsIndexTemplate(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    flatSettings: Option[Boolean] = None,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, ExistsIndexTemplateResponse] = {
    val request = ExistsIndexTemplateRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      flatSettings = flatSettings,
      local = local,
      masterTimeout = masterTimeout
    )

    existsIndexTemplate(request)

  }

  def existsIndexTemplate(
    request: ExistsIndexTemplateRequest
  ): ZIO[Any, FrameworkException, ExistsIndexTemplateResponse] =
    httpService.execute[Json, ExistsIndexTemplateResponse](request)

  /*
   * Returns information about whether a particular index template exists.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The comma separated names of the index templates
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param flatSettings Return settings in flat format (default: false)
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def existsTemplate(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    flatSettings: Option[Boolean] = None,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, ExistsTemplateResponse] = {
    val request = ExistsTemplateRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      flatSettings = flatSettings,
      local = local,
      masterTimeout = masterTimeout
    )

    existsTemplate(request)

  }

  def existsTemplate(
    request: ExistsTemplateRequest
  ): ZIO[Any, FrameworkException, ExistsTemplateResponse] =
    httpService.execute[Json, ExistsTemplateResponse](request)

  /*
   * Returns the field usage stats for each field of an index
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/field-usage-stats.html
   *
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param index Comma-separated list or wildcard expression of index names used to limit the request.

   * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires,
   * the request fails and returns an error.
   * @server_default 30s

   * @param timeout Period to wait for a response. If no response is received before the timeout expires, the request fails
   * and returns an error.
   * @server_default 30s

   * @param waitForActiveShards The number of shard copies that must be active before proceeding with the operation. Set to all or any
   * positive integer up to the total number of shards in the index (`number_of_replicas+1`).
   * @server_default 1

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param fields A comma-separated list of fields to include in the stats if only a subset of fields should be returned (supports wildcards)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def fieldUsageStats(
    indices: Chunk[String] = Chunk.empty,
    index: Chunk[String],
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: WaitForActiveShards,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    fields: Chunk[String] = Chunk.empty,
    ignoreUnavailable: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, FieldUsageStatsResponse] = {
    val request = FieldUsageStatsRequest(
      indices = indices,
      index = index,
      masterTimeout = masterTimeout,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      fields = fields,
      ignoreUnavailable = ignoreUnavailable
    )

    fieldUsageStats(request)

  }

  def fieldUsageStats(
    request: FieldUsageStatsRequest
  ): ZIO[Any, FrameworkException, FieldUsageStatsResponse] =
    httpService.execute[Json, FieldUsageStatsResponse](request)

  /*
   * Performs the flush operation on one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-flush.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param force Whether a flush should be forced even if it is not necessarily needed ie. if no changes will be committed to the index. This is useful if transaction log IDs should be incremented even if no uncommitted changes are present. (This setting can be considered as internal)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string for all indices
   * @param waitIfOngoing If set to true the flush operation will block until the flush can be executed if another flush operation is already executing. The default is true. If set to false the flush will be skipped iff if another flush operation is already running.
   */
  def flush(
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    force: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    waitIfOngoing: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, FlushResponse] = {
    val request = FlushRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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
    request: FlushRequest
  ): ZIO[Any, FrameworkException, FlushResponse] =
    httpService.execute[Json, FlushResponse](request)

  /*
   * Performs the force merge operation on one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-forcemerge.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param flush Specify whether the index should be flushed after performing the operation (default: true)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param maxNumSegments The number of segments the index should be merged into (default: dynamic)
   * @param onlyExpungeDeletes Specify whether the operation should only expunge deleted documents
   * @param waitForCompletion Should the request wait until the force merge is completed.
   */
  def forcemerge(
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flush: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    maxNumSegments: Option[Double] = None,
    onlyExpungeDeletes: Option[Boolean] = None,
    waitForCompletion: Boolean = true
  ): ZIO[Any, FrameworkException, ForcemergeResponse] = {
    val request = ForcemergeRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      flush = flush,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      maxNumSegments = maxNumSegments,
      onlyExpungeDeletes = onlyExpungeDeletes,
      waitForCompletion = waitForCompletion
    )

    forcemerge(request)

  }

  def forcemerge(
    request: ForcemergeRequest
  ): ZIO[Any, FrameworkException, ForcemergeResponse] =
    httpService.execute[Json, ForcemergeResponse](request)

  /*
   * Returns information about one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-index.html
   *
   * @param indices A comma-separated list of index names
   * @param name

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   * @param features Return only information on specified index features
   * @param flatSettings Return settings in flat format (default: false)
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   * @param includeDefaults Whether to return all default setting for each of the indices.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Specify timeout for connection to master
   */
  def get(
    indices: Chunk[String] = Chunk.empty,
    name: DataStreamNames,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    features: Seq[Feature] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    includeDefaults: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, GetResponse] = {
    val request = GetRequest(
      indices = indices,
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      features = features,
      flatSettings = flatSettings,
      ignoreUnavailable = ignoreUnavailable,
      includeDefaults = includeDefaults,
      local = local,
      masterTimeout = masterTimeout
    )

    get(request)

  }

  def get(request: GetRequest): ZIO[Any, FrameworkException, GetResponse] =
    httpService.execute[Json, GetResponse](request)

  /*
   * Returns an alias.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to filter aliases
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param name A comma-separated list of alias names to return
   */
  def getAlias(
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    local: Option[Boolean] = None,
    name: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, GetAliasResponse] = {
    val request = GetAliasRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      local = local,
      name = name
    )

    getAlias(request)

  }

  def getAlias(
    request: GetAliasRequest
  ): ZIO[Any, FrameworkException, GetAliasResponse] =
    httpService.execute[Json, GetAliasResponse](request)

  /*
   * Returns data streams.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/data-streams.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   * @param name A comma-separated list of data streams to get; use `*` to get all data streams
   */
  def getDataStream(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    name: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, GetDataStreamResponse] = {
    val request = GetDataStreamRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      expandWildcards = expandWildcards,
      name = name
    )

    getDataStream(request)

  }

  def getDataStream(
    request: GetDataStreamRequest
  ): ZIO[Any, FrameworkException, GetDataStreamResponse] =
    httpService.execute[Json, GetDataStreamResponse](request)

  /*
   * Returns mapping for one or more fields.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-field-mapping.html
   *
   * @param fields A comma-separated list of fields
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param includeDefaults Whether the default mapping values should be returned as well
   * @param indices A comma-separated list of index names
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def getFieldMapping(
    fields: Chunk[String] = Chunk.empty,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    includeDefaults: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    local: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, GetFieldMappingResponse] = {
    val request = GetFieldMappingRequest(
      fields = fields,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      includeDefaults = includeDefaults,
      indices = indices,
      local = local
    )

    getFieldMapping(request)

  }

  def getFieldMapping(
    request: GetFieldMappingRequest
  ): ZIO[Any, FrameworkException, GetFieldMappingResponse] =
    httpService.execute[Json, GetFieldMappingResponse](request)

  /*
   * Returns an index template.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param flatSettings Return settings in flat format (default: false)
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param name A pattern that returned template names must match
   */
  def getIndexTemplate(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    flatSettings: Option[Boolean] = None,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Option[String] = None
  ): ZIO[Any, FrameworkException, GetIndexTemplateResponse] = {
    val request = GetIndexTemplateRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      flatSettings = flatSettings,
      local = local,
      masterTimeout = masterTimeout,
      name = name
    )

    getIndexTemplate(request)

  }

  def getIndexTemplate(
    request: GetIndexTemplateRequest
  ): ZIO[Any, FrameworkException, GetIndexTemplateResponse] =
    httpService.execute[Json, GetIndexTemplateResponse](request)

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
    indices: Chunk[String] = Chunk.empty,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, GetMappingResponse] = {
    val request = GetMappingRequest(
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      local = local,
      masterTimeout = masterTimeout
    )

    getMapping(request)

  }

  def getMapping(
    request: GetMappingRequest
  ): ZIO[Any, FrameworkException, GetMappingResponse] =
    httpService.execute[Json, GetMappingResponse](request)

  /*
   * Returns settings for one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-settings.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    includeDefaults: Boolean = false,
    indices: Chunk[String] = Chunk.empty,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Option[String] = None
  ): ZIO[Any, FrameworkException, GetSettingsResponse] = {
    val request = GetSettingsRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

  def getSettings(
    request: GetSettingsRequest
  ): ZIO[Any, FrameworkException, GetSettingsResponse] =
    httpService.execute[Json, GetSettingsResponse](request)

  /*
   * Returns an index template.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param flatSettings Return settings in flat format (default: false)
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param name The comma separated names of the index templates
   */
  def getTemplate(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    flatSettings: Option[Boolean] = None,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Option[String] = None
  ): ZIO[Any, FrameworkException, GetTemplateResponse] = {
    val request = GetTemplateRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      flatSettings = flatSettings,
      local = local,
      masterTimeout = masterTimeout,
      name = name
    )

    getTemplate(request)

  }

  def getTemplate(
    request: GetTemplateRequest
  ): ZIO[Any, FrameworkException, GetTemplateResponse] =
    httpService.execute[Json, GetTemplateResponse](request)

  /*
   * Migrates an alias to a data stream
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/data-streams.html
   *
   * @param name The name of the alias to migrate
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def migrateToDataStream(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, MigrateToDataStreamResponse] = {
    val request = MigrateToDataStreamRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    migrateToDataStream(request)

  }

  def migrateToDataStream(
    request: MigrateToDataStreamRequest
  ): ZIO[Any, FrameworkException, MigrateToDataStreamResponse] =
    httpService.execute[Json, MigrateToDataStreamResponse](request)

  /*
   * Modifies a data stream
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/data-streams.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def modifyDataStream(
    body: ModifyDataStreamRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ModifyDataStreamResponse] = {
    val request = ModifyDataStreamRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    modifyDataStream(request)

  }

  def modifyDataStream(
    request: ModifyDataStreamRequest
  ): ZIO[Any, FrameworkException, ModifyDataStreamResponse] =
    httpService.execute[ModifyDataStreamRequestBody, ModifyDataStreamResponse](
      request
    )

  /*
   * Opens an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
   *
   * @param index A comma separated list of indices to open
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Sets the number of active shards to wait for before the operation returns.
   */
  def open(
    index: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, OpenResponse] = {
    val request = OpenRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    open(request)

  }

  def open(request: OpenRequest): ZIO[Any, FrameworkException, OpenResponse] =
    httpService.execute[Json, OpenResponse](request)

  /*
   * Promotes a data stream from a replicated data stream managed by CCR to a regular data stream
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/data-streams.html
   *
   * @param name The name of the data stream
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def promoteDataStream(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PromoteDataStreamResponse] = {
    val request = PromoteDataStreamRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    promoteDataStream(request)

  }

  def promoteDataStream(
    request: PromoteDataStreamRequest
  ): ZIO[Any, FrameworkException, PromoteDataStreamResponse] =
    httpService.execute[Json, PromoteDataStreamResponse](request)

  /*
   * Creates or updates an alias.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param indices A comma-separated list of index names the alias should point to (supports wildcards); use `_all` to perform the operation on all indices.
   * @param name The name of the alias to be created or updated
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit timestamp for the document
   */
  def putAlias(
    indices: Chunk[String] = Chunk.empty,
    name: String,
    body: PutAliasRequestBody = PutAliasRequestBody(),
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, PutAliasResponse] = {
    val request = PutAliasRequest(
      indices = indices,
      name = name,
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    putAlias(request)

  }

  def putAlias(
    request: PutAliasRequest
  ): ZIO[Any, FrameworkException, PutAliasResponse] =
    httpService.execute[PutAliasRequestBody, PutAliasResponse](request)

  /*
   * Creates or updates an index template.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the template
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param cause User defined reason for creating/updating the index template
   * @param create Whether the index template should only be added if new or can also replace an existing one
   * @param masterTimeout Specify timeout for connection to master
   */
  def putIndexTemplate(
    name: String,
    body: PutIndexTemplateRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    cause: String = "false",
    create: Boolean = false,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, PutIndexTemplateResponse] = {
    val request = PutIndexTemplateRequest(
      name = name,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      cause = cause,
      create = create,
      masterTimeout = masterTimeout
    )

    putIndexTemplate(request)

  }

  def putIndexTemplate(
    request: PutIndexTemplateRequest
  ): ZIO[Any, FrameworkException, PutIndexTemplateResponse] =
    httpService.execute[PutIndexTemplateRequestBody, PutIndexTemplateResponse](
      request
    )

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
   * @param writeIndexOnly When true, applies mappings only to the write index of an alias or data stream
   */
  def putMapping(
    indices: Chunk[String] = Chunk.empty,
    body: Json,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    writeIndexOnly: Boolean = false
  ): ZIO[Any, FrameworkException, PutMappingResponse] = {
    val request = PutMappingRequest(
      indices = indices,
      body = body,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      timeout = timeout,
      writeIndexOnly = writeIndexOnly
    )

    putMapping(request)

  }

  def putMapping(
    request: PutMappingRequest
  ): ZIO[Any, FrameworkException, PutMappingResponse] =
    httpService.execute[Json, PutMappingResponse](request)

  /*
   * Updates the index settings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-update-settings.html
   *
   * @param index

   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    index: Chunk[String],
    body: IndexSettings,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    masterTimeout: Option[String] = None,
    preserveExisting: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, PutSettingsResponse] = {
    val request = PutSettingsRequest(
      index = index,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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
    request: PutSettingsRequest
  ): ZIO[Any, FrameworkException, PutSettingsResponse] =
    httpService.execute[IndexSettings, PutSettingsResponse](request)

  /*
   * Creates or updates an index template.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the template
   * @param flatSettings

   * @param timeout

   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param create Whether the index template should only be added if new or can also replace an existing one
   * @param masterTimeout Specify timeout for connection to master
   * @param order The order for this template when merging multiple matching ones (higher numbers are merged later, overriding the lower numbers)
   */
  def putTemplate(
    name: String,
    flatSettings: Boolean,
    timeout: Option[String] = None,
    body: PutTemplateRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    create: Boolean = false,
    masterTimeout: Option[String] = None,
    order: Option[Double] = None
  ): ZIO[Any, FrameworkException, PutTemplateResponse] = {
    val request = PutTemplateRequest(
      name = name,
      flatSettings = flatSettings,
      timeout = timeout,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      create = create,
      masterTimeout = masterTimeout,
      order = order
    )

    putTemplate(request)

  }

  def putTemplate(
    request: PutTemplateRequest
  ): ZIO[Any, FrameworkException, PutTemplateResponse] =
    httpService.execute[PutTemplateRequestBody, PutTemplateResponse](request)

  /*
   * Returns information about ongoing index shard recoveries.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-recovery.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param activeOnly Display only those recoveries that are currently on-going
   * @param detailed Whether to display detailed information about shard recovery
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   */
  def recovery(
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    activeOnly: Boolean = false,
    detailed: Boolean = false,
    indices: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, RecoveryResponse] = {
    val request = RecoveryRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      activeOnly = activeOnly,
      detailed = detailed,
      indices = indices
    )

    recovery(request)

  }

  def recovery(
    request: RecoveryRequest
  ): ZIO[Any, FrameworkException, RecoveryResponse] =
    httpService.execute[Json, RecoveryResponse](request)

  def refresh(index: String): ZIO[Any, FrameworkException, RefreshResponse] = refresh(index = Chunk(index))

  /*
   * Performs the refresh operation in one or more indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-refresh.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   */
  def refresh(
    index: Chunk[String] = Chunk.empty,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, RefreshResponse] = {
    val request = RefreshRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices
    )

    refresh(request)

  }

  def refresh(
    request: RefreshRequest
  ): ZIO[Any, FrameworkException, RefreshResponse] =
    httpService.execute[Json, RefreshResponse](request)

  /*
   * Reloads an index's search analyzers and their resources.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-reload-analyzers.html
   *
   * @param indices A comma-separated list of index names to reload analyzers for
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   */
  def reloadSearchAnalyzers(
    indices: Chunk[String] = Chunk.empty,
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, ReloadSearchAnalyzersResponse] = {
    val request = ReloadSearchAnalyzersRequest(
      indices = indices,
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable
    )

    reloadSearchAnalyzers(request)

  }

  def reloadSearchAnalyzers(
    request: ReloadSearchAnalyzersRequest
  ): ZIO[Any, FrameworkException, ReloadSearchAnalyzersResponse] =
    httpService.execute[Json, ReloadSearchAnalyzersResponse](request)

  /*
   * Returns information about any matching indices, aliases, and data streams
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-resolve-index-api.html
   *
   * @param name A comma-separated list of names or wildcard expressions
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
   */
  def resolveIndex(
    name: Chunk[String] = Chunk.empty,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    expandWildcards: Seq[ExpandWildcards] = Nil
  ): ZIO[Any, FrameworkException, ResolveIndexResponse] = {
    val request = ResolveIndexRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      expandWildcards = expandWildcards
    )

    resolveIndex(request)

  }

  def resolveIndex(
    request: ResolveIndexRequest
  ): ZIO[Any, FrameworkException, ResolveIndexResponse] =
    httpService.execute[Json, ResolveIndexResponse](request)

  /*
   * Updates an alias to point to a new index when the existing index
is considered to be too large or too old.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
   *
   * @param alias The name of the alias to rollover
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param dryRun If set to true the rollover action will only be validated but not actually performed even if a condition matches. The default is false
   * @param masterTimeout Specify timeout for connection to master
   * @param newIndex The name of the rollover index
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for on the newly created rollover index before the operation returns.
   */
  def rollover(
    alias: String,
    body: RolloverRequestBody = RolloverRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    dryRun: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    newIndex: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, RolloverResponse] = {
    val request = RolloverRequest(
      alias = alias,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      dryRun = dryRun,
      masterTimeout = masterTimeout,
      newIndex = newIndex,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    rollover(request)

  }

  def rollover(
    request: RolloverRequest
  ): ZIO[Any, FrameworkException, RolloverResponse] =
    httpService.execute[RolloverRequestBody, RolloverResponse](request)

  /*
   * Provides low-level information about segments in a Lucene index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-segments.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param verbose Includes detailed memory usage by Lucene.
   */
  def segments(
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    verbose: Boolean = false
  ): ZIO[Any, FrameworkException, SegmentsResponse] = {
    val request = SegmentsRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      verbose = verbose
    )

    segments(request)

  }

  def segments(
    request: SegmentsRequest
  ): ZIO[Any, FrameworkException, SegmentsResponse] =
    httpService.execute[Json, SegmentsResponse](request)

  /*
   * Provides store information for shard copies of indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shards-stores.html
   *
   * @param index List of data streams, indices, and aliases used to limit the request.

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param status A comma-separated list of statuses used to filter on shards to get store information for
   */
  def shardStores(
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    status: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, ShardStoresResponse] = {
    val request = ShardStoresRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      status = status
    )

    shardStores(request)

  }

  def shardStores(
    request: ShardStoresRequest
  ): ZIO[Any, FrameworkException, ShardStoresResponse] =
    httpService.execute[Json, ShardStoresResponse](request)

  /*
   * Allow to shrink an existing index into a new index with fewer primary shards.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shrink-index.html
   *
   * @param index The name of the source index to shrink
   * @param target The name of the target index to shrink into
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
   */
  def shrink(
    body: ShrinkRequestBody,
    index: String,
    target: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, ShrinkResponse] = {
    val request = ShrinkRequest(
      index = index,
      target = target,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      masterTimeout = masterTimeout,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    shrink(request)

  }

  def shrink(
    request: ShrinkRequest
  ): ZIO[Any, FrameworkException, ShrinkResponse] =
    httpService.execute[ShrinkRequestBody, ShrinkResponse](request)

  /*
   * Simulate matching the given index name against the index templates in the system
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param name The name of the index (it must be a concrete index name)
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param cause User defined reason for dry-run creating the new template for simulation purposes
   * @param create Whether the index template we optionally defined in the body should only be dry-run added if new or can also replace an existing one
   * @param masterTimeout Specify timeout for connection to master
   */
  def simulateIndexTemplate(
    name: String,
    body: SimulateIndexTemplateRequestBody = SimulateIndexTemplateRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    cause: String = "false",
    create: Boolean = false,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, SimulateIndexTemplateResponse] = {
    val request = SimulateIndexTemplateRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      cause = cause,
      create = create,
      masterTimeout = masterTimeout
    )

    simulateIndexTemplate(request)

  }

  def simulateIndexTemplate(
    request: SimulateIndexTemplateRequest
  ): ZIO[Any, FrameworkException, SimulateIndexTemplateResponse] =
    httpService.execute[SimulateIndexTemplateRequestBody, SimulateIndexTemplateResponse](
      request
    )

  /*
   * Simulate resolving the given template name or body
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param cause User defined reason for dry-run creating the new template for simulation purposes
   * @param create Whether the index template we optionally defined in the body should only be dry-run added if new or can also replace an existing one
   * @param masterTimeout Specify timeout for connection to master
   * @param name The name of the index template
   */
  def simulateTemplate(
    name: String,
    body: IndexTemplate,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    cause: String = "false",
    create: Boolean = false,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, SimulateTemplateResponse] = {
    val request = SimulateTemplateRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      cause = cause,
      create = create,
      masterTimeout = masterTimeout,
      name = name
    )

    simulateTemplate(request)

  }

  def simulateTemplate(
    request: SimulateTemplateRequest
  ): ZIO[Any, FrameworkException, SimulateTemplateResponse] =
    httpService.execute[IndexTemplate, SimulateTemplateResponse](
      request
    )

  /*
   * Allows you to split an existing index into a new index with more primary shards.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-split-index.html
   *
   * @param index The name of the source index to split
   * @param target The name of the target index to split into
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
   */
  def split(
    index: String,
    target: String,
    body: SplitRequestBody = SplitRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, SplitResponse] = {
    val request = SplitRequest(
      index = index,
      target = target,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      masterTimeout = masterTimeout,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    split(request)

  }

  def split(
    request: SplitRequest
  ): ZIO[Any, FrameworkException, SplitResponse] =
    httpService.execute[SplitRequestBody, SplitResponse](request)

  /*
   * Provides statistics on operations happening in an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-stats.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param completionFields A comma-separated list of fields for the `completion` index metric (supports wildcards)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param fielddataFields A comma-separated list of fields for the `fielddata` index metric (supports wildcards)
   * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
   * @param forbidClosedIndices If set to false stats will also collected from closed indices if explicitly specified or if expand_wildcards expands to closed indices
   * @param groups A comma-separated list of search groups for `search` index metric
   * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
   * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param level Return stats aggregated at cluster, index or shard level
   * @param metric Limit the information returned the specific metrics.
   */
  def stats(
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    completionFields: Chunk[String] = Chunk.empty,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    fielddataFields: Chunk[String] = Chunk.empty,
    fields: Chunk[String] = Chunk.empty,
    forbidClosedIndices: Boolean = true,
    groups: Chunk[String] = Chunk.empty,
    includeSegmentFileSizes: Boolean = false,
    includeUnloadedSegments: Boolean = false,
    indices: Chunk[String] = Chunk.empty,
    level: Level = Level.indices,
    metric: Option[String] = None
  ): ZIO[Any, FrameworkException, StatsResponse] = {
    val request = StatsRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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
      metric = metric
    )

    stats(request)

  }

  def stats(
    request: StatsRequest
  ): ZIO[Any, FrameworkException, StatsResponse] =
    httpService.execute[Json, StatsResponse](request)

  /*
   * Updates index aliases.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Request timeout
   */
  def updateAliases(
    body: UpdateAliasesRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, UpdateAliasesResponse] = {
    val request = UpdateAliasesRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    updateAliases(request)

  }

  def updateAliases(
    request: UpdateAliasesRequest
  ): ZIO[Any, FrameworkException, UpdateAliasesResponse] =
    httpService.execute[UpdateAliasesRequestBody, UpdateAliasesResponse](request)

  /*
   * Allows a user to validate a potentially expensive query without executing it.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-validate.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    index: Chunk[String],
    body: ValidateQueryRequestBody = ValidateQueryRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allShards: Option[Boolean] = None,
    allowNoIndices: Option[Boolean] = None,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    explain: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    lenient: Option[Boolean] = None,
    q: Option[String] = None,
    rewrite: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, ValidateQueryResponse] = {
    val request = ValidateQueryRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

  def validateQuery(
    request: ValidateQueryRequest
  ): ZIO[Any, FrameworkException, ValidateQueryResponse] =
    httpService.execute[ValidateQueryRequestBody, ValidateQueryResponse](request)

}
