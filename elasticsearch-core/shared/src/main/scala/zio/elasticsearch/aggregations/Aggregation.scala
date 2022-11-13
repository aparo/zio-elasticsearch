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

import scala.collection.mutable.ListBuffer

import zio.elasticsearch.geo.{ DistanceType, DistanceUnit, GeoPoint }
import zio.elasticsearch.queries.Query
import zio.elasticsearch.script.Script
import zio.elasticsearch.sort.Sort._
import zio.elasticsearch.sort.{ FieldSort, Sorter }
import zio.elasticsearch.{ DateInterval, Regex }
import zio.json._
import zio.json._
import zio.json._

sealed trait Aggregation {

  def meta: Option[Json]

  def NAME: String

  def aggregations: Aggregation.Aggregations

}

object Aggregation {
  type Aggregations = Map[String, Aggregation]
  lazy val EmptyAggregations = Map.empty[String, Aggregation]

  val registered: Map[String, AggregationType[_]] = Map(
    AdjacencyMatrixAggregation.NAME -> AdjacencyMatrixAggregation,
    AvgAggregation.NAME -> AvgAggregation,
    CardinalityAggregation.NAME -> CardinalityAggregation,
    DateHistogramAggregation.NAME -> DateHistogramAggregation,
    DateRangeAggregation.NAME -> DateRangeAggregation,
    ExtendedStatsAggregation.NAME -> ExtendedStatsAggregation,
    FilterAggregation.NAME -> FilterAggregation,
    FiltersAggregation.NAME -> FiltersAggregation,
    GeoBoundsAggregation.NAME -> GeoBoundsAggregation,
    GeoCentroidAggregation.NAME -> GeoCentroidAggregation,
    GeoDistanceAggregation.NAME -> GeoDistanceAggregation,
    GeoHashGridAggregation.NAME -> GeoHashGridAggregation,
    GlobalAggregation.NAME -> GlobalAggregation,
    HistogramAggregation.NAME -> HistogramAggregation,
    IPV4RangeAggregation.NAME -> IPV4RangeAggregation,
    MaxAggregation.NAME -> MaxAggregation,
    MinAggregation.NAME -> MinAggregation,
    MissingAggregation.NAME -> MissingAggregation,
    NestedAggregation.NAME -> NestedAggregation,
    PercentilesAggregation.NAME -> PercentilesAggregation,
    PercentileRanksAggregation.NAME -> PercentileRanksAggregation,
    RangeAggregation.NAME -> RangeAggregation,
    ScriptedMetricAggregation.NAME -> ScriptedMetricAggregation,
    StatsAggregation.NAME -> StatsAggregation,
    SumAggregation.NAME -> SumAggregation,
    TermsAggregation.NAME -> TermsAggregation,
    TopHitsAggregation.NAME -> TopHitsAggregation,
    ValueCountAggregation.NAME -> ValueCountAggregation
  )

  private val specialKeys = List("meta", "aggs", "aggregations")

  implicit final val decodeAggregation: JsonDecoder[Aggregation] =
    JsonDecoder.instance { c =>
      val keys = c.keys.get.toList
      keys.diff(specialKeys).headOption match {
        case None => Left(DecodingFailure("Invalid Aggregation Format", Nil))
        case Some(aggregationType) =>
          if (registered.contains(aggregationType)) {
            val meta = c.downField("meta").as[Json].toOption
            val aggregations: Aggregations =
              if (keys.contains("aggs")) {
                c.downField("aggs").as[Aggregations].toOption.getOrElse(EmptyAggregations)
              } else if (keys.contains("aggregations")) {
                c.downField("aggregations").as[Aggregations].toOption.getOrElse(EmptyAggregations)
              } else {
                EmptyAggregations
              }

            Right(
              registered(aggregationType)
                .parse(
                  c.downField(aggregationType).focus.get,
                  meta = meta,
                  aggregations = aggregations
                )
                .asInstanceOf[Aggregation]
            )
          } else {
            Left(
              DecodingFailure(
                s"AggregationType '$aggregationType' not supported",
                Nil
              )
            )
          }
      }
    }

