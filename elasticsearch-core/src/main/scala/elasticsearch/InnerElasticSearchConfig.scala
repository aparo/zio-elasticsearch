/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.client.ServerAddress
import io.circe.derivation.annotations.JsonCodec
import elasticsearch.common.circe.time._

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
    configs: Map[String, ElasticSearchConfig] =
      Map.empty[String, ElasticSearchConfig]
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
