/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

trait IndexResolverTrait {

  def concreteIndex(index: String): String

  def concreteIndex(index: Option[String] = None): String

}