  implicit final val encodeAggregation: JsonEncoder[Aggregation] =
    JsonEncoder.instance {
      case o: AdjacencyMatrixAggregation =>
        addSubAggregations(Json.Obj(AdjacencyMatrixAggregation.NAME -> o.asJson), o.aggregations)
      case o: AvgAggregation =>
        addSubAggregations(Json.Obj(AvgAggregation.NAME -> o.asJson), o.aggregations)
      case o: CardinalityAggregation =>
        addSubAggregations(Json.Obj(CardinalityAggregation.NAME -> o.asJson), o.aggregations)
      case o: DateHistogramAggregation =>
        addSubAggregations(Json.Obj(DateHistogramAggregation.NAME -> o.asJson), o.aggregations)
      case o: DateRangeAggregation =>
        addSubAggregations(Json.Obj(DateRangeAggregation.NAME -> o.asJson), o.aggregations)
      case o: ExtendedStatsAggregation =>
        addSubAggregations(Json.Obj(ExtendedStatsAggregation.NAME -> o.asJson), o.aggregations)
      case o: FilterAggregation =>
        addSubAggregations(Json.Obj(FilterAggregation.NAME -> o.asJson), o.aggregations)
      case o: FiltersAggregation =>
        addSubAggregations(Json.Obj(FiltersAggregation.NAME -> o.asJson), o.aggregations)
      case o: GeoBoundsAggregation =>
        addSubAggregations(Json.Obj(GeoBoundsAggregation.NAME -> o.asJson), o.aggregations)
      case o: GeoCentroidAggregation =>
        addSubAggregations(Json.Obj(GeoCentroidAggregation.NAME -> o.asJson), o.aggregations)
      case o: GeoDistanceAggregation =>
        addSubAggregations(Json.Obj(GeoDistanceAggregation.NAME -> o.asJson), o.aggregations)
      case o: GeoHashGridAggregation =>
        addSubAggregations(Json.Obj(GeoHashGridAggregation.NAME -> o.asJson), o.aggregations)
      case o: GlobalAggregation =>
        addSubAggregations(Json.Obj(GlobalAggregation.NAME -> o.asJson), o.aggregations)
      case o: HistogramAggregation =>
        addSubAggregations(Json.Obj(HistogramAggregation.NAME -> o.asJson), o.aggregations)
      case o: IPV4RangeAggregation =>
        addSubAggregations(Json.Obj(IPV4RangeAggregation.NAME -> o.asJson), o.aggregations)
      case o: MaxAggregation =>
        addSubAggregations(Json.Obj(MaxAggregation.NAME -> o.asJson), o.aggregations)
      case o: MinAggregation =>
        addSubAggregations(Json.Obj(MinAggregation.NAME -> o.asJson), o.aggregations)
      case o: MissingAggregation =>
        addSubAggregations(Json.Obj(MissingAggregation.NAME -> o.asJson), o.aggregations)
      case o: NestedAggregation =>
        addSubAggregations(Json.Obj(NestedAggregation.NAME -> o.asJson), o.aggregations)
      case o: PercentilesAggregation =>
        addSubAggregations(Json.Obj(PercentilesAggregation.NAME -> o.asJson), o.aggregations)
      case o: PercentileRanksAggregation =>
        addSubAggregations(Json.Obj(PercentileRanksAggregation.NAME -> o.asJson), o.aggregations)
      case o: RangeAggregation =>
        addSubAggregations(Json.Obj(RangeAggregation.NAME -> o.asJson), o.aggregations)
      case o: ScriptedMetricAggregation =>
        addSubAggregations(Json.Obj(ScriptedMetricAggregation.NAME -> o.asJson), o.aggregations)
      case o: StatsAggregation =>
        addSubAggregations(Json.Obj(StatsAggregation.NAME -> o.asJson), o.aggregations)
      case o: SumAggregation =>
        addSubAggregations(Json.Obj(SumAggregation.NAME -> o.asJson), o.aggregations)
      case o: TermsAggregation =>
        addSubAggregations(Json.Obj(TermsAggregation.NAME -> o.asJson), o.aggregations)
      case o: ValueCountAggregation =>
        addSubAggregations(Json.Obj(ValueCountAggregation.NAME -> o.asJson), o.aggregations)
      case o: TopHitsAggregation =>
        addSubAggregations(Json.Obj(TopHitsAggregation.NAME -> o.asJson), o.aggregations)

      //the next lines are to make the compiler happy
      case _: ScriptableAggregation => Json.Null
      case _: SubAggregation        => Json.Null
    }

  def addSubAggregations(json: Json, aggregations: Aggregations): Json =
    if (aggregations.isEmpty) {
      json
    } else {
      val aggs = aggregations.map { agg =>
        agg._1 -> agg._2.asJson
      }
      json.asObject.get.add("aggs", Json.Obj(aggs.toSeq: _*)).asJson
    }

}

sealed trait AggregationType[T] {
  def NAME: String

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): T

}

sealed trait BucketAggregation extends SubAggregation

sealed trait NoBucketAggregation extends Aggregation {
  def aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
}

final case class AdjacencyMatrixAggregation(
  filters: Map[String, Query],
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
  val NAME = AdjacencyMatrixAggregation.NAME
}

object AdjacencyMatrixAggregation extends AggregationType[AdjacencyMatrixAggregation] {
  val NAME = "adjacency_matrix"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): AdjacencyMatrixAggregation = {
    val hc = json.hcursor
    new AdjacencyMatrixAggregation(
      filters = hc.downField("filters").as[Map[String, Query]].toOption.get,
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[AdjacencyMatrixAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      fields += ("filters" -> obj.filters.asJson)
      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Aggregation.addSubAggregations(Json.fromFields(fields), obj.aggregations)
      Json.fromFields(fields)
    }
}

final case class AvgAggregation(
  field: String = "",
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME: String = AvgAggregation.NAME

}

object AvgAggregation extends AggregationType[AvgAggregation] {
  val NAME: String = "avg"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): AvgAggregation = {
    val hc = json.hcursor
    new AvgAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[AvgAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty) {
        fields += ("field" -> obj.field.asJson)
      }
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }

}

