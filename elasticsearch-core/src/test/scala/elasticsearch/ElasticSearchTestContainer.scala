/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import com.dimafeng.testcontainers.{ ForAllTestContainer, GenericContainer }
import org.scalatest.Suite
import org.testcontainers.containers.wait.strategy.Wait

/**
 * Elasticsearch Test Container
 *
 * Setup adapted from [[https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html]].
 *
 * X-Pack disabled so that logins aren't required.
 *
 * Transport and HTTP hosts explicitly wired to local IP addresses so that Elasticsearch's automatic bootstrap checks
 * are never initiated.
 * - [[https://www.elastic.co/guide/en/elasticsearch/reference/5.1/docker.html#docker-cli-run-dev-mode]]
 * - [[https://www.elastic.co/blog/bootstrap_checks_annoying_instead_of_devastating]]
 */
trait ElasticsearchContainer extends ForAllTestContainer { self: Suite =>

  def elasticsearchDockerImage: String =
    "docker.elastic.co/elasticsearch/elasticsearch:7.4.0"

  override val container: GenericContainer = {
    GenericContainer(
      dockerImage = elasticsearchDockerImage,
      exposedPorts = Seq(9200),
      env = Map(
        "transport.host" -> "127.0.0.1",
        "http.host" -> "0.0.0.0",
        "ES_JAVA_OPTS" -> "-Xms512m -Xmx512m",
        "xpack.security.enabled" -> "false"
      ),
      waitStrategy = Wait.forHttp("/")
    )
  }

  lazy val elasticsearchContainerIpAddress: String =
    container.container.getContainerIpAddress

  lazy val elasticsearchPort: Integer = container.container.getMappedPort(9200)
}
