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

package zio.elasticsearch.mappings

import zio.Chunk
import zio.elasticsearch.common.mappings.FieldType
import zio.elasticsearch.common.analysis.Analyzer
import zio.exception.FrameworkException
import zio.json._
import zio.json.ast._

import java.time.OffsetDateTime

// format: off
/**
 * A Mapping can be part of an ObjectMapping.
 * Most sub-classes of Mapping are nestable, but RootDocumentMapping is not!
 */
@jsonDiscriminator("type")
sealed trait Mapping { self =>

  def `type`: FieldType

  def docValues: Option[Boolean]

  def store: Boolean

  def index: Boolean

  def boost: Float

  def indexOptions: Option[IndexOptions]

  def similarity: Option[Similarity]

  def fields: Map[String, Mapping]

  def copyTo: Chunk[String]

  /**
   * Make a JSON representation. Must return a Json.Obj("field" -> Json.Obj(...)).
   */
//  def toJson: Json

  def subFields: Map[String, Mapping] = 
    self match {
      case _: BinaryMapping => Map.empty[String, Mapping]
      case _: JoinMapping => Map.empty[String, Mapping]
      case m: AliasMapping => m.fields
      case m: BooleanMapping => m.fields
      case m: CompletionMapping => m.fields
      case m: DateMapping => m.fields
      case m: DateNanosMapping => m.fields
      case m: RootDocumentMapping => m.properties
      case m: GeoPointMapping => m.fields
      case m: GeoShapeMapping => m.fields
      case m: IpMapping => m.fields
      case m: NumberMapping => m.fields
      case m: RangeMapping => m.fields
      case m: NestedMapping => m.properties
      case m: ObjectMapping => m.properties
      case m: KeywordMapping => m.fields
      case m: FlattenedMapping =>  m.fields
      case m: TextMapping => m.fields
      case m: TokenCountMapping => m.fields
      //toSkip
      case _ => Map.empty[String, Mapping]
    }

  def resolveMapping(path: String): Option[Mapping] = {
    val tokens = path.split('.')
    val head = tokens.head
    this.subFields.get(head) match {
      case Some(prop) =>
        if (tokens.tails.isEmpty) Some(prop) else prop.resolveMapping(tokens.tails.mkString("."))
      case _ => None
    }

  }

  def diff(newMapping: Mapping): Option[Mapping] = None

  def addSubFields(sFields: Map[String, Mapping]): Mapping =
    self match {
      case m: BooleanMapping => m.copy(fields = m.fields ++ sFields)
      case m: DateMapping => m.copy(fields = m.fields ++ sFields)
      case m: DateNanosMapping => m.copy(fields = m.fields ++ sFields)
      case m: GeoPointMapping => m.copy(fields = m.fields ++ sFields)
      case m: GeoShapeMapping => m.copy(fields = m.fields ++ sFields)
      case m: IpMapping => m.copy(fields = m.fields ++ sFields)
      case m: NumberMapping => m match {
        case m2:IntegerMapping => m2.copy(fields = m.fields ++ sFields)
        case m2:LongMapping => m2.copy(fields = m.fields ++ sFields)
        case m2:ShortMapping => m2.copy(fields = m.fields ++ sFields)
        case m2:ByteMapping => m2.copy(fields = m.fields ++ sFields)
        case m2:DoubleMapping => m2.copy(fields = m.fields ++ sFields)
        case m2:HalfFloatMapping => m2.copy(fields = m.fields ++ sFields)
        case m2:ScaledFloatMapping => m2.copy(fields = m.fields ++ sFields)
        case m2:FloatMapping => m2.copy(fields = m.fields ++ sFields)
      } 
      case m: RangeMapping => m match {
        case m2: IntegerRangeMapping => m2.copy(fields = m.fields ++ sFields)
        case m2: DoubleRangeMapping => m2.copy(fields = m.fields ++ sFields)
        case m2: LongRangeMapping => m2.copy(fields = m.fields ++ sFields)
        case m2: FloatRangeMapping => m2.copy(fields = m.fields ++ sFields)
        case m2: DateRangeMapping => m2.copy(fields = m.fields ++ sFields)
        case m2: IpRangeMapping => m2.copy(fields = m.fields ++ sFields)
      }
      case m: NestedMapping => m.copy(properties = m.properties ++ sFields)
      case m: ObjectMapping => m.copy(properties = m.properties ++ sFields)
      case m: RootDocumentMapping => m.copy(properties = m.properties ++ sFields)
      case m: TextMapping => m.copy(fields = m.fields ++ sFields)
      case m: KeywordMapping => m.copy(fields = m.fields ++ sFields)
      case m: FlattenedMapping => m.copy(fields = m.fields ++ sFields)
      //toSkip
      case m => m
    }