final case class CardinalityAggregation(
  field: String = "",
  precisionThreshold: Int = 3000, //Maxium value 40000
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME = CardinalityAggregation.NAME

}

object CardinalityAggregation extends AggregationType[CardinalityAggregation] {
  val NAME = "cardinality"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): CardinalityAggregation = {
    val hc = json.hcursor
    new CardinalityAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      precisionThreshold = hc.downField("precision_threshold").as[Int].toOption.getOrElse(3000),
      missing = hc.downField("missing").as[Json].toOption,
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[CardinalityAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      if (obj.precisionThreshold != 3000)
        fields += ("precision_threshold" -> obj.precisionThreshold.asJson)
      obj.missing.map(v => fields += ("missing" -> v))
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}

final case class ExtendedBounds(min: String, max: String)
object ExtendedBounds {
  implicit val jsonDecoder: JsonDecoder[ExtendedBounds] = DeriveJsonDecoder.gen[ExtendedBounds]
  implicit val jsonEncoder: JsonEncoder[ExtendedBounds] = DeriveJsonEncoder.gen[ExtendedBounds]
}

final case class DateHistogramAggregation(
  field: String = "",
  missing: Option[Json] = None,
  interval: DateInterval = DateInterval("day"),
  offset: Option[String] = None,
  timeZone: Option[String] = None,
  script: Option[Script] = None,
  size: Int = -1,
  shardSize: Int = -1,
  order: Option[Sorter] = None,
  keyed: Boolean = false,
  format: Option[String] = None,
  minDocCount: Int = 1,
  include: Option[Regex] = None,
  exclude: Option[Regex] = None,
  executionHint: Option[ExecutionHint] = None,
  extendedBounds: Option[ExtendedBounds] = None,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
  val NAME = DateHistogramAggregation.NAME

}

object DateHistogramAggregation extends AggregationType[DateHistogramAggregation] {
  val NAME = "date_histogram"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): DateHistogramAggregation = {
    val hc = json.hcursor
    new DateHistogramAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      missing = hc.downField("missing").as[Json].toOption,
      interval = DateInterval(
        hc.downField("interval").as[String].toOption.getOrElse("")
      ),
      offset = hc.downField("offset").as[String].toOption,
      timeZone = hc.downField("time_zone").as[String].toOption,
      script = hc.downField("script").as[Script].toOption,
      size = hc.downField("size").as[Int].toOption.getOrElse(-1),
      shardSize = hc.downField("shard_size").as[Int].toOption.getOrElse(-1),
      order = hc.downField("order").as[Sorter].toOption,
      keyed = hc.downField("keyed").as[Boolean].toOption.getOrElse(false),
      format = hc.downField("format").as[String].toOption,
      minDocCount = hc.downField("min_doc_count").as[Int].toOption.getOrElse(1),
      include = hc.downField("include").as[Regex].toOption,
      exclude = hc.downField("exclude").as[Regex].toOption,
      executionHint = hc.downField("execution_hint").as[ExecutionHint].toOption,
      extendedBounds = hc.downField("extended_bounds").as[ExtendedBounds].toOption,
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[DateHistogramAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      fields += ("keyed" -> obj.keyed.asJson)
      fields += ("interval" -> obj.interval.asJson)
      obj.script.foreach(v => fields += ("script" -> v.asJson))
      obj.timeZone.foreach(v => fields += ("time_zone" -> v.asJson))
      if (obj.size != -1)
        fields += ("size" -> obj.size.asJson)
      if (obj.shardSize != -1 && obj.shardSize >= obj.size)
        fields += ("shard_size" -> obj.shardSize.asJson)
      obj.order.foreach(v => fields += ("order" -> v.asJson))
      obj.format.foreach(v => fields += ("format" -> v.asJson))
      if (obj.minDocCount > 0)
        fields += ("min_doc_count" -> obj.minDocCount.asJson)
      obj.missing.map(v => fields += ("missing" -> v))
      obj.include.foreach(v => fields += ("include" -> v.asJson))
      obj.exclude.foreach(v => fields += ("exclude" -> v.asJson))
      obj.executionHint.foreach(v => fields += ("execution_hint" -> v.asJson))
      obj.extendedBounds.foreach(v => fields += ("extended_bounds" -> v.asJson))
      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Aggregation.addSubAggregations(Json.fromFields(fields), obj.aggregations)
      Json.fromFields(fields)
    }

}

/* for format give a loot to http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html */
final case class DateRangeAggregation(
  field: String = "",
  script: Option[Script] = None,
  keyed: Boolean = false,
  format: Option[String] = None,
  ranges: List[RangeValue] = Nil,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
  val NAME = DateRangeAggregation.NAME

  def addRange(from: String, to: String): DateRangeAggregation =
    this.copy(
      ranges = ranges :+ RangeValue(
        from = Some(Json.Str(from)),
        to = Some(Json.Str(to))
      )
    )

  def addRange(key: String, from: String, to: String): DateRangeAggregation =
    this.copy(
      ranges = ranges :+ RangeValue(
        key = Some(key),
        from = Some(Json.Str(from)),
        to = Some(Json.Str(to))
      )
    )

  def addRange(from: Long, to: Long): DateRangeAggregation =
    this.copy(
      ranges = ranges :+ RangeValue(
        from = Some(Json.Num(from)),
        to = Some(Json.Num(to))
      )
    )

  def addRange(key: String, from: Long, to: Long): DateRangeAggregation =
    this.copy(
      ranges = ranges :+ RangeValue(
        key = Some(key),
        from = Some(Json.Num(from)),
        to = Some(Json.Num(to))
      )
    )

}

object DateRangeAggregation extends AggregationType[DateRangeAggregation] {
  val NAME = "date_range"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): DateRangeAggregation = {
    val hc = json.hcursor
    new DateRangeAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      script = hc.downField("script").as[Script].toOption,
      keyed = hc.downField("keyed").as[Boolean].toOption.getOrElse(false),
      format = hc.downField("format").as[String].toOption,
      ranges = hc.downField("ranges").as[List[RangeValue]].toOption.getOrElse(List.empty[RangeValue]),
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[DateRangeAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      fields += ("keyed" -> obj.keyed.asJson)
      obj.script.foreach(v => fields += ("script" -> v.asJson))
      obj.format.foreach(v => fields += ("format" -> v.asJson))
      fields += ("ranges" -> obj.ranges.asJson)

      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Aggregation.addSubAggregations(Json.fromFields(fields), obj.aggregations)
      Json.fromFields(fields)
    }
}

final case class ExtendedStatsAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME = ExtendedStatsAggregation.NAME
}

