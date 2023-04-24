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

package zio.elasticsearch.common.search

import zio._
import zio.elasticsearch.common.GeoHashPrecision
import zio.json._
import zio.json.ast.Json
final case class CompletionContext(
  boost: Option[Double] = None,
  context: Json,
  neighbours: Option[Chunk[GeoHashPrecision]] = None,
  precision: Option[GeoHashPrecision] = None,
  prefix: Option[Boolean] = None
)

object CompletionContext {
  implicit lazy val jsonCodec: JsonCodec[CompletionContext] =
    DeriveJsonCodec.gen[CompletionContext]
}
