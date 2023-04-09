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

package zio.elasticsearch.responses

import zio.json._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update.html
 *
 * @param index The name of the index
 * @param id Document ID
 * @param body body the body of the call
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param parent ID of the parent document. Is is only used for routing and when for the upsert request
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param timestamp Explicit timestamp for the document
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param version Explicit version number for concurrency control
 * @param retryOnConflict Specify how many times should the operation be retried when a conflict occurs (default: 0)
 * @param versionType Specific version type
 * @param fields A comma-separated list of fields to return in the response
 * @param routing Specific routing value
 * @param lang The script language (default: painless)
 * @param ttl Expiration time for the document
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the update operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
final case class UpdateResponse(
  @jsonField("_index") index: String,
  @jsonField("_id") id: String,
  @jsonField("_version") version: Long = 0,
  @jsonField("_shards") shards: Shards = Shards(),
  result: Option[String] = None,
  created: Boolean = false
)
object UpdateResponse {
  implicit val jsonDecoder: JsonDecoder[UpdateResponse] = DeriveJsonDecoder.gen[UpdateResponse]
  implicit val jsonEncoder: JsonEncoder[UpdateResponse] = DeriveJsonEncoder.gen[UpdateResponse]
}
