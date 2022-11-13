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

package zio.elasticsearch.result

import zio.elasticsearch.SpecHelper
import zio.elasticsearch.responses.SearchResult
import zio.elasticsearch.responses.aggregations.{ BucketAggregation, MetricValue, TopHitsStats }
import io.circe.Json.Obj
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AggregationResultSpec extends AnyFlatSpec with Matchers with SpecHelper {

  "Aggregation" should "deserialize bucket" in {

    val json =
      readResourceJSON("/elasticsearch/result/bucket_aggregation.json")
    val objectEither = json.as[BucketAggregation]
    objectEither.isRight should be(true)
    objectEither.value.isInstanceOf[BucketAggregation] should be(true)
    val result = objectEither.value
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
    val objectEither = json.as[SearchResult[Json.Obj]]
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
    val result = objectEither.value
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
