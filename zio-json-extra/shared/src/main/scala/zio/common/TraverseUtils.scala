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

package zio.common

object TraverseUtils {
  def traverse[A, B](eithers: Seq[Either[A, B]]): Either[A, Seq[B]] = {
    val builder = Seq.newBuilder[B]
    val it = eithers.iterator

    while (it.hasNext) it.next() match {
      case Left(a)  => return Left(a)
      case Right(b) => builder += b
    }

    Right(builder.result())
  }
}
