/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

object StandardESNoSqlContext {

  def apply(
    user: AbstractUser,
    elasticSearch: ElasticSearch
  ): ESNoSqlContext =
    StandardESNoSqlContext(user, elasticSearch)

  var SystemUser: StandardESNoSqlContext = _ //TODO inizialize me
}

final case class StandardESNoSqlContext(
  user: AbstractUser,
  elasticsearch: ElasticSearch
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
