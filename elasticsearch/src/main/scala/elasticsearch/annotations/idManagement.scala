/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.annotations

trait ProvideId {}

trait WithHiddenId extends ProvideId {

  var _id: Option[String] = None
  var _index: Option[String] = None
  var _type: Option[String] = None
  var _version: Option[Long] = None
}

trait WithId extends ProvideId {
  def id: String

  def id_=(value: String): Unit
}
