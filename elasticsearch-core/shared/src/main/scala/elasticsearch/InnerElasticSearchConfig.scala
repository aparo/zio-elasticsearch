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

import elasticsearch.client.ServerAddress
import io.circe.derivation.annotations.JsonCodec
import zio.circe.time._

import scala.concurrent.duration._

@JsonCodec
final case class ElasticSearchConfig(
  database: String,
  useSSL: Option[Boolean] = None,
  validateSSLCerficates: Option[Boolean] = None,
  alias: List[String] = Nil,
  bulkSize: Int = -1,
  queueSize: Int = -1,
  timeout: Option[FiniteDuration] = None,
  user: Option[String] = None,
  password: Option[String] = None,
  hosts: String = ""
) {

  def realHosts = hosts.split(',').toList

  def serverAddress: List[ServerAddress] = realHosts.map { t =>
    ServerAddress.fromString(t)
  }
}

@JsonCodec
final case class InnerElasticSearchConfig(
  database: String,
  authDatabase: String = "",
  bulkSize: Int = 500,
  queueSize: Int = 100,
  timeout: FiniteDuration = 60.seconds,
  useSSL: Boolean = false,
  hosts: String = "127.0.0.1:9200",
  user: Option[String] = None,
  password: Option[String] = None,
  configs: Map[String, ElasticSearchConfig] = Map.empty[String, ElasticSearchConfig]
) {

  def realHosts = hosts.split(',').toList

  def getDefault(nameFixer: String => String): ElasticSearchConfig =
    ElasticSearchConfig(
      database = nameFixer(database),
      hosts = hosts,
      user = user,
      password = password,
      useSSL = Some(useSSL)
    )

  def upgradeConfig(cfg: ElasticSearchConfig): ElasticSearchConfig = {
    var config = cfg
    if (config.hosts.isEmpty) config = config.copy(hosts = hosts)
    if (config.user.isEmpty) config = config.copy(user = user)
    if (config.timeout.isEmpty) config = config.copy(timeout = Some(timeout))
    if (config.password.isEmpty) config = config.copy(password = password)
    if (config.bulkSize <= 0) config = config.copy(bulkSize = bulkSize)
    if (config.queueSize <= 0) config = config.copy(queueSize = queueSize)
    config
  }
}
