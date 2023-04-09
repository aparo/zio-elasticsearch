/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

package zio.elasticsearch.client

import _root_.zio.elasticsearch.test.ZIOTestElasticSearchSupport
import zio.auth.AuthContext
import zio.elasticsearch.queries.TermQuery
import zio.elasticsearch.requests.UpdateByQueryRequest
import zio.elasticsearch.{ ClusterService, ElasticSearchService, IndicesService }
import zio.json._
import zio.json.ast.Json
import zio.schema.elasticsearch.annotations.CustomId
import zio.stream._
import zio.test.Assertion._
import zio.test._
import zio.{ Clock, ZIO }

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
      _ <- ZIO.foreach(SAMPLE_RECORDS) { book =>
        ElasticSearchService.indexDocument(
          index,
          body = Json.Obj(
            "title" -> Json.Str(book.title),
            "pages" -> Json.Num(book.pages),
            "active" -> Json.Bool(false)
          )
        )
      }
      _ <- IndicesService.refresh(index)

    } yield ()

  def countElement = test("count elements") {
    val index = "count_element"
    for {
      _ <- populate(index)
      countResult <- ElasticSearchService.count(Seq(index))
    } yield assert(countResult.count)(equalTo(SAMPLE_RECORDS.length.toLong))
  }

  def updateByQueryElements = test("update by query elements") {
    val index = new Object() {}.getClass.getEnclosingMethod.getName.toLowerCase
    for {
      _ <- populate(index)
      updatedResult <- ElasticSearchService.updateByQuery(
        UpdateByQueryRequest.fromPartialDocument(index, Json.Obj("active" -> Json.Bool(true)))
      )
      _ <- IndicesService.refresh(index)
      qb <- ClusterService.queryBuilder(indices = List(index), filters = List(TermQuery("active", true)))
      searchResult <- qb.results
    } yield assert(updatedResult.updated)(equalTo(SAMPLE_RECORDS.length.toLong)) &&
      assert(searchResult.total.value)(equalTo(SAMPLE_RECORDS.length.toLong))
  }

  def sinker = test("sinker") {
    val index = new Object() {}.getClass.getEnclosingMethod.getName.toLowerCase
    for {
      indicesServices <- ZIO.service[IndicesService]
      _ <- indicesServices.delete(Seq(index)).ignore
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
      _ <- IndicesService.refresh(index)
      qb <- ClusterService.queryBuilder(indices = List(index))
      searchResult <- qb.results
    } yield assert(total)(equalTo(numbers.toLong)) &&
      assert(searchResult.total.value)(equalTo(numbers.toLong))
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
