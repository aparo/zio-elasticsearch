///*
// * Copyright 2019-2020 Alberto Paro
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package elasticsearch.orm
//
//import elasticsearch.client.ZioHTTP4SClient
//import elasticsearch.responses.ResultDocument
//import zio.auth.AuthContext
//import elasticsearch.{ElasticSearchConfig, ElasticSearchService, SpecHelper}
//import io.circe.derivation.annotations.JsonCodec
//import io.circe.syntax._
//import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner
//import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
//import zio.blocking.Blocking
//import zio.clock.Clock
//import zio.console.Console
//import zio.logging.Logging
//import zio.random.Random
//import zio.{Runtime, system}
//
//class QueryBuildSpec
//    extends WordSpec
//    with Matchers
//    with BeforeAndAfterAll
//    with SpecHelper {
//  System.setProperty("es.set.netty.runtime.available.processors", "false")
//
//  private val runner = new ElasticsearchClusterRunner()
//
//  lazy val indexName = "source"
//  implicit lazy val environment: zio.Runtime[
//    Clock with Console with system.System with Random with Blocking] =
//    Runtime.default
//
//  implicit val ec: scala.concurrent.ExecutionContext =
//    scala.concurrent.ExecutionContext.global
//
//  val loggingLayer = Logging.ignore
//
//  implicit val elasticsearch =
//    ZioHTTP4SClient.fullFromConfig(ElasticSearchConfig("localhost:9201"),
//                                   loggingLayer)
//
//  implicit val authContext = AuthContext.System
//
//  //#define-class
//  @JsonCodec
//  case class Book(title: String, pages: Int, active: Boolean = true)
//
//  //#define-class
//
//  lazy val booksDataset = List(
//    Book("Akka in Action", 1),
//    Book("Programming in Scala", 2),
//    Book("Learning Scala", 3),
//    Book("Scala for Spark in Production", 4),
//    Book("Scala Puzzlers", 5),
//    Book("Effective Akka", 6),
//    Book("Akka Concurrency", 7)
//  )
//
//  override def beforeAll() = {
//    runner.build(
//      ElasticsearchClusterRunner.newConfigs().baseHttpPort(9200).numOfNode(1))
//    runner.ensureYellow()
//    environment.unsafeRun(ElasticSearchService.bulkIndex(indexName, booksDataset))
//
////    booksDataset.foreach { book =>
////      environment.unsafeRun(register(indexName, book))
////
////    }
//    flush(indexName)
//  }
//
//  override def afterAll() = {
//    elasticsearch.close()
//    runner.close()
//    runner.clean()
//  }
//
//  private def flush(indexName: String): Unit =
//    environment.unsafeRun(elasticsearch.refresh(indexName))
//
//  private def register(indexName: String, book: Book) =
//    elasticsearch.indexDocument(indexName, body = book.asJsonObject)
//
//  "QueryBuilder" should {
//    "return all elements in scan" in {
//      val scan = elasticsearch.searchScan[Book](
//        TypedQueryBuilder[Book](indices = Seq("source")))
//      val books = environment.unsafeRun(scan.runCollect)
//      books.size should be(booksDataset.length)
//    }
//    "return all elements sorted in scan" in {
//      val query =
//        TypedQueryBuilder[Book](indices = Seq("source")).sortBy("pages")
//
//      val scan = elasticsearch.searchScan[Book](query)
//      val books: List[ResultDocument[Book]] =
//        environment.unsafeRun(scan.runCollect)
//      books.map(_.source.pages) should be(booksDataset.map(_.pages))
//
//    }
//  }
//
//}
