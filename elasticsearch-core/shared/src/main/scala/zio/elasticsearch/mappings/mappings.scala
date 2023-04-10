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
sealed trait Mapping { self =>

  def `type`: String

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
      case m: DateTimeMapping => m.fields
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
      case _: InternalMapping => Map.empty[String, Mapping]
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
      case m: BinaryMapping => m
      case m: JoinMapping => m
      case m: BooleanMapping => m.copy(fields = m.fields ++ sFields)
      case m: DateTimeMapping => m.copy(fields = m.fields ++ sFields)
      case m: DateNanosMapping => m.copy(fields = m.fields ++ sFields)
      case m: GeoPointMapping => m.copy(fields = m.fields ++ sFields)
      case m: GeoShapeMapping => m.copy(fields = m.fields ++ sFields)
      case m: IpMapping => m.copy(fields = m.fields ++ sFields)
      case m: NumberMapping => m.copy(fields = m.fields ++ sFields)
      case m: RangeMapping => m.copy(fields = m.fields ++ sFields)
      case m: NestedMapping => m.copy(properties = m.properties ++ sFields)
      case m: ObjectMapping => m.copy(properties = m.properties ++ sFields)
      case m: RootDocumentMapping => m.copy(properties = m.properties ++ sFields)
      case m: TextMapping => m.copy(fields = m.fields ++ sFields)
      case m: KeywordMapping => m.copy(fields = m.fields ++ sFields)
      case m: FlattenedMapping => m.copy(fields = m.fields ++ sFields)
      case m: TokenCountMapping => m
      case m: CompletionMapping => m
      case m: AliasMapping => m
      //toSkip
      case m: InternalMapping => m
    }
  
  def setIndex(index: Boolean): Mapping = 
    self match {
      case m: BinaryMapping => m
      case m: JoinMapping => m
      case m: BooleanMapping => m.copy(index = index)
      case m: DateTimeMapping => m.copy(index = index)
      case m: DateNanosMapping => m.copy(index = index)
      case m: RootDocumentMapping => m
      case m: GeoPointMapping => m.copy(index = index)
      case m: GeoShapeMapping => m.copy(index = index)
      case m: IpMapping => m.copy(index = index)
      case m: NumberMapping => m.copy(index = index)
      case m: RangeMapping => m.copy(index = index)
      case m: NestedMapping => m.copy(enabled = index)
      case m: ObjectMapping => m.copy(enabled = index)
      case m: CompletionMapping => m
      case m: TextMapping => m.copy(index = index)
      case m: KeywordMapping => m.copy(index = index)
      case m: FlattenedMapping => m.copy(index = index)
      case m: AliasMapping => m
      case m: TokenCountMapping => m
      //toSkip
      case m: InternalMapping => m
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

object Mapping {

  implicit val myListString: JsonDecoder[List[String]] = DeriveJsonDecoder.gen[List[String]].orElse(
    JsonDecoder.string.mapOrFail {
      case value:String => Right(List(value))
    }
  )


  implicit final val decodeMapping: JsonDecoder[Mapping] = Json.Obj.decoder.mapOrFail{ c =>
      val typeName: String = c.getOption[String]("type").getOrElse("object")
      typeName match {
        case ObjectMapping.typeName => c.as[ObjectMapping]
        case NestedMapping.typeName => c.as[NestedMapping]
        case TextMapping.typeName => c.as[TextMapping]
        case KeywordMapping.typeName => c.as[KeywordMapping]
        case FlattenedMapping.typeName => c.as[FlattenedMapping]
        case TokenCountMapping.typeName => c.as[TokenCountMapping]
        case DateTimeMapping.typeName => c.as[DateTimeMapping]
        case BooleanMapping.typeName => c.as[BooleanMapping]
        case JoinMapping.typeName => c.as[JoinMapping]
        case BinaryMapping.typeName => c.as[BinaryMapping]
        case GeoPointMapping.typeName => c.as[GeoPointMapping]
        case GeoShapeMapping.typeName => c.as[GeoPointMapping] //TODO define geoShapie Mapping
        case IpMapping.typeName => c.as[IpMapping]
        case CompletionMapping.typeName => c.as[CompletionMapping]
        case "string" => //to manage very old mappings ES 2.x
          val analyzed = c.getOption[String]("index").getOrElse("yes")
          if (analyzed == "not_analyzed") {
            c.as[KeywordMapping]
          } else {
            c.as[TextMapping]
          }
        case AliasMapping.typeName => c.as[AliasMapping]
        case BoostMapping.typeName => c.as[BoostMapping]
        case IdMapping.typeName => c.as[IdMapping]
        case IndexMapping.typeName => c.as[IndexMapping]
        case ParentMapping.typeName => c.as[ParentMapping]
        case RoutingMapping.typeName => c.as[RoutingMapping]
        case SourceMapping.typeName => c.as[SourceMapping]
        case s:String if NumberMappingType.withNameInsensitiveOption(s).isDefined =>
          c.as[NumberMapping]
        case s:String if RangeMappingType.withNameInsensitiveOption(s).isDefined =>
          c.as[RangeMapping]


      }

    }

  implicit final val encodeMapping: JsonEncoder[Mapping] = DeriveJsonEncoder.gen[Mapping]
//  {
//    JsonEncoder.instance {
//      case m: KeywordMapping => m.asJson
//      case m: TextMapping => m.asJson
//      case m: JoinMapping => m.asJson
//      case m: BooleanMapping => m.asJson
//      case m: CompletionMapping => m.asJson
//      case m: DateTimeMapping => m.asJson
//      case m: DateNanosMapping => m.asJson
//      case m: RootDocumentMapping => m.asJson
//      case m: FlattenedMapping => m.asJson
//      case m: GeoPointMapping => m.asJson
//      case m: GeoShapeMapping => m.asJson
//      case m: IpMapping => m.asJson
//      case m: NumberMapping => m.asJson
//      case m: RangeMapping => m.asJson
//      case m: NestedMapping => m.asJson
//      case m: ObjectMapping => m.asJson
//      case m: TokenCountMapping => m.asJson
//      case m: AliasMapping => m.asJson
//      case m: BinaryMapping => m.asJson
//      //internals
//      case m: BoostMapping => m.asJson
//      case m: IdMapping => m.asJson
//      case m: IndexMapping => m.asJson
//      case m: ParentMapping => m.asJson
//      case m: RoutingMapping => m.asJson
//      case m: SourceMapping => m.asJson
//      case m: TypeMapping => m.asJson
//    }
//  }

//  mappings match {
//    case Json.Obj.fromMap(Map((indexName, Json.Obj.fromMap(Map(("mappings", jsMappings)))))) => jsMappings match {
//      case Json.Obj(fields) => fields.toSeq.map(fromJsonRoot)
//      case _ => Map.empty[String, Mapping]
//    }
//    case _ => throw NoSqlSearchException(status = -1, msg = "Bad mappings received in mappingsFromJson.", json = mappings)
//  }

//  /**
//    * The reads expects JSON of the form
//    * { <index> : { "mappings" : { <type> : { "properties" :  ... } } } }
//    */
//  implicit val reads: Reads[Mapping] = Reads {
//    case json: Json.Obj => JsSuccess(mappingsFromJson(json).head)
//    case mappings => throw NoSqlSearchException(status = -1, msg = "Bad mappings received in reads.", json = mappings)
//  }

}

@jsonHint("binary")
final case class BinaryMapping(@jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = false, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, @JsonNoDefault fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = BinaryMapping.typeName) extends Mapping


object BinaryMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "binary"
implicit val jsonDecoder: JsonDecoder[BinaryMapping] = DeriveJsonDecoder.gen[BinaryMapping]
implicit val jsonEncoder: JsonEncoder[BinaryMapping] = DeriveJsonEncoder.gen[BinaryMapping]
}

