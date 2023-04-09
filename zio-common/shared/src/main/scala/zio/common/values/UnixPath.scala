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

package zio.common.values

import java.nio.file.Path

/**
 * @param path
 *   a path - all separators must be slashes ('/') regardless of platform
 */
case class UnixPath(path: String) extends WrappedString {
  def value: String = path
}

object UnixPath extends StringCompanion[UnixPath] {
  val UnixPathSeparator: Char = '/'
  val WindowsPathSeparator = '\\'
  val Empty: UnixPath = UnixPath("")

  def apply(path: Path): UnixPath = fromRaw(path.toString)

  def fromRaw(s: String): UnixPath = UnixPath(s.replace(WindowsPathSeparator, UnixPathSeparator))
}