  def setIndex(index: Boolean): Mapping =
    self match {
      case m: BooleanMapping => m.copy(index = index)
      case m: DateMapping => m.copy(index = index)
      case m: DateNanosMapping => m.copy(index = index)
      case m: GeoPointMapping => m.copy(index = index)
      case m: GeoShapeMapping => m.copy(index = index)
      case m: IpMapping => m.copy(index = index)
      case m: NumberMapping => m match {
        case m2: IntegerMapping => m2.copy(index=index)
        case m2: LongMapping => m2.copy(index=index)
        case m2: ShortMapping => m2.copy(index=index)
        case m2: ByteMapping => m2.copy(index=index)
        case m2: DoubleMapping => m2.copy(index=index)
        case m2: HalfFloatMapping => m2.copy(index=index)
        case m2: ScaledFloatMapping => m2.copy(index=index)
        case m2: FloatMapping => m2.copy(index=index)
      }
      case m: RangeMapping => m match {
        case m2: IntegerRangeMapping => m2.copy(index = index)
        case m2: DoubleRangeMapping => m2.copy(index = index)
        case m2: LongRangeMapping => m2.copy(index = index)
        case m2: FloatRangeMapping => m2.copy(index = index)
        case m2: DateRangeMapping => m2.copy(index = index)
        case m2: IpRangeMapping => m2.copy(index = index)
      }
      case m: NestedMapping => m.copy(enabled = index)
      case m: ObjectMapping => m.copy(enabled = index)
      case m: TextMapping => m.copy(index = index)
      case m: KeywordMapping => m.copy(index = index)
      case m: FlattenedMapping => m.copy(index = index)
      //toSkip
      case m => m
    }

}

/**
 * The type of a companion object for a sub-class of Mapping.
 */
trait MappingType[T <: Mapping] {

  /**
   * The name for this type.
   */
  val typeName: String
}

/**
 * The type of a companion object for a sub-class of Mapping.
 */
trait InternalMappingType {

  /**
   * The name for this type.
   */
  val typeName: String

}

sealed trait NumberMapping extends Mapping {
  def fields: Map[String, Mapping]
}

sealed trait RangeMapping extends Mapping {
  def fields: Map[String, Mapping]
}


object Mapping {

  implicit val myListString: JsonDecoder[List[String]] = DeriveJsonDecoder.gen[List[String]].orElse(
    JsonDecoder.string.mapOrFail {
      case value:String => Right(List(value))
    }
  )


  implicit final val decodeMapping: JsonDecoder[Mapping] = Json.Obj.decoder.mapOrFail{ c =>
      val typeName: FieldType = c.getOption[FieldType]("type").getOrElse(FieldType.`object`)
    typeName match {
      case FieldType.none => ???
      case FieldType.geo_point => c.as[GeoPointMapping]
      case FieldType.geo_shape => c.as[GeoShapeMapping]
      case FieldType.ip => c.as[IpMapping]
      case FieldType.binary => c.as[BinaryMapping]
      case FieldType.keyword => c.as[KeywordMapping]
      case FieldType.text => c.as[TextMapping]
      case FieldType.search_as_you_type => c.as[SearchAsYouTypeMapping]
      case FieldType.date =>  c.as[DateMapping]
      case FieldType.date_nanos => c.as[DateNanosMapping]
      case FieldType.boolean => c.as[BooleanMapping]
      case FieldType.completion => c.as[CompletionMapping]
      case FieldType.nested => c.as[NestedMapping]
      case FieldType.`object` => c.as[ObjectMapping]
      case FieldType.murmur3 => c.as[Murmur3Mapping]
      case FieldType.token_count =>  c.as[TokenCountMapping]
      case FieldType.percolator => c.as[PercolatorMapping]
      case FieldType.integer => c.as[IntegerMapping]
      case FieldType.long => c.as[LongMapping]
      case FieldType.short => c.as[ShortMapping]
      case FieldType.byte => c.as[ByteMapping]
      case FieldType.float => c.as[FloatMapping]
      case FieldType.half_float => c.as[HalfFloatMapping]
      case FieldType.scaled_float => c.as[ScaledFloatMapping]
      case FieldType.double => c.as[DoubleMapping]
      case FieldType.integer_range => c.as[IntegerRangeMapping]
      case FieldType.float_range => c.as[FloatRangeMapping]
      case FieldType.long_range => c.as[LongRangeMapping]
      case FieldType.double_range => c.as[DoubleRangeMapping]
      case FieldType.date_range => c.as[DateRangeMapping]
      case FieldType.ip_range => c.as[IpRangeMapping]
      case FieldType.alias => c.as[AliasMapping]
      case FieldType.join => c.as[JoinMapping]
      case FieldType.rank_feature => c.as[RankFeatureMapping]
      case FieldType.rank_features => c.as[RankFeaturesMapping]
      case FieldType.flattened => c.as[FlattenedMapping]
      case FieldType.shape => c.as[ShapeMapping]
      case FieldType.histogram => c.as[HistogramMapping]
      case FieldType.constant_keyword => c.as[ConstantKeywordMapping]
      case FieldType.aggregate_metric_double => c.as[AggregateMetricDoubleMapping]
      case FieldType.dense_vector => c.as[DenseVectorMapping]
      case FieldType.match_only_text => c.as[MatchOnlyTextMapping]
// Internals
      case FieldType._boost => c.as[BoostMapping]
      case FieldType._id => c.as[IdMapping]
      case FieldType._index => c.as[IndexMapping]
      case FieldType._parent => c.as[ParentMapping]
      case FieldType._routing => c.as[RoutingMapping]
      case FieldType._source => c.as[SourceMapping]
    }
    }

  implicit final val encodeMapping: JsonEncoder[Mapping] = DeriveJsonEncoder.gen[Mapping]

}

@jsonHint("binary")
final case class BinaryMapping(@jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = false, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty,
                               @JsonNoDefault fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping{
  def `type`: FieldType = FieldType.binary

}


object BinaryMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
implicit val jsonDecoder: JsonDecoder[BinaryMapping] = DeriveJsonDecoder.gen[BinaryMapping]
implicit val jsonEncoder: JsonEncoder[BinaryMapping] = DeriveJsonEncoder.gen[BinaryMapping]
}

