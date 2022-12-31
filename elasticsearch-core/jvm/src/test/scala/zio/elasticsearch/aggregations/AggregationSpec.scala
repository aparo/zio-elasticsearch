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

package zio.elasticsearch.aggregations

import _root_.zio.elasticsearch.geo.GeoPointLatLon
import _root_.zio.elasticsearch.queries.TermQuery
import zio.elasticsearch.aggregations.Aggregation._
import zio.elasticsearch.script.InlineScript
import zio.elasticsearch.sort.{ FieldSort, SortOrder, Sorter }
import zio.elasticsearch.{ DateInterval, SpecHelper }
import zio.json.ast._
import zio.json.ast._
import zio.json._
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AggregationSpec extends AnyFlatSpec with Matchers with SpecHelper with EitherValues {

  final case class Search(aggs: Aggregations)
  object Search {
    implicit val jsonDecoder: JsonDecoder[Search] = DeriveJsonDecoder.gen[Search]
    implicit val jsonEncoder: JsonEncoder[Search] = DeriveJsonEncoder.gen[Search]
  }

  "Aggregation" should "deserialize avg" in {

    val json = readResourceJSON("/zio/elasticsearch/aggregations/avg.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[AvgAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[AvgAggregation]
    myagg.field should be("grade")
  }

  it should "deserialize avg_script" in {

    val json = readResourceJSON("/zio/elasticsearch/aggregations/avg_script.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[AvgAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[AvgAggregation]
    myagg.field.isEmpty should be(true)
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc.grade.value")
  }

  it should "deserialize cardinality" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/cardinality.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[CardinalityAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[CardinalityAggregation]
    myagg.field should be("author")
  }

  it should "deserialize cardinality_precisionControl" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/cardinality_precisionControl.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[CardinalityAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[CardinalityAggregation]
    myagg.field should be("author_hash")
    myagg.precisionThreshold should be(100)
  }

  it should "deserialize cardinality_script1" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/cardinality_script1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[CardinalityAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[CardinalityAggregation]
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.lang should be("painless")
    myscript.source should be(
      "doc['author.first_name'].value + ' ' + doc['author.last_name'].value"
    )
  }

  it should "deserialize cardinality_missingValue" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/cardinality_missingValue.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[CardinalityAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[CardinalityAggregation]
    myagg.field should be("tag")
    myagg.missing.isDefined should be(true)
    myagg.missing.get.as[String].toOption.get should be("N/A")
  }

  it should "deserialize extendedStats" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/extendedStats.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[ExtendedStatsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[ExtendedStatsAggregation]
    myagg.field should be("grade")
  }

  it should "deserialize extendedStats_deviationBounds" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/extendedStats_deviationBounds.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[ExtendedStatsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[ExtendedStatsAggregation]
    myagg.field should be("grade")
  }

  it should "deserialize extendedStats_script1" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/extendedStats_script1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[ExtendedStatsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[ExtendedStatsAggregation]
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myagg.script.isDefined should be(true)
    myscript.source should be("doc['grade'].value")
    myscript.lang should be("painless")
  }

  it should "deserialize extendedStats_valueScript" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/extendedStats_valueScript.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[ExtendedStatsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[ExtendedStatsAggregation]
    myagg.field should be("grade")
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myagg.script.isDefined should be(true)
    myscript.lang should be("painless")
    myscript.source should be("_value * params.correction")
    myscript.params.fields.map(_._1).contains(1.2)
  }

  it should "deserialize extendedStats_missingValue" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/extendedStats_missingValue.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[ExtendedStatsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[ExtendedStatsAggregation]
    myagg.field should be("grade")
    myagg.missing.isDefined should be(true)
    myagg.missing.get.as[Int].toOption.get should be(0)
  }

  it should "deserialize max" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/max.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[MaxAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[MaxAggregation]
    myagg.field should be("price")
  }

  it should "deserialize max_script1" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/max_script1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[MaxAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[MaxAggregation]
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc.price.value")
  }

  it should "deserialize max_valueScript" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/max_valueScript.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[MaxAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[MaxAggregation]
    myagg.field should be("price")
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("_value * params.conversion_rate")
    val conversion_rate =
      myscript.getParam("conversion_rate").get.as[Double].toOption.get
    conversion_rate should be(1.2)
  }

  it should "deserialize max_missingValue" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/max_missingValue.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[MaxAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[MaxAggregation]
    myagg.field should be("grade")
    myagg.missing.isDefined should be(true)
    myagg.missing.get.as[Int].toOption.get should be(10)
  }

  it should "deserialize min" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/min.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[MinAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[MinAggregation]
    myagg.field should be("price")
  }

  it should "deserialize min_script1" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/min_script1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[MinAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[MinAggregation]
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc.price.value")
  }

  it should "deserialize min_valueScript" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/min_valueScript.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[MinAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[MinAggregation]
    myagg.field should be("price")
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("_value * params.conversion_rate")
    val conversion_rate =
      myscript.getParam("conversion_rate").get.as[Double].toOption.get
    conversion_rate should be(1.2)
  }

  it should "deserialize min_missingValue" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/min_missingValue.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[MinAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[MinAggregation]
    myagg.field should be("grade")
    myagg.missing.isDefined should be(true)
    myagg.missing.get.as[Int].toOption.get should be(10)
  }

  it should "deserialize percentiles" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/percentiles.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentilesAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[PercentilesAggregation]
    myagg.field should be("load_time")
  }

  it should "deserialize percentiles_keyedResponse" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/percentiles_keyedResponse.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentilesAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[PercentilesAggregation]
    myagg.field should be("balance")
    myagg.keyed should be(true)
  }

  it should "deserialize percentiles_script1" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/percentiles_script1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentilesAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[PercentilesAggregation]
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc['load_time'].value / params.timeUnit")
    myscript.lang should be("painless")
    val timeUnit = myscript.getParam("timeUnit").get.as[Int].toOption.get
    timeUnit should be(1000)
  }

  it should "deserialize percentiles_compression" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/percentiles_compression.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentilesAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[PercentilesAggregation]
    myagg.field should be("load_time")
    //miss compression attribute
  }

  it should "deserialize percentiles_HDR" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/percentiles_HDR.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentilesAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[PercentilesAggregation]
    myagg.field should be("load_time")
