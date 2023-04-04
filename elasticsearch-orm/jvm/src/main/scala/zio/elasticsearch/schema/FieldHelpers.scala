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

package zio.elasticsearch.schema

import zio.Chunk
import zio.common.OffsetDateTimeHelper
import zio.json.ast._

object FieldHelpers {

  //Expand a date in its json components
  def expandHeapMapValues(prefix: String, value: String): Chunk[(String, Json)] =
    try {
      val dt = OffsetDateTimeHelper.parse(value)
      Chunk(
        (prefix, Json.Str(dt.toString)),
        (prefix + "_year", Json.Num(dt.getYear)),
        (prefix + "_month", Json.Num(dt.getMonthValue)),
        (prefix + "_day", Json.Num(dt.getDayOfMonth)),
        (prefix + "_wday", Json.Num(dt.getDayOfWeek.getValue)),
        (prefix + "_hour", Json.Num(dt.getHour())),
        (prefix + "_minute", Json.Num(dt.getMinute))
      )
    } catch {
      case ex: java.lang.IllegalArgumentException =>
        Chunk.empty
    }

}
