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

package elasticsearch.responses

import io.circe.derivation.annotations._

@JsonCodec
final case class ErrorRoot(
  @JsonKey("type") `type`: String,
  reason: String,
  @JsonKey("resource.type") resourceType: Option[String] = None,
  @JsonKey("resource.id") resourceId: Option[String] = None,
  @JsonKey("index_uuid") indexUUID: Option[String] = None,
  index: Option[String] = None,
  shard: Option[String] = None
)

@JsonCodec
final case class Error(
  @JsonKey("type") `type`: String,
  reason: String,
  @JsonKey("root_cause") rootCause: List[ErrorRoot] = Nil
)

@JsonCodec
final case class ErrorResponse(error: Error, status: Int = 500)

//Test
