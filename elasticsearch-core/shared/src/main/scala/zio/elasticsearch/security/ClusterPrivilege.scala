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

import zio.json._

sealed trait ClusterPrivilege

object ClusterPrivilege {

  case object all extends ClusterPrivilege

  case object cancel_task extends ClusterPrivilege

  case object create_snapshot extends ClusterPrivilege

  case object grant_api_key extends ClusterPrivilege

  case object manage extends ClusterPrivilege

  case object manage_api_key extends ClusterPrivilege

  case object manage_ccr extends ClusterPrivilege

  case object manage_enrich extends ClusterPrivilege

  case object manage_ilm extends ClusterPrivilege

  case object manage_index_templates extends ClusterPrivilege

  case object manage_ingest_pipelines extends ClusterPrivilege

  case object manage_logstash_pipelines extends ClusterPrivilege

  case object manage_ml extends ClusterPrivilege

  case object manage_oidc extends ClusterPrivilege

  case object manage_own_api_key extends ClusterPrivilege

  case object manage_pipeline extends ClusterPrivilege

  case object manage_rollup extends ClusterPrivilege

  case object manage_saml extends ClusterPrivilege

  case object manage_security extends ClusterPrivilege

  case object manage_service_account extends ClusterPrivilege

  case object manage_slm extends ClusterPrivilege

  case object manage_token extends ClusterPrivilege

  case object manage_transform extends ClusterPrivilege

  case object manage_user_profile extends ClusterPrivilege

  case object manage_watcher extends ClusterPrivilege

  case object monitor extends ClusterPrivilege

  case object monitor_ml extends ClusterPrivilege

  case object monitor_rollup extends ClusterPrivilege

  case object monitor_snapshot extends ClusterPrivilege

  case object monitor_text_structure extends ClusterPrivilege

  case object monitor_transform extends ClusterPrivilege

  case object monitor_watcher extends ClusterPrivilege

  case object read_ccr extends ClusterPrivilege

  case object read_ilm extends ClusterPrivilege

  case object read_pipeline extends ClusterPrivilege

  case object read_slm extends ClusterPrivilege

  case object transport_client extends ClusterPrivilege

  implicit final val decoder: JsonDecoder[ClusterPrivilege] =
    DeriveJsonDecoderEnum.gen[ClusterPrivilege]
  implicit final val encoder: JsonEncoder[ClusterPrivilege] =
    DeriveJsonEncoderEnum.gen[ClusterPrivilege]
  implicit final val codec: JsonCodec[ClusterPrivilege] =
    JsonCodec(encoder, decoder)

}
