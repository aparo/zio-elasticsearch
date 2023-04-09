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

package zio.elasticsearch.security

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._
import zio.elasticsearch.security.activate_user_profile.ActivateUserProfileRequest
import zio.elasticsearch.security.activate_user_profile.ActivateUserProfileResponse
import zio.elasticsearch.security.authenticate.AuthenticateRequest
import zio.elasticsearch.security.authenticate.AuthenticateResponse
import zio.elasticsearch.security.bulk_update_api_keys.BulkUpdateApiKeysRequest
import zio.elasticsearch.security.bulk_update_api_keys.BulkUpdateApiKeysResponse
import zio.elasticsearch.security.change_password.ChangePasswordRequest
import zio.elasticsearch.security.change_password.ChangePasswordResponse
import zio.elasticsearch.security.clear_api_key_cache.ClearApiKeyCacheRequest
import zio.elasticsearch.security.clear_api_key_cache.ClearApiKeyCacheResponse
import zio.elasticsearch.security.clear_cached_privileges.ClearCachedPrivilegesRequest
import zio.elasticsearch.security.clear_cached_privileges.ClearCachedPrivilegesResponse
import zio.elasticsearch.security.clear_cached_realms.ClearCachedRealmsRequest
import zio.elasticsearch.security.clear_cached_realms.ClearCachedRealmsResponse
import zio.elasticsearch.security.clear_cached_roles.ClearCachedRolesRequest
import zio.elasticsearch.security.clear_cached_roles.ClearCachedRolesResponse
import zio.elasticsearch.security.clear_cached_service_tokens.ClearCachedServiceTokensRequest
import zio.elasticsearch.security.clear_cached_service_tokens.ClearCachedServiceTokensResponse
import zio.elasticsearch.security.create_api_key.CreateApiKeyRequest
import zio.elasticsearch.security.create_api_key.CreateApiKeyResponse
import zio.elasticsearch.security.create_service_token.CreateServiceTokenRequest
import zio.elasticsearch.security.create_service_token.CreateServiceTokenResponse
import zio.elasticsearch.security.delete_privileges.DeletePrivilegesRequest
import zio.elasticsearch.security.delete_privileges.DeletePrivilegesResponse
import zio.elasticsearch.security.delete_role.DeleteRoleRequest
import zio.elasticsearch.security.delete_role.DeleteRoleResponse
import zio.elasticsearch.security.delete_role_mapping.DeleteRoleMappingRequest
import zio.elasticsearch.security.delete_role_mapping.DeleteRoleMappingResponse
import zio.elasticsearch.security.delete_service_token.DeleteServiceTokenRequest
import zio.elasticsearch.security.delete_service_token.DeleteServiceTokenResponse
import zio.elasticsearch.security.delete_user.DeleteUserRequest
import zio.elasticsearch.security.delete_user.DeleteUserResponse
import zio.elasticsearch.security.disable_user.DisableUserRequest
import zio.elasticsearch.security.disable_user.DisableUserResponse
import zio.elasticsearch.security.disable_user_profile.DisableUserProfileRequest
import zio.elasticsearch.security.disable_user_profile.DisableUserProfileResponse
import zio.elasticsearch.security.enable_user.EnableUserRequest
import zio.elasticsearch.security.enable_user.EnableUserResponse
import zio.elasticsearch.security.enable_user_profile.EnableUserProfileRequest
import zio.elasticsearch.security.enable_user_profile.EnableUserProfileResponse
import zio.elasticsearch.security.enroll_kibana.EnrollKibanaRequest
import zio.elasticsearch.security.enroll_kibana.EnrollKibanaResponse
import zio.elasticsearch.security.enroll_node.EnrollNodeRequest
import zio.elasticsearch.security.enroll_node.EnrollNodeResponse
import zio.elasticsearch.security.get_api_key.GetApiKeyRequest
import zio.elasticsearch.security.get_api_key.GetApiKeyResponse
import zio.elasticsearch.security.get_builtin_privileges.GetBuiltinPrivilegesRequest
import zio.elasticsearch.security.get_builtin_privileges.GetBuiltinPrivilegesResponse
import zio.elasticsearch.security.get_privileges.GetPrivilegesRequest
import zio.elasticsearch.security.get_privileges.GetPrivilegesResponse
import zio.elasticsearch.security.get_role.GetRoleRequest
import zio.elasticsearch.security.get_role.GetRoleResponse
import zio.elasticsearch.security.get_role_mapping.GetRoleMappingRequest
import zio.elasticsearch.security.get_role_mapping.GetRoleMappingResponse
import zio.elasticsearch.security.get_service_accounts.GetServiceAccountsRequest
import zio.elasticsearch.security.get_service_accounts.GetServiceAccountsResponse
import zio.elasticsearch.security.get_service_credentials.GetServiceCredentialsRequest
import zio.elasticsearch.security.get_service_credentials.GetServiceCredentialsResponse
import zio.elasticsearch.security.get_token.GetTokenRequest
import zio.elasticsearch.security.get_token.GetTokenResponse
import zio.elasticsearch.security.get_user.GetUserRequest
import zio.elasticsearch.security.get_user.GetUserResponse
import zio.elasticsearch.security.get_user_privileges.GetUserPrivilegesRequest
import zio.elasticsearch.security.get_user_privileges.GetUserPrivilegesResponse
import zio.elasticsearch.security.get_user_profile.GetUserProfileRequest
import zio.elasticsearch.security.get_user_profile.GetUserProfileResponse
import zio.elasticsearch.security.grant_api_key.GrantApiKeyRequest
import zio.elasticsearch.security.grant_api_key.GrantApiKeyResponse
import zio.elasticsearch.security.has_privileges.HasPrivilegesRequest
import zio.elasticsearch.security.has_privileges.HasPrivilegesResponse
import zio.elasticsearch.security.has_privileges_user_profile.HasPrivilegesUserProfileRequest
import zio.elasticsearch.security.has_privileges_user_profile.HasPrivilegesUserProfileResponse
import zio.elasticsearch.security.invalidate_api_key.InvalidateApiKeyRequest
import zio.elasticsearch.security.invalidate_api_key.InvalidateApiKeyResponse
import zio.elasticsearch.security.invalidate_token.InvalidateTokenRequest
import zio.elasticsearch.security.invalidate_token.InvalidateTokenResponse
import zio.elasticsearch.security.oidc_authenticate.OidcAuthenticateRequest
import zio.elasticsearch.security.oidc_authenticate.OidcAuthenticateResponse
import zio.elasticsearch.security.oidc_logout.OidcLogoutRequest
import zio.elasticsearch.security.oidc_logout.OidcLogoutResponse
import zio.elasticsearch.security.oidc_prepare_authentication.OidcPrepareAuthenticationRequest
import zio.elasticsearch.security.oidc_prepare_authentication.OidcPrepareAuthenticationResponse
import zio.elasticsearch.security.put_privileges.PutPrivilegesRequest
import zio.elasticsearch.security.put_privileges.PutPrivilegesResponse
import zio.elasticsearch.security.put_role.PutRoleRequest
import zio.elasticsearch.security.put_role.PutRoleResponse
import zio.elasticsearch.security.put_role_mapping.PutRoleMappingRequest
import zio.elasticsearch.security.put_role_mapping.PutRoleMappingResponse
import zio.elasticsearch.security.put_user.PutUserRequest
import zio.elasticsearch.security.put_user.PutUserResponse
import zio.elasticsearch.security.query_api_keys.QueryApiKeysRequest
import zio.elasticsearch.security.query_api_keys.QueryApiKeysResponse
import zio.elasticsearch.security.requests._
import zio.elasticsearch.security.saml_authenticate.SamlAuthenticateRequest
import zio.elasticsearch.security.saml_authenticate.SamlAuthenticateResponse
import zio.elasticsearch.security.saml_complete_logout.SamlCompleteLogoutRequest
import zio.elasticsearch.security.saml_complete_logout.SamlCompleteLogoutResponse
import zio.elasticsearch.security.saml_invalidate.SamlInvalidateRequest
import zio.elasticsearch.security.saml_invalidate.SamlInvalidateResponse
import zio.elasticsearch.security.saml_logout.SamlLogoutRequest
import zio.elasticsearch.security.saml_logout.SamlLogoutResponse
import zio.elasticsearch.security.saml_prepare_authentication.SamlPrepareAuthenticationRequest
import zio.elasticsearch.security.saml_prepare_authentication.SamlPrepareAuthenticationResponse
import zio.elasticsearch.security.saml_service_provider_metadata.SamlServiceProviderMetadataRequest
import zio.elasticsearch.security.saml_service_provider_metadata.SamlServiceProviderMetadataResponse
import zio.elasticsearch.security.suggest_user_profiles.SuggestUserProfilesRequest
import zio.elasticsearch.security.suggest_user_profiles.SuggestUserProfilesResponse
import zio.elasticsearch.security.update_api_key.UpdateApiKeyRequest
import zio.elasticsearch.security.update_api_key.UpdateApiKeyResponse
import zio.elasticsearch.security.update_user_profile_data.UpdateUserProfileDataRequest
import zio.elasticsearch.security.update_user_profile_data.UpdateUserProfileDataResponse

object SecurityManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, SecurityManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new SecurityManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait SecurityManager {
  def httpService: ElasticSearchHttpService

  /*
   * Creates or updates the user profile on behalf of another user.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-activate-user-profile.html
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
  def activateUserProfile(
    body: ActivateUserProfileRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ActivateUserProfileResponse] = {
    val request = ActivateUserProfileRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    activateUserProfile(request)

  }

  def activateUserProfile(
    request: ActivateUserProfileRequest
  ): ZIO[Any, FrameworkException, ActivateUserProfileResponse] =
    httpService.execute[ActivateUserProfileRequestBody, ActivateUserProfileResponse](
      request
    )

  /*
   * Enables authentication as a user and retrieve information about the authenticated user.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-authenticate.html
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

   */
  def authenticate(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, AuthenticateResponse] = {
    val request = AuthenticateRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    authenticate(request)

  }

  def authenticate(
    request: AuthenticateRequest
  ): ZIO[Any, FrameworkException, AuthenticateResponse] =
    httpService.execute[Json, AuthenticateResponse](request)

  /*
   * Updates the attributes of multiple existing API keys.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-bulk-update-api-keys.html
   *
   * @param body body the body of the call
   */
  def bulkUpdateApiKeys(
    body: Json
  ): ZIO[Any, FrameworkException, BulkUpdateApiKeysResponse] = {
    val request = BulkUpdateApiKeysRequest(body = body)

    bulkUpdateApiKeys(request)

  }

