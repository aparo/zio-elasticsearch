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

package zio.elasticsearch.mappings

import java.time.OffsetDateTime

import zio.exception._
import elasticsearch.analyzers.Analyzer
import zio.json.ast.Json
import zio.json._
import io.circe.derivation.annotations.{ JsonKey, JsonNoDefault, _ }
import zio.json._

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

  def copyTo: List[String]

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

  implicit val myListString: JsonDecoder[List[String]] = JsonDecoder.decodeList[String].or(
    JsonDecoder.decodeString.emap {
      case value => Right(List(value))
    }
  )


  implicit final val decodeMapping: JsonDecoder[Mapping] =
    JsonDecoder.instance { c =>
      val typeName: String = c.get[String]("type").getOrElse("object")
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
          val analyzed = c.get[String]("index").getOrElse("yes")
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
        case TypeMapping.typeName => c.as[TypeMapping]
        case s:String if NumberMappingType.withNameInsensitiveOption(s).isDefined =>
          c.as[NumberMapping]
        case s:String if RangeMappingType.withNameInsensitiveOption(s).isDefined =>
          c.as[RangeMapping]


      }

    }

  implicit final val encodeMapping: JsonEncoder[Mapping] = {
    JsonEncoder.instance {
      case m: KeywordMapping => m.asJson
      case m: TextMapping => m.asJson
      case m: JoinMapping => m.asJson
      case m: BooleanMapping => m.asJson
      case m: CompletionMapping => m.asJson
      case m: DateTimeMapping => m.asJson
      case m: DateNanosMapping => m.asJson
      case m: RootDocumentMapping => m.asJson
      case m: FlattenedMapping => m.asJson
      case m: GeoPointMapping => m.asJson
      case m: GeoShapeMapping => m.asJson
      case m: IpMapping => m.asJson
      case m: NumberMapping => m.asJson
      case m: RangeMapping => m.asJson
      case m: NestedMapping => m.asJson
      case m: ObjectMapping => m.asJson
      case m: TokenCountMapping => m.asJson
      case m: AliasMapping => m.asJson
      case m: BinaryMapping => m.asJson
      //internals
      case m: BoostMapping => m.asJson
      case m: IdMapping => m.asJson
      case m: IndexMapping => m.asJson
      case m: ParentMapping => m.asJson
      case m: RoutingMapping => m.asJson
      case m: SourceMapping => m.asJson
      case m: TypeMapping => m.asJson
    }
  }

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

@jsonDerive
case class BinaryMapping(@jsonField("doc_values") docValues: Option[Boolean] = None,
                         @JsonNoDefault store: Boolean = false,
                         @JsonNoDefault index: Boolean = false,
                         @JsonNoDefault boost: Float = 1.0f,
                         @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                         similarity: Option[Similarity] = None,
                         @jsonField("copy_to") copyTo: List[String] = Nil,
                         @JsonNoDefault fields: Map[String, Mapping] = Map.empty[String, Mapping],
                         `type`: String = BinaryMapping.typeName)
    extends Mapping {

}

object BinaryMapping extends MappingType[BinaryMapping] {

  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "binary"

}

@jsonDerive
case class JoinMapping(@JsonNoDefault @jsonField("doc_values") docValues: Option[Boolean] = None,
                         @JsonNoDefault store: Boolean = false,
                         @JsonNoDefault index: Boolean = false,
                         @JsonNoDefault boost: Float = 1.0f,
                       @JsonNoDefault @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                       relations: Map[String, String] = Map.empty[String,String],
                         `type`: String = JoinMapping.typeName)
  extends Mapping {
  override def similarity: Option[Similarity] = None

  override def fields: Map[String, Mapping] = Map.empty

  override def copyTo: List[String] = Nil
}

object JoinMapping extends MappingType[JoinMapping] {

  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "join"

}


@jsonDerive
case class AliasMapping(
                         path:String,
                       `type`: String = AliasMapping.typeName)
  extends Mapping {


  override def docValues: Option[Boolean] = None

  override def store: Boolean = false

  override def index: Boolean = false

  override def boost: Float = 1.0f

  override def indexOptions: Option[IndexOptions] = None

  override def similarity: Option[Similarity] = None

  override def fields: Map[String, Mapping] = Map.empty

  override def copyTo: List[String] = Nil
}

