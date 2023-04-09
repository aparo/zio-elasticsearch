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

sealed trait DateKind

object DateKind {

  case object Month extends DateKind {
    def name = "month"
  }

  case object Year extends DateKind {
    def name = "year"
  }

  case object Day extends DateKind {
    def name = "day"
  }

  case object Week extends DateKind {
    def name = "week"
  }

  case object Hour extends DateKind {
    def name = "hour"
  }

  case object Minute extends DateKind {
    def name = "minute"
  }

  case object Second extends DateKind {
    def name = "second"
  }

  case object OneYear extends DateKind {
    def name = "1y"
  }

  case object OneMonth extends DateKind {
    def name = "1m"
  }

  case object OneWeek extends DateKind {
    def name = "1w"
  }

  case object OneDay extends DateKind {
    def name = "1d"
  }

  case object OneHour extends DateKind {
    def name = "1h"
  }

  case object OneMinute extends DateKind {
    def name = "1m"
  }

  case object OneSecond extends DateKind {
    def name = "1s"
  }

  case object Quarter extends DateKind {
    def name = "quarter"
  }

}
