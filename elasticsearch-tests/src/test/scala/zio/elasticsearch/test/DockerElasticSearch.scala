/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

package zio.elasticsearch.test

import com.dimafeng.testcontainers.ElasticsearchContainer
import org.testcontainers.utility.DockerImageName
import zio._
import zio.elasticsearch.{ ElasticSearch, ElasticSearchConfig }
import zio.exception.FrameworkException

object DockerElasticSearch {

  case class DockerElasticSearch(container: ElasticsearchContainer) extends ElasticSearch {
    def esConfig: UIO[ElasticSearchConfig] = ZIO.succeed(
      ElasticSearchConfig(
        container.httpHostAddress,
        useSSL = false,
        user = Some("elastic"),
        password = Some("mypassword")
      )
    )
    def migrate(): ZIO[Any, FrameworkException, Unit] = ZIO.unit
    def start(): UIO[Unit] = ZIO.attempt(container.start()).orDie
    def stop(): UIO[Unit] = ZIO.attempt(container.stop()).ignore.unit
  }

  lazy val defaultElasticsearchDockerVersion =
    s"${sys.env.getOrElse("DOCKER_PROXY", "")}docker.elastic.co/elasticsearch/elasticsearch:7.17.5"

  def elasticsearch(
    imageName: String = s"${sys.env.getOrElse("DOCKER_PROXY", "")}docker.elastic.co/elasticsearch/elasticsearch:7.17.5"
  ): ZLayer[Any, Throwable, ElasticSearch] = {

    val containerInit: ZIO[Any, Throwable, DockerElasticSearch] = {
      val container = new ElasticsearchContainer(
        dockerImageName = DockerImageName.parse(imageName)
      )
      container.container.withPassword("mypassword")
      container.start()
      val result = DockerElasticSearch(container)

      for {
        _ <- result.migrate()
      } yield result
    }

    ZLayer.scoped {
      ZIO.acquireRelease(containerInit)(container => container.stop())
    }

  }
}