object AliasMapping extends MappingType[AliasMapping] {

  val typeName = "alias"

}

@jsonDerive
case class BooleanMapping(`type`: String = BooleanMapping.typeName,
                          @jsonField("null_value") nullValue: Option[Boolean] = None,
                          @jsonField("doc_values") docValues: Option[Boolean] = None,
                          @JsonNoDefault store: Boolean = false,
                          @JsonNoDefault index: Boolean = true,
                          @JsonNoDefault boost: Float = 1.0f,
                          @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                          similarity: Option[Similarity] = None,
                          @jsonField("copy_to") copyTo: List[String] = Nil,
                          fields: Map[String, Mapping] = Map.empty[String, Mapping])
  extends Mapping {

}

object BooleanMapping extends MappingType[BooleanMapping] {

  implicit val myStringList = Mapping.myListString

  val typeName = "boolean"

}



@jsonDerive
case class CompletionMapping(
    analyzer: Option[String] = None,
    @jsonField("search_analyzer") searchAnalyzer: Option[String] = None,
    @JsonNoDefault @jsonField("preserve_separators") preserveSeparators: Boolean = true,
    @JsonNoDefault @jsonField("preserve_position_increments") preservePositionIncrements: Boolean = true,
    @JsonNoDefault @jsonField("max_input_length") maxInputLength: Int = 50,
//                             context: Option[missingCodeName] = None,
    @jsonField("doc_values") docValues: Option[Boolean] = None,
    @JsonNoDefault store: Boolean = false,
    @JsonNoDefault index: Boolean = true,
    @JsonNoDefault boost: Float = 1.0f,
    @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
    similarity: Option[Similarity] = None,
    @jsonField("copy_to") copyTo: List[String] = Nil,
    fields: Map[String, Mapping] = Map.empty[String, Mapping],
    `type`: String = CompletionMapping.typeName)
    extends Mapping {



}

object CompletionMapping extends MappingType[CompletionMapping] {

  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "completion"

}

@jsonDerive
case class DateTimeMapping(@jsonField("null_value") nullValue: Option[OffsetDateTime] = None,
                           @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false,
                           locale: Option[String] = None,
                           format: Option[String] = None,
                           @jsonField("doc_values") docValues: Option[Boolean] = None,
                           @JsonNoDefault store: Boolean = false,
                           @JsonNoDefault index: Boolean = true,
                           @JsonNoDefault boost: Float = 1.0f,
                           @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                           similarity: Option[Similarity] = None,
                           @jsonField("copy_to") copyTo: List[String] = Nil,
                           fields: Map[String, Mapping] = Map.empty[String, Mapping],
                           `type`: String = DateTimeMapping.typeName)
    extends Mapping

object DateTimeMapping extends MappingType[DateTimeMapping] {
  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "date"
}

@jsonDerive
case class DateNanosMapping(@jsonField("null_value") nullValue: Option[OffsetDateTime] = None,
                           @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false,
                           locale: Option[String] = None,
                           format: Option[String] = None,
                           @jsonField("doc_values") docValues: Option[Boolean] = None,
                           @JsonNoDefault store: Boolean = false,
                           @JsonNoDefault index: Boolean = true,
                           @JsonNoDefault boost: Float = 1.0f,
                           @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                           similarity: Option[Similarity] = None,
                           @jsonField("copy_to") copyTo: List[String] = Nil,
                           fields: Map[String, Mapping] = Map.empty[String, Mapping],
                           `type`: String = DateNanosMapping.typeName)
  extends Mapping

object DateNanosMapping extends MappingType[DateNanosMapping] {
  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "date_nanos"
}

@jsonDerive
case class FlattenedMapping(@JsonNoDefault norms: Boolean = false,
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
                          @jsonField("copy_to") copyTo: List[String] = Nil,
                          fields: Map[String, Mapping] = Map.empty[String, Mapping],
                          `type`: String = FlattenedMapping.typeName)
  extends Mapping

object FlattenedMapping extends MappingType[FlattenedMapping] {
  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "flattened"
}