@jsonHint("join")
final case class JoinMapping(@JsonNoDefault @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = false, @JsonNoDefault boost: Float = 1.0f, @JsonNoDefault @jsonField("index_options") indexOptions: Option[IndexOptions] = None, relations: Map[String, String] = Map.empty[String, String], `type`: String = JoinMapping.typeName) extends Mapping {
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
final case class AliasMapping(path: String, `type`: String = AliasMapping.typeName) extends Mapping {
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
val typeName = "alias"
implicit val jsonDecoder: JsonDecoder[AliasMapping] = DeriveJsonDecoder.gen[AliasMapping]
implicit val jsonEncoder: JsonEncoder[AliasMapping] = DeriveJsonEncoder.gen[AliasMapping]
}

@jsonHint("boolean")
final case class BooleanMapping(`type`: String = BooleanMapping.typeName, @jsonField("null_value") nullValue: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping


object BooleanMapping {
implicit val myStringList = Mapping.myListString
val typeName = "boolean"
implicit val jsonDecoder: JsonDecoder[BooleanMapping] = DeriveJsonDecoder.gen[BooleanMapping]
implicit val jsonEncoder: JsonEncoder[BooleanMapping] = DeriveJsonEncoder.gen[BooleanMapping]
}



@jsonHint("completion")
final case class CompletionMapping(analyzer: Option[String] = None, @jsonField("search_analyzer") searchAnalyzer: Option[String] = None, @JsonNoDefault @jsonField("preserve_separators") preserveSeparators: Boolean = true, @JsonNoDefault @jsonField("preserve_position_increments") preservePositionIncrements: Boolean = true, @JsonNoDefault @jsonField("max_input_length") maxInputLength: Int = 50, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = CompletionMapping.typeName) extends Mapping


object CompletionMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "completion"
implicit val jsonDecoder: JsonDecoder[CompletionMapping] = DeriveJsonDecoder.gen[CompletionMapping]
implicit val jsonEncoder: JsonEncoder[CompletionMapping] = DeriveJsonEncoder.gen[CompletionMapping]
}

@jsonHint("date")
final case class DateTimeMapping(@jsonField("null_value") nullValue: Option[OffsetDateTime] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, locale: Option[String] = None, format: Option[String] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = DateTimeMapping.typeName) extends Mapping


object DateTimeMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "date"
implicit val jsonDecoder: JsonDecoder[DateTimeMapping] = DeriveJsonDecoder.gen[DateTimeMapping]
implicit val jsonEncoder: JsonEncoder[DateTimeMapping] = DeriveJsonEncoder.gen[DateTimeMapping]
}

