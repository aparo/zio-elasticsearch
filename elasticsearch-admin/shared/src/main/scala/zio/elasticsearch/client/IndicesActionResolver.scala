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

trait IndicesActionResolver extends IndicesClientActions with ClientActionResolver {

  def execute(
    request: IndicesAnalyzeRequest
  ): ZIO[Any, FrameworkException, IndicesAnalyzeResponse] =
    doCall(request).flatMap(convertResponse[IndicesAnalyzeResponse](request))

  def execute(
    request: IndicesClearCacheRequest
  ): ZIO[Any, FrameworkException, IndicesClearCacheResponse] =
    doCall(request).flatMap(convertResponse[IndicesClearCacheResponse](request))

  def execute(
    request: IndicesCloneRequest
  ): ZIO[Any, FrameworkException, IndicesCloneResponse] =
    doCall(request).flatMap(convertResponse[IndicesCloneResponse](request))

  def execute(
    request: IndicesCloseRequest
  ): ZIO[Any, FrameworkException, IndicesCloseResponse] =
    doCall(request).flatMap(convertResponse[IndicesCloseResponse](request))

  def execute(
    request: IndicesCreateRequest
  ): ZIO[Any, FrameworkException, IndicesCreateResponse] =
    doCall(request).flatMap(convertResponse[IndicesCreateResponse](request))

  def execute(
    request: IndicesDeleteRequest
  ): ZIO[Any, FrameworkException, IndicesDeleteResponse] =
    doCall(request).flatMap(convertResponse[IndicesDeleteResponse](request))

  def execute(
    request: IndicesDeleteAliasRequest
  ): ZIO[Any, FrameworkException, IndicesDeleteAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesDeleteAliasResponse](request))

  def execute(
    request: IndicesDeleteTemplateRequest
  ): ZIO[Any, FrameworkException, IndicesDeleteTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesDeleteTemplateResponse](request))

  def execute(
    request: IndicesExistsRequest
  ): ZIO[Any, FrameworkException, IndicesExistsResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsResponse](request))

  def execute(
    request: IndicesExistsAliasRequest
  ): ZIO[Any, FrameworkException, IndicesExistsAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsAliasResponse](request))

  def execute(
    request: IndicesExistsTemplateRequest
  ): ZIO[Any, FrameworkException, IndicesExistsTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsTemplateResponse](request))

  def execute(
    request: IndicesExistsTypeRequest
  ): ZIO[Any, FrameworkException, IndicesExistsTypeResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsTypeResponse](request))

  def execute(
    request: IndicesFlushRequest
  ): ZIO[Any, FrameworkException, IndicesFlushResponse] =
    doCall(request).flatMap(convertResponse[IndicesFlushResponse](request))

  def execute(
    request: IndicesFlushSyncedRequest
  ): ZIO[Any, FrameworkException, IndicesFlushSyncedResponse] =
    doCall(request).flatMap(convertResponse[IndicesFlushSyncedResponse](request))

  def execute(
    request: IndicesForcemergeRequest
  ): ZIO[Any, FrameworkException, IndicesForcemergeResponse] =
    doCall(request).flatMap(convertResponse[IndicesForcemergeResponse](request))

  def execute(
    request: IndicesGetRequest
  ): ZIO[Any, FrameworkException, IndicesGetResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetResponse](request))

  def execute(
    request: IndicesGetAliasRequest
  ): ZIO[Any, FrameworkException, IndicesGetAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetAliasResponse](request))

  def execute(
    request: IndicesGetFieldMappingRequest
  ): ZIO[Any, FrameworkException, IndicesGetFieldMappingResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetFieldMappingResponse](request))

  def execute(
    request: IndicesGetMappingRequest
  ): ZIO[Any, FrameworkException, IndicesGetMappingResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetMappingResponse](request))

  def execute(
    request: IndicesGetSettingsRequest
  ): ZIO[Any, FrameworkException, IndicesGetSettingsResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetSettingsResponse](request))

  def execute(
    request: IndicesGetTemplateRequest
  ): ZIO[Any, FrameworkException, IndicesGetTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetTemplateResponse](request))

  def execute(
    request: IndicesGetUpgradeRequest
  ): ZIO[Any, FrameworkException, IndicesGetUpgradeResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetUpgradeResponse](request))

  def execute(
    request: IndicesOpenRequest
  ): ZIO[Any, FrameworkException, IndicesOpenResponse] =
    doCall(request).flatMap(convertResponse[IndicesOpenResponse](request))

  def execute(
    request: IndicesPutAliasRequest
  ): ZIO[Any, FrameworkException, IndicesPutAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutAliasResponse](request))

  def execute(
    request: IndicesPutMappingRequest
  ): ZIO[Any, FrameworkException, IndicesPutMappingResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutMappingResponse](request))

  def execute(
    request: IndicesPutSettingsRequest
  ): ZIO[Any, FrameworkException, IndicesPutSettingsResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutSettingsResponse](request))

  def execute(
    request: IndicesPutTemplateRequest
  ): ZIO[Any, FrameworkException, IndicesPutTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutTemplateResponse](request))

  def execute(
    request: IndicesRecoveryRequest
  ): ZIO[Any, FrameworkException, IndicesRecoveryResponse] =
    doCall(request).flatMap(convertResponse[IndicesRecoveryResponse](request))

  def execute(
    request: IndicesRefreshRequest
  ): ZIO[Any, FrameworkException, IndicesRefreshResponse] =
    doCall(request).flatMap(convertResponse[IndicesRefreshResponse](request))

  def execute(
    request: IndicesRolloverRequest
  ): ZIO[Any, FrameworkException, IndicesRolloverResponse] =
    doCall(request).flatMap(convertResponse[IndicesRolloverResponse](request))

  def execute(
    request: IndicesSegmentsRequest
  ): ZIO[Any, FrameworkException, IndicesSegmentsResponse] =
    doCall(request).flatMap(convertResponse[IndicesSegmentsResponse](request))

  def execute(
    request: IndicesShardStoresRequest
  ): ZIO[Any, FrameworkException, IndicesShardStoresResponse] =
    doCall(request).flatMap(convertResponse[IndicesShardStoresResponse](request))

  def execute(
    request: IndicesShrinkRequest
  ): ZIO[Any, FrameworkException, IndicesShrinkResponse] =
    doCall(request).flatMap(convertResponse[IndicesShrinkResponse](request))

  def execute(
    request: IndicesSplitRequest
  ): ZIO[Any, FrameworkException, IndicesSplitResponse] =
    doCall(request).flatMap(convertResponse[IndicesSplitResponse](request))

  def execute(
    request: IndicesStatsRequest
  ): ZIO[Any, FrameworkException, IndicesStatsResponse] =
    doCall(request).flatMap(convertResponse[IndicesStatsResponse](request))

  def execute(
    request: IndicesUpdateAliasesRequest
  ): ZIO[Any, FrameworkException, IndicesUpdateAliasesResponse] =
    doCall(request).flatMap(convertResponse[IndicesUpdateAliasesResponse](request))

  def execute(
    request: IndicesUpgradeRequest
  ): ZIO[Any, FrameworkException, IndicesUpgradeResponse] =
    doCall(request).flatMap(convertResponse[IndicesUpgradeResponse](request))

  def execute(
    request: IndicesValidateQueryRequest
  ): ZIO[Any, FrameworkException, IndicesValidateQueryResponse] =
    doCall(request).flatMap(convertResponse[IndicesValidateQueryResponse](request))

}