@jsonHint("join")
final case class JoinMapping(@JsonNoDefault @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = false, @JsonNoDefault boost: Float = 1.0f, @JsonNoDefault @jsonField("index_options") indexOptions: Option[IndexOptions] = None, relations: Map[String, String] = Map.empty[String, String]) extends Mapping {
  def `type`: FieldType = FieldType.join
  override def similarity: Option[Similarity] = None
  override def fields: Map[String, Mapping] = Map.empty
  override def copyTo: Chunk[String] = Chunk.empty
}


object JoinMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "join"
implicit val jsonDecoder: JsonDecoder[JoinMapping] = DeriveJsonDecoder.gen[JoinMapping]
implicit val jsonEncoder: JsonEncoder[JoinMapping] = DeriveJsonEncoder.gen[JoinMapping]
}


@jsonHint("alias")
final case class AliasMapping(path: String) extends Mapping {
  def `type`: FieldType=FieldType.alias
  override def docValues: Option[Boolean] = None
  override def store: Boolean = false
  override def index: Boolean = false
  override def boost: Float = 1.0f
  override def indexOptions: Option[IndexOptions] = None
  override def similarity: Option[Similarity] = None
  override def fields: Map[String, Mapping] = Map.empty
  override def copyTo: Chunk[String] = Chunk.empty
}


object AliasMapping {
implicit val jsonDecoder: JsonDecoder[AliasMapping] = DeriveJsonDecoder.gen[AliasMapping]
implicit val jsonEncoder: JsonEncoder[AliasMapping] = DeriveJsonEncoder.gen[AliasMapping]
}

@jsonHint("dense_vector")
final case class DenseVectorMapping(path: String) extends Mapping {
  def `type`: FieldType=FieldType.dense_vector
  override def docValues: Option[Boolean] = None
  override def store: Boolean = false
  override def index: Boolean = false
  override def boost: Float = 1.0f
  override def indexOptions: Option[IndexOptions] = None
  override def similarity: Option[Similarity] = None
  override def fields: Map[String, Mapping] = Map.empty
  override def copyTo: Chunk[String] = Chunk.empty
}


object DenseVectorMapping {
  implicit val jsonDecoder: JsonDecoder[DenseVectorMapping] = DeriveJsonDecoder.gen[DenseVectorMapping]
  implicit val jsonEncoder: JsonEncoder[DenseVectorMapping] = DeriveJsonEncoder.gen[DenseVectorMapping]
}

@jsonHint("histogram")
final case class HistogramMapping(path: String) extends Mapping {
  def `type`: FieldType=FieldType.histogram
  override def docValues: Option[Boolean] = None
  override def store: Boolean = false
  override def index: Boolean = false
  override def boost: Float = 1.0f
  override def indexOptions: Option[IndexOptions] = None
  override def similarity: Option[Similarity] = None
  override def fields: Map[String, Mapping] = Map.empty
  override def copyTo: Chunk[String] = Chunk.empty
}


object HistogramMapping {
  implicit val jsonDecoder: JsonDecoder[HistogramMapping] = DeriveJsonDecoder.gen[HistogramMapping]
  implicit val jsonEncoder: JsonEncoder[HistogramMapping] = DeriveJsonEncoder.gen[HistogramMapping]
}

@jsonHint("aggregate_metric_double")
final case class AggregateMetricDoubleMapping(path: String) extends Mapping {
  def `type`: FieldType=FieldType.aggregate_metric_double
  override def docValues: Option[Boolean] = None
  override def store: Boolean = false
  override def index: Boolean = false
  override def boost: Float = 1.0f
  override def indexOptions: Option[IndexOptions] = None
  override def similarity: Option[Similarity] = None
  override def fields: Map[String, Mapping] = Map.empty
  override def copyTo: Chunk[String] = Chunk.empty
}


object AggregateMetricDoubleMapping {
  implicit val jsonDecoder: JsonDecoder[AggregateMetricDoubleMapping] = DeriveJsonDecoder.gen[AggregateMetricDoubleMapping]
  implicit val jsonEncoder: JsonEncoder[AggregateMetricDoubleMapping] = DeriveJsonEncoder.gen[AggregateMetricDoubleMapping]
}

@jsonHint("rank_feature")
final case class RankFeatureMapping() extends Mapping {
  def `type`: FieldType=FieldType.rank_features
  override def docValues: Option[Boolean] = None
  override def store: Boolean = false
  override def index: Boolean = false
  override def boost: Float = 1.0f
  override def indexOptions: Option[IndexOptions] = None
  override def similarity: Option[Similarity] = None
  override def fields: Map[String, Mapping] = Map.empty
  override def copyTo: Chunk[String] = Chunk.empty
}


object RankFeatureMapping {
  implicit val jsonDecoder: JsonDecoder[RankFeatureMapping] = DeriveJsonDecoder.gen[RankFeatureMapping]
  implicit val jsonEncoder: JsonEncoder[RankFeatureMapping] = DeriveJsonEncoder.gen[RankFeatureMapping]
}

@jsonHint("rank_features")
final case class RankFeaturesMapping() extends Mapping {
  def `type`: FieldType=FieldType.rank_features
  override def docValues: Option[Boolean] = None
  override def store: Boolean = false
  override def index: Boolean = false
  override def boost: Float = 1.0f
  override def indexOptions: Option[IndexOptions] = None
  override def similarity: Option[Similarity] = None
  override def fields: Map[String, Mapping] = Map.empty
  override def copyTo: Chunk[String] = Chunk.empty
}


