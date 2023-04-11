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

import fixures.models.{ Person, PersonInIndex }
import zio.Random._
import zio._
import zio.auth.AuthContext
import zio.elasticsearch.orm.OrmManager
import zio.stream._
import zio.test.Assertion._
import zio.test._

trait ORMSpec {

  implicit def authContext: AuthContext

  def ormBulker = zio.test.test("orm bulker check") {
    import zio.elasticsearch.mappings._
    for {
      ormManager <- ZIO.service[OrmManager]
      _ <- ormManager.deleteMapping[Person]
      mapping <- ormManager.getMapping[Person]
      indexCreationResponse <- ormManager.createIndex[Person]
      records <- nextLongBetween(2000, 10000)
      bulker <- ormManager.bulkStream[Person](
        ZStream.fromIterable(1.to(records.toInt)).map(i => Person(s"test$i", "TestUser", "TestSurname", Some(i)))
      )
      _ <- ormManager.refresh[Person]
      items <- ormManager.count[Person]
    } yield assert(records)(
      equalTo(
        items
      )
    )

  }

  def ormMultiTypeIndexBulker = zio.test.test("orm multitype index bulker check") {
    for {
      ormManager <- ZIO.service[OrmManager]
      _ <- ZIO.logInfo("Executing orm multitype index bulker check")
      _ <- ormManager.deleteMapping[PersonInIndex]
      mapping <- ormManager.getMapping[PersonInIndex]
      indexCreationResponse <- ormManager.createIndex[PersonInIndex]
      records <- nextLongBetween(2000, 10000)
      bulker <- ormManager.bulkStream[PersonInIndex](
        ZStream.fromIterable(1.to(records.toInt)).map(i => PersonInIndex(s"test$i", "TestUser", "TestSurname", Some(i)))
      )
      _ <- ZIO.logInfo("flush")
      _ <- ormManager.refresh[PersonInIndex]
      qb <- ormManager.query[PersonInIndex]
      itemsCount <- qb.count
      items <- ormManager.count[PersonInIndex]
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

//  def ormMultiCallOnCreate = zio.test.test("orm multitype call on create check") {
//    for {
//      _                                         <- ZIO.logInfo("Executing orm multitype call on create check")
//      implicit0(clusterService: ClusterService) <- ORMService.clusterService
//      _                                         <- PersonInIndex.esHelper.drop().ignore
//      _                                         <- ElasticSearchSchemaManagerService.createMapping[PersonInIndex].ignore
//      persons                                    = 1.to(100).map(i => PersonInIndex(s"test$i", "TestUser", "TestSurname", Some(i))).toList
//      fork1                                     <- PersonInIndex.esHelper.createMany(persons, skipExisting = true).fork
//      firstCreate                               <- fork1.join
//      _                                         <- PersonInIndex.esHelper.refresh()
//      secondCreate                              <- PersonInIndex.esHelper.createMany(persons, skipExisting = true)
//      _                                         <- ZIO.logDebug(s"${secondCreate.length}")
//    } yield assert(firstCreate.length)(
//      equalTo(
//        100
//      )
//    ) && assert(secondCreate.length)(
//      equalTo(
//        0
//      )
//    )
//
//  }
  //
//  def ormSchemaCheck = test("orm schema check") {
//    import zio.elasticsearch.mappings._
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