object ExtendedStatsAggregation extends AggregationType[ExtendedStatsAggregation] {
  val NAME = "extended_stats"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): ExtendedStatsAggregation = {
    val hc = json.hcursor
    new ExtendedStatsAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      missing = hc.downField("missing").as[Json].toOption,
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[ExtendedStatsAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.missing.map(v => fields += ("missing" -> v))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}

final case class FilterAggregation(
  filter: Query,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
  val NAME = FilterAggregation.NAME
}

object FilterAggregation extends AggregationType[FilterAggregation] {
  val NAME = "filter"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): FilterAggregation = {
    val hc = json.hcursor
    val filter = hc.as[Query]
    new FilterAggregation(
      filter = filter.toOption.get,
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[FilterAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      fields += ("filter" -> obj.filter.asJson)

      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Aggregation.addSubAggregations(Json.fromFields(fields), obj.aggregations)
      Json.fromFields(fields)
    }
}

final case class FiltersAggregation(
  filters: Map[String, Query],
  otherBucketKey: Option[String] = None,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
  val NAME = FiltersAggregation.NAME
}

object FiltersAggregation extends AggregationType[FiltersAggregation] {
  val NAME = "filters"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): FiltersAggregation = {
    val hc = json.hcursor
    new FiltersAggregation(
      filters = hc.downField("filters").as[Map[String, Query]].toOption.get,
      otherBucketKey = hc.downField("other_bucket_key").as[String].toOption,
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[FiltersAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      fields += ("filters" -> obj.filters.asJson)
      obj.otherBucketKey.map(v => fields += ("other_bucket_key" -> v.asJson))
      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Aggregation.addSubAggregations(Json.fromFields(fields), obj.aggregations)
      Json.fromFields(fields)
    }
}

final case class GeoBoundsAggregation(
  field: String,
  wrapLongitude: Boolean = true,
  meta: Option[Json] = None
) extends NoBucketAggregation {
  val NAME = GeoBoundsAggregation.NAME

}

object GeoBoundsAggregation extends AggregationType[GeoBoundsAggregation] {
  val NAME = "geo_bounds"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): GeoBoundsAggregation = {
    val hc = json.hcursor
    new GeoBoundsAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      wrapLongitude = hc.downField("wrap_longitude").as[Boolean].toOption.getOrElse(true),
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[GeoBoundsAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      fields += ("field" -> obj.field.asJson)
      fields += ("wrap_longitude" -> obj.wrapLongitude.asJson)
      Json.fromFields(fields)
    }

}

final case class GeoCentroidAggregation(
  field: String,
  meta: Option[Json] = None
) extends NoBucketAggregation {
  val NAME = GeoCentroidAggregation.NAME

}

object GeoCentroidAggregation extends AggregationType[GeoCentroidAggregation] {
  val NAME = "geo_centroid"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): GeoCentroidAggregation = {
    val hc = json.hcursor
    new GeoCentroidAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[GeoCentroidAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      fields += ("field" -> obj.field.asJson)
      Json.fromFields(fields)
    }

}

final case class GeoDistanceAggregation(
  field: String,
  origin: GeoPoint,
  ranges: List[RangeValue] = Nil,
  keyed: Boolean = false,
  valueField: Option[String] = None,
  distanceUnit: Option[DistanceUnit] = None,
  distanceType: Option[DistanceType] = None,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
  val NAME = GeoDistanceAggregation.NAME

}

object GeoDistanceAggregation extends AggregationType[GeoDistanceAggregation] {
  val NAME = "geo_distance"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): GeoDistanceAggregation = {
    val hc = json.hcursor
    new GeoDistanceAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      keyed = hc.downField("keyed").as[Boolean].toOption.getOrElse(false),
      origin = hc.downField("origin").as[GeoPoint].toOption.getOrElse(GeoPoint(0, 0)),
      valueField = hc.downField("value_field").as[String].toOption,
      ranges = hc.downField("ranges").as[List[RangeValue]].toOption.getOrElse(List.empty[RangeValue]),
      distanceUnit = hc.downField("distance_unit").as[DistanceUnit].toOption,
      distanceType = hc.downField("distance_type").as[DistanceType].toOption,
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[GeoDistanceAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      fields += ("origin" -> obj.origin.asJson)
      fields += ("keyed" -> obj.keyed.asJson)
      obj.valueField.foreach(v => fields += ("value_field" -> v.asJson))
      fields += ("ranges" -> obj.ranges.asJson)
      obj.distanceUnit.foreach(v => fields += ("distance_unit" -> v.asJson))
      obj.distanceType.foreach(v => fields += ("distance_type" -> v.asJson))

      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Aggregation.addSubAggregations(Json.fromFields(fields), obj.aggregations)
      Json.fromFields(fields)
    }

}

final case class GeoHashGridAggregation(
  field: String,
  precision: Int = 3,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
  val NAME = GeoHashGridAggregation.NAME

}

object GeoHashGridAggregation extends AggregationType[GeoHashGridAggregation] {
  val NAME = "geohash_grid"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): GeoHashGridAggregation = {
    val hc = json.hcursor
    new GeoHashGridAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      precision = hc.downField("precision").as[Int].toOption.getOrElse(3),
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[GeoHashGridAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      fields += ("precision" -> obj.precision.asJson)

      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Aggregation.addSubAggregations(Json.fromFields(fields), obj.aggregations)
      Json.fromFields(fields)
    }
}

final case class GlobalAggregation(
  aggregations: Aggregation.Aggregations,
  meta: Option[Json] = None
) extends Aggregation {
  val NAME = GlobalAggregation.NAME
}

object GlobalAggregation extends AggregationType[GlobalAggregation] {
  val NAME = "global"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): GlobalAggregation =
    new GlobalAggregation(
      aggregations = aggregations,
      meta = meta
    )

  implicit final val encodeAggregation: JsonEncoder[GlobalAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      fields += ("global" -> Json.Obj())

      obj.meta.map(v => fields += ("meta" -> v.asJson))
      Json.fromFields(fields)
    }
}

final case class HistogramAggregation(
  field: String = "",
  interval: Long = 10,
  script: Option[Script] = None,
  size: Int = -1,
  shardSize: Int = -1,
  order: Option[Sorter] = None,
  keyed: Boolean = false,
  minDocCount: Int = 1,
  include: Option[Regex] = None,
  exclude: Option[Regex] = None,
  executionHint: Option[ExecutionHint] = None,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
  val NAME = HistogramAggregation.NAME

}

object HistogramAggregation extends AggregationType[HistogramAggregation] {
  val NAME = "histogram"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): HistogramAggregation = {
    val hc = json.hcursor
    new HistogramAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      interval = hc.downField("interval").as[Long].toOption.getOrElse(10),
      script = hc.downField("script").as[Script].toOption,
      size = hc.downField("size").as[Int].toOption.getOrElse(-1),
      shardSize = hc.downField("shard_size").as[Int].toOption.getOrElse(-1),
      order = hc.downField("order").as[Sorter].toOption,
      keyed = hc.downField("keyed").as[Boolean].toOption.getOrElse(false),
      minDocCount = hc.downField("min_doc_count").as[Int].toOption.getOrElse(1),
      include = hc.downField("include").as[Regex].toOption,
      exclude = hc.downField("exclude").as[Regex].toOption,
      executionHint = hc.downField("execution_hint").as[ExecutionHint].toOption,
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[HistogramAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty) {
        fields += ("field" -> obj.field.asJson)
      }
      fields += ("keyed" -> obj.keyed.asJson)
      fields += ("interval" -> obj.interval.asJson)
      obj.script.foreach(v => fields += ("script" -> v.asJson))
      if (obj.size != -1) {
        fields += ("size" -> obj.size.asJson)
      }
      if (obj.shardSize != -1 && obj.shardSize >= obj.size) {
        fields += ("shard_size" -> obj.shardSize.asJson)
      }
      obj.order.foreach(v => fields += ("order" -> v.asJson))
      if (obj.minDocCount > 0) {
        fields += ("min_doc_count" -> obj.minDocCount.asJson)
      }

      obj.include.foreach(v => fields += ("include" -> v.asJson))
      obj.exclude.foreach(v => fields += ("exclude" -> v.asJson))
      obj.executionHint.foreach(v => fields += ("execution_hint" -> v.asJson))

      obj.meta.map(v => fields += ("meta" -> v.asJson))
      Json.fromFields(fields)
    }
}

final case class IPV4RangeAggregation(
  field: String = "",
  script: Option[Script] = None,
  keyed: Boolean = false,
  format: Option[String] = None,
  ranges: List[RangeValue] = Nil,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
  override def NAME: String = IPV4RangeAggregation.NAME
}

object IPV4RangeAggregation extends AggregationType[IPV4RangeAggregation] {
  val NAME = "ip_range"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): IPV4RangeAggregation = {
    val hc = json.hcursor
    new IPV4RangeAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      script = hc.downField("script").as[Script].toOption,
      keyed = hc.downField("keyed").as[Boolean].toOption.getOrElse(false),
      format = hc.downField("format").as[String].toOption,
      ranges = hc.downField("ranges").as[List[RangeValue]].toOption.getOrElse(List.empty[RangeValue]),
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[IPV4RangeAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      fields += ("keyed" -> obj.keyed.asJson)
      obj.script.foreach(v => fields += ("script" -> v.asJson))
      obj.format.foreach(v => fields += ("format" -> v.asJson))
      fields += ("ranges" -> obj.ranges.asJson)

      obj.meta.map(v => fields += ("meta" -> v.asJson))
      Json.fromFields(fields)
    }
}

final case class MaxAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME = MaxAggregation.NAME
}