  def bulkUpdateApiKeys(
    request: BulkUpdateApiKeysRequest
  ): ZIO[Any, FrameworkException, BulkUpdateApiKeysResponse] =
    httpService.execute[Json, BulkUpdateApiKeysResponse](request)

  /*
   * Changes the passwords of users in the native realm and built-in users.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-change-password.html
   *
   * @param username The username of the user to change the password for
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def changePassword(
    username: String,
    body: ChangePasswordRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, ChangePasswordResponse] = {
    val request = ChangePasswordRequest(
      username = username,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    changePassword(request)

  }

  def changePassword(
    request: ChangePasswordRequest
  ): ZIO[Any, FrameworkException, ChangePasswordResponse] =
    httpService.execute[ChangePasswordRequestBody, ChangePasswordResponse](request)

  /*
   * Clear a subset or all entries from the API key cache.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-clear-api-key-cache.html
   *
   * @param ids A comma-separated list of IDs of API keys to clear from the cache
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
  def clearApiKeyCache(
    ids: Seq[String] = Nil,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ClearApiKeyCacheResponse] = {
    val request = ClearApiKeyCacheRequest(
      ids = ids,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    clearApiKeyCache(request)

  }

  def clearApiKeyCache(
    request: ClearApiKeyCacheRequest
  ): ZIO[Any, FrameworkException, ClearApiKeyCacheResponse] =
    httpService.execute[Json, ClearApiKeyCacheResponse](request)

  /*
   * Evicts application privileges from the native application privileges cache.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-clear-privilege-cache.html
   *
   * @param application A comma-separated list of application names
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
  def clearCachedPrivileges(
    application: Seq[String] = Nil,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ClearCachedPrivilegesResponse] = {
    val request = ClearCachedPrivilegesRequest(
      application = application,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    clearCachedPrivileges(request)

  }

  def clearCachedPrivileges(
    request: ClearCachedPrivilegesRequest
  ): ZIO[Any, FrameworkException, ClearCachedPrivilegesResponse] =
    httpService.execute[Json, ClearCachedPrivilegesResponse](request)

  /*
   * Evicts users from the user cache. Can completely clear the cache or evict specific users.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-clear-cache.html
   *
   * @param realms Comma-separated list of realms to clear
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

   * @param usernames Comma-separated list of usernames to clear from the cache
   */
  def clearCachedRealms(
    realms: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    usernames: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, ClearCachedRealmsResponse] = {
    val request = ClearCachedRealmsRequest(
      realms = realms,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      usernames = usernames
    )

    clearCachedRealms(request)

  }

  def clearCachedRealms(
    request: ClearCachedRealmsRequest
  ): ZIO[Any, FrameworkException, ClearCachedRealmsResponse] =
    httpService.execute[Json, ClearCachedRealmsResponse](request)