@jsonDerive
case class FielddataFrequencyFilter(@JsonNoDefault min: Int = 0,
                                    @JsonNoDefault max: Int = Integer.MAX_VALUE,
                                    @JsonNoDefault @jsonField("min_segment_size") minSegmentSize: Int = 0)

@jsonDerive
case class GeoPointMapping(@JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false,
                           @jsonField("doc_values") docValues: Option[Boolean] = None,
                           @JsonNoDefault store: Boolean = false,
                           @JsonNoDefault index: Boolean = true,
                           @JsonNoDefault boost: Float = 1.0f,
                           @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                           similarity: Option[Similarity] = None,
                           @jsonField("copy_to") copyTo: List[String] = Nil,
                           fields: Map[String, Mapping] = Map.empty[String, Mapping],
                           `type`: String = GeoPointMapping.typeName)
    extends Mapping

object GeoPointMapping extends MappingType[GeoPointMapping] {
  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "geo_point"
}

@jsonDerive
case class GeoShapeMapping(tree: Option[String] = None,
                           @jsonField("tree_levels") treeLevels: Option[Int] = None,
                           precision: Option[String] = None,
                           @jsonField("distance_error_pct") distanceErrorPct: Option[String] = None,
                           orientation: Option[String] = None,
                           strategy: Option[String] = None,
                           coerce: Option[Boolean] = None,
                           @jsonField("points_only") pointsOnly: Option[Boolean] = None,
                           @jsonField("doc_values") docValues: Option[Boolean] = None,
                           @JsonNoDefault store: Boolean = false,
                           @JsonNoDefault index: Boolean = true,
                           @JsonNoDefault boost: Float = 1.0f,
                           @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                           similarity: Option[Similarity] = None,
                           @jsonField("copy_to") copyTo: List[String] = Nil,
                           fields: Map[String, Mapping] = Map.empty[String, Mapping],
                           `type`: String = GeoShapeMapping.typeName)
    extends Mapping

object GeoShapeMapping extends MappingType[GeoShapeMapping] {
  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "geo_shape"
}

@jsonDerive
case class IpMapping(@jsonField("null_value") nullValue: Option[String] = None,
                     @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false,
                     @jsonField("doc_values") docValues: Option[Boolean] = None,
                     @JsonNoDefault store: Boolean = false,
                     @JsonNoDefault index: Boolean = true,
                     @JsonNoDefault boost: Float = 1.0f,
                     @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                     similarity: Option[Similarity] = None,
                     @jsonField("copy_to") copyTo: List[String] = Nil,
                     fields: Map[String, Mapping] = Map.empty[String, Mapping],
                     `type`: String = IpMapping.typeName)
    extends Mapping {


}

object IpMapping extends MappingType[IpMapping] {

  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "ip"

}

@jsonDerive
case class KeywordMapping(@JsonNoDefault norms: Boolean = false,
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
                          @jsonField("copy_to") copyTo: List[String] = Nil,
                          fields: Map[String, Mapping] = Map.empty[String, Mapping],
                          `type`: String = KeywordMapping.typeName)
    extends Mapping {


}

object KeywordMapping extends MappingType[KeywordMapping] {

  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "keyword"

  def code(name: String): (String, KeywordMapping) = name -> KeywordMapping()

}

@jsonDerive
final case class NestedMapping(@JsonNoDefault properties: Map[String, Mapping],
                               @JsonNoDefault dynamic: String = NestedMapping.defaultDynamic,
                               @JsonNoDefault enabled: Boolean = NestedMapping.defaultEnabled,
                               path: Option[String] = None,
                               @jsonField("include_in_parent") includeInParent: Boolean = false,
                               `type`: String = NestedMapping.typeName)
    extends Mapping
    with MappingObject {



  override def dynamicString: String = dynamic.toString

  override def docValues: Option[Boolean] = None

  override def copyTo: List[String] = Nil

  override def store: Boolean = false

  override def index: Boolean = false

  override def boost: Float = 1.0f

  override def indexOptions: Option[IndexOptions] = None

  override def similarity: Option[Similarity] = None

  override def fields: Map[String, Mapping] = properties

}

object NestedMapping extends MappingType[NestedMapping] {

  implicit val myBooleanDecoder: JsonDecoder[String] = JsonDecoder.decodeString.or(
    JsonDecoder.decodeBoolean.emap {
      case true => Right("true")
      case false => Right("false")
    }
  )

