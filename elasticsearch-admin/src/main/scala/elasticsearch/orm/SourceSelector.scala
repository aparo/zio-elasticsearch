/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.orm

import io.circe.derivation.annotations.JsonCodec

@JsonCodec
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
