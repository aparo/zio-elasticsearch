/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.annotations

trait WithIndex {
  def index: String

  def index_=(value: String): Unit
}

trait WithType {
  def `type`: String

  def type_=(value: String): Unit
}

trait WithVersion {
  def version: Long

  def version_=(value: Long): Unit
}

trait FullId extends WithId with WithType with WithIndex