  /*
   * Evicts roles from the native role cache.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-clear-role-cache.html
   *
   * @param name Role name
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
  def clearCachedRoles(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ClearCachedRolesResponse] = {
    val request = ClearCachedRolesRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    clearCachedRoles(request)

  }

  def clearCachedRoles(
    request: ClearCachedRolesRequest
  ): ZIO[Any, FrameworkException, ClearCachedRolesResponse] =
    httpService.execute[Json, ClearCachedRolesResponse](request)

  /*
   * Evicts tokens from the service account token caches.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-clear-service-token-caches.html
   *
   * @param namespace An identifier for the namespace
   * @param service An identifier for the service name
   * @param name A comma-separated list of service token names
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
  def clearCachedServiceTokens(
    namespace: String,
    service: String,
    name: Seq[String] = Nil,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ClearCachedServiceTokensResponse] = {
    val request = ClearCachedServiceTokensRequest(
      namespace = namespace,
      service = service,
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    clearCachedServiceTokens(request)

  }

  def clearCachedServiceTokens(
    request: ClearCachedServiceTokensRequest
  ): ZIO[Any, FrameworkException, ClearCachedServiceTokensResponse] =
    httpService.execute[Json, ClearCachedServiceTokensResponse](request)

  /*
   * Creates an API key for access without requiring basic authentication.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-create-api-key.html
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def createApiKey(
    body: CreateApiKeyRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, CreateApiKeyResponse] = {
    val request = CreateApiKeyRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    createApiKey(request)

  }

  def createApiKey(
    request: CreateApiKeyRequest
  ): ZIO[Any, FrameworkException, CreateApiKeyResponse] =
    httpService.execute[CreateApiKeyRequestBody, CreateApiKeyResponse](request)

  /*
   * Creates a service account token for access without requiring basic authentication.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-create-service-token.html
   *
   * @param namespace An identifier for the namespace
   * @param service An identifier for the service name
   * @param name An identifier for the token name
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

   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` (the default) then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def createServiceToken(
    namespace: String,
    service: String,
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, CreateServiceTokenResponse] = {
    val request = CreateServiceTokenRequest(
      namespace = namespace,
      service = service,
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    createServiceToken(request)

  }

  def createServiceToken(
    request: CreateServiceTokenRequest
  ): ZIO[Any, FrameworkException, CreateServiceTokenResponse] =
    httpService.execute[Json, CreateServiceTokenResponse](request)

  /*
   * Removes application privileges.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-delete-privilege.html
   *
   * @param application Application name
   * @param name Privilege name
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def deletePrivileges(
    application: String,
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, DeletePrivilegesResponse] = {
    val request = DeletePrivilegesRequest(
      application = application,
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    deletePrivileges(request)

  }

  def deletePrivileges(
    request: DeletePrivilegesRequest
  ): ZIO[Any, FrameworkException, DeletePrivilegesResponse] =
    httpService.execute[Json, DeletePrivilegesResponse](request)

  /*
   * Removes roles in the native realm.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-delete-role.html
   *
   * @param name Role name
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def deleteRole(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, DeleteRoleResponse] = {
    val request = DeleteRoleRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    deleteRole(request)

  }

  def deleteRole(
    request: DeleteRoleRequest
  ): ZIO[Any, FrameworkException, DeleteRoleResponse] =
    httpService.execute[Json, DeleteRoleResponse](request)

  /*
   * Removes role mappings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-delete-role-mapping.html
   *
   * @param name Role-mapping name
   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def deleteRoleMapping(
    name: String,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, DeleteRoleMappingResponse] = {
    val request = DeleteRoleMappingRequest(name = name, refresh = refresh)

    deleteRoleMapping(request)

  }

  def deleteRoleMapping(
    request: DeleteRoleMappingRequest
  ): ZIO[Any, FrameworkException, DeleteRoleMappingResponse] =
    httpService.execute[Json, DeleteRoleMappingResponse](request)

  /*
   * Deletes a service account token.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-delete-service-token.html
   *
   * @param namespace An identifier for the namespace
   * @param service An identifier for the service name
   * @param name An identifier for the token name
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

   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` (the default) then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def deleteServiceToken(
    namespace: String,
    service: String,
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, DeleteServiceTokenResponse] = {
    val request = DeleteServiceTokenRequest(
      namespace = namespace,
      service = service,
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    deleteServiceToken(request)

  }

  def deleteServiceToken(
    request: DeleteServiceTokenRequest
  ): ZIO[Any, FrameworkException, DeleteServiceTokenResponse] =
    httpService.execute[Json, DeleteServiceTokenResponse](request)

  /*
   * Deletes users from the native realm.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-delete-user.html
   *
   * @param username username
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def deleteUser(
    username: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, DeleteUserResponse] = {
    val request = DeleteUserRequest(
      username = username,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    deleteUser(request)

  }

  def deleteUser(
    request: DeleteUserRequest
  ): ZIO[Any, FrameworkException, DeleteUserResponse] =
    httpService.execute[Json, DeleteUserResponse](request)

  /*
   * Disables users in the native realm.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-disable-user.html
   *
   * @param username The username of the user to disable
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def disableUser(
    username: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, DisableUserResponse] = {
    val request = DisableUserRequest(
      username = username,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    disableUser(request)

  }

  def disableUser(
    request: DisableUserRequest
  ): ZIO[Any, FrameworkException, DisableUserResponse] =
    httpService.execute[Json, DisableUserResponse](request)

  /*
   * Disables a user profile so it's not visible in user profile searches.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/security-api-disable-user-profile.html
   *
   * @param uid Unique identifier for the user profile
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

   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` (the default) then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def disableUserProfile(
    uid: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, DisableUserProfileResponse] = {
    val request = DisableUserProfileRequest(
      uid = uid,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    disableUserProfile(request)

  }

  def disableUserProfile(
    request: DisableUserProfileRequest
  ): ZIO[Any, FrameworkException, DisableUserProfileResponse] =
    httpService.execute[Json, DisableUserProfileResponse](request)

  /*
   * Enables users in the native realm.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-enable-user.html
   *
   * @param username The username of the user to enable
   * @param uid Unique identifier for the user profile.

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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def enableUser(
    username: String,
    uid: UserProfileId,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, EnableUserResponse] = {
    val request = EnableUserRequest(
      username = username,
      uid = uid,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    enableUser(request)

  }

  def enableUser(
    request: EnableUserRequest
  ): ZIO[Any, FrameworkException, EnableUserResponse] =
    httpService.execute[Json, EnableUserResponse](request)

  /*
   * Enables a user profile so it's visible in user profile searches.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/security-api-enable-user-profile.html
   *
   * @param uid An unique identifier of the user profile
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

   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` (the default) then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def enableUserProfile(
    uid: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, EnableUserProfileResponse] = {
    val request = EnableUserProfileRequest(
      uid = uid,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    enableUserProfile(request)

  }

  def enableUserProfile(
    request: EnableUserProfileRequest
  ): ZIO[Any, FrameworkException, EnableUserProfileResponse] =
    httpService.execute[Json, EnableUserProfileResponse](request)

  /*
   * Allows a kibana instance to configure itself to communicate with a secured elasticsearch cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/security-api-kibana-enrollment.html
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

   */
  def enrollKibana(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, EnrollKibanaResponse] = {
    val request = EnrollKibanaRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    enrollKibana(request)

  }

  def enrollKibana(
    request: EnrollKibanaRequest
  ): ZIO[Any, FrameworkException, EnrollKibanaResponse] =
    httpService.execute[Json, EnrollKibanaResponse](request)