object MaxAggregation extends AggregationType[MaxAggregation] {
  val NAME = "max"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): MaxAggregation = {
    val hc = json.hcursor
    new MaxAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      missing = hc.downField("missing").as[Json].toOption,
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[MaxAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.missing.map(v => fields += ("missing" -> v))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}

final case class MinAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME = MinAggregation.NAME
}

object MinAggregation extends AggregationType[MinAggregation] {
  val NAME = "min"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): MinAggregation = {
    val hc = json.hcursor
    new MinAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      missing = hc.downField("missing").as[Json].toOption,
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[MinAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.missing.map(v => fields += ("missing" -> v))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}

final case class MissingAggregation(
  field: String,
  meta: Option[Json] = None,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
) extends Aggregation
    with BucketAggregation {
  val NAME = MissingAggregation.NAME

}

object MissingAggregation extends AggregationType[MissingAggregation] {
  val NAME = "missing"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): MissingAggregation = {
    val hc = json.hcursor
    new MissingAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      meta = meta,
      aggregations = aggregations
    )
  }

  implicit final val encodeAggregation: JsonEncoder[MissingAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      obj.meta.map(v => fields += ("meta" -> v.asJson))
      Json.fromFields(fields)
    }
}

final case class NestedAggregation(
  path: String,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
  val NAME = NestedAggregation.NAME
}

