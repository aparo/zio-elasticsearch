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

package zio.openapi

import scala.collection.immutable.ListMap

import io.circe.derivation.annotations.JsonCodec

// todo: external docs, callbacks, security
@JsonCodec
final case class Operation(
  operationId: String,
  tags: List[String] = Nil,
  summary: Option[String] = None,
  description: Option[String] = None,
  parameters: List[ReferenceOr[Parameter]] = Nil,
  requestBody: Option[ReferenceOr[RequestBody]] = None,
  responses: ListMap[ResponsesKey, ReferenceOr[Response]] = ListMap.empty,
  deprecated: Option[Boolean] = None,
  security: List[SecurityRequirement] = Nil,
  servers: List[Server] = Nil
)
