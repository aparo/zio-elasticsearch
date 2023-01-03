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

import zio.elasticsearch.geo.{ DistanceType, DistanceUnit, GeoPoint }
import zio.elasticsearch.queries.Query
import zio.elasticsearch.script.Script
import zio.elasticsearch.sort.Sort._
import zio.elasticsearch.sort.{ FieldSort, Sorter }
import zio.elasticsearch.{ DateInterval, Regex }
import zio.json._
import zio.json.ast._
import zio.json.internal.RetractReader

final case class ComposedAggregation(aggregation: Aggregation, subAggregations: Aggregation.Aggregations)
object ComposedAggregation {
  implicit val jsonDecoder: JsonDecoder[ComposedAggregation] = Json.Obj.decoder.mapOrFail { jObj =>
    import Aggregation._
    val sub = jObj.getOption[Aggregations]("aggs").getOrElse(Aggregation.EmptyAggregations)
    jObj.remove("aggs").as[Aggregation] match {
      case Left(value)        => Left(value)
      case Right(aggregation) => Right(ComposedAggregation(aggregation = aggregation, subAggregations = sub))
    }

  }
  implicit val jsonEncoder: JsonEncoder[ComposedAggregation] = Json.encoder.contramap { obj =>
    import Aggregation._
    Json.Obj().add("aggs", obj.subAggregations.toJsonAST).merge(obj.aggregation.toJsonAST.getOrElse(Json.Obj()))
  }
  DeriveJsonEncoder.gen[ComposedAggregation]

}
sealed trait Aggregation {

  def meta: Option[Json]

}

//case class ComposedAggregation()

object Aggregation {