object NestedAggregation extends AggregationType[NestedAggregation] {
  val NAME = "nested"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): NestedAggregation = {
    val hc = json.hcursor
    new NestedAggregation(
      path = hc.downField("path").as[String].toOption.getOrElse(""),
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[NestedAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.path.nonEmpty)
        fields += ("path" -> obj.path.asJson)
      obj.meta.map(v => fields += ("meta" -> v.asJson))
      Json.fromFields(fields)
    }
}

final case class PercentilesAggregationTDigest(compression: Int)
object PercentilesAggregationTDigest {
  implicit val jsonDecoder: JsonDecoder[PercentilesAggregationTDigest] =
    DeriveJsonDecoder.gen[PercentilesAggregationTDigest]
  implicit val jsonEncoder: JsonEncoder[PercentilesAggregationTDigest] =
    DeriveJsonEncoder.gen[PercentilesAggregationTDigest]
}

final case class PercentilesAggregationHDR(number_of_significant_value_digits: Int)
object PercentilesAggregationHDR {
  implicit val jsonDecoder: JsonDecoder[PercentilesAggregationHDR] = DeriveJsonDecoder.gen[PercentilesAggregationHDR]
  implicit val jsonEncoder: JsonEncoder[PercentilesAggregationHDR] = DeriveJsonEncoder.gen[PercentilesAggregationHDR]
}

final case class PercentilesAggregation(
  field: String = "",
  percents: List[Double] = Nil,
  missing: Option[Json] = None,
  keyed: Boolean = true,
  tdigest: Option[PercentilesAggregationTDigest] = None,
  hdr: Option[PercentilesAggregationHDR] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME = PercentilesAggregation.NAME

}