//    val precents = Array(95, 99, 99.9)
    //miss hdr attribute
  }

  it should "deserialize percentiles_missingValue" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/percentiles_missingValue.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentilesAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[PercentilesAggregation]
    myagg.field should be("grade")
    myagg.missing.isDefined should be(true)
    myagg.missing.get.as[Int].toOption.get should be(10)
  }

  it should "deserialize percentileRanks" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/percentileRanks.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentileRanksAggregation] should be(
      true
    )
    val myagg = aggregations.head._2.asInstanceOf[PercentileRanksAggregation]
    myagg.field should be("load_time")
    myagg.values should be(List(15.0, 30.0))
  }

  it should "deserialize percentileRanks_keyedResponse" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/percentileRanks_keyedResponse.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentileRanksAggregation] should be(
      true
    )
    val myagg = aggregations.head._2.asInstanceOf[PercentileRanksAggregation]
    myagg.field should be("balance")
    myagg.values should be(List(25000, 50000))
    myagg.keyed should be(true)
  }

  it should "deserialize percentileRanks_script1" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/percentileRanks_script1.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentileRanksAggregation] should be(
      true
    )
    val myagg = aggregations.head._2.asInstanceOf[PercentileRanksAggregation]
    myagg.values should be(List(3.0, 5.0))
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc['load_time'].value / params.timeUnit")
    myscript.lang should be("painless")
    val timeUnit = myscript.getParam("timeUnit").get.as[Int].toOption.get
    timeUnit should be(1000)
  }

  it should "deserialize percentileRanks_missingValue" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/percentileRanks_missingValue.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentileRanksAggregation] should be(
      true
    )
    val myagg = aggregations.head._2.asInstanceOf[PercentileRanksAggregation]
    myagg.field should be("grade")
    myagg.missing.isDefined should be(true)
    myagg.missing.get should be(Json.Num(10))
  }

  it should "deserialize percentileRanks_HDR" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/percentileRanks_HDR.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[PercentileRanksAggregation] should be(
      true
    )
    val myagg = aggregations.head._2.asInstanceOf[PercentileRanksAggregation]
    myagg.field should be("load_time")
    myagg.values should be(List(15, 30))
