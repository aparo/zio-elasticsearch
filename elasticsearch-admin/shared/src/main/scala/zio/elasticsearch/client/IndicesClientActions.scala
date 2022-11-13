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

package zio.elasticsearch.client

import zio.elasticsearch.ZioResponse
import zio.elasticsearch.requests.indices._
import zio.elasticsearch.responses.indices._

trait IndicesClientActions {
  def execute(request: IndicesAnalyzeRequest): ZioResponse[IndicesAnalyzeResponse]
  def execute(request: IndicesClearCacheRequest): ZioResponse[IndicesClearCacheResponse]
  def execute(request: IndicesCloneRequest): ZioResponse[IndicesCloneResponse]
  def execute(request: IndicesCloseRequest): ZioResponse[IndicesCloseResponse]
  def execute(request: IndicesCreateRequest): ZioResponse[IndicesCreateResponse]
  def execute(request: IndicesDeleteRequest): ZioResponse[IndicesDeleteResponse]
  def execute(request: IndicesDeleteAliasRequest): ZioResponse[IndicesDeleteAliasResponse]
  def execute(request: IndicesDeleteTemplateRequest): ZioResponse[IndicesDeleteTemplateResponse]
  def execute(request: IndicesExistsRequest): ZioResponse[IndicesExistsResponse]
  def execute(request: IndicesExistsAliasRequest): ZioResponse[IndicesExistsAliasResponse]
  def execute(request: IndicesExistsTemplateRequest): ZioResponse[IndicesExistsTemplateResponse]
  def execute(request: IndicesExistsTypeRequest): ZioResponse[IndicesExistsTypeResponse]
  def execute(request: IndicesFlushRequest): ZioResponse[IndicesFlushResponse]
  def execute(request: IndicesFlushSyncedRequest): ZioResponse[IndicesFlushSyncedResponse]
  def execute(request: IndicesForcemergeRequest): ZioResponse[IndicesForcemergeResponse]
  def execute(request: IndicesGetRequest): ZioResponse[IndicesGetResponse]
  def execute(request: IndicesGetAliasRequest): ZioResponse[IndicesGetAliasResponse]
  def execute(request: IndicesGetFieldMappingRequest): ZioResponse[IndicesGetFieldMappingResponse]
  def execute(request: IndicesGetMappingRequest): ZioResponse[IndicesGetMappingResponse]
  def execute(request: IndicesGetSettingsRequest): ZioResponse[IndicesGetSettingsResponse]
  def execute(request: IndicesGetTemplateRequest): ZioResponse[IndicesGetTemplateResponse]
  def execute(request: IndicesGetUpgradeRequest): ZioResponse[IndicesGetUpgradeResponse]
  def execute(request: IndicesOpenRequest): ZioResponse[IndicesOpenResponse]
  def execute(request: IndicesPutAliasRequest): ZioResponse[IndicesPutAliasResponse]
  def execute(request: IndicesPutMappingRequest): ZioResponse[IndicesPutMappingResponse]
  def execute(request: IndicesPutSettingsRequest): ZioResponse[IndicesPutSettingsResponse]
  def execute(request: IndicesPutTemplateRequest): ZioResponse[IndicesPutTemplateResponse]
  def execute(request: IndicesRecoveryRequest): ZioResponse[IndicesRecoveryResponse]
  def execute(request: IndicesRefreshRequest): ZioResponse[IndicesRefreshResponse]
  def execute(request: IndicesRolloverRequest): ZioResponse[IndicesRolloverResponse]
  def execute(request: IndicesSegmentsRequest): ZioResponse[IndicesSegmentsResponse]
  def execute(request: IndicesShardStoresRequest): ZioResponse[IndicesShardStoresResponse]
  def execute(request: IndicesShrinkRequest): ZioResponse[IndicesShrinkResponse]
  def execute(request: IndicesSplitRequest): ZioResponse[IndicesSplitResponse]
  def execute(request: IndicesStatsRequest): ZioResponse[IndicesStatsResponse]
  def execute(request: IndicesUpdateAliasesRequest): ZioResponse[IndicesUpdateAliasesResponse]
  def execute(request: IndicesUpgradeRequest): ZioResponse[IndicesUpgradeResponse]
  def execute(request: IndicesValidateQueryRequest): ZioResponse[IndicesValidateQueryResponse]
}
