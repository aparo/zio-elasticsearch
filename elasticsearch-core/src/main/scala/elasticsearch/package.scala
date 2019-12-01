/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import elasticsearch.exception.FrameworkException
import zio._
package object elasticsearch {
  type ZioResponse[T] = ZIO[Any, FrameworkException, T]

}