@jsonHint("date_nanos")
final case class DateNanosMapping(@jsonField("null_value") nullValue: Option[OffsetDateTime] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, locale: Option[String] = None, format: Option[String] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = DateNanosMapping.typeName) extends Mapping


object DateNanosMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "date_nanos"
implicit val jsonDecoder: JsonDecoder[DateNanosMapping] = DeriveJsonDecoder.gen[DateNanosMapping]
implicit val jsonEncoder: JsonEncoder[DateNanosMapping] = DeriveJsonEncoder.gen[DateNanosMapping]
}

@jsonHint("flattened")
final case class FlattenedMapping(@JsonNoDefault norms: Boolean = false, @jsonField("ignore_above") ignoreAbove: Option[Int] = None, @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @jsonField("normalizer") normalizer: Option[String] = None, @jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = FlattenedMapping.typeName) extends Mapping


object FlattenedMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "flattened"
implicit val jsonDecoder: JsonDecoder[FlattenedMapping] = DeriveJsonDecoder.gen[FlattenedMapping]
implicit val jsonEncoder: JsonEncoder[FlattenedMapping] = DeriveJsonEncoder.gen[FlattenedMapping]
}


final case class FielddataFrequencyFilter(@JsonNoDefault min: Int = 0, @JsonNoDefault max: Int = Integer.MAX_VALUE, @JsonNoDefault @jsonField("min_segment_size") minSegmentSize: Int = 0)
object FielddataFrequencyFilter {
implicit val jsonDecoder: JsonDecoder[FielddataFrequencyFilter] = DeriveJsonDecoder.gen[FielddataFrequencyFilter]
implicit val jsonEncoder: JsonEncoder[FielddataFrequencyFilter] = DeriveJsonEncoder.gen[FielddataFrequencyFilter]
}

@jsonHint("geo_point")
final case class GeoPointMapping(@JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = GeoPointMapping.typeName) extends Mapping


object GeoPointMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "geo_point"
implicit val jsonDecoder: JsonDecoder[GeoPointMapping] = DeriveJsonDecoder.gen[GeoPointMapping]
implicit val jsonEncoder: JsonEncoder[GeoPointMapping] = DeriveJsonEncoder.gen[GeoPointMapping]
}

@jsonHint("geo_shape")
final case class GeoShapeMapping(tree: Option[String] = None, @jsonField("tree_levels") treeLevels: Option[Int] = None, precision: Option[String] = None, @jsonField("distance_error_pct") distanceErrorPct: Option[String] = None, orientation: Option[String] = None, strategy: Option[String] = None, coerce: Option[Boolean] = None, @jsonField("points_only") pointsOnly: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = GeoShapeMapping.typeName) extends Mapping


object GeoShapeMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "geo_shape"
implicit val jsonDecoder: JsonDecoder[GeoShapeMapping] = DeriveJsonDecoder.gen[GeoShapeMapping]
implicit val jsonEncoder: JsonEncoder[GeoShapeMapping] = DeriveJsonEncoder.gen[GeoShapeMapping]
}

@jsonHint("ip")
final case class IpMapping(@jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = IpMapping.typeName) extends Mapping


object IpMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "ip"
implicit val jsonDecoder: JsonDecoder[IpMapping] = DeriveJsonDecoder.gen[IpMapping]
implicit val jsonEncoder: JsonEncoder[IpMapping] = DeriveJsonEncoder.gen[IpMapping]
}

@jsonHint("keyword")
final case class KeywordMapping(@JsonNoDefault norms: Boolean = false, @jsonField("ignore_above") ignoreAbove: Option[Int] = None, @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @jsonField("normalizer") normalizer: Option[String] = None, @jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = KeywordMapping.typeName) extends Mapping


object KeywordMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "keyword"
def code(name: String): (String, KeywordMapping) = name -> KeywordMapping()
implicit val jsonDecoder: JsonDecoder[KeywordMapping] = DeriveJsonDecoder.gen[KeywordMapping]
implicit val jsonEncoder: JsonEncoder[KeywordMapping] = DeriveJsonEncoder.gen[KeywordMapping]
}

@jsonHint("nested")
final case class NestedMapping(@JsonNoDefault properties: Map[String, Mapping], @JsonNoDefault dynamic: String = NestedMapping.defaultDynamic, @JsonNoDefault enabled: Boolean = NestedMapping.defaultEnabled, path: Option[String] = None, @jsonField("include_in_parent") includeInParent: Boolean = false, `type`: String = NestedMapping.typeName) extends Mapping with MappingObject {
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

final case class NumberMapping(@jsonField("type") `type`: String, @jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping


object NumberMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "number"
implicit val jsonDecoder: JsonDecoder[NumberMapping] = DeriveJsonDecoder.gen[NumberMapping]
implicit val jsonEncoder: JsonEncoder[NumberMapping] = DeriveJsonEncoder.gen[NumberMapping]
}

@jsonHint("object")
final case class ObjectMapping(@JsonNoDefault properties: Map[String, Mapping] = Map.empty[String, Mapping], @JsonNoDefault dynamic: Boolean = ObjectMapping.defaultDynamic, @JsonNoDefault enabled: Boolean = ObjectMapping.defaultEnabled, path: Option[String] = None, _source: Option[Map[String, Boolean]] = None, _type: Option[Map[String, Boolean]] = None, _all: Option[Map[String, Boolean]] = None, `type`: String = ObjectMapping.typeName) extends Mapping with MappingObject {
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

final case class RangeMapping(@jsonField("type") `type`: String, @jsonField("null_value") nullValue: Option[Json] = None, @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false, coerce: Option[Boolean] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping]) extends Mapping


object RangeMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "range"
implicit val jsonDecoder: JsonDecoder[RangeMapping] = DeriveJsonDecoder.gen[RangeMapping]
implicit val jsonEncoder: JsonEncoder[RangeMapping] = DeriveJsonEncoder.gen[RangeMapping]
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

  override def `type`: String = RootDocumentMapping.typeName

  override def store: Boolean = false

  override def index: Boolean = true

  override def boost: Float = 1.0f

  override def indexOptions: Option[IndexOptions] = None

  override def similarity: Option[Similarity] = None

  override def fields: Map[String, Mapping] = properties

  override def copyTo: Chunk[String] = Chunk.empty

  override def docValues: Option[Boolean] = None

  def getFieldType(field: Array[String], props: Map[String, Mapping] = properties): Option[String] = props.get(field.head) match {
    case Some(map) =>
      val mappingType = map.`type`
      if (field.tail.isEmpty) Some(mappingType) else {
        mappingType match {
          case s: String if s == ObjectMapping.typeName =>
            getFieldType(field.tail, map.asInstanceOf[ObjectMapping].properties)
          case s: String if s == NestedMapping.typeName =>
            getFieldType(field.tail, map.asInstanceOf[NestedMapping].properties)
          case _ =>
            None
        }
      }
    case None =>
      None
  }
  def getNestedPaths(field: String): Chunk[String] = {
    val tokens = field.split(".")
    if (tokens.isEmpty) Chunk.empty else Chunk.fromIterable(Range(0, tokens.length).flatMap(n => getFieldType(tokens.take(n)) match {
      case Some(t) =>
        if (t == NestedMapping.typeName) Some(t) else None
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

@jsonHint("text")
final case class TextMapping(@JsonNoDefault fielddata: Boolean = false, @jsonField("position_increment_gap") positionIncrementGap: Option[Int] = None, @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None, @jsonField("fielddata_frequency_filter") fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @jsonField("analyzer") analyzer: Option[Analyzer] = None, @jsonField("search_analyzer") searchAnalyzer: Option[Boolean] = None, @jsonField("normalizer") normalizer: Option[String] = None, @jsonField("null_value") nullValue: Option[String] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = TextMapping.typeName) extends Mapping


object TextMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "text"
implicit val jsonDecoder: JsonDecoder[TextMapping] = DeriveJsonDecoder.gen[TextMapping]
implicit val jsonEncoder: JsonEncoder[TextMapping] = DeriveJsonEncoder.gen[TextMapping]
}

@jsonHint("token_count")
final case class TokenCountMapping(@jsonField("null_value") nullValue: Option[Int] = None, analyzer: Option[String] = None, @jsonField("doc_values") docValues: Option[Boolean] = None, @JsonNoDefault store: Boolean = false, @JsonNoDefault index: Boolean = true, @JsonNoDefault boost: Float = 1.0f, @jsonField("index_options") indexOptions: Option[IndexOptions] = None, similarity: Option[Similarity] = None, @jsonField("copy_to") copyTo: Chunk[String] = Chunk.empty, fields: Map[String, Mapping] = Map.empty[String, Mapping], `type`: String = TextMapping.typeName) extends Mapping


object TokenCountMapping {
implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString
val typeName = "token_count"
implicit val jsonDecoder: JsonDecoder[TokenCountMapping] = DeriveJsonDecoder.gen[TokenCountMapping]
implicit val jsonEncoder: JsonEncoder[TokenCountMapping] = DeriveJsonEncoder.gen[TokenCountMapping]
}

sealed trait InternalMapping extends Mapping {
  def NAME: String

  def `type`: String = NAME

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

final case class BoostMapping(name: Option[String] = None, @jsonField("null_value") nullValue: Option[Float] = None) extends InternalMapping { override def NAME = BoostMapping.typeName }


object BoostMapping {
val typeName = "_boost"
implicit val jsonDecoder: JsonDecoder[BoostMapping] = DeriveJsonDecoder.gen[BoostMapping]
implicit val jsonEncoder: JsonEncoder[BoostMapping] = DeriveJsonEncoder.gen[BoostMapping]
}

final case class IdMapping(name: Option[String] = None) extends InternalMapping {
  def field = "_id"
  override def NAME = IdMapping.typeName
}


object IdMapping {
val typeName = "_id"
implicit val jsonDecoder: JsonDecoder[IdMapping] = DeriveJsonDecoder.gen[IdMapping]
implicit val jsonEncoder: JsonEncoder[IdMapping] = DeriveJsonEncoder.gen[IdMapping]
}

final case class IndexMapping(enabled: Option[Boolean] = None, path: Option[String] = None) extends InternalMapping {
  def field = "_index"
  override def NAME = IndexMapping.typeName
}


object IndexMapping {
val typeName = "_index"
implicit val jsonDecoder: JsonDecoder[IndexMapping] = DeriveJsonDecoder.gen[IndexMapping]
implicit val jsonEncoder: JsonEncoder[IndexMapping] = DeriveJsonEncoder.gen[IndexMapping]
}

final case class ParentMapping(postingsFormat: Option[String] = None, path: Option[String] = None) extends InternalMapping {
  def field = "_parent"
  override def NAME = ParentMapping.typeName
}


object ParentMapping {
val typeName = "_parent"
implicit val jsonDecoder: JsonDecoder[ParentMapping] = DeriveJsonDecoder.gen[ParentMapping]
implicit val jsonEncoder: JsonEncoder[ParentMapping] = DeriveJsonEncoder.gen[ParentMapping]
}

final case class RoutingMapping(var _required: Option[Boolean] = None, path: Option[String] = None) extends InternalMapping {
  def field = "_routing"
  override def NAME = RoutingMapping.typeName
}

object RoutingMapping {
val typeName = "_routing"
implicit val jsonDecoder: JsonDecoder[RoutingMapping] = DeriveJsonDecoder.gen[RoutingMapping]
implicit val jsonEncoder: JsonEncoder[RoutingMapping] = DeriveJsonEncoder.gen[RoutingMapping]
}

final case class SourceMapping(enabled: Option[Boolean] = None) extends InternalMapping {
  def field = "_source"
  override def NAME = SourceMapping.typeName
}


object SourceMapping {
val typeName = "_source"
implicit val jsonDecoder: JsonDecoder[SourceMapping] = DeriveJsonDecoder.gen[SourceMapping]
implicit val jsonEncoder: JsonEncoder[SourceMapping] = DeriveJsonEncoder.gen[SourceMapping]
}
// format: on
