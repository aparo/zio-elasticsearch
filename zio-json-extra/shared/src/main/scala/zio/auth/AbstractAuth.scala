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

package zio.auth

import zio.auth.permission.WildcardPermission

trait AbstractAuth {

  def perms: List[String]

  private var cookedPermissions = Set.empty[Permission]

  def can(perm: String, partial: Boolean = false): Boolean = {
    if (cookedPermissions.isEmpty) {
      cookedPermissions = this.perms.map { p =>
        WildcardPermission(p, false)
      }.toSet[Permission]
    }
    val toCheckPerm = WildcardPermission(perm)
    cookedPermissions.exists(p => p.implies(toCheckPerm, partial))
  }

  /**
   * Checks if an user can access one of the permission
   * @param perms
   *   a list of permission
   * @return
   *   if the user can access to the element
   */
  def can(perms: Seq[String], partial: Boolean): Boolean =
    perms.forall(p => can(p, partial))

}
