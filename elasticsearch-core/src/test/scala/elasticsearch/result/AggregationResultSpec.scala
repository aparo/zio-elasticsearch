/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.result

import elasticsearch.responses.SearchResult
import elasticsearch.responses.aggregations.{ BucketAggregation, MetricValue, TopHitsStats }
import io.circe.JsonObject
import elasticsearch.SpecHelper
import org.scalatest._
import org.scalatest.FlatSpec

class AggregationResultSpec extends FlatSpec with Matchers with SpecHelper {

  "Aggregation" should "deserialize bucket" in {

    val json = readResourceJSON("/elasticsearch/result/bucket_aggregation.json")
    val objectEither = json.as[BucketAggregation]
    objectEither.isRight should be(true)
    objectEither.right.get.isInstanceOf[BucketAggregation] should be(true)
    val result = objectEither.right.get
    result.buckets.size should be(5)
    val bkt = result.buckets.head
    bkt.keyToString should be("PN04872576P")
    bkt.docCount should be(591)
    bkt.subAggs.keySet.contains("vinto_totale") should be(true)
    val agg = bkt.subAggs("vinto_totale")
    agg.isInstanceOf[MetricValue] should be(true)
    agg.asInstanceOf[MetricValue].value should be(3097699.9961395264)

  }

  it should "deserialize more complex buckets" in {

    val json = readResourceJSON("/elasticsearch/result/sample001.json")
    val objectEither = json.as[SearchResult[JsonObject]]
    if (objectEither.isLeft)
      println(objectEither)
    objectEither.isRight should be(true)
  }

  it should "deserialize TopHits buckets" in {

    val json =
      readResourceJSON("/elasticsearch/result/topHits_aggregation.json")
    val objectEither = json.as[BucketAggregation]
    if (objectEither.isLeft)
      println(objectEither)
    objectEither.isRight should be(true)
    val result = objectEither.right.get
    result.buckets.size should be(3)
    val bkt = result.buckets.head
    bkt.keyToString should be("hat")
    bkt.docCount should be(3)
    bkt.subAggs.keySet.contains("top_sales_hits") should be(true)
    val agg = bkt.subAggs("top_sales_hits")
    agg.isInstanceOf[TopHitsStats] should be(true)
    agg.asInstanceOf[TopHitsStats].hits.total should be(3)
  }
}