  val typeName = "nested"

  val defaultDynamic = "true"
  val defaultEnabled = true

}

@jsonDerive
case class NumberMapping(@jsonField("type") `type`: String,
                         @jsonField("null_value") nullValue: Option[Json] = None,
                         @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false,
                         coerce: Option[Boolean] = None,
                         @jsonField("doc_values") docValues: Option[Boolean] = None,
                         @JsonNoDefault store: Boolean = false,
                         @JsonNoDefault index: Boolean = true,
                         @JsonNoDefault boost: Float = 1.0f,
                         @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                         similarity: Option[Similarity] = None,
                         @jsonField("copy_to") copyTo: List[String] = Nil,
                         fields: Map[String, Mapping] = Map.empty[String, Mapping])
    extends Mapping

object NumberMapping extends MappingType[NumberMapping] {

  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "number"

}

@jsonDerive
final case class ObjectMapping(@JsonNoDefault properties: Map[String, Mapping] = Map.empty[String, Mapping],
                               @JsonNoDefault dynamic: Boolean = ObjectMapping.defaultDynamic,
                               @JsonNoDefault enabled: Boolean = ObjectMapping.defaultEnabled,
                               path: Option[String] = None,
                               _source: Option[Map[String, Boolean]] = None,
                               _type: Option[Map[String, Boolean]] = None,
                               _all: Option[Map[String, Boolean]] = None,
                               `type`: String = ObjectMapping.typeName)
    extends Mapping
    with MappingObject {

  def NAME = ObjectMapping.typeName



  override def dynamicString: String = dynamic.toString
  override def docValues: Option[Boolean] = None

  /**
   * The name of the field that uses the mapping.
   */
  override def store: Boolean = false

  override def index: Boolean = enabled

  override def boost: Float = 1.0f

  override def indexOptions: Option[IndexOptions] = None

  override def similarity: Option[Similarity] = None

  override def fields: Map[String, Mapping] = properties

  override def copyTo: List[String] = Nil

}

object ObjectMapping extends MappingType[ObjectMapping] {
  implicit val myBooleanDecoder: JsonDecoder[Boolean] = JsonDecoder.decodeBoolean.or(
    JsonDecoder.decodeString.emap {
      case "true" => Right(true)
      case "false" => Right(false)
      case _ => Left("Boolean")
    }
  )

  val typeName = "object"

  val defaultDynamic = true
  val defaultEnabled = true

  def noIndex() = 
    new ObjectMapping(enabled = false)

  }

@jsonDerive
case class RangeMapping(@jsonField("type") `type`: String,
                         @jsonField("null_value") nullValue: Option[Json] = None,
                         @JsonNoDefault @jsonField("ignore_malformed") ignoreMalformed: Boolean = false,
                         coerce: Option[Boolean] = None,
                         @jsonField("doc_values") docValues: Option[Boolean] = None,
                         @JsonNoDefault store: Boolean = false,
                         @JsonNoDefault index: Boolean = true,
                         @JsonNoDefault boost: Float = 1.0f,
                         @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                         similarity: Option[Similarity] = None,
                         @jsonField("copy_to") copyTo: List[String] = Nil,
                         fields: Map[String, Mapping] = Map.empty[String, Mapping])
  extends Mapping

object RangeMapping extends MappingType[RangeMapping] {

  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "range"

}


/**
 * This Mapping must only be used at the top-level of a mapping tree, in other words it must not be contained within an ObjectMapping's properties.
 * TODO: dynamic_templates
 */
