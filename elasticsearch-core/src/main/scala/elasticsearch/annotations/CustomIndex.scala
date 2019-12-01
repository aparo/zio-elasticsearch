/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.annotations

trait CustomIndex {

  def calcIndex(): String

}
