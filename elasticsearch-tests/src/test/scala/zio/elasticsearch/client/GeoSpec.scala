/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

package zio.elasticsearch.client

import fixures.models.GeoModel
import zio.Random._
import zio.ZIO
import zio.auth.AuthContext
import zio.elasticsearch.ClusterService
import zio.elasticsearch.geo.{ GeoPoint, GeoPointLatLon }
import zio.elasticsearch.orm.{ ORMService, TypedQueryBuilder }
import zio.elasticsearch.schema.ElasticSearchSchemaManagerService
import zio.stream._
import zio.test.Assertion._
import zio.test._
trait GeoSpec {

  implicit def authContext: AuthContext

  def geoIndexAndSorting = test("geoIndex and Sorting") {
    for {
      _ <- ElasticSearchSchemaManagerService.deleteMapping[GeoModel]
      mapping <- ElasticSearchSchemaManagerService.getMapping[GeoModel]
      indexCreationResponse <- ElasticSearchSchemaManagerService.createMapping[GeoModel]
      implicit0(clusterService: ClusterService) <- ORMService.clusterService
      records <- nextLongBetween(10, 20)
      toIndex <- ZIO.foreach(1.to(records.toInt).toList) { i =>
        for {
          lat <- nextDoubleBetween(-90.0, 90.0)
          lon <- nextDoubleBetween(-180.0, 180.0)
        } yield GeoModel(s"test$i", GeoPointLatLon(lat, lon))
      }
      bulker <- GeoModel.esHelper.bulkStream(
        ZStream.fromIterable(toIndex)
      )
      _ <- bulker.flushBulk()
      _ <- GeoModel.esHelper.refresh()
      count <- GeoModel.esHelper.count()
      qb: TypedQueryBuilder[GeoModel] <- ORMService.query(GeoModel)
      qbQuery = qb.sortByDistance("geoPoint", GeoPoint(0, 0), unit = "km")
      result <- qbQuery.results
    } yield assert(records)(equalTo(count)) &&
      assert(result.hits.hits.length)(equalTo(10)) &&
      assert(result.hits.hits.head.sort.length)(equalTo(1))
  }

}