@jsonDerive
final case class RootDocumentMapping(@JsonNoDefault properties: Map[String, Mapping]=Map.empty[String, Mapping],
                                     @JsonNoDefault dynamic: String = RootDocumentMapping.defaultDynamic,
                                     @JsonNoDefault enabled: Boolean = RootDocumentMapping.defaultEnabled,
                                     path: Option[String] = None,
                                     analyzer: Option[String] = None,
                                     indexAnalyzer: Option[String] = None,
                                     searchAnalyzer: Option[String] = None,
                                     dynamicDateFormats: Option[Seq[String]] = None,
                                     @JsonNoDefault dateDetection: Boolean = RootDocumentMapping.defaultDateDetection,
                                     @JsonNoDefault numericDetection: Boolean =
                                       RootDocumentMapping.defaultNumericDetection,
                                     @jsonField("_id") id: Option[IdMapping] = None,
                                     @jsonField("_index") _index: Option[IndexMapping] = None,
                                     @jsonField("_source") source: Option[SourceMapping] = None,
                                     @jsonField("_type") _type: Option[TypeMapping] = None,
                                     @jsonField("_meta") meta: MetaObject = new MetaObject(),
                                     @jsonField("_routing") routing: Option[RoutingMapping] = None,
                                     @jsonField("_parent") parent: Option[ParentMapping] = None)
    extends Mapping
    with MappingObject {

  override def dynamicString: String = dynamic

  /**
   * The name of the field that uses the mapping.
   */
  override def `type`: String = RootDocumentMapping.typeName

  override def store: Boolean = false

  override def index: Boolean = true

  override def boost: Float = 1.0f

  override def indexOptions: Option[IndexOptions] = None

  override def similarity: Option[Similarity] = None

  override def fields: Map[String, Mapping] = properties

  override def copyTo: List[String] = Nil

  override def docValues: Option[Boolean] = None

  var context: Option[MetaAliasContext] = None

  /**
   * Make a JSON representation. Must return a Json.Obj("field" -> Json.Obj(...)).
   */
//  override def toJson: Json = this.asJson

  def getFieldType(field: Array[String], props: Map[String, Mapping] = properties): Option[String] = 
    props.get(field.head) match {
      case Some(map) =>
        val mappingType = map.`type`
        if (field.tail.isEmpty) Some(mappingType)
        else {
          mappingType match {
            case s: String if s == ObjectMapping.typeName =>
              getFieldType(field.tail, map.asInstanceOf[ObjectMapping].properties)
            case s: String if s == NestedMapping.typeName =>
              getFieldType(field.tail, map.asInstanceOf[NestedMapping].properties)
            case _ => None
          }
        }
      case None => None
    }
  
  def getNestedPaths(field: String): List[String] = {
    //return a list of nested path encounter before arrive to the field
    val tokens = field.split(".")
    if (tokens.isEmpty) Nil
    else
      Range(0, tokens.length)
        .flatMap(n =>
          getFieldType(tokens.take(n)) match {
            case Some(t) => if (t == NestedMapping.typeName) Some(t) else None
            case None => None
        })
        .toList
  }

  def aliasFor: List[MetaAlias] = this.meta.alias

  def hasGraphSupport:Boolean=false // todo implement check on join document if has edge support


}

object RootDocumentMapping {

  val empty=new RootDocumentMapping()

  implicit val myBooleanDecoder: JsonDecoder[String] = JsonDecoder.decodeString.or(
    JsonDecoder.decodeBoolean.emap {
      case true => Right("true")
      case false => Right("false")
    }
  )

  val typeName: String = "root"

  val defaultDynamic = "true"
  val defaultEnabled = true
  val defaultDateDetection = true
  val defaultNumericDetection = true

  //  /**
//    * Add to a mapping from a field-specification
//    *
//    * @param fieldSpec The field-specification, at the level where it has a "type" field.
//    * @param mapping   The mapping that will be augmented if the field specification has root properties.
//    */
//  def addToFromJson(fieldSpec: Json, mapping: Mapping): Mapping =
//    fieldSpec match {
//      case spec: Json if spec.isObject && (spec.asObject.get.fields.toSet & rootFields).nonEmpty => RootDocumentMapping(mapping, RootProperties.fromJson(spec))
//      case _ => mapping
//    }

  def fromJson(json: Json): RootDocumentMapping = 
    json.as[RootDocumentMapping] match {
      case Left(ex) => throw FrameworkException(ex)
      case Right(doc) => doc
    }

  
  def build(properties: Map[String, Mapping]): RootDocumentMapping =
    RootDocumentMapping(properties = properties)

}

