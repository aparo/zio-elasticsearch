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

package elasticsearch.responses.repository

import io.circe.derivation.annotations.{ JsonKey, _ }

@JsonCodec
final case class RepositoryList(
  repos: List[Repository] = List.empty[Repository]
)

@JsonCodec
final case class RepositorySettings(
  location: Option[String] = None,
  @JsonKey("chunk_size") chunkSize: Option[String] = None,
  @JsonKey("max_restore_bytes_per_sec") maxRestoreBytesPerSec: Option[
    String
  ] = None,
  @JsonKey("max_snapshot_bytes_per_sec") maxSnapshotBytesPerSec: Option[
    String
  ] = None,
  compress: Option[String] = None,
  url: Option[String] = None,
  region: Option[String] = None,
  bucket: Option[String] = None,
  @JsonKey("base_path") basePath: Option[String] = None,
  @JsonKey("access_key") accessKey: Option[String] = None,
  @JsonKey("secret_key") secretKey: Option[String] = None,
  //chunk_size:Option[String] = None,
  @JsonKey("max_retries") maxRetries: Option[String] = None,
  //compress:Option[Boolean] = None,
  @JsonKey("server_side_encryption") serverSideEncryption: Option[Boolean] = None,
  uri: Option[String] = None,
  path: Option[String] = None,
  @JsonKey("load_defaults") loadDefaults: Option[Boolean] = None,
  @JsonKey("conf_location") confLocation: Option[String] = None,
  @JsonKey("concurrent_streams") concurrentStreams: Option[String] = None,
  //compress:Option[Boolean] = None,
  //chunk_size:Option[String] = None,
  container: Option[String] = None
  //base_path:Option[String] = None
  //concurrent_streams:Option[String] = None
  //chunk_size:Option[String] = None
  //compress:Option[Boolean] = None
)

@JsonCodec
final case class Repository(
  name: Option[String] = None,
  `type`: String = "",
  settings: RepositorySettings = RepositorySettings()
)
