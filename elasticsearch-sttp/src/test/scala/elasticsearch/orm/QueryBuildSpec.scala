/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.orm

import elasticsearch.client.ZioSttpClient
import elasticsearch.responses.ResultDocument
import elasticsearch.{ ESSystemUser, SpecHelper, StandardESNoSqlContext }
import io.circe.derivation.annotations.JsonCodec
import io.circe.syntax._
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ DefaultRuntime, system }

class QueryBuildSpec extends WordSpec with Matchers with BeforeAndAfterAll with SpecHelper {
  System.setProperty("es.set.netty.runtime.available.processors", "false")

  private val runner = new ElasticsearchClusterRunner()

  lazy val indexName = "source"

  implicit val elasticsearch = ZioSttpClient("localhost", 9201)

  lazy val environment: zio.Runtime[Clock with Console with system.System with Random with Blocking] =
    new DefaultRuntime {}

  implicit val context =
    new StandardESNoSqlContext(ESSystemUser, elasticsearch = elasticsearch)

  //#define-class
  @JsonCodec
  case class Book(title: String, pages: Int, active: Boolean = true)

  //#define-class

  lazy val booksDataset = List(
    Book("Akka in Action", 1),
    Book("Programming in Scala", 2),
    Book("Learning Scala", 3),
    Book("Scala for Spark in Production", 4),
    Book("Scala Puzzlers", 5),
    Book("Effective Akka", 6),
    Book("Akka Concurrency", 7)
  )

  override def beforeAll() = {
    runner.build(ElasticsearchClusterRunner.newConfigs().baseHttpPort(9200).numOfNode(1))
    runner.ensureYellow()
    booksDataset.foreach { book =>
      environment.unsafeRun(register(indexName, book))

    }
    flush("source")
  }

  override def afterAll() = {
    elasticsearch.close()
    runner.close()
    runner.clean()
  }

  private def flush(indexName: String): Unit =
    environment.unsafeRun(elasticsearch.refresh(indexName))

  private def register(indexName: String, book: Book) =
    elasticsearch.indexDocument(indexName, body = book.asJsonObject)

  "QueryBuilder" should {
    "return all elements in scan" in {
      val scan = elasticsearch.searchScan[Book](TypedQueryBuilder[Book](indices = Seq("source")))
      val books = scan.toList
      books.size should be(booksDataset.length)
    }
    "return all elements sorted in scan" in {
      val query = TypedQueryBuilder[Book](indices = Seq("source")).sortBy("pages")

      val scan = elasticsearch.searchScan[Book](query)
      val books: List[ResultDocument[Book]] = scan.toList
      books.map(_.source.pages) should be(booksDataset.map(_.pages))

    }
  }

}
