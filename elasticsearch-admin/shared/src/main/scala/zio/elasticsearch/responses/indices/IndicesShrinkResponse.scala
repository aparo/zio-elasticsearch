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

package zio.elasticsearch.responses.indices

import zio.json.ast._
/*
 * Allow to shrink an existing index into a new index with fewer primary shards.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shrink-index.html
 *
 * @param index The name of the source index to shrink
 * @param target The name of the target index to shrink into
 * @param body body the body of the call
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
 */
@JsonCodec
final case class IndicesShrinkResponse() {}
