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

package zio.elasticsearch.client

import zio.elasticsearch.test.ZIOTestElasticSearchSupport
import zio.auth.AuthContext
import zio.elasticsearch.indices.IndicesManager
import zio.elasticsearch.queries.TermQuery
import zio.elasticsearch.ElasticSearchService
import zio.elasticsearch.common.update_by_query.UpdateByQueryRequest
import zio.elasticsearch.orm.OrmManager
import zio.json._
import zio.json.ast.Json
import zio.schema.elasticsearch.annotations.CustomId
import zio.stream._
import zio.test.Assertion._
import zio.test._
import zio.{ Chunk, Clock, ZIO }

object ElasticSearchSpec extends ZIOSpecDefault with ZIOTestElasticSearchSupport with ORMSpec with GeoSpec {
  //#define-class
  case class Book(id: Int, title: String, pages: Int) extends CustomId {
    override def calcId(): String = id.toString
  }
  object Book {
    implicit final val decoder: JsonDecoder[Book] =
      DeriveJsonDecoder.gen[Book]
    implicit final val encoder: JsonEncoder[Book] =
      DeriveJsonEncoder.gen[Book]
    implicit final val codec: JsonCodec[Book] = JsonCodec(encoder, decoder)

  }

  implicit val authContext = AuthContext.System

  val SAMPLE_RECORDS = Seq(
    Book(1, "Akka in Action", 1),
    Book(2, "Programming in Scala", 2),
    Book(3, "Learning Scala", 3),
    Book(4, "Scala for Spark in Production", 4),
    Book(5, "Scala Puzzlers", 5),
    Book(6, "Effective Akka", 6),
    Book(7, "Akka Concurrency", 7)
  )

  def populate(index: String) =
    for {
      elasticsearchService <- ZIO.service[ElasticSearchService]
      indicesManager <- ZIO.service[IndicesManager]
      _ <- ZIO.foreachDiscard(SAMPLE_RECORDS) { book =>
        elasticsearchService.index(
          index,
          body = Json.Obj(
            "title" -> Json.Str(book.title),
            "pages" -> Json.Num(book.pages),
            "active" -> Json.Bool(false)
          )
        )
      }
      _ <- indicesManager.refresh(index)

    } yield ()

  def countElement = test("count elements") {
    val index = "count_element"
    for {
      elasticsearchService <- ZIO.service[ElasticSearchService]
      _ <- populate(index)
      countResult <- elasticsearchService.count(Chunk(index))
    } yield assert(countResult.count)(equalTo(SAMPLE_RECORDS.length.toLong))
  }

  def updateByQueryElements = test("update by query elements") {
    val index = new Object() {}.getClass.getEnclosingMethod.getName.toLowerCase
    for {
      _ <- populate(index)
      elasticSearchService <- ZIO.service[ElasticSearchService]
      indicesManager <- ZIO.service[IndicesManager]
      ormManager <- ZIO.service[OrmManager]
      updatedResult <- elasticSearchService.updateByQuery(
        UpdateByQueryRequest.fromPartialDocument(index, Json.Obj("active" -> Json.Bool(true)))
      )
      _ <- indicesManager.refresh(index)
      qb <- ormManager.queryBuilder(indices = Chunk(index), filters = Chunk(TermQuery("active", true)))
      searchResult <- qb.results
    } yield assert(updatedResult.updated)(equalTo(SAMPLE_RECORDS.length.toLong)) &&
      assert(searchResult.total)(equalTo(SAMPLE_RECORDS.length.toLong))
  }

  def sinker = test("sinker") {
    val index = new Object() {}.getClass.getEnclosingMethod.getName.toLowerCase
    for {
      ormManager <- ZIO.service[OrmManager]
      indicesManager <- ZIO.service[IndicesManager]
      _ <- indicesManager.delete(Chunk(index)).ignore
      numbers <- zio.Random.nextIntBetween(20, 100)
      esService <- ZIO.service[ElasticSearchService]
      sink = esService.sink[Book](index = index, bulkSize = 10)
      total <- ZStream
        .fromIterable(1.to(numbers))
        .mapZIO { i =>
          for {
            s <- zio.Random.nextString(10)
          } yield Book(i, s, i)

        }
        .run(sink)
      _ <- indicesManager.refresh(index)
      qb <- ormManager.queryBuilder(indices = Chunk(index))
      searchResult <- qb.results
    } yield assert(total)(equalTo(numbers.toLong)) &&
      assert(searchResult.total)(equalTo(numbers.toLong))
  }

  override def spec: Spec[TestEnvironment, Throwable] =
    suite("ElasticSearchSpec")(
      // sinker
      // countElement,
      updateByQueryElements,
//      ormSchemaCheck,
      ormBulker,
      ormMultiTypeIndexBulker,
//      ormMultiCallOnCreate,
      geoIndexAndSorting
    ).provideSomeLayerShared[TestEnvironment](
      esLayer
    ) @@ TestAspect.sequential @@ TestAspect.withLiveClock

}
