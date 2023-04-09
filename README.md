# zio-elasticsearch
ElasticSearch client for Scala based on ZIO and FP.
Only Elasticsearch 7.x is supported.

The project targets are:
- simply API for JVM and JS (targeting Scala 2.12/2.13 for now)
- completely functional approach on the library based on ZIO
- full typesafe Query, Aggregation, Request and Response of Elasticsearch
- http layer based on [sttp](https://github.com/softwaremill/sttp) (in future zio-http when it will be released)
- using [zio-json]() for json management 
- full coverage of Elasticsearch call/responses (generated from API)

All the code is based on Manager (ZIO Services) that can be composed to provide required functionalities to your project.
Splitting the API in different reduces the size of the classes for the overall project.

The actual modules are:

 - zio-elasticsearch-core: it provides all the core functionalities: document management and searching (query, aggregation, ...)
 - zio-elasticsearch-async-search: it provides support for async search API
 - zio-elasticsearch-autoscaling: it provides support for autoscaling API
 - zio-elasticsearch-cluster: it provides support for cluster API
 - zio-elasticsearch-ccr: it provides support for Cross Cluster Replication API
 - zio-elasticsearch-dangling-indices: it provides support for Dangling Indices API
 - zio-elasticsearch-enrich: it provides support for Enrich API
 - zio-elasticsearch-eql: it provides support for EQL API
 - zio-elasticsearch-features: it provides support for Features API
 - zio-elasticsearch-fleet: it provides support for Fleet API
 - zio-elasticsearch-graph: it provides support for Graph API
 - zio-elasticsearch-indices: it provides support for Index Management API
 - zio-elasticsearch-ilm: it provides support for Index Management API
 - zio-elasticsearch-ingest: it provides support for Ingest API with strong typed Process support
 - zio-elasticsearch-license: it provides support for License API
 - zio-elasticsearch-logstash: it provides support for Logstash API
 - zio-elasticsearch-migration: it provides support for Migration API
 - zio-elasticsearch-ml: it provides support for Machine Learning API
 - zio-elasticsearch-monitoring: it provides support for Monitoring API
 - zio-elasticsearch-nodes: it provides support for Nodes API
 - zio-elasticsearch-rollup: it provides support for Roll Up API
 - zio-elasticsearch-searchable-snapshots: it provides support for Searchable Snapshot API
 - zio-elasticsearch-security: it provides support for Security API
 - zio-elasticsearch-shutdown: it provides support for Shutdown API
 - zio-elasticsearch-slm: it provides support for SLM API
 - zio-elasticsearch-snapshot: it provides support for Snapshot API
 - zio-elasticsearch-sql: it provides support for SQL API
 - zio-elasticsearch-ssl: it provides support for SSL API
 - zio-elasticsearch-tasks: it provides support for Tasks API
 - zio-elasticsearch-text-structure: it provides support for Text Structure API
 - zio-elasticsearch-transform: it provides support for Transform API
 - zio-elasticsearch-watcher: it provides support for Watcher API
 - zio-elasticsearch-xpack: it provides support for XPACK API

And their dependencies are the following ones:
![Module dependencies](dependencies.png)


## Quick usage tour

The follow overcommented example is taken from test directory:



```
package zio.elasticsearch.client

import zio.elasticsearch.orm.QueryBuilder
import zio.elasticsearch.queries.TermQuery
import zio.elasticsearch.requests.UpdateByQueryRequest
import zio.elasticsearch.{ ESSystemUser, SpecHelper, AuthContext }
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