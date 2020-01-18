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

package elasticsearch.schema

import io.circe.Json
import zio.common.OffsetDateTimeHelper

object FieldHelpers {

  //Expand a date in its json components
  def expandHeapMapValues(prefix: String, value: String): List[(String, Json)] =
    try {
      val dt = OffsetDateTimeHelper.parse(value)
      List(
        (prefix, Json.fromString(dt.toString)),
        (prefix + "_year", Json.fromInt(dt.getYear)),
        (prefix + "_month", Json.fromInt(dt.getMonthValue)),
        (prefix + "_day", Json.fromInt(dt.getDayOfMonth)),
        (prefix + "_wday", Json.fromInt(dt.getDayOfWeek.getValue)),
        (prefix + "_hour", Json.fromInt(dt.getHour())),
        (prefix + "_minute", Json.fromInt(dt.getMinute))
      )
    } catch {
      case ex: java.lang.IllegalArgumentException =>
        Nil
    }

}
