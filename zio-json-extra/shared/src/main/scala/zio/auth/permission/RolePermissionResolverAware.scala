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

package zio.auth.permission

/**
 * Interface implemented by a component that wishes to use any
 * application-configured <tt>RolePermissionResolver</tt> that might already
 * exist instead of potentially creating one itself.
 *
 * <p>This is mostly implemented by {@link zio.auth.Authorizer Authorizer} and
 * {@link org.apache.shiro.realm.Realm Realm} implementations since they are the
 * ones performing permission checks and need to know how to resolve Strings
 * into {@link zio.auth.Permission Permission} instances.
 *
 * @since 1.0
 */
trait RolePermissionResolverAware {

  /**
   * Sets the specified <tt>RolePermissionResolver</tt> on this instance.
   *
   * @param rpr
   *   the <tt>RolePermissionResolver</tt> being set.
   */
  def setRolePermissionResolver(rpr: RolePermissionResolver): Unit
}