object RankFeaturesMapping {
  implicit val jsonDecoder: JsonDecoder[RankFeaturesMapping] = DeriveJsonDecoder.gen[RankFeaturesMapping]
  implicit val jsonEncoder: JsonEncoder[RankFeaturesMapping] = DeriveJsonEncoder.gen[RankFeaturesMapping]
}


@jsonHint("boolean")
final case class BooleanMapping(@jsonField("null_value") nullValue: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.boolean
}


object BooleanMapping {
implicit val jsonDecoder: JsonDecoder[BooleanMapping] = DeriveJsonDecoder.gen[BooleanMapping]
implicit val jsonEncoder: JsonEncoder[BooleanMapping] = DeriveJsonEncoder.gen[BooleanMapping]
}



@jsonHint("completion")
final case class CompletionMapping(analyzer: Option[String] = None, @jsonField("search_analyzer") searchAnalyzer: Option[String] = None,
                                   @JsonNoDefault @jsonField("preserve_separators") preserveSeparators: Boolean = true,
                                   @JsonNoDefault @jsonField("preserve_position_increments") preservePositionIncrements: Boolean = true,
                                   @JsonNoDefault @jsonField("max_input_length") maxInputLength: Int = 50,
                                   @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false,
                                   @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.completion
}

object CompletionMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "completion"
implicit val jsonDecoder: JsonDecoder[CompletionMapping] = DeriveJsonDecoder.gen[CompletionMapping]
implicit val jsonEncoder: JsonEncoder[CompletionMapping] = DeriveJsonEncoder.gen[CompletionMapping]
}

@jsonHint("date")
final case class DateMapping(@jsonField("null_value") nullValue: Option[OffsetDateTime] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, locale: Option[String] = None, format: Option[String] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType = FieldType.date
}

object DateMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "date"
implicit val jsonDecoder: JsonDecoder[DateMapping] = DeriveJsonDecoder.gen[DateMapping]
implicit val jsonEncoder: JsonEncoder[DateMapping] = DeriveJsonEncoder.gen[DateMapping]
}

@jsonHint("date_nanos")
final case class DateNanosMapping(@jsonField("null_value") nullValue: Option[OffsetDateTime] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, locale: Option[String] = None, format: Option[String] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.date_nanos
}


object DateNanosMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
implicit val jsonDecoder: JsonDecoder[DateNanosMapping] = DeriveJsonDecoder.gen[DateNanosMapping]
implicit val jsonEncoder: JsonEncoder[DateNanosMapping] = DeriveJsonEncoder.gen[DateNanosMapping]
}

@jsonHint("flattened")
final case class FlattenedMapping(@JsonNoDefault norms: Boolean = false, 
                                  @jsonField("ignore_above") ignoreAbove: Option[Int] = None, 
                                  @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, 
                                  @jsonField("doc_values") docValues: Option[Boolean] = None, 
                                  @jsonField("normalizer") normalizer: Option[String] = None, 
                                  @jsonField("null_value") nullValue: Option[String] = None, 
                                  @JsonNoDefault store: Boolean = false, 
                                  @JsonNoDefault index: Boolean = true, 
                                  @JsonNoDefault boost: Float = 1.0f, 
                                  @jsonField("index_options") indexOptions: Option[IndexOptions] = None, 
                                  similarity: Option[Similarity] = None, 
                                  @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, 
                                  fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.flattened
}


object FlattenedMapping {
implicit val jsonDecoder: JsonDecoder[FlattenedMapping] = DeriveJsonDecoder.gen[FlattenedMapping]
implicit val jsonEncoder: JsonEncoder[FlattenedMapping] = DeriveJsonEncoder.gen[FlattenedMapping]
}

@jsonHint("percolator")
final case class PercolatorMapping(@JsonNoDefault norms: Boolean = false,
                                  @jsonField("ignore_above") ignoreAbove: Option[Int] = None,
                                  @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None,
                                  @jsonField("doc_values") docValues: Option[Boolean] = None,
                                  @jsonField("normalizer") normalizer: Option[String] = None,
                                  @jsonField("null_value") nullValue: Option[String] = None,
                                  @JsonNoDefault store: Boolean = false,
                                  @JsonNoDefault index: Boolean = true,
                                  @JsonNoDefault boost: Float = 1.0f,
                                  @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                                  similarity: Option[Similarity] = None,
                                  @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty,
                                  fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.percolator
}


object PercolatorMapping {
  implicit val jsonDecoder: JsonDecoder[PercolatorMapping] = DeriveJsonDecoder.gen[PercolatorMapping]
  implicit val jsonEncoder: JsonEncoder[PercolatorMapping] = DeriveJsonEncoder.gen[PercolatorMapping]
}


final case class FielddataFrequencyFilter(@JsonNoDefault min: Int = 0, @JsonNoDefault max: Int = Integer.MAX_VALUE, @JsonNoDefault @jsonField("min_segment_size") minSegmentSize: Int = 0)
object FielddataFrequencyFilter {
implicit val jsonDecoder: JsonDecoder[FielddataFrequencyFilter] = DeriveJsonDecoder.gen[FielddataFrequencyFilter]
implicit val jsonEncoder: JsonEncoder[FielddataFrequencyFilter] = DeriveJsonEncoder.gen[FielddataFrequencyFilter]
}

@jsonHint("geo_point")
final case class GeoPointMapping(@JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.geo_point
}


object GeoPointMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
implicit val jsonDecoder: JsonDecoder[GeoPointMapping] = DeriveJsonDecoder.gen[GeoPointMapping]
implicit val jsonEncoder: JsonEncoder[GeoPointMapping] = DeriveJsonEncoder.gen[GeoPointMapping]
}

@jsonHint("geo_shape")
final case class GeoShapeMapping(tree: Option[String] = None, @jsonField("tree_levels") treeLevels: Option[Int] = None, precision: Option[String] = None, @jsonField("distance_error_pct") distanceErrorPct: Option[String] = None, orientation: Option[String] = None, strategy: Option[String] = None, coerce: Option[Boolean] = None, @jsonField("points_only") pointsOnly: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.geo_shape
}


object GeoShapeMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
implicit val jsonDecoder: JsonDecoder[GeoShapeMapping] = DeriveJsonDecoder.gen[GeoShapeMapping]
implicit val jsonEncoder: JsonEncoder[GeoShapeMapping] = DeriveJsonEncoder.gen[GeoShapeMapping]
}

@jsonHint("shape")
final case class ShapeMapping(precision: Option[String] = None, @jsonField("distance_error_pct") distanceErrorPct: Option[String] = None, orientation: Option[String] = None, strategy: Option[String] = None, coerce: Option[Boolean] = None, @jsonField("points_only") pointsOnly: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.shape
}


object ShapeMapping {
  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
  implicit val jsonDecoder: JsonDecoder[ShapeMapping] = DeriveJsonDecoder.gen[ShapeMapping]
  implicit val jsonEncoder: JsonEncoder[ShapeMapping] = DeriveJsonEncoder.gen[ShapeMapping]
}


@jsonHint("ip")
final case class IpMapping(@jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.ip
}


object IpMapping {
implicit val jsonDecoder: JsonDecoder[IpMapping] = DeriveJsonDecoder.gen[IpMapping]
implicit val jsonEncoder: JsonEncoder[IpMapping] = DeriveJsonEncoder.gen[IpMapping]
}

@jsonHint("keyword")
final case class KeywordMapping(@JsonNoDefault norms: Boolean = false, @jsonField("ignore_above") ignoreAbove: Option[Int] = None, @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @jsonField("normalizer") normalizer: Option[String] = None, @jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.keyword
}


object KeywordMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
def code(name: String): (String, KeywordMapping) = name -> KeywordMapping()
implicit val jsonDecoder: JsonDecoder[KeywordMapping] = DeriveJsonDecoder.gen[KeywordMapping]
implicit val jsonEncoder: JsonEncoder[KeywordMapping] = DeriveJsonEncoder.gen[KeywordMapping]
}

@jsonHint("constant_keyword")
final case class ConstantKeywordMapping(@JsonNoDefault norms: Boolean = false, @jsonField("ignore_above") ignoreAbove: Option[Int] = None, @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @jsonField("normalizer") normalizer: Option[String] = None, @jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.constant_keyword
}


object ConstantKeywordMapping {
  implicit val jsonDecoder: JsonDecoder[ConstantKeywordMapping] = DeriveJsonDecoder.gen[ConstantKeywordMapping]
  implicit val jsonEncoder: JsonEncoder[ConstantKeywordMapping] = DeriveJsonEncoder.gen[ConstantKeywordMapping]
}

@jsonHint("murmur3")
final case class Murmur3Mapping(@JsonNoDefault norms: Boolean = false, @jsonField("ignore_above") ignoreAbove: Option[Int] = None, @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @jsonField("normalizer") normalizer: Option[String] = None, @jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType=FieldType.murmur3
}


object Murmur3Mapping {
  implicit val jsonDecoder: JsonDecoder[Murmur3Mapping] = DeriveJsonDecoder.gen[Murmur3Mapping]
  implicit val jsonEncoder: JsonEncoder[Murmur3Mapping] = DeriveJsonEncoder.gen[Murmur3Mapping]
}

@jsonHint("nested")
final case class NestedMapping(@JsonNoDefault properties: Map[String, Mapping], @JsonNoDefault dynamic: String = NestedMapping.defaultDynamic, @JsonNoDefault enabled: Boolean = NestedMapping.defaultEnabled, path: Option[String] = None, @jsonField("include_in_parent") includeInParent: Boolean = false) extends Mapping with MappingObject {
  def `type`: FieldType=FieldType.nested
  override def dynamicString: String = dynamic.toString
  override def docValues: Option[Boolean] = None
  override def copyTo: Chunk[String] = Chunk.empty
  override def store: Boolean = false
  override def index: Boolean = false
  override def boost: Float = 1.0f
  override def indexOptions: Option[IndexOptions] = None
  override def similarity: Option[Similarity] = None
  override def fields: Map[String, Mapping] = properties
}


object NestedMapping {
implicit val myBooleanDecoder: JsonDecoder[String] = JsonDecoder.string.orElse(
    JsonDecoder.boolean.mapOrFail {
      case true => Right("true")
      case false => Right("false")
    }
  )
val typeName = "nested"
val defaultDynamic = "true"
val defaultEnabled = true
implicit val jsonDecoder: JsonDecoder[NestedMapping] = DeriveJsonDecoder.gen[NestedMapping]
implicit val jsonEncoder: JsonEncoder[NestedMapping] = DeriveJsonEncoder.gen[NestedMapping]
}

/* Number Mappings*/
@jsonHint("integer")
final case class IntegerMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends NumberMapping {
  def `type`: FieldType=FieldType.integer
}
object IntegerMapping {
  implicit val jsonCodec: JsonCodec[IntegerMapping] = DeriveJsonCodec.gen[IntegerMapping]
}

