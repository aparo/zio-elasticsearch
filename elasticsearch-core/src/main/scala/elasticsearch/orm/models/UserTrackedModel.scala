/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.orm.models

trait UserTrackedModel {
  def createdBy: String

  def modifiedBy: String

}
