/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

//package zio.elasticsearch.orm
//
//import zio.elasticsearch.SpecHelper
//import zio.elasticsearch.client.ZioHTTP4SClient
//import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner
//import org.scalatest.{WordSpec, _}
//import zio.auth.AuthContext
//
//import zio.Clock
//import zio.console.Console
//import zio.Random
//import zio.{DefaultRuntime, ZIO, system}
//
//class ElasticSearchORMSpec
//    extends WordSpec
//    with Matchers
//    with BeforeAndAfterAll
//    with SpecHelper {
//
//  private val runner = new ElasticsearchClusterRunner()
//
//  implicit lazy val environment: zio.Runtime[
//    Clock with Console with system.System with Random with Blocking] =
//    new DefaultRuntime {}
//
//  implicit val ec: scala.concurrent.ExecutionContext =
//    scala.concurrent.ExecutionContext.global
//
//  implicit val elasticsearch = ZioHTTP4SClient("localhost", 9201)
//
//  //#init-client
//
//  implicit val authContext = AuthContext.System
//
//  //#define-class
//
//  override def beforeAll(): Unit = {
//    runner.build(
//      ElasticsearchClusterRunner.newConfigs().baseHttpPort(9200).numOfNode(1))
//    runner.ensureYellow()
//  }
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
//  "ORM" should {
//    "create mapping" in {
////      println(ORMAllMappingTest._schema)
//      for {
//        _ <- elasticsearch.elasticSearchSchemaManagerService
//          .createMapping[ORMClassTest]
//        _ <- elasticsearch.elasticSearchSchemaManagerService
//          .createMapping[ORMAllMappingTest]
//        res <- ZIO.foreachParN(4)(0.to(100)) { i =>
//          elasticsearch.ormService.create(
//            ORMClassTest(i.toString, s"Name $i", i))
//        }
//        _ <- elasticsearch.refresh()
//      } yield ()
//    }
//  }
//
//}
