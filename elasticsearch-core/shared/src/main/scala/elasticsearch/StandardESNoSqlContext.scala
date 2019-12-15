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

package elasticsearch

object StandardESNoSqlContext {

  def apply(
    user: AbstractUser,
    elasticSearch: BaseElasticSearchSupport
  ): ESNoSqlContext =
    StandardESNoSqlContext(user, elasticSearch)

  var SystemUser: StandardESNoSqlContext = _ //TODO inizialize me
}

final case class StandardESNoSqlContext(
  user: AbstractUser,
  elasticsearch: BaseElasticSearchSupport
) extends ESNoSqlContext {

  def systemNoSQLContext(): ESNoSqlContext = this.copy(user = ESSystemUser)

  //    NoSqlContext(getConnection(), user = User.SystemUser)

  def systemNoSQLContext(index: String): ESNoSqlContext =
    this.copy(user = ESSystemUser)

  def userNoSQLContext(implicit user: AbstractUser): ESNoSqlContext =
    this.copy(user = user)

  def userNoSQLContext(
    index: String
  )(implicit user: AbstractUser): ESNoSqlContext =
    this.copy(user = user)

}
