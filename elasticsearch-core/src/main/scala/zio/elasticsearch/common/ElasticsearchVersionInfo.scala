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

package zio.elasticsearch.common
import java.time._

import zio.json._
final case class ElasticsearchVersionInfo(
  @jsonField("build_date") buildDate: LocalDateTime,
  @jsonField("build_flavor") buildFlavor: String,
  @jsonField("build_hash") buildHash: String,
  @jsonField("build_snapshot") buildSnapshot: Boolean,
  @jsonField("build_type") buildType: String,
  @jsonField("lucene_version") luceneVersion: String,
  @jsonField(
    "minimum_index_compatibility_version"
  ) minimumIndexCompatibilityVersion: String,
  @jsonField(
    "minimum_wire_compatibility_version"
  ) minimumWireCompatibilityVersion: String,
  number: String
)

object ElasticsearchVersionInfo {
  implicit lazy val jsonCodec: JsonCodec[ElasticsearchVersionInfo] =
    DeriveJsonCodec.gen[ElasticsearchVersionInfo]
}