//    miss hdr attribute
  }

  it should "deserialize scripted metrics" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/scriptedMetric.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[ScriptedMetricAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[ScriptedMetricAggregation]
    myagg.mapScript should be(
      "params._agg.transactions.add(doc.type.value == 'sale' ? doc.amount.value : -1 * doc.amount.value)"
    )
    myagg.initScript should be(Some("params._agg.transactions = []"))
    myagg.combineScript should be(
      Some(
        "double profit = 0; for (t in params._agg.transactions) { profit += t } return profit"
      )
    )
    myagg.reduceScript should be(
      Some(
        "double profit = 0; for (a in params._aggs) { profit += a } return profit"
      )
    )

  }

  it should "deserialize stats" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/stats.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[StatsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[StatsAggregation]
    myagg.field should be("grade")
  }

  it should "deserialize stats_script1" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/stats_script1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[StatsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[StatsAggregation]
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc['grade'].value")
    myscript.lang should be("painless")
  }

  it should "deserialize stats_valueScript" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/stats_valueScript.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[StatsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[StatsAggregation]
    myagg.field should be("grade")
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("_value * params.correction")
    myscript.lang should be("painless")
    val correction = myscript.getParam("correction").get.as[Double].toOption.get
    correction should be(1.2)
  }

  it should "deserialize stats_missingValue" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/stats_missingValue.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[StatsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[StatsAggregation]
    myagg.field should be("grade")
    myagg.missing.isDefined should be(true)
    myagg.missing.get.as[Int].toOption.get should be(0)
  }

  it should "deserialize sum" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/sum.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[SumAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[SumAggregation]
    myagg.field should be("price")
    //miss query
  }

  it should "deserialize sum_script1" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/sum_script1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[SumAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[SumAggregation]
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc.price.value")
    //miss query
  }

  it should "deserialize sum_valueScript" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/sum_valueScript.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[SumAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[SumAggregation]
    myagg.field should be("price")
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("_value * _value")
    //miss query
  }

  it should "deserialize sum_missingValue" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/sum_missingValue.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[SumAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[SumAggregation]
    myagg.field should be("price")
    myagg.missing.isDefined should be(true)
    myagg.missing.get.as[Int].toOption.get should be(100)
    //miss query
  }

  it should "deserialize valueCount" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/valueCount.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[ValueCountAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[ValueCountAggregation]
    myagg.field should be("type")
  }

  it should "deserialize valueCount_script1" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/valueCount_script1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[ValueCountAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[ValueCountAggregation]
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc['type'].value")
  }

  it should "deserialize geoBounds" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/geoBounds.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[GeoBoundsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[GeoBoundsAggregation]
    myagg.field should be("location")
    myagg.wrapLongitude should be(true)
  }

  it should "deserialize geoCentroid" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/geoCentroid.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[GeoCentroidAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[GeoCentroidAggregation]
    myagg.field should be("location")
  }

  it should "deserialize terms" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/terms.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[TermsAggregation]
    myagg.field should be("genre")
  }
  it should "deserialize TopHits" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/topHits.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[TopHitsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[TopHitsAggregation]
    myagg.size should be(1)
    myagg.order should be(Some(List(Sorter("date", false))))
  }

  it should "deserialize terms_order1" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/terms_order1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[TermsAggregation]
    myagg.field should be("genre")
    myagg.order should be(Some(FieldSort("_count")))
  }

