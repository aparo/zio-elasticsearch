/*
 * Copyright 2019 - NTTDATA Italia S.P.A. All Rights Reserved.
 */

package elasticsearch.nosql

import akka.actor.ActorSystem
import elasticsearch.auth.models.{AbstractUser, User}
import elasticsearch.{ESNoSqlContext, ElasticSearch}

trait NoSqlContext extends ESNoSqlContext {

  def user: AbstractUser

  def elasticsearch: ElasticSearch

  def columnarEngine: ColumnarEngine

}

object NoSqlContext {

  def apply(
      user: User,
      client: ColumnarEngine,
      elasticsearch: ElasticSearch,
      actorSystem: ActorSystem
  ): NoSqlContext =
    StandardNoSqlContext(user, client, elasticsearch, actorSystem)

  var SystemUser: StandardNoSqlContext = StandardNoSqlContext(
    User.SystemUser,
    ColumnarEngine("TOBEDONE"),
    null,
    null) //TODO inizialize me
}

final case class StandardNoSqlContext(
    user: User,
    columnarEngine: ColumnarEngine,
    elasticsearch: ElasticSearch,
    akkaSystem: ActorSystem
) extends NoSqlContext {
  def systemNoSQLContext(): NoSqlContext = this.copy(user = User.SystemUser)
  //    NoSqlContext(getConnection(), user = User.SystemUser)

  def systemNoSQLContext(index: String): NoSqlContext =
    this.copy(user = User.SystemUser)

  def userNoSQLContext(implicit user: User): NoSqlContext =
    this.copy(user = user)

  def userNoSQLContext(index: String)(implicit user: User): NoSqlContext =
    this.copy(user = user)

}