  /*
   * Allows a new node to enroll to an existing cluster with security enabled.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/security-api-node-enrollment.html
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

   */
  def enrollNode(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, EnrollNodeResponse] = {
    val request = EnrollNodeRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    enrollNode(request)

  }

  def enrollNode(
    request: EnrollNodeRequest
  ): ZIO[Any, FrameworkException, EnrollNodeResponse] =
    httpService.execute[Json, EnrollNodeResponse](request)

  /*
   * Retrieves information for one or more API keys.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-api-key.html
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

   * @param id API key id of the API key to be retrieved
   * @param name API key name of the API key to be retrieved
   * @param owner flag to query API keys owned by the currently authenticated user
   * @param realmName realm name of the user who created this API key to be retrieved
   * @param username user name of the user who created this API key to be retrieved
   * @param withLimitedBy flag to show the limited-by role descriptors of API Keys
   */
  def getApiKey(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    id: Option[String] = None,
    name: Option[String] = None,
    owner: Boolean = false,
    realmName: Option[String] = None,
    username: Option[String] = None,
    withLimitedBy: Boolean = false
  ): ZIO[Any, FrameworkException, GetApiKeyResponse] = {
    val request = GetApiKeyRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      id = id,
      name = name,
      owner = owner,
      realmName = realmName,
      username = username,
      withLimitedBy = withLimitedBy
    )

