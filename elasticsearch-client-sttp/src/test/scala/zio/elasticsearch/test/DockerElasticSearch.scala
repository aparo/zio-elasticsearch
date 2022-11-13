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

package zio.elasticsearch.test

import zio.exception.FrameworkException
import com.dimafeng.testcontainers.ElasticsearchContainer
import zio.elasticsearch.{ ElasticSearch, ElasticSearchConfig }
import org.testcontainers.utility.DockerImageName
import zio._

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
