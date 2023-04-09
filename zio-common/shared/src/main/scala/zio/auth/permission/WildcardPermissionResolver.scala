/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.auth.permission

import zio.auth.Permission

/**
 * <tt>PermissionResolver</tt> implementation that returns a new {@link
 * WildcardPermission WildcardPermission} based on the input string.
 */
class WildcardPermissionResolver extends PermissionResolver {

  /**
   * Returns a new {@link WildcardPermission WildcardPermission} instance
   * constructed based on the specified <tt>permissionString</tt>.
   *
   * @param permissionString
   *   the permission string to convert to a { @link Permission Permission}
   *   instance.
   * @return
   *   a new { @link WildcardPermission WildcardPermission} instance constructed
   *   based on the specified <tt>permissionString</tt>
   */
  def resolvePermission(permissionString: String): Permission =
    WildcardPermission(permissionString)

}