    getApiKey(request)

  }

  def getApiKey(
    request: GetApiKeyRequest
  ): ZIO[Any, FrameworkException, GetApiKeyResponse] =
    httpService.execute[Json, GetApiKeyResponse](request)

  /*
   * Retrieves the list of cluster privileges and index privileges that are available in this version of Elasticsearch.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-builtin-privileges.html
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

   */
  def getBuiltinPrivileges(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetBuiltinPrivilegesResponse] = {
    val request = GetBuiltinPrivilegesRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getBuiltinPrivileges(request)

  }

  def getBuiltinPrivileges(
    request: GetBuiltinPrivilegesRequest
  ): ZIO[Any, FrameworkException, GetBuiltinPrivilegesResponse] =
    httpService.execute[Json, GetBuiltinPrivilegesResponse](request)

  /*
   * Retrieves application privileges.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-privileges.html
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

   * @param application Application name
   * @param name Privilege name
   */
  def getPrivileges(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    application: Option[String] = None,
    name: Option[String] = None
  ): ZIO[Any, FrameworkException, GetPrivilegesResponse] = {
    val request = GetPrivilegesRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      application = application,
      name = name
    )

    getPrivileges(request)

  }

  def getPrivileges(
    request: GetPrivilegesRequest
  ): ZIO[Any, FrameworkException, GetPrivilegesResponse] =
    httpService.execute[Json, GetPrivilegesResponse](request)

  /*
   * Retrieves roles in the native realm.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-role.html
   *
   * @param name A comma-separated list of role names
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
  def getRole(
    name: Seq[String] = Nil,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetRoleResponse] = {
    val request = GetRoleRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getRole(request)

  }

  def getRole(
    request: GetRoleRequest
  ): ZIO[Any, FrameworkException, GetRoleResponse] =
    httpService.execute[Json, GetRoleResponse](request)

  /*
   * Retrieves role mappings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-role-mapping.html
   *
   * @param name A comma-separated list of role-mapping names
   */
  def getRoleMapping(
    name: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, GetRoleMappingResponse] = {
    val request = GetRoleMappingRequest(name = name)

    getRoleMapping(request)

  }

  def getRoleMapping(
    request: GetRoleMappingRequest
  ): ZIO[Any, FrameworkException, GetRoleMappingResponse] =
    httpService.execute[Json, GetRoleMappingResponse](request)

  /*
   * Retrieves information about service accounts.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-service-accounts.html
   *
   * @param namespace An identifier for the namespace
   * @param service An identifier for the service name
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
  def getServiceAccounts(
    namespace: String,
    service: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetServiceAccountsResponse] = {
    val request = GetServiceAccountsRequest(
      namespace = namespace,
      service = service,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getServiceAccounts(request)

  }

  def getServiceAccounts(
    request: GetServiceAccountsRequest
  ): ZIO[Any, FrameworkException, GetServiceAccountsResponse] =
    httpService.execute[Json, GetServiceAccountsResponse](request)

  /*
   * Retrieves information of all service credentials for a service account.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-service-credentials.html
   *
   * @param namespace An identifier for the namespace
   * @param service An identifier for the service name
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
  def getServiceCredentials(
    namespace: String,
    service: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetServiceCredentialsResponse] = {
    val request = GetServiceCredentialsRequest(
      namespace = namespace,
      service = service,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getServiceCredentials(request)

  }

  def getServiceCredentials(
    request: GetServiceCredentialsRequest
  ): ZIO[Any, FrameworkException, GetServiceCredentialsResponse] =
    httpService.execute[Json, GetServiceCredentialsResponse](request)

  /*
   * Creates a bearer token for access without requiring basic authentication.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-token.html
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
  def getToken(
    body: GetTokenRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetTokenResponse] = {
    val request = GetTokenRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getToken(request)

  }

  def getToken(
    request: GetTokenRequest
  ): ZIO[Any, FrameworkException, GetTokenResponse] =
    httpService.execute[GetTokenRequestBody, GetTokenResponse](request)

  /*
   * Retrieves information about users in the native realm and built-in users.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-user.html
   *
   * @param username A comma-separated list of usernames
   * @param uid A unique identifier for the user profile.

   * @param data List of filters for the `data` field of the profile document.
   * To return all content use `data=*`. To return a subset of content
   * use `data=<key>` to retrieve content nested under the specified `<key>`.
   * By default returns no `data` content.

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

   * @param withProfileUid flag to retrieve profile uid (if exists) associated to the user
   */
  def getUser(
    username: Seq[String] = Nil,
    uid: Chunk[UserProfileId],
    data: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    withProfileUid: Boolean = false
  ): ZIO[Any, FrameworkException, GetUserResponse] = {
    val request = GetUserRequest(
      username = username,
      uid = uid,
      data = data,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      withProfileUid = withProfileUid
    )

    getUser(request)

  }

  def getUser(
    request: GetUserRequest
  ): ZIO[Any, FrameworkException, GetUserResponse] =
    httpService.execute[Json, GetUserResponse](request)

  /*
   * Retrieves security privileges for the logged in user.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-user-privileges.html
   *
   * @param application The name of the application. Application privileges are always associated with exactly one application. If you do not specify this parameter, the API returns information about all privileges for all applications.

   * @param priviledge The name of the privilege. If you do not specify this parameter, the API returns information about all privileges for the requested application.

   * @param username

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
  def getUserPrivileges(
    application: String,
    priviledge: String,
    username: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetUserPrivilegesResponse] = {
    val request = GetUserPrivilegesRequest(
      application = application,
      priviledge = priviledge,
      username = username,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getUserPrivileges(request)

  }

  def getUserPrivileges(
    request: GetUserPrivilegesRequest
  ): ZIO[Any, FrameworkException, GetUserPrivilegesResponse] =
    httpService.execute[Json, GetUserPrivilegesResponse](request)

  /*
   * Retrieves user profiles for the given unique ID(s).
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-user-profile.html
   *
   * @param uid A comma-separated list of unique identifier for user profiles
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

   * @param data A comma-separated list of keys for which the corresponding application data are retrieved.
   */
  def getUserProfile(
    uid: Seq[String] = Nil,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    data: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, GetUserProfileResponse] = {
    val request = GetUserProfileRequest(
      uid = uid,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      data = data
    )

    getUserProfile(request)

  }

  def getUserProfile(
    request: GetUserProfileRequest
  ): ZIO[Any, FrameworkException, GetUserProfileResponse] =
    httpService.execute[Json, GetUserProfileResponse](request)

  /*
   * Creates an API key on behalf of another user.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-grant-api-key.html
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def grantApiKey(
    body: GrantApiKeyRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, GrantApiKeyResponse] = {
    val request = GrantApiKeyRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    grantApiKey(request)

  }

  def grantApiKey(
    request: GrantApiKeyRequest
  ): ZIO[Any, FrameworkException, GrantApiKeyResponse] =
    httpService.execute[GrantApiKeyRequestBody, GrantApiKeyResponse](request)

  /*
   * Determines whether the specified user has a specified list of privileges.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-has-privileges.html
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

   * @param user Username
   */
  def hasPrivileges(
    body: HasPrivilegesRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    user: Option[String] = None
  ): ZIO[Any, FrameworkException, HasPrivilegesResponse] = {
    val request = HasPrivilegesRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      user = user
    )

    hasPrivileges(request)

  }

  def hasPrivileges(
    request: HasPrivilegesRequest
  ): ZIO[Any, FrameworkException, HasPrivilegesResponse] =
    httpService.execute[HasPrivilegesRequestBody, HasPrivilegesResponse](request)

  /*
   * Determines whether the users associated with the specified profile IDs have all the requested privileges.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-has-privileges-user-profile.html
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
  def hasPrivilegesUserProfile(
    body: HasPrivilegesUserProfileRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, HasPrivilegesUserProfileResponse] = {
    val request = HasPrivilegesUserProfileRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    hasPrivilegesUserProfile(request)

  }

  def hasPrivilegesUserProfile(
    request: HasPrivilegesUserProfileRequest
  ): ZIO[Any, FrameworkException, HasPrivilegesUserProfileResponse] =
    httpService.execute[
      HasPrivilegesUserProfileRequestBody,
      HasPrivilegesUserProfileResponse
    ](request)

  /*
   * Invalidates one or more API keys.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-invalidate-api-key.html
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
  def invalidateApiKey(
    body: InvalidateApiKeyRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, InvalidateApiKeyResponse] = {
    val request = InvalidateApiKeyRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    invalidateApiKey(request)

  }

  def invalidateApiKey(
    request: InvalidateApiKeyRequest
  ): ZIO[Any, FrameworkException, InvalidateApiKeyResponse] =
    httpService.execute[InvalidateApiKeyRequestBody, InvalidateApiKeyResponse](
      request
    )

  /*
   * Invalidates one or more access tokens or refresh tokens.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-invalidate-token.html
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
  def invalidateToken(
    body: InvalidateTokenRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, InvalidateTokenResponse] = {
    val request = InvalidateTokenRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    invalidateToken(request)

  }

  def invalidateToken(
    request: InvalidateTokenRequest
  ): ZIO[Any, FrameworkException, InvalidateTokenResponse] =
    httpService.execute[InvalidateTokenRequestBody, InvalidateTokenResponse](request)

  /*
   * Exchanges an OpenID Connection authentication response message for an Elasticsearch access token and refresh token pair
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-oidc-authenticate.html
   *
   * @param body body the body of the call
   */
  def oidcAuthenticate(
    body: Json
  ): ZIO[Any, FrameworkException, OidcAuthenticateResponse] = {
    val request = OidcAuthenticateRequest(body = body)

    oidcAuthenticate(request)

  }

  def oidcAuthenticate(
    request: OidcAuthenticateRequest
  ): ZIO[Any, FrameworkException, OidcAuthenticateResponse] =
    httpService.execute[Json, OidcAuthenticateResponse](request)

  /*
   * Invalidates a refresh token and access token that was generated from the OpenID Connect Authenticate API
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-oidc-logout.html
   *
   * @param body body the body of the call
   */
  def oidcLogout(
    body: Json
  ): ZIO[Any, FrameworkException, OidcLogoutResponse] = {
    val request = OidcLogoutRequest(body = body)

    oidcLogout(request)

  }

  def oidcLogout(
    request: OidcLogoutRequest
  ): ZIO[Any, FrameworkException, OidcLogoutResponse] =
    httpService.execute[Json, OidcLogoutResponse](request)

  /*
   * Creates an OAuth 2.0 authentication request as a URL string
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-oidc-prepare-authentication.html
   *
   * @param body body the body of the call
   */
  def oidcPrepareAuthentication(
    body: Json
  ): ZIO[Any, FrameworkException, OidcPrepareAuthenticationResponse] = {
    val request = OidcPrepareAuthenticationRequest(body = body)

    oidcPrepareAuthentication(request)

  }

  def oidcPrepareAuthentication(
    request: OidcPrepareAuthenticationRequest
  ): ZIO[Any, FrameworkException, OidcPrepareAuthenticationResponse] =
    httpService.execute[Json, OidcPrepareAuthenticationResponse](request)

  /*
   * Adds or updates application privileges.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-put-privileges.html
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def putPrivileges(
    body: Json,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, PutPrivilegesResponse] = {
    val request = PutPrivilegesRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    putPrivileges(request)

  }

  def putPrivileges(
    request: PutPrivilegesRequest
  ): ZIO[Any, FrameworkException, PutPrivilegesResponse] =
    httpService.execute[Json, PutPrivilegesResponse](request)

  /*
   * Adds and updates roles in the native realm.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-put-role.html
   *
   * @param name Role name
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def putRole(
    name: String,
    body: PutRoleRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, PutRoleResponse] = {
    val request = PutRoleRequest(
      name = name,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    putRole(request)

  }

  def putRole(
    request: PutRoleRequest
  ): ZIO[Any, FrameworkException, PutRoleResponse] =
    httpService.execute[PutRoleRequestBody, PutRoleResponse](request)

  /*
   * Creates and updates role mappings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-put-role-mapping.html
   *
   * @param name Role-mapping name
   * @param body body the body of the call
   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def putRoleMapping(
    name: String,
    body: Json,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, PutRoleMappingResponse] = {
    val request =
      PutRoleMappingRequest(name = name, body = body, refresh = refresh)

    putRoleMapping(request)

  }

  def putRoleMapping(
    request: PutRoleMappingRequest
  ): ZIO[Any, FrameworkException, PutRoleMappingResponse] =
    httpService.execute[Json, PutRoleMappingResponse](request)

  /*
   * Adds and updates users in the native realm. These users are commonly referred to as native users.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-put-user.html
   *
   * @param username The username of the User
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

   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def putUser(
    username: String,
    body: PutUserRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, PutUserResponse] = {
    val request = PutUserRequest(
      username = username,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      refresh = refresh
    )

    putUser(request)

  }

  def putUser(
    request: PutUserRequest
  ): ZIO[Any, FrameworkException, PutUserResponse] =
    httpService.execute[PutUserRequestBody, PutUserResponse](request)

  /*
   * Retrieves information for API keys using a subset of query DSL
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-query-api-key.html
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
   * @param withLimitedBy flag to show the limited-by role descriptors of API Keys
   */
  def queryApiKeys(
    body: QueryApiKeysRequestBody = QueryApiKeysRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    withLimitedBy: Boolean = false
  ): ZIO[Any, FrameworkException, QueryApiKeysResponse] = {
    val request = QueryApiKeysRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      withLimitedBy = withLimitedBy
    )

    queryApiKeys(request)

  }

  def queryApiKeys(
    request: QueryApiKeysRequest
  ): ZIO[Any, FrameworkException, QueryApiKeysResponse] =
    httpService.execute[QueryApiKeysRequestBody, QueryApiKeysResponse](request)

  /*
   * Exchanges a SAML Response message for an Elasticsearch access token and refresh token pair
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-saml-authenticate.html
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
  def samlAuthenticate(
    body: SamlAuthenticateRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, SamlAuthenticateResponse] = {
    val request = SamlAuthenticateRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    samlAuthenticate(request)

  }

  def samlAuthenticate(
    request: SamlAuthenticateRequest
  ): ZIO[Any, FrameworkException, SamlAuthenticateResponse] =
    httpService.execute[SamlAuthenticateRequestBody, SamlAuthenticateResponse](
      request
    )

  /*
   * Verifies the logout response sent from the SAML IdP
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-saml-complete-logout.html
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
  def samlCompleteLogout(
    body: SamlCompleteLogoutRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, SamlCompleteLogoutResponse] = {
    val request = SamlCompleteLogoutRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    samlCompleteLogout(request)

  }

  def samlCompleteLogout(
    request: SamlCompleteLogoutRequest
  ): ZIO[Any, FrameworkException, SamlCompleteLogoutResponse] =
    httpService.execute[SamlCompleteLogoutRequestBody, SamlCompleteLogoutResponse](
      request
    )

  /*
   * Consumes a SAML LogoutRequest
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-saml-invalidate.html
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
  def samlInvalidate(
    body: SamlInvalidateRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, SamlInvalidateResponse] = {
    val request = SamlInvalidateRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    samlInvalidate(request)

  }

  def samlInvalidate(
    request: SamlInvalidateRequest
  ): ZIO[Any, FrameworkException, SamlInvalidateResponse] =
    httpService.execute[SamlInvalidateRequestBody, SamlInvalidateResponse](request)

  /*
   * Invalidates an access token and a refresh token that were generated via the SAML Authenticate API
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-saml-logout.html
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
  def samlLogout(
    body: SamlLogoutRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, SamlLogoutResponse] = {
    val request = SamlLogoutRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    samlLogout(request)

  }

  def samlLogout(
    request: SamlLogoutRequest
  ): ZIO[Any, FrameworkException, SamlLogoutResponse] =
    httpService.execute[SamlLogoutRequestBody, SamlLogoutResponse](request)

  /*
   * Creates a SAML authentication request
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-saml-prepare-authentication.html
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
  def samlPrepareAuthentication(
    body: SamlPrepareAuthenticationRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, SamlPrepareAuthenticationResponse] = {
    val request = SamlPrepareAuthenticationRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    samlPrepareAuthentication(request)

  }

  def samlPrepareAuthentication(
    request: SamlPrepareAuthenticationRequest
  ): ZIO[Any, FrameworkException, SamlPrepareAuthenticationResponse] =
    httpService.execute[
      SamlPrepareAuthenticationRequestBody,
      SamlPrepareAuthenticationResponse
    ](request)

  /*
   * Generates SAML metadata for the Elastic stack SAML 2.0 Service Provider
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-saml-sp-metadata.html
   *
   * @param realmName The name of the SAML realm to get the metadata for
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
  def samlServiceProviderMetadata(
    realmName: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, SamlServiceProviderMetadataResponse] = {
    val request = SamlServiceProviderMetadataRequest(
      realmName = realmName,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    samlServiceProviderMetadata(request)

  }

  def samlServiceProviderMetadata(
    request: SamlServiceProviderMetadataRequest
  ): ZIO[Any, FrameworkException, SamlServiceProviderMetadataResponse] =
    httpService.execute[Json, SamlServiceProviderMetadataResponse](request)

  /*
   * Get suggestions for user profiles that match specified search criteria.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/security-api-suggest-user-profile.html
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
   * @param data A comma-separated list of keys for which the corresponding application data are retrieved.
   */
  def suggestUserProfiles(
    body: SuggestUserProfilesRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    data: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, SuggestUserProfilesResponse] = {
    val request = SuggestUserProfilesRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      data = data
    )

    suggestUserProfiles(request)

  }

  def suggestUserProfiles(
    request: SuggestUserProfilesRequest
  ): ZIO[Any, FrameworkException, SuggestUserProfilesResponse] =
    httpService.execute[SuggestUserProfilesRequestBody, SuggestUserProfilesResponse](
      request
    )

  /*
   * Updates attributes of an existing API key.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-update-api-key.html
   *
   * @param id The ID of the API key to update
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
   */
  def updateApiKey(
    id: String,
    body: UpdateApiKeyRequestBody = UpdateApiKeyRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, UpdateApiKeyResponse] = {
    val request = UpdateApiKeyRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    updateApiKey(request)

  }

  def updateApiKey(
    request: UpdateApiKeyRequest
  ): ZIO[Any, FrameworkException, UpdateApiKeyResponse] =
    httpService.execute[UpdateApiKeyRequestBody, UpdateApiKeyResponse](request)

  /*
   * Update application specific data for the user profile of the given unique ID.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-update-user-profile-data.html
   *
   * @param uid An unique identifier of the user profile
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

   * @param ifPrimaryTerm only perform the update operation if the last operation that has changed the document has the specified primary term
   * @param ifSeqNo only perform the update operation if the last operation that has changed the document has the specified sequence number
   * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
   */
  def updateUserProfileData(
    uid: String,
    body: UpdateUserProfileDataRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    refresh: Option[Refresh] = None
  ): ZIO[Any, FrameworkException, UpdateUserProfileDataResponse] = {
    val request = UpdateUserProfileDataRequest(
      uid = uid,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      ifPrimaryTerm = ifPrimaryTerm,
      ifSeqNo = ifSeqNo,
      refresh = refresh
    )

    updateUserProfileData(request)

  }

  def updateUserProfileData(
    request: UpdateUserProfileDataRequest
  ): ZIO[Any, FrameworkException, UpdateUserProfileDataResponse] =
    httpService.execute[UpdateUserProfileDataRequestBody, UpdateUserProfileDataResponse](
      request
    )

}