//  it should "serialize terms_order1" in {
//    println(TermsAggregation("date", order = Some(FieldSort("_key"))).toJson)
//  }

  it should "deserialize terms_minimumDocumentCount" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/terms_minimumDocumentCount.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[TermsAggregation]
    myagg.field should be("tags")
    myagg.minDocCount should be(10)
  }

  it should "deserialize terms_script1" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/terms_script1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[TermsAggregation]
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc['genre'].value")
    myscript.lang should be("painless")
  }

  it should "deserialize terms_valueScript" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/terms_valueScript.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[TermsAggregation]
    myagg.field should be("gender")
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("'Genre: ' +_value")
    myscript.lang should be("painless")
  }

  it should "deserialize terms_executionHint" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/terms_executionHint.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[TermsAggregation]
    myagg.field should be("tags")
    myagg.executionHint should be(Some(ExecutionHint.Map))
  }

  it should "deserialize terms_missingValue" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/terms_missingValue.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[TermsAggregation]
    myagg.field should be("tags")
    myagg.missing.isDefined should be(true)
    myagg.missing.get.as[String].toOption.get should be("N/A")
  }

  it should "deserialize missing" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/missing.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[MissingAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[MissingAggregation]
    myagg.field should be("price")
  }

  it should "deserialize dateHistogram" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/dateHistogram.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[DateHistogramAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[DateHistogramAggregation]
    myagg.field should be("date")
    myagg.interval should be(DateInterval("day"))
  }

  it should "deserialize dateHistogram_keys" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/dateHistogram_keys.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[DateHistogramAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[DateHistogramAggregation]
    myagg.field should be("date")
    myagg.interval should be(DateInterval("1M"))
    myagg.format should be(Some("yyyy-MM-dd"))
  }

  it should "deserialize dateHistogram_timeZone" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/dateHistogram_timeZone.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[DateHistogramAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[DateHistogramAggregation]
    myagg.field should be("date")
    myagg.interval should be(DateInterval("month"))
  }

  it should "deserialize dateHistogram_offset" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/dateHistogram_offset.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[DateHistogramAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[DateHistogramAggregation]
    myagg.field should be("date")
    myagg.interval should be(DateInterval("day"))
    myagg.offset should be(Some("+6h"))
  }

  it should "deserialize dateHistogram_keyedResponse" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/dateHistogram_keyedResponse.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[DateHistogramAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[DateHistogramAggregation]
    myagg.field should be("date")
    myagg.interval should be(DateInterval("1M"))
    myagg.format should be(Some("yyyy-MM-dd"))
    myagg.keyed should be(true)
  }

  it should "deserialize dateHistogram_missingValue" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/dateHistogram_missingValue.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[DateHistogramAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[DateHistogramAggregation]
    myagg.field should be("date")
    myagg.interval should be(DateInterval("year"))
    myagg.missing.isDefined should be(true)
    myagg.missing.get.as[String].toOption.get should be("2000/01/01")
  }

  it should "deserialize histogram" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/histogram.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[HistogramAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[HistogramAggregation]
    myagg.field should be("price")
    myagg.interval should be(50)
  }

  it should "deserialize histogram_minimumDocumentCount" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/histogram_minimumDocumentCount.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[HistogramAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[HistogramAggregation]
    myagg.field should be("price")
    myagg.interval should be(50)
    myagg.minDocCount should be(1)
  }

  it should "deserialize histogram_order" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/histogram_order.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[HistogramAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[HistogramAggregation]
    myagg.field should be("price")
    myagg.interval should be(50)
    myagg.order.isDefined should be(true)
    val order = myagg.order.get.asInstanceOf[FieldSort]
    order.order should be(SortOrder.Desc)
    order.field should be("_key")
  }

  it should "deserialize range" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/range.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[RangeAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[RangeAggregation]
    myagg.field should be("price")
    myagg.ranges.head.to should be(Some(Json.Num(50)))
    myagg.ranges(1).from should be(Some(Json.Num(50)))
    myagg.ranges(1).to should be(Some(Json.Num(100)))
    myagg.ranges(2).from should be(Some(Json.Num(100)))
  }

  it should "deserialize range_keyedResponse1" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/range_keyedResponse1.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[RangeAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[RangeAggregation]
    myagg.field should be("price")
    myagg.keyed should be(true)
    myagg.ranges.head.to should be(Some(Json.Num(50)))
    myagg.ranges(1).from should be(Some(Json.Num(50)))
    myagg.ranges(1).to should be(Some(Json.Num(100)))
    myagg.ranges(2).from should be(Some(Json.Num(100)))
  }

  it should "deserialize range_keyedResponse2" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/range_keyedResponse2.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[RangeAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[RangeAggregation]
    myagg.field should be("price")
    myagg.keyed should be(true)
    myagg.ranges.head.to should be(Some(Json.Num(50)))
    myagg.ranges(1).from should be(Some(Json.Num(50)))
    myagg.ranges(1).to should be(Some(Json.Num(100)))
    myagg.ranges(2).from should be(Some(Json.Num(100)))
    myagg.ranges.head.key should be(Some("cheap"))
    myagg.ranges(1).key should be(Some("average"))
    myagg.ranges(2).key should be(Some("expensive"))
  }

  it should "deserialize range_script" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/range_script.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[RangeAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[RangeAggregation]
    myagg.ranges.head.to should be(Some(Json.Num(50)))
    myagg.ranges(1).from should be(Some(Json.Num(50)))
    myagg.ranges(1).to should be(Some(Json.Num(100)))
    myagg.ranges(2).from should be(Some(Json.Num(100)))
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("doc['price'].value")
    myscript.lang should be("painless")
  }

  it should "deserialize range_valueScript" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/range_valueScript.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[RangeAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[RangeAggregation]
    myagg.field should be("price")
    myagg.ranges.head.to should be(Some(Json.Num(35)))
    myagg.ranges(1).from should be(Some(Json.Num(35)))
    myagg.ranges(1).to should be(Some(Json.Num(70)))
    myagg.ranges(2).from should be(Some(Json.Num(70)))
    myagg.script.isDefined should be(true)
    val myscript = myagg.script.get.asInstanceOf[InlineScript]
    myscript.source should be("_value * params.conversion_rate")
    myscript.lang should be("painless")
    val conversion_rate =
      myscript.getParam("conversion_rate").get.as[Double].toOption.get
    conversion_rate should be(0.8)
  }

  it should "deserialize range_subAggregations" in {
    val json1 =
      readResourceJSON("/zio/elasticsearch/aggregations/range_subAggregations.json")
    val searchEither = json1.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations1 = searchEither.value.aggs
    aggregations1.size should be(1)
    aggregations1.head._2.isInstanceOf[RangeAggregation] should be(true)
    val myagg1 = aggregations1.head._2.asInstanceOf[RangeAggregation]
    myagg1.field should be("price")
    myagg1.ranges.head.to should be(Some(Json.Num(50)))
    myagg1.ranges(1).from should be(Some(Json.Num(50)))
    myagg1.ranges(1).to should be(Some(Json.Num(100)))
    myagg1.ranges(2).from should be(Some(Json.Num(100)))
    val aggregations2 = myagg1.aggregations
    aggregations2.size should be(1)
    aggregations2.head._2.isInstanceOf[StatsAggregation] should be(true)
    val myagg2 = aggregations2.head._2.asInstanceOf[StatsAggregation]
    myagg2.field should be("price")
  }

  it should "deserialize dateRange" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/dateRange.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[DateRangeAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[DateRangeAggregation]
    myagg.field should be("date")
    myagg.format.isDefined should be(true)
    myagg.format should be(Some("MM-yyy"))
    myagg.ranges.head.to should be(Some(Json.Str("now-10M/M")))
    myagg.ranges(1).from should be(Some(Json.Str("now-10M/M")))
  }

  it should "deserialize dateRange_keyedResponse" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/dateRange_keyedResponse.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[DateRangeAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[DateRangeAggregation]
    myagg.field should be("date")
    myagg.format.isDefined should be(true)
    myagg.format should be(Some("MM-yyy"))
    myagg.ranges.head.to should be(Some(Json.Str("now-10M/M")))
    myagg.ranges(1).from should be(Some(Json.Str("now-10M/M")))
    myagg.keyed should be(true)
  }

  it should "deserialize filter" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/filter.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[FilterAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[FilterAggregation]
    myagg.filter.isInstanceOf[TermQuery]
    val myFilter = myagg.filter.asInstanceOf[TermQuery]
    myFilter.field should be("type")
    myFilter.value should be(Json.Str("t-shirt"))
    val sub_aggregations = myagg.aggregations
    sub_aggregations.size should be(1)
    sub_aggregations.head._2.isInstanceOf[AvgAggregation]
    val myagg2 = sub_aggregations.head._2.asInstanceOf[AvgAggregation]
    myagg2.field should be("price")
  }

  it should "deserialize geoDistance" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/geoDistance.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[GeoDistanceAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[GeoDistanceAggregation]
    myagg.field should be("location")
    val origin = myagg.origin.asInstanceOf[GeoPointLatLon]
    origin.lat should be(52.3760)
    origin.lon should be(4.894)
//    myagg.origin.toString should be("52.3760, 4.894")
    myagg.ranges.head.to should be(Some(Json.Num(100000)))
    myagg.ranges(1).from should be(Some(Json.Num(100000)))
    myagg.ranges(1).to should be(Some(Json.Num(300000)))
    myagg.ranges(2).from should be(Some(Json.Num(300000)))
  }

  it should "deserialize geoDistance_keyedResponse" in {
    val json = readResourceJSON(
      "/zio/elasticsearch/aggregations/geoDistance_keyedResponse.json"
    )
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[GeoDistanceAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[GeoDistanceAggregation]
    myagg.field should be("location")
    val origin = myagg.origin.asInstanceOf[GeoPointLatLon]
    origin.lat should be(52.3760)
    origin.lon should be(4.894)

    //myagg.origin.toString should be("52.3760, 4.894")
    myagg.ranges.head.to should be(Some(Json.Num(100000)))
    myagg.ranges(1).from should be(Some(Json.Num(100000)))
    myagg.ranges(1).to should be(Some(Json.Num(300000)))
    myagg.ranges(2).from should be(Some(Json.Num(300000)))
    myagg.keyed should be(true)
  }

  it should "deserialize geoHashGrid" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/geoHashGrid.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[GeoHashGridAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[GeoHashGridAggregation]
    myagg.field should be("location")
    myagg.precision should be(3)
  }

  it should "deserialize ipRange" in {
    val json = readResourceJSON("/zio/elasticsearch/aggregations/ipRange.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[IPRangeAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[IPRangeAggregation]
    myagg.field should be("ip")
    myagg.ranges.head.to should be(Some(Json.Str("10.0.0.5")))
    myagg.ranges(1).from should be(Some(Json.Str("10.0.0.5")))
  }

  it should "deserialize ipRange_keyedResponse" in {
    val json =
      readResourceJSON("/zio/elasticsearch/aggregations/ipRange_keyedResponse.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[IPRangeAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[IPRangeAggregation]
    myagg.field should be("remote_ip")
    myagg.ranges.head.to should be(Some(Json.Str("10.0.0.5")))
    myagg.ranges(1).from should be(Some(Json.Str("10.0.0.5")))
    myagg.keyed should be(true)
  }

//  it should "deserialize diversifiedSampler" in {
//    val json = readResourceJSON("/zio/elasticsearch/aggregations/diversifiedSampler.json")
//    val searchEither = json.as[Search]
//    oQuery.value.isInstanceOf[QueryStringQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[QueryStringQuery]
//    val nJson = oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//    searchEither.isRight should be(true)
//    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
//    val aggregations = searchEither.value.aggs
//    aggregations.size should be(1)
//    aggregations.head._2.isInstanceOf[IPV4RangeAggregation] should be(true)
//    val myagg = aggregations.head._2.asInstanceOf[Divers]
//    myagg.field should be("remote_ip")
//    myagg.ranges.head.to should be(Json.Str("10.0.0.5"))
//    myagg.ranges(1).from should be(Json.Str("10.0.0.5"))
//    myagg.keyed should be(true)
//  }

  it should "deserialize nested" in {
    //missing query
    val json = readResourceJSON("/zio/elasticsearch/aggregations/nested.json")
    val searchEither = json.as[Search]
    searchEither.isRight should be(true)
    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
    val aggregations = searchEither.value.aggs
    aggregations.size should be(1)
    aggregations.head._2.isInstanceOf[NestedAggregation] should be(true)
    val myagg = aggregations.head._2.asInstanceOf[NestedAggregation]
    myagg.path should be("resellers")
    val sub_aggregations = myagg.aggregations
    sub_aggregations.size should be(1)
    sub_aggregations.head._2.isInstanceOf[MinAggregation] should be(true)
    val myagg2 = sub_aggregations.head._2.asInstanceOf[MinAggregation]
    myagg2.field should be("resellers.price")
  }

  //TODO

//  it should "deserialize reverseNested" in {
//    //missing query
//    val json = readResourceJSON("/zio/elasticsearch/aggregations/reverseNested.json")
//    val searchEither = json.as[Search]
//    searchEither.isRight should be(true)
//    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
//    val aggregations = searchEither.value.aggs
//    aggregations.size should be(1)
//    aggregations.head._2.isInstanceOf[NestedAggregation] should be(true)
//    val myagg = aggregations.head._2.asInstanceOf[NestedAggregation]
//    myagg.path should be("comments")
//    val sub_aggregations = myagg.aggregations
//    sub_aggregations.size should be(1)
//    sub_aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
//    val myagg2 = sub_aggregations.head._2.asInstanceOf[TermsAggregation]
//    myagg2.field should be("comment.username")
//    val sub_sub_aggregations = myagg2.aggregations
//    sub_sub_aggregations.size should be(1)
//    sub_sub_aggregations.head._2.isInstanceOf[ReverseNestedAggregation]
//    val myagg3 = sub_sub_aggregations.head._2.asInstanceOf[ReverseNestedAggregation]
//    val sub_sub_sub_aggregations = myagg3.aggreagtions
//    sub_sub_sub_aggregations.size should be(1)
//    sub_sub_aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
//    val myagg4 = sub_sub_aggregations.head._2.asInstanceOf[TermsAggregation]
//    myagg4.field should be("tags")
//  }
//
//  it should "deserialize significantTerms" in {
//    //missing query
//    val json = readResourceJSON("/zio/elasticsearch/aggregations/significantTerms.json")
//    val searchEither = json.as[Search]
//    searchEither.isRight should be(true)
//    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
//    val aggregations = searchEither.value.aggs
//    aggregations.size should be(1)
//    aggregations.head._2.isInstanceOf[SignificantTermsAggregation] should be(true)
//    val myagg = aggregations.head._2.asInstanceOf[SignificantTermsAggregation]
//    myagg.field should be("crime_type")
//  }
//
//  it should "deserialize significantTerms_multiSetAnalysis" in {
//    //missing query
//    val json = readResourceJSON("/zio/elasticsearch/aggregations/significantTerms_multiTestAnalysis.json")
//    val searchEither = json.as[Search]
//    searchEither.isRight should be(true)
//    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
//    val aggregations = searchEither.value.aggs
//    aggregations.size should be(1)
//    aggregations.head._2.isInstanceOf[TermsAggregation] should be(true)
//    val myagg = aggregations.head._2.asInstanceOf[TermsAggregation]
//    myagg.field should be("force")
//    val sub_aggregations = myagg.aggregations
//    sub_aggregations.size should be(1)
//    sub_aggregations.head._2.isInstanceOf[SignificantTermsAggregation]
//    val myagg2 = sub_aggregations.head._2.asInstanceOf[SignificantTermsAggregation]
//    myagg2.field should be("crime_type")
//  }
//
//  it should "deserialize significantTerms_multiSetAnalysis2" in {
//    //missing query
//    val json = readResourceJSON("/zio/elasticsearch/aggregations/significantTerms_multiTestAnalysis2.json")
//    val searchEither = json.as[Search]
//    searchEither.isRight should be(true)
//    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
//    val aggregations = searchEither.value.aggs
//    aggregations.size should be(1)
//    aggregations.head._2.isInstanceOf[GeoHashGridAggregation] should be(true)
//    val myagg = aggregations.head._2.asInstanceOf[GeoHashGridAggregation]
//    myagg.field should be("location")
//    myagg.precision should be(5)
//    val sub_aggregations = myagg.aggregations
//    sub_aggregations.size should be(1)
//    sub_aggregations.head._2.isInstanceOf[SignificantTermsAggregation]
//    val myagg2 = sub_aggregations.head._2.asInstanceOf[SignificantTermsAggregation]
//    myagg2.field should be("crime_type")
//  }
//
//  it should "deserialize significantTerms_minimumDocumentCount" in {
//    //missing query
//    val json = readResourceJSON("/zio/elasticsearch/aggregations/significantTerms_minimumDocumentCount.json")
//    val searchEither = json.as[Search]
//    searchEither.isRight should be(true)
//    searchEither.value.aggs.isInstanceOf[Aggregations] should be(true)
//    val aggregations = searchEither.value.aggs
//    aggregations.size should be(1)
//    aggregations.head._2.isInstanceOf[SignificantTermsAggregation] should be(true)
//    val myagg = aggregations.head._2.asInstanceOf[SignificantTermsAggregation]
//    myagg.field should be("tags")
//    val execution_int: String = "map"
//  }
}
