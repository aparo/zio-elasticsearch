/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.orm

import elasticsearch.mappings.MetaUser

trait UserMetaTrait {
  lazy val metaUser = MetaUser(
    field = userField,
    track_changes = trackUserChanged,
    track_created = trackUserCreated,
    track_deleted = trackUserDeleted,
    auto_owner = autoOwner
  )

  //track user changed
  def trackUserChanged: Boolean = false

  //track user deleted
  def trackUserDeleted: Boolean = false

  //track user created
  def trackUserCreated: Boolean = false

  //track auto owner This object is saved on single user
  def autoOwner: Boolean = false

  //the id to fallback if the record is missing
  def autoOwnerFallBack: Option[String] = None

  // force filtering for user
  def userField: Option[String] = None

}
