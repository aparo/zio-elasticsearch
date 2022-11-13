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

package zio.elasticsearch.responses.snapshot

import io.circe.derivation.annotations._
/*
 * Returns information about a snapshot.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A repository name
 * @param snapshot A comma-separated list of snapshot names
 * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param verbose Whether to show verbose snapshot info or only show the basic info found in the repository index blob
 */
@JsonCodec
final case class SnapshotGetResponse() {}
