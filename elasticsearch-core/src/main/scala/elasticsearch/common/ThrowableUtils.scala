/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.common

import java.io.{ PrintWriter, StringWriter }

object ThrowableUtils {

  /**
   * Convert a stacktrace Throwable to a string
   * @param ex the Throwable to be converted
   * @return the string representing this throwable
   */
  def stackTraceToString(ex: Throwable): String = {
    val errors = new StringWriter()
    ex.printStackTrace(new PrintWriter(errors))
    errors.toString
  }
}
