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

package zio.elasticsearch.orm

import zio.json._

@jsonDerive
final case class SourceSelector(
  includes: List[String] = Nil,
  excludes: List[String] = Nil
) {
  def isEmpty: Boolean = includes.isEmpty && excludes.isEmpty

  def nonEmpty: Boolean = !isEmpty
}

object SourceSelector {
  lazy val noSource = SourceSelector(excludes = List("*"))
  lazy val all = SourceSelector()

}
