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

package zio.elasticsearch.xpack.usage
import zio.json._
final case class DataTiers(
  @jsonField("data_warm") dataWarm: DataTierPhaseStatistics,
  @jsonField("data_frozen") dataFrozen: Option[DataTierPhaseStatistics] = None,
  @jsonField("data_cold") dataCold: DataTierPhaseStatistics,
  @jsonField("data_content") dataContent: DataTierPhaseStatistics,
  @jsonField("data_hot") dataHot: DataTierPhaseStatistics,
  available: Boolean,
  enabled: Boolean
)

object DataTiers {
  implicit lazy val jsonCodec: JsonCodec[DataTiers] = DeriveJsonCodec.gen[DataTiers]
}
