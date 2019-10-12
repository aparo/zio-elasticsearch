/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import zio.{ DefaultRuntime, system }
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.random.Random

import scala.concurrent.duration._
import scala.concurrent.{ Await, Awaitable }

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

case object ESSystemUser extends AbstractUser {
  val id: String = "system"
  val username: String = id
  val firstName: String = "system"
  val lastName: String = "system"
  val email: String = "system@elasticsearch.com"
  val language: String = "en"
  val active: Boolean = true
  val perms: List[String] = Nil
}

trait ESNoSqlContext {
  var defaultTimeout: FiniteDuration = 100.seconds

  def user: AbstractUser

  def elasticsearch: ElasticSearch

  def applicationName: String = elasticsearch.applicationName

  def awaitResult[T](t: Awaitable[T]): T = Await.result(t, defaultTimeout)

  def systemNoSQLContext(): ESNoSqlContext

  //TODO manage a better one
  lazy val environment: zio.Runtime[Clock with Console with system.System with Random with Blocking] =
    new DefaultRuntime {}

}