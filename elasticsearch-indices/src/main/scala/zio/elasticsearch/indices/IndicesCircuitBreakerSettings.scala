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

package zio.elasticsearch.indices
import zio.json._
final case class IndicesCircuitBreakerSettings(
  @jsonField("fielddata_limit") fielddataLimit: String,
  @jsonField("fielddata_overhead") fielddataOverhead: Float,
  @jsonField("request_limit") requestLimit: String,
  @jsonField("request_overhead") requestOverhead: Float,
  @jsonField("total_limit") totalLimit: String
)

object IndicesCircuitBreakerSettings {
  implicit lazy val jsonCodec: JsonCodec[IndicesCircuitBreakerSettings] =
    DeriveJsonCodec.gen[IndicesCircuitBreakerSettings]
}
