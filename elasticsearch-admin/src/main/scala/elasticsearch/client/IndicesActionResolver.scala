package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.indices._
import elasticsearch.responses.indices._

trait IndicesActionResolver extends IndicesClientActions with ClientActionResolver {

  def execute(
               request: IndicesAnalyzeRequest
             ): ZioResponse[IndicesAnalyzeResponse] =
    doCall(request).flatMap(convertResponse[IndicesAnalyzeResponse](request))

  def execute(
               request: IndicesClearCacheRequest
             ): ZioResponse[IndicesClearCacheResponse] =
    doCall(request).flatMap(convertResponse[IndicesClearCacheResponse](request))

  def execute(
               request: IndicesCloneRequest
             ): ZioResponse[IndicesCloneResponse] =
    doCall(request).flatMap(convertResponse[IndicesCloneResponse](request))

  def execute(
               request: IndicesCloseRequest
             ): ZioResponse[IndicesCloseResponse] =
    doCall(request).flatMap(convertResponse[IndicesCloseResponse](request))

  def execute(
               request: IndicesCreateRequest
             ): ZioResponse[IndicesCreateResponse] =
    doCall(request).flatMap(convertResponse[IndicesCreateResponse](request))

  def execute(
               request: IndicesDeleteRequest
             ): ZioResponse[IndicesDeleteResponse] =
    doCall(request).flatMap(convertResponse[IndicesDeleteResponse](request))

  def execute(
               request: IndicesDeleteAliasRequest
             ): ZioResponse[IndicesDeleteAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesDeleteAliasResponse](request))

  def execute(
               request: IndicesDeleteTemplateRequest
             ): ZioResponse[IndicesDeleteTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesDeleteTemplateResponse](request))

  def execute(
               request: IndicesExistsRequest
             ): ZioResponse[IndicesExistsResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsResponse](request))

  def execute(
               request: IndicesExistsAliasRequest
             ): ZioResponse[IndicesExistsAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsAliasResponse](request))

  def execute(
               request: IndicesExistsTemplateRequest
             ): ZioResponse[IndicesExistsTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsTemplateResponse](request))

  def execute(
               request: IndicesExistsTypeRequest
             ): ZioResponse[IndicesExistsTypeResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsTypeResponse](request))

  def execute(
               request: IndicesFlushRequest
             ): ZioResponse[IndicesFlushResponse] =
    doCall(request).flatMap(convertResponse[IndicesFlushResponse](request))

  def execute(
               request: IndicesFlushSyncedRequest
             ): ZioResponse[IndicesFlushSyncedResponse] =
    doCall(request).flatMap(convertResponse[IndicesFlushSyncedResponse](request))

  def execute(
               request: IndicesForcemergeRequest
             ): ZioResponse[IndicesForcemergeResponse] =
    doCall(request).flatMap(convertResponse[IndicesForcemergeResponse](request))

  def execute(
               request: IndicesGetRequest
             ): ZioResponse[IndicesGetResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetResponse](request))

  def execute(
               request: IndicesGetAliasRequest
             ): ZioResponse[IndicesGetAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetAliasResponse](request))

  def execute(
               request: IndicesGetFieldMappingRequest
             ): ZioResponse[IndicesGetFieldMappingResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetFieldMappingResponse](request))

  def execute(
               request: IndicesGetMappingRequest
             ): ZioResponse[IndicesGetMappingResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetMappingResponse](request))

  def execute(
               request: IndicesGetSettingsRequest
             ): ZioResponse[IndicesGetSettingsResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetSettingsResponse](request))

  def execute(
               request: IndicesGetTemplateRequest
             ): ZioResponse[IndicesGetTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetTemplateResponse](request))

  def execute(
               request: IndicesGetUpgradeRequest
             ): ZioResponse[IndicesGetUpgradeResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetUpgradeResponse](request))

  def execute(
               request: IndicesOpenRequest
             ): ZioResponse[IndicesOpenResponse] =
    doCall(request).flatMap(convertResponse[IndicesOpenResponse](request))

  def execute(
               request: IndicesPutAliasRequest
             ): ZioResponse[IndicesPutAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutAliasResponse](request))

  def execute(
               request: IndicesPutMappingRequest
             ): ZioResponse[IndicesPutMappingResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutMappingResponse](request))

  def execute(
               request: IndicesPutSettingsRequest
             ): ZioResponse[IndicesPutSettingsResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutSettingsResponse](request))

  def execute(
               request: IndicesPutTemplateRequest
             ): ZioResponse[IndicesPutTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutTemplateResponse](request))

  def execute(
               request: IndicesRecoveryRequest
             ): ZioResponse[IndicesRecoveryResponse] =
    doCall(request).flatMap(convertResponse[IndicesRecoveryResponse](request))

  def execute(
               request: IndicesRefreshRequest
             ): ZioResponse[IndicesRefreshResponse] =
    doCall(request).flatMap(convertResponse[IndicesRefreshResponse](request))

  def execute(
               request: IndicesRolloverRequest
             ): ZioResponse[IndicesRolloverResponse] =
    doCall(request).flatMap(convertResponse[IndicesRolloverResponse](request))

  def execute(
               request: IndicesSegmentsRequest
             ): ZioResponse[IndicesSegmentsResponse] =
    doCall(request).flatMap(convertResponse[IndicesSegmentsResponse](request))

  def execute(
               request: IndicesShardStoresRequest
             ): ZioResponse[IndicesShardStoresResponse] =
    doCall(request).flatMap(convertResponse[IndicesShardStoresResponse](request))

  def execute(
               request: IndicesShrinkRequest
             ): ZioResponse[IndicesShrinkResponse] =
    doCall(request).flatMap(convertResponse[IndicesShrinkResponse](request))

  def execute(
               request: IndicesSplitRequest
             ): ZioResponse[IndicesSplitResponse] =
    doCall(request).flatMap(convertResponse[IndicesSplitResponse](request))

  def execute(
               request: IndicesStatsRequest
             ): ZioResponse[IndicesStatsResponse] =
    doCall(request).flatMap(convertResponse[IndicesStatsResponse](request))

  def execute(
               request: IndicesUpdateAliasesRequest
             ): ZioResponse[IndicesUpdateAliasesResponse] =
    doCall(request).flatMap(convertResponse[IndicesUpdateAliasesResponse](request))

  def execute(
               request: IndicesUpgradeRequest
             ): ZioResponse[IndicesUpgradeResponse] =
    doCall(request).flatMap(convertResponse[IndicesUpgradeResponse](request))

  def execute(
               request: IndicesValidateQueryRequest
             ): ZioResponse[IndicesValidateQueryResponse] =
    doCall(request).flatMap(convertResponse[IndicesValidateQueryResponse](request))

}
