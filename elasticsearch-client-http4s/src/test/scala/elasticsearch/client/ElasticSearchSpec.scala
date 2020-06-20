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

package elasticsearch.client

import elasticsearch.{ ElasticSearchService, EmbeededElasticSearchSupport, IndicesService }
import io.circe._
import io.circe.derivation.annotations.JsonCodec
import zio.ZIO
import zio.auth.AuthContext
import zio.clock.Clock
import zio.test.Assertion._
import zio.test._
import zio.test.environment._

object ElasticSearchSpec extends DefaultRunnableSpec with EmbeededElasticSearchSupport {
  //#define-class
  @JsonCodec
  case class Book(title: String, pages: Int)

  implicit val authContext = AuthContext.System

  val SAMPLE_RECORDS = Seq(
    Book("Akka in Action", 1),
    Book("Programming in Scala", 2),
    Book("Learning Scala", 3),
    Book("Scala for Spark in Production", 4),
    Book("Scala Puzzlers", 5),
    Book("Effective Akka", 6),
    Book("Akka Concurrency", 7)
  )

  def populate(index: String) =
    for {
      _ <- ZIO.foreach(SAMPLE_RECORDS) { book =>
        ElasticSearchService.indexDocument(
          index,
          body = JsonObject.fromMap(
            Map(
              "title" -> Json.fromString(book.title),
              "pages" -> Json.fromInt(book.pages),
              "active" -> Json.fromBoolean(false)
            )
          )
        )
      }
      _ <- IndicesService.flush(index)

    } yield ()

  def countElement = testM("count elements") {
    val index = "count_element"
    for {
      _ <- populate(index)
      countResult <- ElasticSearchService.count(Seq(index))
    } yield assert(countResult.count)(equalTo(7L))
  }

//
//
//  override def afterAll() = {
//    elasticsearch.close()
//    runner.close()
//    runner.clean()
//  }
//
//  def flush(indexName: String): Unit =
//    environment.unsafeRun(elasticsearch.refresh(indexName))
//
//
//
//  "Client" should {
//    "count elements" in {
//      val count = environment.unsafeRun(elasticsearch.countAll("source"))
//      count should be(7)
//    }
//    "update pages" in {
//      val multipleResultE = environment.unsafeRun(
//        elasticsearch.updateByQuery(
//          UpdateByQueryRequest.fromPartialDocument("source", JsonObject("active" -> Json.fromBoolean(true)))
//        )
//      )
//
//      multipleResultE.updated should be(7)
//      flush("source")
//      val searchResultE = environment.unsafeRun(
//        elasticsearch.search(QueryBuilder(indices = List("source"), filters = List(TermQuery("active", true))))
//      )
//
//      searchResultE.total.value should be(7)
//    }
//  }
  override def spec: ZSpec[TestEnvironment, Throwable] =
    suite("ElasticSearchSpec")(countElement).provideSomeLayerShared[TestEnvironment](
      (esLayer).mapError(TestFailure.fail) ++ Clock.live
    )
}