object PercentilesAggregation extends AggregationType[PercentilesAggregation] {
  val NAME = "percentiles"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): PercentilesAggregation = {
    val hc = json.hcursor
    new PercentilesAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      percents = hc.downField("percents").as[List[Double]].toOption.getOrElse(Nil),
      missing = hc.downField("missing").as[Json].toOption,
      tdigest = hc.downField("tdigest").as[PercentilesAggregationTDigest].toOption,
      hdr = hc.downField("hdr").as[PercentilesAggregationHDR].toOption,
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[PercentilesAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty) {
        fields += ("field" -> obj.field.asJson)
      }
      if (obj.percents.nonEmpty) {
        fields += ("percents" -> obj.percents.asJson)
      }
      if (!obj.keyed) {
        fields += ("keyed" -> obj.keyed.asJson)
      }
      obj.tdigest.map(v => fields += ("tdigest" -> v.asJson))
      obj.hdr.map(v => fields += ("hdr" -> v.asJson))
      obj.missing.map(v => fields += ("missing" -> v))
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}

final case class PercentileRanksAggregation(
  field: String = "",
  values: List[Double] = Nil,
  missing: Option[Json] = None,
  keyed: Boolean = true,
  hdr: Option[PercentilesAggregationHDR] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME = PercentileRanksAggregation.NAME

}

object PercentileRanksAggregation extends AggregationType[PercentileRanksAggregation] {
  val NAME = "percentile_ranks"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): PercentileRanksAggregation = {
    val hc = json.hcursor
    new PercentileRanksAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      values = hc.downField("values").as[List[Double]].toOption.getOrElse(Nil),
      missing = hc.downField("missing").as[Json].toOption,
      hdr = hc.downField("hdr").as[PercentilesAggregationHDR].toOption,
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[PercentileRanksAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      fields += ("values" -> obj.values.asJson)
      if (obj.field.nonEmpty) {
        fields += ("field" -> obj.field.asJson)
      }
      if (!obj.keyed) {
        fields += ("keyed" -> obj.keyed.asJson)
      }
      obj.hdr.map(v => fields += ("hdr" -> v.asJson))
      obj.missing.map(v => fields += ("missing" -> v))
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}

final case class RangeAggregation(
  field: String = "",
  script: Option[Script] = None,
  keyed: Boolean = false,
  ranges: List[RangeValue] = Nil,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
  val NAME = RangeAggregation.NAME

  def addRange(from: Double, to: Double): RangeAggregation =
    this.copy(
      ranges = ranges :+ RangeValue(
        from = Json.Num(from),
        to = Json.Num(to)
      )
    )

  def addRange(key: String, from: Double, to: Double): RangeAggregation =
    this.copy(
      ranges = ranges :+ RangeValue(
        key = Some(key),
        from = Json.Num(from),
        to = Json.Num(to)
      )
    )

}

object RangeAggregation extends AggregationType[RangeAggregation] {
  val NAME = "range"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): RangeAggregation = {
    val hc = json.hcursor
    new RangeAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      script = hc.downField("script").as[Script].toOption,
      keyed = hc.downField("keyed").as[Boolean].toOption.getOrElse(false),
      ranges = hc.downField("ranges").as[List[RangeValue]].toOption.getOrElse(List.empty[RangeValue]),
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[RangeAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      fields += ("keyed" -> obj.keyed.asJson)
      obj.script.foreach(v => fields += ("script" -> v.asJson))
      fields += ("ranges" -> obj.ranges.asJson)

      obj.meta.map(v => fields += ("meta" -> v.asJson))
      Json.fromFields(fields)
    }
}

sealed trait ScriptableAggregation extends Aggregation {
  def field: String

  def script: Option[Script]

}

trait SubAggregation extends Aggregation

final case class ScriptedMetricAggregation(
  mapScript: String,
  initScript: Option[String] = None,
  combineScript: Option[String] = None,
  reduceScript: Option[String] = None,
  meta: Option[Json] = None
) extends Aggregation
    with NoBucketAggregation {
  val NAME = ScriptedMetricAggregation.NAME

}

object ScriptedMetricAggregation extends AggregationType[ScriptedMetricAggregation] {
  val NAME = "scripted_metric"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): ScriptedMetricAggregation = {
    val hc = json.hcursor
    new ScriptedMetricAggregation(
      mapScript = hc.downField("map_script").as[String].toOption.get,
      initScript = hc.downField("init_script").as[String].toOption,
      combineScript = hc.downField("combine_script").as[String].toOption,
      reduceScript = hc.downField("reduce_script").as[String].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[ScriptedMetricAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      fields += ("map_script" -> Json.Str(obj.mapScript))
      obj.initScript.map(v => fields += ("init_script" -> Json.Str(v)))
      obj.combineScript.map(v => fields += ("combine_script" -> Json.Str(v)))
      obj.reduceScript.map(v => fields += ("reduce_script" -> Json.Str(v)))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}

final case class StatsAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME = StatsAggregation.NAME
}

object StatsAggregation extends AggregationType[StatsAggregation] {
  val NAME = "stats"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): StatsAggregation = {
    val hc = json.hcursor
    new StatsAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      missing = hc.downField("missing").as[Json].toOption,
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[StatsAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty)
        fields += ("field" -> obj.field.asJson)
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.missing.map(v => fields += ("missing" -> v))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}

final case class SumAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME = SumAggregation.NAME
}

object SumAggregation extends AggregationType[SumAggregation] {
  val NAME = "sum"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): SumAggregation = {
    val hc = json.hcursor
    new SumAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      missing = hc.downField("missing").as[Json].toOption,
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[SumAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty) {
        fields += ("field" -> obj.field.asJson)
      }
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.missing.map(v => fields += ("missing" -> v))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}
final case class TopHitsAggregation(
  size: Int = 10,
  meta: Option[Json] = None,
  order: Option[Sort] = None,
  include: Option[Json] = None,
  exclude: Option[Json] = None
) extends Aggregation
    with NoBucketAggregation {
  val NAME = TopHitsAggregation.NAME
}

object TopHitsAggregation extends AggregationType[TopHitsAggregation] {
  val NAME = "top_hits"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): TopHitsAggregation = {
    val hc = json.hcursor
    new TopHitsAggregation(
      size = hc.downField("size").as[Int].toOption.getOrElse(10),
      meta = meta,
      order = hc.downField("sort").as[Sort].toOption,
      include = hc.downField("_source").downField("includes").as[Json].toOption,
      exclude = hc.downField("_source").downField("excludes").as[Json].toOption
    )
  }

  implicit final val encodeAggregation: JsonEncoder[TopHitsAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.size != 10) {
        fields += ("size" -> Json.Num(obj.size))
      }
      obj.order match {
        case Some(value) => fields += ("sort" -> value.asJson)
        case None        =>
      }
      obj.meta.map(v => fields += ("meta" -> v.asJson))
      fields += ("_source" -> Json
        .Obj()
        .add("includes", obj.include.getOrElse(Json.Null).asJson)
        .add("excludes", obj.exclude.getOrElse(Json.Null).asJson)
        .asJson)
      Json.fromFields(fields)
    }
}