  type Aggregations = Map[String, ComposedAggregation]
  lazy val EmptyAggregations = Map.empty[String, ComposedAggregation]
  implicit val jsonDecoder: JsonDecoder[Aggregation] = DeriveJsonDecoder.gen[Aggregation]
  implicit val jsonEncoder: JsonEncoder[Aggregation] = DeriveJsonEncoder.gen[Aggregation]
//  implicit val jsonMDecoder: JsonDecoder[Map[String, ComposedAggregation]] =
//    DeriveJsonDecoder.gen[Map[String, ComposedAggregation]]
//  implicit val jsonMEncoder: JsonEncoder[Map[String, ComposedAggregation]] =
//    DeriveJsonEncoder.gen[Map[String, ComposedAggregation]]
  //  val registered: Map[String, AggregationType[_]] = Map(
//    AdjacencyMatrixAggregation.NAME -> AdjacencyMatrixAggregation,
//    AvgAggregation.NAME -> AvgAggregation,
//    CardinalityAggregation.NAME -> CardinalityAggregation,
//    DateHistogramAggregation.NAME -> DateHistogramAggregation,
//    DateRangeAggregation.NAME -> DateRangeAggregation,
//    ExtendedStatsAggregation.NAME -> ExtendedStatsAggregation,
//    FilterAggregation.NAME -> FilterAggregation,
//    FiltersAggregation.NAME -> FiltersAggregation,
//    GeoBoundsAggregation.NAME -> GeoBoundsAggregation,
//    GeoCentroidAggregation.NAME -> GeoCentroidAggregation,
//    GeoDistanceAggregation.NAME -> GeoDistanceAggregation,
//    GeoHashGridAggregation.NAME -> GeoHashGridAggregation,
//    GlobalAggregation.NAME -> GlobalAggregation,
//    HistogramAggregation.NAME -> HistogramAggregation,
//    IPV4RangeAggregation.NAME -> IPV4RangeAggregation,
//    MaxAggregation.NAME -> MaxAggregation,
//    MinAggregation.NAME -> MinAggregation,
//    MissingAggregation.NAME -> MissingAggregation,
//    NestedAggregation.NAME -> NestedAggregation,
//    PercentilesAggregation.NAME -> PercentilesAggregation,
//    PercentileRanksAggregation.NAME -> PercentileRanksAggregation,
//    RangeAggregation.NAME -> RangeAggregation,
//    ScriptedMetricAggregation.NAME -> ScriptedMetricAggregation,
//    StatsAggregation.NAME -> StatsAggregation,
//    SumAggregation.NAME -> SumAggregation,
//    TermsAggregation.NAME -> TermsAggregation,
//    TopHitsAggregation.NAME -> TopHitsAggregation,
//    ValueCountAggregation.NAME -> ValueCountAggregation
//  )
//
//  private val specialKeys = List("meta", "aggs", "aggregations")
//
//  implicit final val decodeAggregation: JsonDecoder[Aggregation] =
//    JsonDecoder.instance { c =>
//      val keys = c.keys.get.toList
//      keys.diff(specialKeys).headOption match {
//        case None => Left(DecodingFailure("Invalid Aggregation Format", Nil))
//        case Some(aggregationType) =>
//          if (registered.contains(aggregationType)) {
//            val meta = jObj.get[Json]("meta").toOption
//            val aggregations: Aggregations =
//              if (keys.contains("aggs")) {
//                jObj.get[Aggregations]("aggs").toOption.getOrElse(EmptyAggregations)
//              } else if (keys.contains("aggregations")) {
//                jObj.get[Aggregations]("aggregations").toOption.getOrElse(EmptyAggregations)
//              } else {
//                EmptyAggregations
//              }
//
//            Right(
//              registered(aggregationType)
//                .parse(
//                  c.downField(aggregationType).focus.get,
//                  meta = meta,
//                  aggregations = aggregations
//                )
//                .asInstanceOf[Aggregation]
//            )
//          } else {
//            Left(
//              DecodingFailure(
//                s"AggregationType '$aggregationType' not supported",
//                Nil
//              )
//            )
//          }
//      }
//    }
//
//  implicit final val encodeAggregation: JsonEncoder[Aggregation] =
//    JsonEncoder.instance {
//      case o: AdjacencyMatrixAggregation =>
//        addSubAggregations(Json.Obj(AdjacencyMatrixAggregation.NAME -> o.asJson), o.aggregations)
//      case o: AvgAggregation =>
//        addSubAggregations(Json.Obj(AvgAggregation.NAME -> o.asJson), o.aggregations)
//      case o: CardinalityAggregation =>
//        addSubAggregations(Json.Obj(CardinalityAggregation.NAME -> o.asJson), o.aggregations)
//      case o: DateHistogramAggregation =>
//        addSubAggregations(Json.Obj(DateHistogramAggregation.NAME -> o.asJson), o.aggregations)
//      case o: DateRangeAggregation =>
//        addSubAggregations(Json.Obj(DateRangeAggregation.NAME -> o.asJson), o.aggregations)
//      case o: ExtendedStatsAggregation =>
//        addSubAggregations(Json.Obj(ExtendedStatsAggregation.NAME -> o.asJson), o.aggregations)
//      case o: FilterAggregation =>
//        addSubAggregations(Json.Obj(FilterAggregation.NAME -> o.asJson), o.aggregations)
//      case o: FiltersAggregation =>
//        addSubAggregations(Json.Obj(FiltersAggregation.NAME -> o.asJson), o.aggregations)
//      case o: GeoBoundsAggregation =>
//        addSubAggregations(Json.Obj(GeoBoundsAggregation.NAME -> o.asJson), o.aggregations)
//      case o: GeoCentroidAggregation =>
//        addSubAggregations(Json.Obj(GeoCentroidAggregation.NAME -> o.asJson), o.aggregations)
//      case o: GeoDistanceAggregation =>
//        addSubAggregations(Json.Obj(GeoDistanceAggregation.NAME -> o.asJson), o.aggregations)
//      case o: GeoHashGridAggregation =>
//        addSubAggregations(Json.Obj(GeoHashGridAggregation.NAME -> o.asJson), o.aggregations)
//      case o: GlobalAggregation =>
//        addSubAggregations(Json.Obj(GlobalAggregation.NAME -> o.asJson), o.aggregations)
//      case o: HistogramAggregation =>
//        addSubAggregations(Json.Obj(HistogramAggregation.NAME -> o.asJson), o.aggregations)
//      case o: IPV4RangeAggregation =>
//        addSubAggregations(Json.Obj(IPV4RangeAggregation.NAME -> o.asJson), o.aggregations)
//      case o: MaxAggregation =>
//        addSubAggregations(Json.Obj(MaxAggregation.NAME -> o.asJson), o.aggregations)
//      case o: MinAggregation =>
//        addSubAggregations(Json.Obj(MinAggregation.NAME -> o.asJson), o.aggregations)
//      case o: MissingAggregation =>
//        addSubAggregations(Json.Obj(MissingAggregation.NAME -> o.asJson), o.aggregations)
//      case o: NestedAggregation =>
//        addSubAggregations(Json.Obj(NestedAggregation.NAME -> o.asJson), o.aggregations)
//      case o: PercentilesAggregation =>
//        addSubAggregations(Json.Obj(PercentilesAggregation.NAME -> o.asJson), o.aggregations)
//      case o: PercentileRanksAggregation =>
//        addSubAggregations(Json.Obj(PercentileRanksAggregation.NAME -> o.asJson), o.aggregations)
//      case o: RangeAggregation =>
//        addSubAggregations(Json.Obj(RangeAggregation.NAME -> o.asJson), o.aggregations)
//      case o: ScriptedMetricAggregation =>
//        addSubAggregations(Json.Obj(ScriptedMetricAggregation.NAME -> o.asJson), o.aggregations)
//      case o: StatsAggregation =>
//        addSubAggregations(Json.Obj(StatsAggregation.NAME -> o.asJson), o.aggregations)
//      case o: SumAggregation =>
//        addSubAggregations(Json.Obj(SumAggregation.NAME -> o.asJson), o.aggregations)
//      case o: TermsAggregation =>
//        addSubAggregations(Json.Obj(TermsAggregation.NAME -> o.asJson), o.aggregations)
//      case o: ValueCountAggregation =>
//        addSubAggregations(Json.Obj(ValueCountAggregation.NAME -> o.asJson), o.aggregations)
//      case o: TopHitsAggregation =>
//        addSubAggregations(Json.Obj(TopHitsAggregation.NAME -> o.asJson), o.aggregations)
//
//      //the next lines are to make the compiler happy
//      case _: ScriptableAggregation => Json.Null
//      case _: SubAggregation        => Json.Null
//    }
//
//  def addSubAggregations(json: Json, aggregations: Aggregations): Json =
//    if (aggregations.isEmpty) {
//      json
//    } else {
//      val aggs = aggregations.flatMap { agg =>
//        agg._2.toJsonAST.toOption.map(v => agg._1 -> v)
//      }
//      json.asInstanceOf[Json.Obj].add("aggs", Json.Obj(aggs.toSeq: _*))
//    }

}

