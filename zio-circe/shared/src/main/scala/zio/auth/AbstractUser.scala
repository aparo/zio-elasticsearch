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

trait AbstractUser {
  def id: String

  def username: String

  def firstName: String

  def lastName: String

  def email: String

  def language: String

  def active: Boolean

  def perms: List[String]
}

object AbstractUser {
  case object Anonymous extends AbstractUser {
    val id: String = "anonymous"
    val username: String = id
    val firstName: String = "anonymous"
    val lastName: String = "anonymous"
    val email: String = "anonymous@megl.io"
    val language: String = "en"
    val active: Boolean = true
    val perms: List[String] = Nil
  }
  case object SystemUser extends AbstractUser {
    val id: String = "system"
    val username: String = id
    val firstName: String = "system"
    val lastName: String = "system"
    val email: String = "system@megl.io"
    val language: String = "en"
    val active: Boolean = true
    val perms: List[String] = Nil
  }
}