final case class TermsAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  size: Int = -1,
  shardSize: Int = -1,
  order: Option[FieldSort] = None,
  minDocCount: Int = 1,
  include: Option[Json] = None,
  exclude: Option[Json] = None,
  executionHint: Option[ExecutionHint] = None,
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
  val NAME = TermsAggregation.NAME

}

object TermsAggregation extends AggregationType[TermsAggregation] {
  def NAME = "terms"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): TermsAggregation = {
    val hc = json.hcursor
    new TermsAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      missing = hc.downField("missing").as[Json].toOption,
      script = hc.downField("script").as[Script].toOption,
      size = hc.downField("size").as[Int].toOption.getOrElse(-1),
      shardSize = hc.downField("shard_size").as[Int].toOption.getOrElse(-1),
      order = hc.downField("order").as[FieldSort].toOption,
      minDocCount = hc.downField("min_doc_count").as[Int].toOption.getOrElse(1),
      include = hc.downField("include").as[Json].toOption,
      exclude = hc.downField("exclude").as[Json].toOption,
      executionHint = hc.downField("execution_hint").as[ExecutionHint].toOption,
      aggregations = aggregations,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[TermsAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty) {
        fields += ("field" -> obj.field.asJson)
      }
      obj.script.foreach(v => fields += ("script" -> v.asJson))
      if (obj.size != -1) {
        fields += ("size" -> obj.size.asJson)
      }
      if (obj.shardSize != -1 && obj.shardSize >= obj.size) {
        fields += ("shard_size" -> obj.shardSize.asJson)
      }
      obj.order match {
        case Some(value) =>
          fields += ("order" -> Json.fromJsonObject(Json.Obj((value.field, Json.Str(value.order.toString)))))
        case None =>
      }
      //obj.order.foreach(v =>  fields += ("order" -> v.asJson))
      if (obj.minDocCount > 0) {
        fields += ("min_doc_count" -> obj.minDocCount.asJson)
      }

      obj.missing.map(v => fields += ("missing" -> v))

      obj.include.foreach(v => fields += ("include" -> v))
      obj.exclude.foreach(v => fields += ("exclude" -> v))
      obj.executionHint.foreach(v => fields += ("execution_hint" -> v.asJson))

      obj.meta.map(v => fields += ("meta" -> v.asJson))
      Json.fromFields(fields)
    }
}

final case class ValueCountAggregation(
  field: String = "",
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
  val NAME = ValueCountAggregation.NAME

}

object ValueCountAggregation extends AggregationType[ValueCountAggregation] {
  val NAME = "value_count"

  def parse(
    json: Json,
    meta: Option[Json],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
  ): ValueCountAggregation = {
    val hc = json.hcursor
    new ValueCountAggregation(
      field = hc.downField("field").as[String].toOption.getOrElse(""),
      script = hc.downField("script").as[Script].toOption,
      meta = meta
    )
  }

  implicit final val encodeAggregation: JsonEncoder[ValueCountAggregation] =
    JsonEncoder.instance { obj =>
      val fields = new ListBuffer[(String, Json)]()
      if (obj.field.nonEmpty) {
        fields += ("field" -> obj.field.asJson)
      }
      obj.script.map(v => fields += ("script" -> v.asJson))
      obj.meta.map(v => fields += ("meta" -> v.asJson))

      Json.fromFields(fields)

    }
}
