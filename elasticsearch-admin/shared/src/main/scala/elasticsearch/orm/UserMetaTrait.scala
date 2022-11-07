/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.elasticsearch.orm

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