//sealed trait AggregationType[T] {
//  def NAME: String
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): T
//
//}

sealed trait BucketAggregation extends SubAggregation

sealed trait NoBucketAggregation extends Aggregation {}

@jsonHint("adjacency_matrix")
final case class AdjacencyMatrixAggregation(
  filters: Map[String, Query],
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
//  val NAME = AdjacencyMatrixAggregation.NAME
}

//object AdjacencyMatrixAggregation extends AggregationType[AdjacencyMatrixAggregation] {
//  val NAME = "adjacency_matrix"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): AdjacencyMatrixAggregation = {
//    val hc = json.hcursor
//    new AdjacencyMatrixAggregation(
//      filters = hjObj.get[Map[String, Query]]("filters").toOption.get,
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[AdjacencyMatrixAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      fields += ("filters" -> obj.filters.asJson)
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
////      Aggregation.addSubAggregations(Json.Obj(Chunk.fromIterable(fields)), obj.aggregations)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("avg")
final case class AvgAggregation(
  field: String = "",
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
//  val NAME: String = AvgAggregation.NAME

}

//object AvgAggregation extends AggregationType[AvgAggregation] {
//  val NAME: String = "avg"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): AvgAggregation = {
//    val hc = json.hcursor
//    new AvgAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[AvgAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty) {
//        fields += ("field" -> obj.field.asJson)
//      }
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//
//}

@jsonHint("cardinality")
final case class CardinalityAggregation(
  field: String = "",
  @jsonField("precision_threshold") precisionThreshold: Int = 3000, //Maximum value 40000
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
//  val NAME = CardinalityAggregation.NAME

}

//object CardinalityAggregation extends AggregationType[CardinalityAggregation] {
//  val NAME = "cardinality"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): CardinalityAggregation = {
//    val hc = json.hcursor
//    new CardinalityAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      precisionThreshold = hjObj.get[Int]("precision_threshold").toOption.getOrElse(3000),
//      missing = hjObj.get[Json]("missing").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[CardinalityAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      if (obj.precisionThreshold != 3000)
//        fields += ("precision_threshold" -> obj.precisionThreshold.asJson)
//      obj.missing.map(v => fields += ("missing" -> v))
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}

final case class ExtendedBounds(min: String, max: String)
object ExtendedBounds {
  implicit val jsonDecoder: JsonDecoder[ExtendedBounds] = DeriveJsonDecoder.gen[ExtendedBounds]
  implicit val jsonEncoder: JsonEncoder[ExtendedBounds] = DeriveJsonEncoder.gen[ExtendedBounds]
}

@jsonHint("date_histogram")
final case class DateHistogramAggregation(
  field: String = "",
  missing: Option[Json] = None,
  interval: DateInterval = DateInterval("day"),
  offset: Option[String] = None,
  @jsonField("timezone") timeZone: Option[String] = None,
  script: Option[Script] = None,
  size: Option[Int] = None, //-1,
  shardSize: Option[Int] = None, // -1,
  order: Option[Sorter] = None,
  keyed: Boolean = false,
  format: Option[String] = None,
  @jsonField("min_doc_count") minDocCount: Option[Int] = None,
  include: Option[Regex] = None,
  exclude: Option[Regex] = None,
  @jsonField("execution_hint") executionHint: Option[ExecutionHint] = None,
  @jsonField("extended_bounds") extendedBounds: Option[ExtendedBounds] = None,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
//  val NAME = DateHistogramAggregation.NAME

}

