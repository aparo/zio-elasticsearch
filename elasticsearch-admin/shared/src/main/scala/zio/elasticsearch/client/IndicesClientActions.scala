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

package zio.elasticsearch.client

import zio.elasticsearch.ZioResponse
import zio.elasticsearch.requests.indices._
import zio.elasticsearch.responses.indices._

trait IndicesClientActions {
  def execute(request: IndicesAnalyzeRequest): ZIO[Any, FrameworkException, IndicesAnalyzeResponse]
  def execute(request: IndicesClearCacheRequest): ZIO[Any, FrameworkException, IndicesClearCacheResponse]
  def execute(request: IndicesCloneRequest): ZIO[Any, FrameworkException, IndicesCloneResponse]
  def execute(request: IndicesCloseRequest): ZIO[Any, FrameworkException, IndicesCloseResponse]
  def execute(request: IndicesCreateRequest): ZIO[Any, FrameworkException, IndicesCreateResponse]
  def execute(request: IndicesDeleteRequest): ZIO[Any, FrameworkException, IndicesDeleteResponse]
  def execute(request: IndicesDeleteAliasRequest): ZIO[Any, FrameworkException, IndicesDeleteAliasResponse]
  def execute(request: IndicesDeleteTemplateRequest): ZIO[Any, FrameworkException, IndicesDeleteTemplateResponse]
  def execute(request: IndicesExistsRequest): ZIO[Any, FrameworkException, IndicesExistsResponse]
  def execute(request: IndicesExistsAliasRequest): ZIO[Any, FrameworkException, IndicesExistsAliasResponse]
  def execute(request: IndicesExistsTemplateRequest): ZIO[Any, FrameworkException, IndicesExistsTemplateResponse]
  def execute(request: IndicesExistsTypeRequest): ZIO[Any, FrameworkException, IndicesExistsTypeResponse]
  def execute(request: IndicesFlushRequest): ZIO[Any, FrameworkException, IndicesFlushResponse]
  def execute(request: IndicesFlushSyncedRequest): ZIO[Any, FrameworkException, IndicesFlushSyncedResponse]
  def execute(request: IndicesForcemergeRequest): ZIO[Any, FrameworkException, IndicesForcemergeResponse]
  def execute(request: IndicesGetRequest): ZIO[Any, FrameworkException, IndicesGetResponse]
  def execute(request: IndicesGetAliasRequest): ZIO[Any, FrameworkException, IndicesGetAliasResponse]
  def execute(request: IndicesGetFieldMappingRequest): ZIO[Any, FrameworkException, IndicesGetFieldMappingResponse]
  def execute(request: IndicesGetMappingRequest): ZIO[Any, FrameworkException, IndicesGetMappingResponse]
  def execute(request: IndicesGetSettingsRequest): ZIO[Any, FrameworkException, IndicesGetSettingsResponse]
  def execute(request: IndicesGetTemplateRequest): ZIO[Any, FrameworkException, IndicesGetTemplateResponse]
  def execute(request: IndicesGetUpgradeRequest): ZIO[Any, FrameworkException, IndicesGetUpgradeResponse]
  def execute(request: IndicesOpenRequest): ZIO[Any, FrameworkException, IndicesOpenResponse]
  def execute(request: IndicesPutAliasRequest): ZIO[Any, FrameworkException, IndicesPutAliasResponse]
  def execute(request: IndicesPutMappingRequest): ZIO[Any, FrameworkException, IndicesPutMappingResponse]
  def execute(request: IndicesPutSettingsRequest): ZIO[Any, FrameworkException, IndicesPutSettingsResponse]
  def execute(request: IndicesPutTemplateRequest): ZIO[Any, FrameworkException, IndicesPutTemplateResponse]
  def execute(request: IndicesRecoveryRequest): ZIO[Any, FrameworkException, IndicesRecoveryResponse]
  def execute(request: IndicesRefreshRequest): ZIO[Any, FrameworkException, IndicesRefreshResponse]
  def execute(request: IndicesRolloverRequest): ZIO[Any, FrameworkException, IndicesRolloverResponse]
  def execute(request: IndicesSegmentsRequest): ZIO[Any, FrameworkException, IndicesSegmentsResponse]
  def execute(request: IndicesShardStoresRequest): ZIO[Any, FrameworkException, IndicesShardStoresResponse]
  def execute(request: IndicesShrinkRequest): ZIO[Any, FrameworkException, IndicesShrinkResponse]
  def execute(request: IndicesSplitRequest): ZIO[Any, FrameworkException, IndicesSplitResponse]
  def execute(request: IndicesStatsRequest): ZIO[Any, FrameworkException, IndicesStatsResponse]
  def execute(request: IndicesUpdateAliasesRequest): ZIO[Any, FrameworkException, IndicesUpdateAliasesResponse]
  def execute(request: IndicesUpgradeRequest): ZIO[Any, FrameworkException, IndicesUpgradeResponse]
  def execute(request: IndicesValidateQueryRequest): ZIO[Any, FrameworkException, IndicesValidateQueryResponse]
}
