/*
 * Copyright 2019-2020 Alberto Paro
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

import zio.exception.FrameworkException
import elasticsearch.responses.ResultDocument
import zio._
package object elasticsearch {
  type ZioResponse[T] = ZIO[Any, FrameworkException, T]
  type ESCursor[T] = zio.stream.Stream[FrameworkException, ResultDocument[T]]
}