//object DateHistogramAggregation extends AggregationType[DateHistogramAggregation] {
//  val NAME = "date_histogram"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): DateHistogramAggregation = {
//    val hc = json.hcursor
//    new DateHistogramAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      missing = hjObj.get[Json]("missing").toOption,
//      interval = DateInterval(
//        hjObj.get[String]("interval").toOption.getOrElse("")
//      ),
//      offset = hjObj.get[String]("offset").toOption,
//      timeZone = hjObj.get[String]("time_zone").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      size = hjObj.get[Int]("size").toOption.getOrElse(-1),
//      shardSize = hjObj.get[Int]("shard_size").toOption.getOrElse(-1),
//      order = hjObj.get[Sorter]("order").toOption,
//      keyed = hjObj.get[Boolean]("keyed").toOption.getOrElse(false),
//      format = hjObj.get[String]("format").toOption,
//      minDocCount = hjObj.get[Int]("min_doc_count").toOption.getOrElse(1),
//      include = hjObj.get[Regex]("include").toOption,
//      exclude = hjObj.get[Regex]("exclude").toOption,
//      executionHint = hjObj.get[ExecutionHint]("execution_hint").toOption,
//      extendedBounds = hjObj.get[ExtendedBounds]("extended_bounds").toOption,
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[DateHistogramAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      fields += ("keyed" -> obj.keyed.asJson)
//      fields += ("interval" -> obj.interval.asJson)
//      obj.script.foreach(v => fields += ("script" -> v.asJson))
//      obj.timeZone.foreach(v => fields += ("time_zone" -> v.asJson))
//      if (obj.size != -1)
//        fields += ("size" -> obj.size.asJson)
//      if (obj.shardSize != -1 && obj.shardSize >= obj.size)
//        fields += ("shard_size" -> obj.shardSize.asJson)
//      obj.order.foreach(v => fields += ("order" -> v.asJson))
//      obj.format.foreach(v => fields += ("format" -> v.asJson))
//      if (obj.minDocCount > 0)
//        fields += ("min_doc_count" -> obj.minDocCount.asJson)
//      obj.missing.map(v => fields += ("missing" -> v))
//      obj.include.foreach(v => fields += ("include" -> v.asJson))
//      obj.exclude.foreach(v => fields += ("exclude" -> v.asJson))
//      obj.executionHint.foreach(v => fields += ("execution_hint" -> v.asJson))
//      obj.extendedBounds.foreach(v => fields += ("extended_bounds" -> v.asJson))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
////      Aggregation.addSubAggregations(Json.Obj(Chunk.fromIterable(fields)), obj.aggregations)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//
//}

/* for format give a look to http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html */
@jsonHint("date_range")
final case class DateRangeAggregation(
  field: String = "",
  script: Option[Script] = None,
  keyed: Boolean = false,
  format: Option[String] = None,
  ranges: List[RangeValue] = Nil,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
//  val NAME = DateRangeAggregation.NAME

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

//object DateRangeAggregation extends AggregationType[DateRangeAggregation] {
//  val NAME = "date_range"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): DateRangeAggregation = {
//    val hc = json.hcursor
//    new DateRangeAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      script = hjObj.get[Script]("script").toOption,
//      keyed = hjObj.get[Boolean]("keyed").toOption.getOrElse(false),
//      format = hjObj.get[String]("format").toOption,
//      ranges = hjObj.get[List[RangeValue]].toOption.getOrElse(List.empty[RangeValue]("ranges")),
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[DateRangeAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      fields += ("keyed" -> obj.keyed.asJson)
//      obj.script.foreach(v => fields += ("script" -> v.asJson))
//      obj.format.foreach(v => fields += ("format" -> v.asJson))
//      fields += ("ranges" -> obj.ranges.asJson)
//
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
////      Aggregation.addSubAggregations(Json.Obj(Chunk.fromIterable(fields)), obj.aggregations)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("extended_stats")
final case class ExtendedStatsAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
//  val NAME = ExtendedStatsAggregation.NAME
}

//object ExtendedStatsAggregation extends AggregationType[ExtendedStatsAggregation] {
//  val NAME = "extended_stats"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): ExtendedStatsAggregation = {
//    val hc = json.hcursor
//    new ExtendedStatsAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      missing = hjObj.get[Json]("missing").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[ExtendedStatsAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.missing.map(v => fields += ("missing" -> v))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}

@jsonHint("filter")
final case class FilterAggregation(
  filter: Query,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
//  val NAME = FilterAggregation.NAME
}
object FilterAggregation {
  implicit final val decodeAggregation: JsonDecoder[FilterAggregation] =
    Query.jsonDecoder.map(q => FilterAggregation(filter = q))
  implicit final val encodeAggregation: JsonEncoder[FilterAggregation] = Query.jsonEncoder.contramap(_.filter)

  //  implicit final val encodeAggregation: JsonEncoder[FilterAggregation] =

}

//object FilterAggregation extends AggregationType[FilterAggregation] {
//  val NAME = "filter"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): FilterAggregation = {
//    val hc = json.hcursor
//    val filter = hc.as[Query]
//    new FilterAggregation(
//      filter = filter.toOption.get,
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[FilterAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      fields += ("filter" -> obj.filter.asJson)
//
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
////      Aggregation.addSubAggregations(Json.Obj(Chunk.fromIterable(fields)), obj.aggregations)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("filters")
final case class FiltersAggregation(
  filters: Map[String, Query],
  otherBucketKey: Option[String] = None,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
//  val NAME = FiltersAggregation.NAME
}

//object FiltersAggregation extends AggregationType[FiltersAggregation] {
//  val NAME = "filters"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): FiltersAggregation = {
//    val hc = json.hcursor
//    new FiltersAggregation(
//      filters = hjObj.get[Map[String, Query]]("filters").toOption.get,
//      otherBucketKey = hjObj.get[String]("other_bucket_key").toOption,
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[FiltersAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      fields += ("filters" -> obj.filters.asJson)
//      obj.otherBucketKey.map(v => fields += ("other_bucket_key" -> v.asJson))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
////      Aggregation.addSubAggregations(Json.Obj(Chunk.fromIterable(fields)), obj.aggregations)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("geo_bounds")
final case class GeoBoundsAggregation(
  field: String,
  wrapLongitude: Boolean = true,
  meta: Option[Json] = None
) extends NoBucketAggregation {
//  val NAME = GeoBoundsAggregation.NAME
}

//object GeoBoundsAggregation extends AggregationType[GeoBoundsAggregation] {
//  val NAME = "geo_bounds"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): GeoBoundsAggregation = {
//    val hc = json.hcursor
//    new GeoBoundsAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      wrapLongitude = hjObj.get[Boolean]("wrap_longitude").toOption.getOrElse(true),
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[GeoBoundsAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      fields += ("field" -> obj.field.asJson)
//      fields += ("wrap_longitude" -> obj.wrapLongitude.asJson)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//
//}

@jsonHint("geo_centroid")
final case class GeoCentroidAggregation(
  field: String,
  meta: Option[Json] = None
) extends NoBucketAggregation {
//  val NAME = GeoCentroidAggregation.NAME

}

//object GeoCentroidAggregation extends AggregationType[GeoCentroidAggregation] {
//  val NAME = "geo_centroid"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): GeoCentroidAggregation = {
//    val hc = json.hcursor
//    new GeoCentroidAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[GeoCentroidAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      fields += ("field" -> obj.field.asJson)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//
//}

@jsonHint("geo_distance")
final case class GeoDistanceAggregation(
  field: String,
  origin: GeoPoint,
  ranges: List[RangeValue] = Nil,
  keyed: Boolean = false,
  valueField: Option[String] = None,
  distanceUnit: Option[DistanceUnit] = None,
  distanceType: Option[DistanceType] = None,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
//  val NAME = GeoDistanceAggregation.NAME

}

//object GeoDistanceAggregation extends AggregationType[GeoDistanceAggregation] {
//  val NAME = "geo_distance"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): GeoDistanceAggregation = {
//    val hc = json.hcursor
//    new GeoDistanceAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      keyed = hjObj.get[Boolean]("keyed").toOption.getOrElse(false),
//      origin = hjObj.get[GeoPoint]("origin").toOption.getOrElse(GeoPoint(0, 0)),
//      valueField = hjObj.get[String]("value_field").toOption,
//      ranges = hjObj.get[List[RangeValue]].toOption.getOrElse(List.empty[RangeValue]("ranges")),
//      distanceUnit = hjObj.get[DistanceUnit]("distance_unit").toOption,
//      distanceType = hjObj.get[DistanceType]("distance_type").toOption,
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[GeoDistanceAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      fields += ("origin" -> obj.origin.asJson)
//      fields += ("keyed" -> obj.keyed.asJson)
//      obj.valueField.foreach(v => fields += ("value_field" -> v.asJson))
//      fields += ("ranges" -> obj.ranges.asJson)
//      obj.distanceUnit.foreach(v => fields += ("distance_unit" -> v.asJson))
//      obj.distanceType.foreach(v => fields += ("distance_type" -> v.asJson))
//
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
////      Aggregation.addSubAggregations(Json.Obj(Chunk.fromIterable(fields)), obj.aggregations)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//
//}

@jsonHint("geohash_grid")
final case class GeoHashGridAggregation(
  field: String,
  precision: Int = 3,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
//  val NAME = GeoHashGridAggregation.NAME
}

//object GeoHashGridAggregation extends AggregationType[GeoHashGridAggregation] {
//  val NAME = "geohash_grid"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): GeoHashGridAggregation = {
//    val hc = json.hcursor
//    new GeoHashGridAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      precision = hjObj.get[Int]("precision").toOption.getOrElse(3),
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[GeoHashGridAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      fields += ("precision" -> obj.precision.asJson)
//
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
////      Aggregation.addSubAggregations(Json.Obj(Chunk.fromIterable(fields)), obj.aggregations)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("global")
final case class GlobalAggregation(
  meta: Option[Json] = None
) extends Aggregation {
//  val NAME = GlobalAggregation.NAME
}

//object GlobalAggregation extends AggregationType[GlobalAggregation] {
//  val NAME = "global"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): GlobalAggregation =
//    new GlobalAggregation(
//      aggregations = aggregations,
//      meta = meta
//    )
//
//  implicit final val encodeAggregation: JsonEncoder[GlobalAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      fields += ("global" -> Json.Obj())
//
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("histogram")
final case class HistogramAggregation(
  field: String = "",
  interval: Long = 10,
  script: Option[Script] = None,
  size: Option[Int] = None, // -1,
  shardSize: Option[Int] = None, // -1,
  order: Option[Sorter] = None,
  keyed: Boolean = false,
  minDocCount: Option[Int] = None, // 1,
  include: Option[Regex] = None,
  exclude: Option[Regex] = None,
  executionHint: Option[ExecutionHint] = None,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
//  val NAME = HistogramAggregation.NAME
}

//object HistogramAggregation extends AggregationType[HistogramAggregation] {
//  val NAME = "histogram"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): HistogramAggregation = {
//    val hc = json.hcursor
//    new HistogramAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      interval = hjObj.get[Long]("interval").toOption.getOrElse(10),
//      script = hjObj.get[Script]("script").toOption,
//      size = hjObj.get[Int]("size").toOption.getOrElse(-1),
//      shardSize = hjObj.get[Int]("shard_size").toOption.getOrElse(-1),
//      order = hjObj.get[Sorter]("order").toOption,
//      keyed = hjObj.get[Boolean]("keyed").toOption.getOrElse(false),
//      minDocCount = hjObj.get[Int]("min_doc_count").toOption.getOrElse(1),
//      include = hjObj.get[Regex]("include").toOption,
//      exclude = hjObj.get[Regex]("exclude").toOption,
//      executionHint = hjObj.get[ExecutionHint]("execution_hint").toOption,
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[HistogramAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty) {
//        fields += ("field" -> obj.field.asJson)
//      }
//      fields += ("keyed" -> obj.keyed.asJson)
//      fields += ("interval" -> obj.interval.asJson)
//      obj.script.foreach(v => fields += ("script" -> v.asJson))
//      if (obj.size != -1) {
//        fields += ("size" -> obj.size.asJson)
//      }
//      if (obj.shardSize != -1 && obj.shardSize >= obj.size) {
//        fields += ("shard_size" -> obj.shardSize.asJson)
//      }
//      obj.order.foreach(v => fields += ("order" -> v.asJson))
//      if (obj.minDocCount > 0) {
//        fields += ("min_doc_count" -> obj.minDocCount.asJson)
//      }
//
//      obj.include.foreach(v => fields += ("include" -> v.asJson))
//      obj.exclude.foreach(v => fields += ("exclude" -> v.asJson))
//      obj.executionHint.foreach(v => fields += ("execution_hint" -> v.asJson))
//
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("ip_range")
final case class IPRangeAggregation(
  field: String = "",
  script: Option[Script] = None,
  keyed: Boolean = false,
  format: Option[String] = None,
  ranges: List[RangeValue] = Nil,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
//  override def NAME: String = IPRangeAggregation.NAME
}

//object IPRangeAggregation extends AggregationType[IPRangeAggregation] {
//  val NAME = "ip_range"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): IPRangeAggregation = {
//    val hc = json.hcursor
//    new IPRangeAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      script = hjObj.get[Script]("script").toOption,
//      keyed = hjObj.get[Boolean]("keyed").toOption.getOrElse(false),
//      format = hjObj.get[String]("format").toOption,
//      ranges = hjObj.get[List[RangeValue]].toOption.getOrElse(List.empty[RangeValue]("ranges")),
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[IPRangeAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      fields += ("keyed" -> obj.keyed.asJson)
//      obj.script.foreach(v => fields += ("script" -> v.asJson))
//      obj.format.foreach(v => fields += ("format" -> v.asJson))
//      fields += ("ranges" -> obj.ranges.asJson)
//
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("max")
final case class MaxAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
//  val NAME = MaxAggregation.NAME
}

//object MaxAggregation extends AggregationType[MaxAggregation] {
//  val NAME = "max"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): MaxAggregation = {
//    val hc = json.hcursor
//    new MaxAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      missing = hjObj.get[Json]("missing").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[MaxAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.missing.map(v => fields += ("missing" -> v))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}

@jsonHint("min")
final case class MinAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
//  val NAME = MinAggregation.NAME
}

//object MinAggregation extends AggregationType[MinAggregation] {
//  val NAME = "min"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): MinAggregation = {
//    val hc = json.hcursor
//    new MinAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      missing = hjObj.get[Json]("missing").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[MinAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.missing.map(v => fields += ("missing" -> v))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}

@jsonHint("missing")
final case class MissingAggregation(
  field: String,
  meta: Option[Json] = None
) extends Aggregation
    with BucketAggregation {
//  val NAME = MissingAggregation.NAME

}

//object MissingAggregation extends AggregationType[MissingAggregation] {
//  val NAME = "missing"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): MissingAggregation = {
//    val hc = json.hcursor
//    new MissingAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      meta = meta,
//      aggregations = aggregations
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[MissingAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("nested")
final case class NestedAggregation(
  path: String,
  meta: Option[Json] = None
) extends Aggregation
    with SubAggregation {
//  val NAME = NestedAggregation.NAME
}

//object NestedAggregation extends AggregationType[NestedAggregation] {
//  val NAME = "nested"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): NestedAggregation = {
//    val hc = json.hcursor
//    new NestedAggregation(
//      path = hjObj.get[String]("path").toOption.getOrElse(""),
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[NestedAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.path.nonEmpty)
//        fields += ("path" -> obj.path.asJson)
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

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

@jsonHint("percentiles")
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
//  val NAME = PercentilesAggregation.NAME
}

//object PercentilesAggregation extends AggregationType[PercentilesAggregation] {
//  val NAME = "percentiles"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): PercentilesAggregation = {
//    val hc = json.hcursor
//    new PercentilesAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      percents = hjObj.get[List[Double]]("percents").toOption.getOrElse(Nil),
//      missing = hjObj.get[Json]("missing").toOption,
//      tdigest = hjObj.get[PercentilesAggregationTDigest]("tdigest").toOption,
//      hdr = hjObj.get[PercentilesAggregationHDR]("hdr").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[PercentilesAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty) {
//        fields += ("field" -> obj.field.asJson)
//      }
//      if (obj.percents.nonEmpty) {
//        fields += ("percents" -> obj.percents.asJson)
//      }
//      if (!obj.keyed) {
//        fields += ("keyed" -> obj.keyed.asJson)
//      }
//      obj.tdigest.map(v => fields += ("tdigest" -> v.asJson))
//      obj.hdr.map(v => fields += ("hdr" -> v.asJson))
//      obj.missing.map(v => fields += ("missing" -> v))
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}

@jsonHint("percentile_ranks")
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
//  val NAME = PercentileRanksAggregation.NAME

}

//object PercentileRanksAggregation extends AggregationType[PercentileRanksAggregation] {
//  val NAME = "percentile_ranks"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): PercentileRanksAggregation = {
//    val hc = json.hcursor
//    new PercentileRanksAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      values = hjObj.get[List[Double]]("values").toOption.getOrElse(Nil),
//      missing = hjObj.get[Json]("missing").toOption,
//      hdr = hjObj.get[PercentilesAggregationHDR]("hdr").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[PercentileRanksAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      fields += ("values" -> obj.values.asJson)
//      if (obj.field.nonEmpty) {
//        fields += ("field" -> obj.field.asJson)
//      }
//      if (!obj.keyed) {
//        fields += ("keyed" -> obj.keyed.asJson)
//      }
//      obj.hdr.map(v => fields += ("hdr" -> v.asJson))
//      obj.missing.map(v => fields += ("missing" -> v))
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}

@jsonHint("range")
final case class RangeAggregation(
  field: String = "",
  script: Option[Script] = None,
  keyed: Boolean = false,
  ranges: List[RangeValue] = Nil,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
//  val NAME = RangeAggregation.NAME

  def addRange(from: Double, to: Double): RangeAggregation =
    this.copy(
      ranges = ranges :+ RangeValue(
        from = Some(Json.Num(from)),
        to = Some(Json.Num(to))
      )
    )

  def addRange(key: String, from: Double, to: Double): RangeAggregation =
    this.copy(
      ranges = ranges :+ RangeValue(
        key = Some(key),
        from = Some(Json.Num(from)),
        to = Some(Json.Num(to))
      )
    )

}

//object RangeAggregation extends AggregationType[RangeAggregation] {
//  val NAME = "range"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): RangeAggregation = {
//    val hc = json.hcursor
//    new RangeAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      script = hjObj.get[Script]("script").toOption,
//      keyed = hjObj.get[Boolean]("keyed").toOption.getOrElse(false),
//      ranges = hjObj.get[List[RangeValue]].toOption.getOrElse(List.empty[RangeValue]("ranges")),
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[RangeAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      fields += ("keyed" -> obj.keyed.asJson)
//      obj.script.foreach(v => fields += ("script" -> v.asJson))
//      fields += ("ranges" -> obj.ranges.asJson)
//
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

sealed trait ScriptableAggregation extends Aggregation {
  def field: String

  def script: Option[Script]

}

sealed trait SubAggregation extends Aggregation

@jsonHint("scripted_metric")
final case class ScriptedMetricAggregation(
  @jsonField("map_script") mapScript: String,
  @jsonField("init_script") initScript: Option[String] = None,
  @jsonField("combine_script") combineScript: Option[String] = None,
  @jsonField("reduce_script") reduceScript: Option[String] = None,
  meta: Option[Json] = None
) extends Aggregation
    with NoBucketAggregation {
//  val NAME = ScriptedMetricAggregation.NAME
}

//object ScriptedMetricAggregation extends AggregationType[ScriptedMetricAggregation] {
//  val NAME = "scripted_metric"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): ScriptedMetricAggregation = {
//    val hc = json.hcursor
//    new ScriptedMetricAggregation(
//      mapScript = hjObj.get[String]("map_script").toOption.get,
//      initScript = hjObj.get[String]("init_script").toOption,
//      combineScript = hjObj.get[String]("combine_script").toOption,
//      reduceScript = hjObj.get[String]("reduce_script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[ScriptedMetricAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      fields += ("map_script" -> Json.Str(obj.mapScript))
//      obj.initScript.map(v => fields += ("init_script" -> Json.Str(v)))
//      obj.combineScript.map(v => fields += ("combine_script" -> Json.Str(v)))
//      obj.reduceScript.map(v => fields += ("reduce_script" -> Json.Str(v)))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}

@jsonHint("stats")
final case class StatsAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
//  val NAME = StatsAggregation.NAME
}

//object StatsAggregation extends AggregationType[StatsAggregation] {
//  val NAME = "stats"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): StatsAggregation = {
//    val hc = json.hcursor
//    new StatsAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      missing = hjObj.get[Json]("missing").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[StatsAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty)
//        fields += ("field" -> obj.field.asJson)
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.missing.map(v => fields += ("missing" -> v))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}

@jsonHint("sum")
final case class SumAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
//  val NAME = SumAggregation.NAME
}

//object SumAggregation extends AggregationType[SumAggregation] {
//  val NAME = "sum"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): SumAggregation = {
//    val hc = json.hcursor
//    new SumAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      missing = hjObj.get[Json]("missing").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[SumAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty) {
//        fields += ("field" -> obj.field.asJson)
//      }
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.missing.map(v => fields += ("missing" -> v))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}

@jsonHint("top_hits")
final case class TopHitsAggregation(
  size: Int = 10,
  meta: Option[Json] = None,
  @jsonField("sort") order: Option[Sort] = None,
  include: Option[Json] = None,
  exclude: Option[Json] = None
) extends Aggregation
    with NoBucketAggregation {
//  val NAME = TopHitsAggregation.NAME
}

//object TopHitsAggregation extends AggregationType[TopHitsAggregation] {
//  val NAME = "top_hits"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): TopHitsAggregation = {
//    val hc = json.hcursor
//    new TopHitsAggregation(
//      size = hjObj.get[Int]("size").toOption.getOrElse(10),
//      meta = meta,
//      order = hjObj.get[Sort]("sort").toOption,
//      include = hjObj.get[Json]("_source").downField("includes").toOption,
//      exclude = hjObj.get[Json]("_source").downField("excludes").toOption
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[TopHitsAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.size != 10) {
//        fields += ("size" -> Json.Num(obj.size))
//      }
//      obj.order match {
//        case Some(value) => fields += ("sort" -> value.asJson)
//        case None        =>
//      }
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      fields += ("_source" -> Json
//        .Obj()
//        .add("includes", obj.include.getOrElse(Json.Null).asJson)
//        .add("excludes", obj.exclude.getOrElse(Json.Null).asJson)
//        .asJson)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("terms")
final case class TermsAggregation(
  field: String = "",
  missing: Option[Json] = None,
  script: Option[Script] = None,
  size: Int = 10,
  @jsonField("shard_size") shardSize: Option[Int] = None, // = -1,
  order: Option[FieldSort] = None,
  @jsonField("min_doc_count") minDocCount: Option[Int] = None, // 1,
  include: Option[Json] = None,
  exclude: Option[Json] = None,
  @jsonField("execution_hint") executionHint: Option[ExecutionHint] = None,
  meta: Option[Json] = None
) extends BucketAggregation
    with ScriptableAggregation {
//  val NAME = TermsAggregation.NAME
}
object TermsAggregation {
  implicit val jsonDecoder: JsonDecoder[TermsAggregation] = DeriveJsonDecoder.gen[TermsAggregation]
  implicit val jsonEncoder: JsonEncoder[TermsAggregation] = DeriveJsonEncoder.gen[TermsAggregation]

}
//object TermsAggregation extends AggregationType[TermsAggregation] {
//  def NAME = "terms"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): TermsAggregation = {
//    val hc = json.hcursor
//    new TermsAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      missing = hjObj.get[Json]("missing").toOption,
//      script = hjObj.get[Script]("script").toOption,
//      size = hjObj.get[Int]("size").toOption.getOrElse(-1),
//      shardSize = hjObj.get[Int]("shard_size").toOption.getOrElse(-1),
//      order = hjObj.get[FieldSort]("order").toOption,
//      minDocCount = hjObj.get[Int]("min_doc_count").toOption.getOrElse(1),
//      include = hjObj.get[Json]("include").toOption,
//      exclude = hjObj.get[Json]("exclude").toOption,
//      executionHint = hjObj.get[ExecutionHint]("execution_hint").toOption,
//      aggregations = aggregations,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[TermsAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty) {
//        fields += ("field" -> obj.field.asJson)
//      }
//      obj.script.foreach(v => fields += ("script" -> v.asJson))
//      if (obj.size != -1) {
//        fields += ("size" -> obj.size.asJson)
//      }
//      if (obj.shardSize != -1 && obj.shardSize >= obj.size) {
//        fields += ("shard_size" -> obj.shardSize.asJson)
//      }
//      obj.order match {
//        case Some(value) =>
//          fields += ("order" -> Json.fromJsonObject(Json.Obj((value.field, Json.Str(value.order.toString)))))
//        case None =>
//      }
//      //obj.order.foreach(v =>  fields += ("order" -> v.asJson))
//      if (obj.minDocCount > 0) {
//        fields += ("min_doc_count" -> obj.minDocCount.asJson)
//      }
//
//      obj.missing.map(v => fields += ("missing" -> v))
//
//      obj.include.foreach(v => fields += ("include" -> v))
//      obj.exclude.foreach(v => fields += ("exclude" -> v))
//      obj.executionHint.foreach(v => fields += ("execution_hint" -> v.asJson))
//
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//      Json.Obj(Chunk.fromIterable(fields))
//    }
//}

@jsonHint("value_count")
final case class ValueCountAggregation(
  field: String = "",
  script: Option[Script] = None,
  meta: Option[Json] = None
) extends ScriptableAggregation
    with NoBucketAggregation {
//  val NAME = ValueCountAggregation.NAME

}

//object ValueCountAggregation extends AggregationType[ValueCountAggregation] {
//  val NAME = "value_count"
//
//  def parse(
//    json: Json,
//    meta: Option[Json],
//    @jsonField("aggs") aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations
//  ): ValueCountAggregation = {
//    val hc = json.hcursor
//    new ValueCountAggregation(
//      field = hjObj.get[String]("field").toOption.getOrElse(""),
//      script = hjObj.get[Script]("script").toOption,
//      meta = meta
//    )
//  }
//
//  implicit final val encodeAggregation: JsonEncoder[ValueCountAggregation] =
//    JsonEncoder.instance { obj =>
//      val fields = new ListBuffer[(String, Json)]()
//      if (obj.field.nonEmpty) {
//        fields += ("field" -> obj.field.asJson)
//      }
//      obj.script.map(v => fields += ("script" -> v.asJson))
//      obj.meta.map(v => fields += ("meta" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
//}
