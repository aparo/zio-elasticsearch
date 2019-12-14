/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.orm.models

trait UserTrackingModel extends TimeStampedModel {
  def modifiedBy: String
  def createdBy: String

}
