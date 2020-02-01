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

package elasticsearch.client

import elasticsearch.orm.QueryBuilder
import elasticsearch.queries.TermQuery
import elasticsearch.requests.UpdateByQueryRequest
import elasticsearch.SpecHelper
import io.circe.derivation.annotations.JsonCodec
import io.circe._
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner
import org.scalatest._
import zio.blocking.Blocking
import org.scalatest.WordSpec
import zio.auth.AuthContext
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ DefaultRuntime, system }

class ElasticSearchSpec extends WordSpec with Matchers with BeforeAndAfterAll with SpecHelper {

  private val runner = new ElasticsearchClusterRunner()

  implicit val elasticsearch = ZioSttpClient("localhost", 9201)

  //#init-client

  lazy val environment: zio.Runtime[Clock with Console with system.System with Random with Blocking] =
    new DefaultRuntime {}

  implicit val authContext = AuthContext.System

  //#define-class
  @JsonCodec
  case class Book(title: String, pages: Int)

  //#define-class

  override def beforeAll() = {
    runner.build(ElasticsearchClusterRunner.newConfigs().baseHttpPort(9200).numOfNode(1))
    runner.ensureYellow()

    val load = for {
      _ <- register("source", "Akka in Action", 1)
      _ <- register("source", "Programming in Scala", 2)
      _ <- register("source", "Learning Scala", 3)
      _ <- register("source", "Scala for Spark in Production", 4)
      _ <- register("source", "Scala Puzzlers", 5)
      _ <- register("source", "Effective Akka", 6)
      _ <- register("source", "Akka Concurrency", 7)
      _ <- elasticsearch.refresh("source")

    } yield ()

    environment.unsafeRun(load)
  }

  override def afterAll() = {
    elasticsearch.close()
    runner.close()
    runner.clean()
  }

  def flush(indexName: String): Unit =
    environment.unsafeRun(elasticsearch.refresh(indexName))

  private def register(indexName: String, title: String, pages: Int) =
    elasticsearch.indexDocument(
      indexName,
      body = JsonObject.fromMap(
        Map("title" -> Json.fromString(title), "pages" -> Json.fromInt(pages), "active" -> Json.fromBoolean(false))
      )
    )

  "Client" should {
    "count elements" in {
      val count = environment.unsafeRun(elasticsearch.countAll("source"))
      count should be(7)
    }
    "update pages" in {
      val multipleResultE = environment.unsafeRun(
        elasticsearch.updateByQuery(
          UpdateByQueryRequest.fromPartialDocument("source", JsonObject("active" -> Json.fromBoolean(true)))
        )
      )

      multipleResultE.updated should be(7)
      flush("source")
      val searchResultE = environment.unsafeRun(
        elasticsearch.search(QueryBuilder(indices = List("source"), filters = List(TermQuery("active", true))))
      )

      searchResultE.total.value should be(7)
    }
  }

}