@jsonHint("long")
final case class LongMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends NumberMapping {
  def `type`: FieldType=FieldType.long
}
object LongMapping {
  implicit val jsonCodec: JsonCodec[LongMapping] = DeriveJsonCodec.gen[LongMapping]
}


@jsonHint("short")
final case class ShortMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends NumberMapping {
  def `type`: FieldType=FieldType.short
}
object ShortMapping {
  implicit val jsonCodec: JsonCodec[ShortMapping] = DeriveJsonCodec.gen[ShortMapping]
}

@jsonHint("byte")
final case class ByteMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends NumberMapping {
  def `type`: FieldType=FieldType.byte
}
object ByteMapping {
  implicit val jsonCodec: JsonCodec[ByteMapping] = DeriveJsonCodec.gen[ByteMapping]
}

@jsonHint("double")
final case class DoubleMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends NumberMapping {
  def `type`: FieldType=FieldType.double
}
object DoubleMapping {
  implicit val jsonCodec: JsonCodec[DoubleMapping] = DeriveJsonCodec.gen[DoubleMapping]
}

@jsonHint("half_float")
final case class HalfFloatMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends NumberMapping {
  def `type`: FieldType=FieldType.half_float
}
object HalfFloatMapping {
  implicit val jsonCodec: JsonCodec[HalfFloatMapping] = DeriveJsonCodec.gen[HalfFloatMapping]
}

@jsonHint("scaled_float")
final case class ScaledFloatMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends NumberMapping {
  def `type`: FieldType=FieldType.scaled_float
}
object ScaledFloatMapping {
  implicit val jsonCodec: JsonCodec[ScaledFloatMapping] = DeriveJsonCodec.gen[ScaledFloatMapping]
}


@jsonHint("float")
final case class FloatMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends NumberMapping {
  def `type`: FieldType=FieldType.float
}
object FloatMapping {
  implicit val jsonCodec: JsonCodec[FloatMapping] = DeriveJsonCodec.gen[FloatMapping]
}

/* End Number Mappings */

/* Range Mappings*/
@jsonHint("integer_range")
final case class IntegerRangeMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends RangeMapping{
  def `type`: FieldType=FieldType.integer_range
}
object IntegerRangeMapping {
  implicit val jsonCodec: JsonCodec[IntegerRangeMapping] = DeriveJsonCodec.gen[IntegerRangeMapping]
}

@jsonHint("double_range")
final case class DoubleRangeMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends RangeMapping {
  def `type`: FieldType=FieldType.double_range
}
object DoubleRangeMapping {
  implicit val jsonCodec: JsonCodec[DoubleRangeMapping] = DeriveJsonCodec.gen[DoubleRangeMapping]
}

@jsonHint("long_range")
final case class LongRangeMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends RangeMapping {
  def `type`: FieldType=FieldType.long_range
}
object LongRangeMapping {
  implicit val jsonCodec: JsonCodec[LongRangeMapping] = DeriveJsonCodec.gen[LongRangeMapping]
}

@jsonHint("float_range")
final case class FloatRangeMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends RangeMapping {
  def `type`: FieldType=FieldType.float_range
}
object FloatRangeMapping {
  implicit val jsonCodec: JsonCodec[FloatRangeMapping] = DeriveJsonCodec.gen[FloatRangeMapping]
}


@jsonHint("date_range")
final case class DateRangeMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends RangeMapping {
  def `type`: FieldType=FieldType.date_range
}
object DateRangeMapping {
  implicit val jsonCodec: JsonCodec[DateRangeMapping] = DeriveJsonCodec.gen[DateRangeMapping]
}

