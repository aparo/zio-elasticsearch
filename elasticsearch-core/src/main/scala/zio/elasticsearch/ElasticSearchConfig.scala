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

package zio.elasticsearch

import java.time.LocalDate

import scala.util.Random

import zio.elasticsearch.client.ServerAddress
import zio.json._
import zio.json.ast.time._
import zio.{ Chunk, Duration }

final case class ElasticSearchConfig(
  hosts: String = "localhost:9200",
  database: Option[String] = None,
  useSSL: Boolean = false,
  validateSSLCertificates: Boolean = true,
  alias: Chunk[String] = Chunk.empty,
  bulkSize: Int = 1000,
  queueSize: Int = -1,
  timeout: Duration = Duration.fromSeconds(1000),
  bulkTimeout: Duration = Duration.fromSeconds(5 * 60),
  creationSleep: Duration = Duration.fromSeconds(1),
  bulkMaxSize: Int = 1024 * 1024,
  concurrentConnections: Int = 10,
  maxRetries: Int = 2,
  user: Option[String] = None,
  password: Option[String] = None,
  applicationName: Option[String] = None,
  indexPrefix: Option[String] = None,
  indexPrefixSkip: Chunk[String] = Chunk.empty,
  headers: Map[String, String] = Map.empty[String, String],
  prometheus: Boolean = false,
  tracing: Boolean = false
) {
  def realHosts: Chunk[String] = Chunk.fromIterable(hosts.split(',').toList)
  def serverAddresses: Chunk[ServerAddress] = realHosts.map { t =>
    ServerAddress.fromString(t)
  }
  lazy val hostsWithScheme: Chunk[String] = serverAddresses.map(_.httpUrl(useSSL))
  def getHost: String = Random.shuffle(hostsWithScheme).head
  def expandVariables(value: String): String = if (value.contains('%'))
    value
      .replace("%APPPLICATIONNAME%", applicationName.getOrElse(""))
      .replace("%APPNAME%", applicationName.getOrElse(""))
      .replace("%YEAR%", LocalDate.now().getYear.toString)
      .replace("%MONTH%", LocalDate.now().getMonthValue.toString)
      .replace("%DAY%", LocalDate.now().getDayOfMonth.toString)
  else value
  def concreteIndexName(idx: String): String = expandVariables(idx) match {
    case s: String if indexPrefixSkip.contains(s) =>
      idx
    case s: String if s.startsWith(".") =>
      idx
    case s: String if indexPrefix.isDefined && !s.startsWith(indexPrefix.get + "-") =>
      indexPrefix.get + "-" + idx
    case _ =>
      idx
  }
}
object ElasticSearchConfig {
  implicit val jsonDecoder: JsonDecoder[ElasticSearchConfig] = DeriveJsonDecoder.gen[ElasticSearchConfig]
  implicit val jsonEncoder: JsonEncoder[ElasticSearchConfig] = DeriveJsonEncoder.gen[ElasticSearchConfig]
}

final case class InnerElasticSearchConfig(
  database: String,
  authDatabase: String = "",
  bulkSize: Int = 500,
  queueSize: Int = 100,
  timeout: Option[Duration] = None,
  useSSL: Option[Boolean] = None,
  hosts: String = "127.0.0.1:9200",
  user: Option[String] = None,
  password: Option[String] = None,
  configs: Map[String, ElasticSearchConfig] = Map.empty[String, ElasticSearchConfig]
) {
  def realHosts: List[String] = hosts.split(',').toList
  def getDefault(nameFixer: String => String): ElasticSearchConfig = ElasticSearchConfig(
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
object InnerElasticSearchConfig {
  implicit val jsonDecoder: JsonDecoder[InnerElasticSearchConfig] = DeriveJsonDecoder.gen[InnerElasticSearchConfig]
  implicit val jsonEncoder: JsonEncoder[InnerElasticSearchConfig] = DeriveJsonEncoder.gen[InnerElasticSearchConfig]
}
