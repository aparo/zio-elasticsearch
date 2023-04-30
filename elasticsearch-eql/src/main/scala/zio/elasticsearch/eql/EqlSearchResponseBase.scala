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

package zio.elasticsearch.eql
import zio.json._
final case class EqlSearchResponseBase(
  id: Option[String] = None,
  @jsonField("is_partial") isPartial: Option[Boolean] = None,
  @jsonField("is_running") isRunning: Option[Boolean] = None,
  took: Option[Long] = None,
  @jsonField("timed_out") timedOut: Option[Boolean] = None,
  hits: EqlHits
)

object EqlSearchResponseBase {
  implicit lazy val jsonCodec: JsonCodec[EqlSearchResponseBase] =
    DeriveJsonCodec.gen[EqlSearchResponseBase]
}
