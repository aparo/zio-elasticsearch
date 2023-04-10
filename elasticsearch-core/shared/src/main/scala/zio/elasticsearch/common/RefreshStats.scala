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
import zio.json._
import zio.json.ast._
final case class RefreshStats(
  @jsonField("external_total") externalTotal: Long,
  @jsonField("external_total_time_in_millis") externalTotalTimeInMillis: Long,
  listeners: Long,
  total: Long,
  @jsonField("total_time") totalTime: Option[String] = None,
  @jsonField("total_time_in_millis") totalTimeInMillis: Long
)

object RefreshStats {
  implicit val jsonCodec: JsonCodec[RefreshStats] =
    DeriveJsonCodec.gen[RefreshStats]
}