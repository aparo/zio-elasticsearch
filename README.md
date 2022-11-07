# zio-elasticsearch
ElasticSearch client for Scala based on ZIO and FP.
Only Elasticsearch 7.x is supported.

The project targets are:
- simply API
- completely functional approach on the library based on ZIO
- full typesafe Query, Aggregation, Request and Response of Elasticsearch
- http layer based on [sttp](https://github.com/softwaremill/sttp) (in future zio-http when it ill be released)
- using [circe]() for json management and [circe-derivation](https://github.com/circe/circe-derivation) for fast compiling time 
- full coverage of Elasticsearch call/responses (generated from API)

## Quick usage tour

The follow overcommented example is taken from test directory:



```
package zio.elasticsearch.client

import elasticsearch.orm.QueryBuilder
import elasticsearch.queries.TermQuery
import elasticsearch.requests.UpdateByQueryRequest
import elasticsearch.{ ESSystemUser, SpecHelper, AuthContext }
import zio.json._
import io.circe._
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner
import org.scalatest._
import zio.blocking.Blocking
import org.scalatest.WordSpec
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ DefaultRuntime, system }

class ElasticSearchSpec extends WordSpec with Matchers with BeforeAndAfterAll with SpecHelper {

  // we init a cluster for test
  private val runner = new ElasticsearchClusterRunner()

  // we init a n ElasticSearch Client
  implicit val elasticsearch = ZioClient("localhost", 9201)

  // we need a ZIO Enrvironment to "runUnsafe" out code
  lazy val environment: zio.Runtime[Clock with Console with system.System with Random with Blocking] =
    new DefaultRuntime {}

  // a context propagate user and other info for every call without need to pass the arguments to all functions
  implicit val context =
    new AuthContext(ESSystemUser, elasticsearch = elasticsearch)

  // we create a case class that contains our data
  // JsonCodec is a macro annotation that create encoder and decoder for circe
  @JsonCodec
  case class Book(title: String, pages: Int)


  // we init the data 
  override def beforeAll() = {
    runner.build(ElasticsearchClusterRunner.newConfigs().baseHttpPort(9200).numOfNode(1))
    runner.ensureYellow()

    // we prepare he store statement with an ending refresh
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

    // we execute the statement with the ZIO environment
    environment.unsafeRun(load)
  }

  // called on test completion
  override def afterAll() = {
    elasticsearch.close()
    runner.close()
    runner.clean()
  }

  // helper function to flush ES to allow to search data
  def flush(indexName: String): Unit =
    environment.unsafeRun(elasticsearch.refresh(indexName))

  // helper function to create an index request
  private def register(indexName: String, title: String, pages: Int) =
    elasticsearch.indexDocument(
      indexName,
      body = JsonObject.fromMap(
        Map("title" -> Json.fromString(title), "pages" -> Json.fromInt(pages), "active" -> Json.fromBoolean(false))
      )
    )

  "Client" should {

    "count elements" in {
      // we call the countAll elements inside a index
      val count = environment.unsafeRun(elasticsearch.countAll("source"))
      count should be(7)
    }

    "update pages" in {
      
      // we call the updateByQuery
      val multipleResultE = environment.unsafeRun(
        elasticsearch.updateByQuery(
          UpdateByQueryRequest.fromPartialDocument("source", JsonObject("active" -> Json.fromBoolean(true)))
        )
      )

      multipleResultE.updated should be(7)
      flush("source")
      
      // we execute a query on updated data
      val searchResultE = environment.unsafeRun(
        elasticsearch.search(QueryBuilder(indices = List("source"), filters = List(TermQuery("active", true))))
      )

      searchResultE.total.value should be(7)
    }
  }

}
```