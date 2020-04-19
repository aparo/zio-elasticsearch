/*
 * Copyright 2019-2020 Alberto Paro
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

import elasticsearch.client.ServerAddress
import io.circe.derivation.annotations.JsonCodec
import zio.circe.time._

import scala.concurrent.duration._
import scala.util.Random

@JsonCodec
final case class ElasticSearchConfig(
  hosts: String = "localhost:9200",
  database: Option[String] = None,
  useSSL: Boolean = false,
  validateSSLCertificates: Boolean = true,
  alias: List[String] = Nil,
  bulkSize: Int = 1000,
  queueSize: Int = -1,
  timeout: FiniteDuration = 1000.seconds,
  bulkTimeout: FiniteDuration = 5.minutes,
  creationSleep: FiniteDuration = 1.seconds,
  bulkMaxSize: Int = 1024 * 1024,
  concurrentConnections: Int = 10,
  maxRetries: Int = 2,
  user: Option[String] = None,
  password: Option[String] = None,
  applicationName: Option[String] = None,
  headers: Map[String, String] = Map.empty[String, String]
) {

//  var connectionLimits = 10
//  protected var innerBulkSize: Int = bulkSize
//
  def realHosts = hosts.split(',').toList

  def serverAddresses: List[ServerAddress] = realHosts.map { t =>
    ServerAddress.fromString(t)
  }

  lazy val hostsWithScheme: Seq[String] =
    serverAddresses.map(_.httpUrl(useSSL))

  def getHost: String = Random.shuffle(hostsWithScheme).head
}

@JsonCodec
final case class InnerElasticSearchConfig(
  database: String,
  authDatabase: String = "",
  bulkSize: Int = 500,
  queueSize: Int = 100,
  timeout: Option[FiniteDuration] = None,
  useSSL: Option[Boolean] = None,
  hosts: String = "127.0.0.1:9200",
  user: Option[String] = None,
  password: Option[String] = None,
  configs: Map[String, ElasticSearchConfig] = Map.empty[String, ElasticSearchConfig]
) {

  def realHosts = hosts.split(',').toList

  def getDefault(nameFixer: String => String): ElasticSearchConfig =
    ElasticSearchConfig(
      database = Some(nameFixer(database)),
      hosts = hosts,
      user = user,
      password = password,
      useSSL = useSSL.getOrElse(false)
    )

  def upgradeConfig(cfg: ElasticSearchConfig): ElasticSearchConfig = {
    var config = cfg
    if (config.hosts.isEmpty) config = config.copy(hosts = hosts)
    if (config.user.isEmpty) config = config.copy(user = user)
    if (this.timeout.nonEmpty) config = config.copy(timeout = timeout.get)
    if (config.password.isEmpty) config = config.copy(password = password)
    if (config.bulkSize <= 0) config = config.copy(bulkSize = bulkSize)
    if (config.queueSize <= 0) config = config.copy(queueSize = queueSize)
    config
  }
}
