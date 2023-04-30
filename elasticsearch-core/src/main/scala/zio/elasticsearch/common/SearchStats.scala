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
final case class SearchStats(
  @jsonField("fetch_current") fetchCurrent: Long,
  @jsonField("fetch_time") fetchTime: Option[String] = None,
  @jsonField("fetch_time_in_millis") fetchTimeInMillis: Long,
  @jsonField("fetch_total") fetchTotal: Long,
  @jsonField("open_contexts") openContexts: Option[Long] = None,
  @jsonField("query_current") queryCurrent: Long,
  @jsonField("query_time") queryTime: Option[String] = None,
  @jsonField("query_time_in_millis") queryTimeInMillis: Long,
  @jsonField("query_total") queryTotal: Long,
  @jsonField("scroll_current") scrollCurrent: Long,
  @jsonField("scroll_time") scrollTime: Option[String] = None,
  @jsonField("scroll_time_in_millis") scrollTimeInMillis: Long,
  @jsonField("scroll_total") scrollTotal: Long,
  @jsonField("suggest_current") suggestCurrent: Long,
  @jsonField("suggest_time") suggestTime: Option[String] = None,
  @jsonField("suggest_time_in_millis") suggestTimeInMillis: Long,
  @jsonField("suggest_total") suggestTotal: Long,
  groups: Option[Map[String, SearchStats]] = None
)

object SearchStats {
  implicit lazy val jsonCodec: JsonCodec[SearchStats] =
    DeriveJsonCodec.gen[SearchStats]
}