@jsonHint("ip_range")
final case class IpRangeMapping(@jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends RangeMapping {
  def `type`: FieldType=FieldType.ip_range
}
object IpRangeMapping {
  implicit val jsonCodec: JsonCodec[IpRangeMapping] = DeriveJsonCodec.gen[IpRangeMapping]
}


/* End Range Mappings */

@jsonHint("object")
final case class ObjectMapping(@JsonNoDefault properties: Map[String, Mapping] = Map.empty[String, Mapping], @JsonNoDefault dynamic: Boolean = ObjectMapping.defaultDynamic, @JsonNoDefault enabled: Boolean = ObjectMapping.defaultEnabled, path: Option[String] = None, _source: Option[Map[String, Boolean]] = None, _type: Option[Map[String, Boolean]] = None, _all: Option[Map[String, Boolean]] = None) extends Mapping with MappingObject {
  def `type`: FieldType=FieldType.`object`
  def NAME = ObjectMapping.typeName
  override def dynamicString: String = dynamic.toString
  override def docValues: Option[Boolean] = None
  override def store: Boolean = false
  override def index: Boolean = enabled
  override def boost: Float = 1.0f
  override def indexOptions: Option[IndexOptions] = None
  override def similarity: Option[Similarity] = None
  override def fields: Map[String, Mapping] = properties
  override def copyTo: Chunk[String] = Chunk.empty
}


object ObjectMapping {
implicit val myBooleanDecoder: JsonDecoder[Boolean] = JsonDecoder.boolean.orElse(
    JsonDecoder.string.mapOrFail {
      case "true" => Right(true)
      case "false" => Right(false)
      case _ => Left("ObjectMapping: Invalid value for Boolean")
    }
  )
val typeName = "object"
val defaultDynamic = true
val defaultEnabled = true
def noIndex() = 
    new ObjectMapping(enabled = false)
implicit val jsonDecoder: JsonDecoder[ObjectMapping] = DeriveJsonDecoder.gen[ObjectMapping]
implicit val jsonEncoder: JsonEncoder[ObjectMapping] = DeriveJsonEncoder.gen[ObjectMapping]
}

/**
 * This Mapping must only be used at the top-level of a mapping tree, in other words it must not be contained within an ObjectMapping's properties.
 * TODO: dynamic_templates
 */
final case class RootDocumentMapping(@JsonNoDefault properties: Map[String, Mapping] = Map.empty[String, Mapping],
                                     @JsonNoDefault dynamic: String = RootDocumentMapping.defaultDynamic,
                                     @JsonNoDefault enabled: Boolean = RootDocumentMapping.defaultEnabled,
                                     path: Option[String] = None, analyzer: Option[String] = None,
                                     @jsonField("index_analyzer") indexAnalyzer: Option[String] = None,
                                     @jsonField("search_analyzer") searchAnalyzer: Option[String] = None,
                                     @jsonField("dynamic_date_formats") dynamicDateFormats: Option[Chunk[String]] = None,
                                     @jsonField("date_detection") @JsonNoDefault dateDetection: Boolean = RootDocumentMapping.defaultDateDetection,
                                     @jsonField("numeric_detection") @JsonNoDefault numericDetection: Boolean = RootDocumentMapping.defaultNumericDetection,
                                     @jsonField("_id") id: Option[IdMapping] = None, @jsonField("_index") _index: Option[IndexMapping] = None,
                                     @jsonField("_source") source: Option[SourceMapping] = None,
                                     @jsonField("_meta") meta: MetaObject = new MetaObject(),
                                     @jsonField("_routing") routing: Option[RoutingMapping] = None,
                                     @jsonField("_parent") parent: Option[ParentMapping] = None) extends Mapping with MappingObject {
  var context: Option[MetaAliasContext] = None

  override def dynamicString: String = dynamic


  override def `type`: FieldType = FieldType.`object`

  override def store: Boolean = false

  override def index: Boolean = true

  override def boost: Float = 1.0f

  override def indexOptions: Option[IndexOptions] = None

  override def similarity: Option[Similarity] = None

  override def fields: Map[String, Mapping] = properties

  override def copyTo: Chunk[String] = Chunk.empty

  override def docValues: Option[Boolean] = None

  def getFieldType(field: Array[String], props: Map[String, Mapping] = properties): Option[FieldType] = props.get(field.head) match {
    case Some(map) =>
      val mappingType = map.`type`
      if (field.tail.isEmpty) Some(mappingType) else {
        mappingType match {
          case FieldType.nested| FieldType.`object` => getFieldType(field.tail, map.asInstanceOf[ObjectMapping].properties)
          case _ => None
        }
      }
    case None =>
      None
  }
  def getNestedPaths(field: String): Chunk[FieldType] = {
    val tokens = field.split(".")
    if (tokens.isEmpty) Chunk.empty else Chunk.fromIterable(Range(0, tokens.length).flatMap(n => getFieldType(tokens.take(n)) match {
      case Some(t) =>
        if (t == FieldType.nested) Some(t) else None
      case None =>
        None
    }))
  }
  def aliasFor: List[MetaAlias] = this.meta.alias
  def hasGraphSupport: Boolean = false
}


object RootDocumentMapping {
val empty=new RootDocumentMapping()
implicit val myBooleanDecoder: JsonDecoder[String] = JsonDecoder.string.orElse(
    JsonDecoder.boolean.mapOrFail {
      case true => Right("true")
      case false => Right("false")
    }
  )
val typeName: String = "root"
val defaultDynamic = "true"
val defaultEnabled = true
val defaultDateDetection = true
val defaultNumericDetection = true
def fromJson(json: Json): RootDocumentMapping = 
    json.as[RootDocumentMapping] match {
      case Left(ex) => throw FrameworkException(ex)
      case Right(doc) => doc
    }
def build(properties: Map[String, Mapping]): RootDocumentMapping =
    RootDocumentMapping(properties = properties)
implicit val jsonDecoder: JsonDecoder[RootDocumentMapping] = DeriveJsonDecoder.gen[RootDocumentMapping]
implicit val jsonEncoder: JsonEncoder[RootDocumentMapping] = DeriveJsonEncoder.gen[RootDocumentMapping]
}



@jsonHint("search_as_you_type")
final case class SearchAsYouTypeMapping(@JsonNoDefault fielddata: Boolean = false, @jsonField("position_increment_gap") positionIncrementGap: Option[Int] = None, @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, @jsonField("fielddata_frequency_filter") fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @jsonField("analyzer") analyzer: Option[Analyzer] = None, @jsonField("search_analyzer") searchAnalyzer: Option[Boolean] = None, @jsonField("normalizer") normalizer: Option[String] = None, @jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType = FieldType.search_as_you_type
}


object SearchAsYouTypeMapping {
  implicit val jsonDecoder: JsonDecoder[SearchAsYouTypeMapping] = DeriveJsonDecoder.gen[SearchAsYouTypeMapping]
  implicit val jsonEncoder: JsonEncoder[SearchAsYouTypeMapping] = DeriveJsonEncoder.gen[SearchAsYouTypeMapping]
}

@jsonHint("match_only_text")
final case class MatchOnlyTextMapping(@JsonNoDefault fielddata: Boolean = false, @jsonField("position_increment_gap") positionIncrementGap: Option[Int] = None, @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, @jsonField("fielddata_frequency_filter") fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @jsonField("analyzer") analyzer: Option[Analyzer] = None, @jsonField("search_analyzer") searchAnalyzer: Option[Boolean] = None, @jsonField("normalizer") normalizer: Option[String] = None, @jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType = FieldType.match_only_text
}


object MatchOnlyTextMapping {
  implicit val jsonDecoder: JsonDecoder[MatchOnlyTextMapping] = DeriveJsonDecoder.gen[MatchOnlyTextMapping]
  implicit val jsonEncoder: JsonEncoder[MatchOnlyTextMapping] = DeriveJsonEncoder.gen[MatchOnlyTextMapping]
}

@jsonHint("text")
final case class TextMapping(@JsonNoDefault fielddata: Boolean = false, @jsonField("position_increment_gap") positionIncrementGap: Option[Int] = None, @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, @jsonField("fielddata_frequency_filter") fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @jsonField("analyzer") analyzer: Option[Analyzer] = None, @jsonField("search_analyzer") searchAnalyzer: Option[Boolean] = None, @jsonField("normalizer") normalizer: Option[String] = None, @jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType = FieldType.text
}


object TextMapping {
implicit val jsonDecoder: JsonDecoder[TextMapping] = DeriveJsonDecoder.gen[TextMapping]
implicit val jsonEncoder: JsonEncoder[TextMapping] = DeriveJsonEncoder.gen[TextMapping]
}

@jsonHint("token_count")
final case class TokenCountMapping(@jsonField("null_value") nullValue: Option[Int] = None, analyzer: Option[String] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping {
  def `type`: FieldType = FieldType.token_count
}


object TokenCountMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "token_count"
implicit val jsonDecoder: JsonDecoder[TokenCountMapping] = DeriveJsonDecoder.gen[TokenCountMapping]
implicit val jsonEncoder: JsonEncoder[TokenCountMapping] = DeriveJsonEncoder.gen[TokenCountMapping]
}

sealed trait InternalMapping extends Mapping {
  def NAME: FieldType

  def `type`: FieldType = NAME

  def metadata: MetaField = MetaField()

  override def store: Boolean = false

  override def index: Boolean = false

  override def boost: Float = 1.0f

  override def indexOptions: Option[IndexOptions] = None

  override def similarity: Option[Similarity] = None

  override def fields: Map[String, Mapping] = Map.empty[String, Mapping]

  override def copyTo: Chunk[String] = Chunk.empty

  /**
   * Make a JSON representation. Must return a Json.Obj("field" -> Json.Obj(...)).
   */
  override def docValues: Option[Boolean] = None


}

final case class BoostMapping(name: Option[String] = None, @jsonField("null_value") nullValue: Option[Float] = None) extends InternalMapping {
  override def NAME = FieldType._boost

}


object BoostMapping {
val typeName = "_boost"
implicit val jsonDecoder: JsonDecoder[BoostMapping] = DeriveJsonDecoder.gen[BoostMapping]
implicit val jsonEncoder: JsonEncoder[BoostMapping] = DeriveJsonEncoder.gen[BoostMapping]
}

final case class IdMapping(name: Option[String] = None) extends InternalMapping {
  def field = "_id"
  override def NAME = FieldType._id
}


object IdMapping {
implicit val jsonDecoder: JsonDecoder[IdMapping] = DeriveJsonDecoder.gen[IdMapping]
implicit val jsonEncoder: JsonEncoder[IdMapping] = DeriveJsonEncoder.gen[IdMapping]
}

final case class IndexMapping(enabled: Option[Boolean] = None, path: Option[String] = None) extends InternalMapping {
  def field = "_index"
  override def NAME = FieldType._index
}


object IndexMapping {
implicit val jsonDecoder: JsonDecoder[IndexMapping] = DeriveJsonDecoder.gen[IndexMapping]
implicit val jsonEncoder: JsonEncoder[IndexMapping] = DeriveJsonEncoder.gen[IndexMapping]
}

final case class ParentMapping(postingsFormat: Option[String] = None, path: Option[String] = None) extends InternalMapping {
  def field = "_parent"
  override def NAME = FieldType._parent
}


object ParentMapping {
implicit val jsonDecoder: JsonDecoder[ParentMapping] = DeriveJsonDecoder.gen[ParentMapping]
implicit val jsonEncoder: JsonEncoder[ParentMapping] = DeriveJsonEncoder.gen[ParentMapping]
}

final case class RoutingMapping(var _required: Option[Boolean] = None, path: Option[String] = None) extends InternalMapping {
  def field = "_routing"
  override def NAME = FieldType._routing
}

object RoutingMapping {
implicit val jsonDecoder: JsonDecoder[RoutingMapping] = DeriveJsonDecoder.gen[RoutingMapping]
implicit val jsonEncoder: JsonEncoder[RoutingMapping] = DeriveJsonEncoder.gen[RoutingMapping]
}

final case class SourceMapping(enabled: Option[Boolean] = None) extends InternalMapping {
  def field = "_source"
  override def NAME = FieldType._source
}


object SourceMapping {
implicit val jsonDecoder: JsonDecoder[SourceMapping] = DeriveJsonDecoder.gen[SourceMapping]
implicit val jsonEncoder: JsonEncoder[SourceMapping] = DeriveJsonEncoder.gen[SourceMapping]
}
// format: on
