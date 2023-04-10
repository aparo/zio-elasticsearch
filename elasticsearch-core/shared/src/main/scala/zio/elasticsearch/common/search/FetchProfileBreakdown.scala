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

import zio.json._
final case class FetchProfileBreakdown(
  @jsonField("load_source") loadSource: Option[Int] = None,
  @jsonField("load_source_count") loadSourceCount: Option[Int] = None,
  @jsonField("load_stored_fields") loadStoredFields: Option[Int] = None,
  @jsonField("load_stored_fields_count") loadStoredFieldsCount: Option[Int] = None,
  @jsonField("next_reader") nextReader: Option[Int] = None,
  @jsonField("next_reader_count") nextReaderCount: Option[Int] = None,
  @jsonField("process_count") processCount: Option[Int] = None,
  process: Option[Int] = None
)

object FetchProfileBreakdown {
  implicit lazy val jsonCodec: JsonCodec[FetchProfileBreakdown] =
    DeriveJsonCodec.gen[FetchProfileBreakdown]
}
