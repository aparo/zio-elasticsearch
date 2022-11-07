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

package zio.elasticsearch.client

import zio.auth.AuthContext
import elasticsearch.ClusterService
import elasticsearch.fixures.models.{ Person, PersonInIndex }
import elasticsearch.orm.ORMService
import elasticsearch.schema.ElasticSearchSchemaManagerService
import zio.Random._
import zio._
import zio.stream._
import zio.test.Assertion._
import zio.test._

trait ORMSpec {

  implicit def authContext: AuthContext

  def ormBulker = zio.test.test("orm bulker check") {
    import elasticsearch.mappings._
    for {
      _ <- ElasticSearchSchemaManagerService.deleteMapping[Person]
      mapping <- ElasticSearchSchemaManagerService.getMapping[Person]
      indexCreationResponse <- ElasticSearchSchemaManagerService.createMapping[Person]
      implicit0(clusterService: ClusterService) <- ORMService.clusterService
      records <- nextLongBetween(2000, 10000)
      bulker <- Person.esHelper.bulkStream(
        ZStream.fromIterable(1.to(records.toInt)).map(i => Person(s"test$i", "TestUser", "TestSurname", Some(i)))
      )
      _ <- bulker.flushBulk()
      _ <- Person.esHelper.refresh()
      items <- Person.esHelper.count()
    } yield assert(records)(
      equalTo(
        items
      )
    )

  }

  def ormMultiTypeIndexBulker = zio.test.test("orm multitype index bulker check") {
    for {
      _ <- ZIO.logInfo("Executing orm multitype index bulker check")
      _ <- ElasticSearchSchemaManagerService.deleteMapping[PersonInIndex]
      mapping <- ElasticSearchSchemaManagerService.getMapping[PersonInIndex]
      indexCreationResponse <- ElasticSearchSchemaManagerService.createMapping[PersonInIndex]
      implicit0(clusterService: ClusterService) <- ORMService.clusterService
      records <- nextLongBetween(2000, 10000)
      bulker <- PersonInIndex.esHelper.bulkStream(
        ZStream.fromIterable(1.to(records.toInt)).map(i => PersonInIndex(s"test$i", "TestUser", "TestSurname", Some(i)))
      )
      _ <- ZIO.logInfo("flush")
      _ <- bulker.flushBulk()
      _ <- PersonInIndex.esHelper.refresh()
      qb <- ORMService.query(PersonInIndex)
      itemsCount <- qb.count
      items <- PersonInIndex.esHelper.count()
    } yield assert(records)(
      equalTo(
        items
      )
    ) && assert(records)(
      equalTo(
        itemsCount
      )
    )

  }

  def ormMultiCallOnCreate = zio.test.test("orm multitype call on create check") {
    for {
      _ <- ZIO.logInfo("Executing orm multitype call on create check")
      implicit0(clusterService: ClusterService) <- ORMService.clusterService
      _ <- PersonInIndex.esHelper.drop().ignore
      _ <- ElasticSearchSchemaManagerService.createMapping[PersonInIndex].ignore
      persons = 1.to(100).map(i => PersonInIndex(s"test$i", "TestUser", "TestSurname", Some(i))).toList
      fork1 <- PersonInIndex.esHelper.createMany(persons, skipExisting = true).fork
      firstCreate <- fork1.join
      _ <- PersonInIndex.esHelper.refresh()
      secondCreate <- PersonInIndex.esHelper.createMany(persons, skipExisting = true)
      _ <- ZIO.logDebug(s"${secondCreate.length}")
    } yield assert(firstCreate.length)(
      equalTo(
        100
      )
    ) && assert(secondCreate.length)(
      equalTo(
        0
      )
    )

  }
  //
//  def ormSchemaCheck = test("orm schema check") {
//    import elasticsearch.mappings._
//    for {
//      mapping <- ElasticSearchSchemaManagerService.getMapping[Person]
//      indexCreationResponse <- ElasticSearchSchemaManagerService.createMapping[Person]
//      p = Person("test", "TestUser", "TestSurname", Some(10))
//      saved <- ORMService.create[Person](p)
//      saved2 <- ORMService.create[Person](p).catchAll(_ => ZIO.succeed("failed"))
//      _ <- ZIO.attempt(println(indexCreationResponse))
//    } yield assert(mapping)(
//      equalTo(
//        RootDocumentMapping(
//          Map(
//            "username" -> KeywordMapping(
//              false,
//              None,
//              None,
//              None,
//              None,
//              None,
//              false,
//              true,
//              1.0f,
//              None,
//              None,
//              List(),
//              Map(),
//              "keyword"
//            ),
//            "name" -> TextMapping(
//              false,
//              None,
//              None,
//              None,
//              None,
//              None,
//              None,
//              None,
//              None,
//              false,
//              true,
//              1.0f,
//              None,
//              None,
//              List(),
//              Map(
//                "keyword" -> KeywordMapping(
//                  false,
//                  None,
//                  None,
//                  None,
//                  None,
//                  None,
//                  false,
//                  true,
//                  1.0f,
//                  None,
//                  None,
//                  List(),
//                  Map(),
//                  "keyword"
//                )
//              ),
//              "text"
//            ),
//            "surname" -> TextMapping(
//              false,
//              None,
//              None,
//              None,
//              None,
//              None,
//              None,
//              None,
//              None,
//              false,
//              true,
//              1.0f,
//              None,
//              None,
//              List(),
//              Map(
//                "keyword" -> KeywordMapping(
//                  false,
//                  None,
//                  None,
//                  None,
//                  None,
//                  None,
//                  false,
//                  true,
//                  1.0f,
//                  None,
//                  None,
//                  List(),
//                  Map(),
//                  "keyword"
//                )
//              ),
//              "text"
//            ),
//            "age" -> NumberMapping("integer", None, false, None, None, false, true, 1.0f, None, None, List(), Map())
//          ),
//          "true",
//          true,
//          None,
//          None,
//          None,
//          None,
//          None,
//          true,
//          true,
//          None,
//          None,
//          None,
//          None,
//          MetaObject(
//            None,
//            None,
//            None,
//            None,
//            None,
//            None,
//            List(),
//            None,
//            None,
//            MetaUser(false, false, false, false, false, None),
//            List()
//          ),
//          None,
//          None
//        )
//      )
//    ) && assert(saved.username)(
//      equalTo(
//        "test"
//      )
//    ) && assert(saved2)(
//      equalTo(
//        "failed"
//      )
//    )
//
//  }
}