@jsonDerive
case class TextMapping(@JsonNoDefault fielddata: Boolean = false,
                        @jsonField("position_increment_gap") positionIncrementGap: Option[Int] = None,
                        @jsonField("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None,
                        @jsonField("fielddata_frequency_filter") fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None,
                        @jsonField("doc_values") docValues: Option[Boolean] = None,
                        @jsonField("analyzer") analyzer: Option[Analyzer] = None,
                        @jsonField("search_analyzer") searchAnalyzer: Option[Boolean] = None,
                        @jsonField("normalizer") normalizer: Option[String] = None,
                        @jsonField("null_value") nullValue: Option[String] = None,
                        @JsonNoDefault store: Boolean = false,
                        @JsonNoDefault index: Boolean = true,
                        @JsonNoDefault boost: Float = 1.0f,
                        @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                        similarity: Option[Similarity] = None,
                        @jsonField("copy_to") copyTo: List[String] = Nil,
                        fields: Map[String, Mapping] = Map.empty[String, Mapping],
                        `type`: String = TextMapping.typeName)
                        extends Mapping {}

object TextMapping extends MappingType[TextMapping] {

  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "text"

}

@jsonDerive
case class TokenCountMapping(@jsonField("null_value") nullValue: Option[Int] = None,
                             analyzer: Option[String] = None,
                             @jsonField("doc_values") docValues: Option[Boolean] = None,
                             @JsonNoDefault store: Boolean = false,
                             @JsonNoDefault index: Boolean = true,
                             @JsonNoDefault boost: Float = 1.0f,
                             @jsonField("index_options") indexOptions: Option[IndexOptions] = None,
                             similarity: Option[Similarity] = None,
                             @jsonField("copy_to") copyTo: List[String] = Nil,
                             fields: Map[String, Mapping] = Map.empty[String, Mapping],
                             `type`: String = TextMapping.typeName)
    extends Mapping {

}

object TokenCountMapping extends MappingType[TokenCountMapping] {

  implicit val myListString: JsonDecoder[List[String]]=Mapping.myListString

  val typeName = "token_count"

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

  override def copyTo: List[String] = Nil

  /**
   * Make a JSON representation. Must return a Json.Obj("field" -> Json.Obj(...)).
   */
  override def docValues: Option[Boolean] = None


}

@jsonDerive
final case class BoostMapping(name: Option[String] = None, @jsonField("null_value") nullValue: Option[Float] = None)
    extends InternalMapping {

  override def NAME = BoostMapping.typeName


}

object BoostMapping extends InternalMappingType {
  val typeName = "_boost"

}

@jsonDerive
final case class IdMapping(name: Option[String] = None) extends InternalMapping {

  def field = "_id"

  override def NAME = IdMapping.typeName

}

object IdMapping extends InternalMappingType {
  val typeName = "_id"

}

@jsonDerive
final case class IndexMapping(enabled: Option[Boolean] = None, path: Option[String] = None) extends InternalMapping {

  def field = "_index"

  override def NAME = IndexMapping.typeName

//  override def toJson: Json = this.asJson

}

object IndexMapping extends InternalMappingType {
  val typeName = "_index"
}

@jsonDerive
final case class ParentMapping(postingsFormat: Option[String] = None, path: Option[String] = None)
    extends InternalMapping {
  def field = "_parent"

  override def NAME = ParentMapping.typeName

//  override def toJson: Json = this.asJson

}

object ParentMapping extends InternalMappingType {
  val typeName = "_parent"

}

@jsonDerive
final case class RoutingMapping(var _required: Option[Boolean] = None, path: Option[String] = None)
    extends InternalMapping {
  def field = "_routing"

  override def NAME = RoutingMapping.typeName

//  override def toJson: Json = this.asJson

}
object RoutingMapping extends InternalMappingType {
  val typeName = "_routing"
}

@jsonDerive
final case class SourceMapping(enabled: Option[Boolean] = None) extends InternalMapping {
  def field = "_source"

  override def NAME = SourceMapping.typeName

//  override def toJson: Json = this.asJson

}

object SourceMapping extends InternalMappingType {
  val typeName = "_source"
}

@jsonDerive
final case class TypeMapping(
    name: Option[String] = None //,
    //                             index: Option[Boolean] = None,
    //                             store: Option[Boolean] = None,
) extends InternalMapping {

  override def NAME = TypeMapping.typeName

  def field = "_type"

//  override def toJson: Json = this.asJson
}

object TypeMapping extends InternalMappingType {
  val typeName = "_type"
}
// format: on
