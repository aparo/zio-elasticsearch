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

package elasticsearch.mappings

import java.time.OffsetDateTime

import io.circe.syntax._
import io.circe.derivation.annotations._
import io.circe.syntax._
import zio.exception._
import io.circe._

import elasticsearch.analyzers.Analyzer
import io.circe.derivation.annotations.{ JsonKey, JsonNoDefault }

// format: off
/**
 * A Mapping can be part of an ObjectMapping.
 * Most sub-classes of Mapping are nestable, but RootDocumentMapping is not!
 */
sealed trait Mapping { self =>

  /**
   * The name of the field that uses the mapping.
   */
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
   * Make a JSON representation. Must return a JsonObject("field" -> JsonObject(...)).
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
      case m: RootDocumentMapping => m.properties
      case m: GeoPointMapping => m.fields
      case m: GeoShapeMapping => m.fields
      case m: IpMapping => m.fields
      case m: NumberMapping => m.fields
      case m: NestedMapping => m.properties
      case m: ObjectMapping => m.properties
      case m: KeywordMapping => m.fields
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
      case m: GeoPointMapping => m.copy(fields = m.fields ++ sFields)
      case m: GeoShapeMapping => m.copy(fields = m.fields ++ sFields)
      case m: IpMapping => m.copy(fields = m.fields ++ sFields)
      case m: NumberMapping => m.copy(fields = m.fields ++ sFields)
      case m: NestedMapping => m.copy(properties = m.properties ++ sFields)
      case m: ObjectMapping => m.copy(properties = m.properties ++ sFields)
      case m: RootDocumentMapping => m.copy(properties = m.properties ++ sFields)
      case m: TextMapping => m.copy(fields = m.fields ++ sFields)
      case m: KeywordMapping => m.copy(fields = m.fields ++ sFields)
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
      case m: RootDocumentMapping => m
      case m: GeoPointMapping => m.copy(index = index)
      case m: GeoShapeMapping => m.copy(index = index)
      case m: IpMapping => m.copy(index = index)
      case m: NumberMapping => m.copy(index = index)
      case m: NestedMapping => m.copy(enabled = index)
      case m: ObjectMapping => m.copy(enabled = index)
      case m: CompletionMapping => m
      case m: TextMapping => m.copy(index = index)
      case m: KeywordMapping => m.copy(index = index)
      case m: AliasMapping => m
      case m: TokenCountMapping => m
      //toSkip
      case m: InternalMapping => m
    }

  //TODO restore
//  /** Try to merge two mapping. Return new mapping or exception and way to fix in other case
//   *
//   * @param name the current mapping name
//   * @param otherName the other mapping name
//   * @param otherMapping the mapping to merge
//   * @return The merged mapping or the an an Exception
//   */
//    def merge(name:String, otherName:String, otherMapping: Mapping): (Seq[MergeMappingException],Option[Mapping])  =
//      MappingMerger.merge(name, self, otherName, otherMapping)
    

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

  implicit val myListString: Decoder[List[String]] = Decoder.decodeList[String].or(
    Decoder.decodeString.emap {
      case value => Right(List(value))
    }
  )


  implicit final val decodeMapping: Decoder[Mapping] =
    Decoder.instance { c =>
      val typeName: String = c.get[String]("type").getOrElse("object")
      typeName match {
        case ObjectMapping.typeName => c.as[ObjectMapping]
        case NestedMapping.typeName => c.as[NestedMapping]
        case TextMapping.typeName => c.as[TextMapping]
        case KeywordMapping.typeName => c.as[KeywordMapping]
        case NumberMapping.BYTE => c.as[NumberMapping]
        case NumberMapping.DOUBLE => c.as[NumberMapping]
        case NumberMapping.FLOAT => c.as[NumberMapping]
        case NumberMapping.HALF_FLOAT => c.as[NumberMapping]
        case NumberMapping.SHORT => c.as[NumberMapping]
        case NumberMapping.INTEGER => c.as[NumberMapping]
        case NumberMapping.LONG => c.as[NumberMapping]
        case TokenCountMapping.typeName => c.as[TokenCountMapping]
        case DateTimeMapping.typeName => c.as[DateTimeMapping]
        case BooleanMapping.typeName => c.as[BooleanMapping]
        case JoinMapping.typeName => c.as[JoinMapping]
        case BinaryMapping.typeName => c.as[BinaryMapping]
        case GeoPointMapping.typeName => c.as[GeoPointMapping]
        case GeoShapeMapping.typeName => c.as[GeoPointMapping] //TODO define geoShapie Mapping
        case IpMapping.typeName => c.as[IpMapping]
        case CompletionMapping.typeName => c.as[CompletionMapping]
        case "string" =>
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
      }

    }

  implicit final val encodeMapping: Encoder[Mapping] = {
    Encoder.instance {
      case m: BinaryMapping => m.asJson
      case m: JoinMapping => m.asJson
      case m: BooleanMapping => m.asJson
      case m: CompletionMapping => m.asJson
      case m: DateTimeMapping => m.asJson
      case m: RootDocumentMapping => m.asJson
      case m: GeoPointMapping => m.asJson
      case m: GeoShapeMapping => m.asJson
      case m: IpMapping => m.asJson
      case m: NumberMapping => m.asJson
      case m: NestedMapping => m.asJson
      case m: ObjectMapping => m.asJson
      case m: KeywordMapping => m.asJson
      case m: TextMapping => m.asJson
      case m: TokenCountMapping => m.asJson
      case m: AliasMapping => m.asJson
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
//    case JsonObject.fromMap(Map((indexName, JsonObject.fromMap(Map(("mappings", jsMappings)))))) => jsMappings match {
//      case JsonObject(fields) => fields.toSeq.map(fromJsonRoot)
//      case _ => Map.empty[String, Mapping]
//    }
//    case _ => throw NoSqlSearchException(status = -1, msg = "Bad mappings received in mappingsFromJson.", json = mappings)
//  }

//  /**
//    * The reads expects JSON of the form
//    * { <index> : { "mappings" : { <type> : { "properties" :  ... } } } }
//    */
//  implicit val reads: Reads[Mapping] = Reads {
//    case json: JsonObject => JsSuccess(mappingsFromJson(json).head)
//    case mappings => throw NoSqlSearchException(status = -1, msg = "Bad mappings received in reads.", json = mappings)
//  }

}

@JsonCodec
case class BinaryMapping(@JsonKey("doc_values") docValues: Option[Boolean] = None,
                         @JsonNoDefault store: Boolean = false,
                         @JsonNoDefault index: Boolean = false,
                         @JsonNoDefault boost: Float = 1.0f,
                         @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                         similarity: Option[Similarity] = None,
                         @JsonKey("copy_to") copyTo: List[String] = Nil,
                         @JsonNoDefault fields: Map[String, Mapping] = Map.empty[String, Mapping],
                         `type`: String = BinaryMapping.typeName)
    extends Mapping {

}

object BinaryMapping extends MappingType[BinaryMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "binary"

}

@JsonCodec
case class JoinMapping(@JsonNoDefault @JsonKey("doc_values") docValues: Option[Boolean] = None,
                         @JsonNoDefault store: Boolean = false,
                         @JsonNoDefault index: Boolean = false,
                         @JsonNoDefault boost: Float = 1.0f,
                       @JsonNoDefault @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                       relations: Map[String, String] = Map.empty[String,String],
                         `type`: String = JoinMapping.typeName)
  extends Mapping {
  override def similarity: Option[Similarity] = None

  override def fields: Map[String, Mapping] = Map.empty

  override def copyTo: List[String] = Nil
}

object JoinMapping extends MappingType[JoinMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "join"

}


@JsonCodec
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

@JsonCodec
case class BooleanMapping(`type`: String = BooleanMapping.typeName,
                          @JsonKey("null_value") nullValue: Option[Boolean] = None,
                          @JsonKey("doc_values") docValues: Option[Boolean] = None,
                          @JsonNoDefault store: Boolean = false,
                          @JsonNoDefault index: Boolean = true,
                          @JsonNoDefault boost: Float = 1.0f,
                          @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                          similarity: Option[Similarity] = None,
                          @JsonKey("copy_to") copyTo: List[String] = Nil,
                          fields: Map[String, Mapping] = Map.empty[String, Mapping])
  extends Mapping {

}

object BooleanMapping extends MappingType[BooleanMapping] {

  implicit val myStringList = Mapping.myListString

  val typeName = "boolean"

}



@JsonCodec
case class CompletionMapping(
    analyzer: Option[String] = None,
    @JsonKey("search_analyzer") searchAnalyzer: Option[String] = None,
    @JsonNoDefault @JsonKey("preserve_separators") preserveSeparators: Boolean = true,
    @JsonNoDefault @JsonKey("preserve_position_increments") preservePositionIncrements: Boolean = true,
    @JsonNoDefault @JsonKey("max_input_length") maxInputLength: Int = 50,
//                             context: Option[missingCodeName] = None,
    @JsonKey("doc_values") docValues: Option[Boolean] = None,
    @JsonNoDefault store: Boolean = false,
    @JsonNoDefault index: Boolean = true,
    @JsonNoDefault boost: Float = 1.0f,
    @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
    similarity: Option[Similarity] = None,
    @JsonKey("copy_to") copyTo: List[String] = Nil,
    fields: Map[String, Mapping] = Map.empty[String, Mapping],
    `type`: String = CompletionMapping.typeName)
    extends Mapping {



}

object CompletionMapping extends MappingType[CompletionMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "completion"

}

@JsonCodec
case class DateTimeMapping(@JsonKey("null_value") nullValue: Option[OffsetDateTime] = None,
                           @JsonNoDefault @JsonKey("ignore_malformed") ignoreMalformed: Boolean = false,
                           locale: Option[String] = None,
                           format: Option[String] = None,
                           @JsonKey("doc_values") docValues: Option[Boolean] = None,
                           @JsonNoDefault store: Boolean = false,
                           @JsonNoDefault index: Boolean = true,
                           @JsonNoDefault boost: Float = 1.0f,
                           @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                           similarity: Option[Similarity] = None,
                           @JsonKey("copy_to") copyTo: List[String] = Nil,
                           fields: Map[String, Mapping] = Map.empty[String, Mapping],
                           `type`: String = DateTimeMapping.typeName)
    extends Mapping {


}

object DateTimeMapping extends MappingType[DateTimeMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "date"

}

@JsonCodec
case class FielddataFrequencyFilter(@JsonNoDefault min: Int = 0,
                                    @JsonNoDefault max: Int = Integer.MAX_VALUE,
                                    @JsonNoDefault @JsonKey("min_segment_size") minSegmentSize: Int = 0)

@JsonCodec
case class GeoPointMapping(@JsonNoDefault @JsonKey("ignore_malformed") ignoreMalformed: Boolean = false,
                           @JsonKey("doc_values") docValues: Option[Boolean] = None,
                           @JsonNoDefault store: Boolean = false,
                           @JsonNoDefault index: Boolean = true,
                           @JsonNoDefault boost: Float = 1.0f,
                           @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                           similarity: Option[Similarity] = None,
                           @JsonKey("copy_to") copyTo: List[String] = Nil,
                           fields: Map[String, Mapping] = Map.empty[String, Mapping],
                           `type`: String = GeoPointMapping.typeName)
    extends Mapping {

}

object GeoPointMapping extends MappingType[GeoPointMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "geo_point"

}

@JsonCodec
case class GeoShapeMapping(tree: Option[String] = None,
                           @JsonKey("tree_levels") treeLevels: Option[Int] = None,
                           precision: Option[String] = None,
                           @JsonKey("distance_error_pct") distanceErrorPct: Option[String] = None,
                           orientation: Option[String] = None,
                           strategy: Option[String] = None,
                           coerce: Option[Boolean] = None,
                           @JsonKey("points_only") pointsOnly: Option[Boolean] = None,
                           @JsonKey("doc_values") docValues: Option[Boolean] = None,
                           @JsonNoDefault store: Boolean = false,
                           @JsonNoDefault index: Boolean = true,
                           @JsonNoDefault boost: Float = 1.0f,
                           @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                           similarity: Option[Similarity] = None,
                           @JsonKey("copy_to") copyTo: List[String] = Nil,
                           fields: Map[String, Mapping] = Map.empty[String, Mapping],
                           `type`: String = GeoShapeMapping.typeName)
    extends Mapping {

}

object GeoShapeMapping extends MappingType[GeoShapeMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "geo_shape"

}

@JsonCodec
case class IpMapping(@JsonKey("null_value") nullValue: Option[String] = None,
                     @JsonNoDefault @JsonKey("ignore_malformed") ignoreMalformed: Boolean = false,
                     @JsonKey("doc_values") docValues: Option[Boolean] = None,
                     @JsonNoDefault store: Boolean = false,
                     @JsonNoDefault index: Boolean = true,
                     @JsonNoDefault boost: Float = 1.0f,
                     @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                     similarity: Option[Similarity] = None,
                     @JsonKey("copy_to") copyTo: List[String] = Nil,
                     fields: Map[String, Mapping] = Map.empty[String, Mapping],
                     `type`: String = IpMapping.typeName)
    extends Mapping {


}

object IpMapping extends MappingType[IpMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "ip"

}

@JsonCodec
case class KeywordMapping(@JsonNoDefault norms: Boolean = false,
                          @JsonKey("ignore_above") ignoreAbove: Option[Int] = None,
                          @JsonKey("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None,
                          @JsonKey("doc_values") docValues: Option[Boolean] = None,
                          @JsonKey("normalizer") normalizer: Option[String] = None,
                          @JsonKey("null_value") nullValue: Option[String] = None,
                          @JsonNoDefault store: Boolean = false,
                          @JsonNoDefault index: Boolean = true,
                          @JsonNoDefault boost: Float = 1.0f,
                          @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                          similarity: Option[Similarity] = None,
                          @JsonKey("copy_to") copyTo: List[String] = Nil,
                          fields: Map[String, Mapping] = Map.empty[String, Mapping],
                          `type`: String = KeywordMapping.typeName)
    extends Mapping {


}

object KeywordMapping extends MappingType[KeywordMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "keyword"

  def code(name: String): (String, KeywordMapping) = name -> KeywordMapping()

}

@JsonCodec
final case class NestedMapping(@JsonNoDefault properties: Map[String, Mapping],
                               @JsonNoDefault dynamic: String = NestedMapping.defaultDynamic,
                               @JsonNoDefault enabled: Boolean = NestedMapping.defaultEnabled,
                               path: Option[String] = None,
                               @JsonKey("include_in_parent") includeInParent: Boolean = false,
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

  implicit val myBooleanDecoder: Decoder[String] = Decoder.decodeString.or(
    Decoder.decodeBoolean.emap {
      case true => Right("true")
      case false => Right("false")
    }
  )

  val typeName = "nested"

  val defaultDynamic = "true"
  val defaultEnabled = true

}

@JsonCodec
case class NumberMapping(@JsonKey("type") `type`: String,
                         @JsonKey("null_value") nullValue: Option[Json] = None,
                         @JsonNoDefault @JsonKey("ignore_malformed") ignoreMalformed: Boolean = false,
                         coerce: Option[Boolean] = None,
                         @JsonKey("doc_values") docValues: Option[Boolean] = None,
                         @JsonNoDefault store: Boolean = false,
                         @JsonNoDefault index: Boolean = true,
                         @JsonNoDefault boost: Float = 1.0f,
                         @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                         similarity: Option[Similarity] = None,
                         @JsonKey("copy_to") copyTo: List[String] = Nil,
                         fields: Map[String, Mapping] = Map.empty[String, Mapping])
    extends Mapping {


}

object NumberMapping extends MappingType[NumberMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "number"

  lazy val BYTE: String = "byte"
  lazy val DOUBLE: String = "double"
  lazy val FLOAT: String = "float"
  lazy val HALF_FLOAT: String = "half_float"
  lazy val SHORT: String = "short"
  lazy val INTEGER: String = "integer"
  lazy val LONG: String = "long"

}

@JsonCodec
final case class ObjectMapping(@JsonNoDefault properties: Map[String, Mapping] = Map.empty[String, Mapping],
                               @JsonNoDefault dynamic: String = ObjectMapping.defaultDynamic,
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

  /**
   * Make a JSON representation. Must return a JsonObject("field" -> JsonObject(...)).
   */
//  override def toJson: Json = Json.obj(
//    "type" -> Json.fromString(ObjectMapping.typeName),
//    "properties" -> properties.asJson,
//    "dynamic" -> CirceUtils.toJsonIfNot(dynamic, ObjectMapping.defaultDynamic),
//    "enabled" -> CirceUtils.toJsonIfNot(enabled, ObjectMapping.defaultEnabled),
//    "path" -> CirceUtils.toJsonIfNot(path, None))

}

object ObjectMapping extends MappingType[ObjectMapping] {

  implicit val myBooleanDecoder: Decoder[String] = Decoder.decodeString.or(
    Decoder.decodeBoolean.emap {
      case true => Right("true")
      case false => Right("false")
    }
  )

  val typeName = "object"

  val defaultDynamic = "true"
  val defaultEnabled = true

  def noIndex() = 
    new ObjectMapping(enabled = false)

  }

/**
 * This Mapping must only be used at the top-level of a mapping tree, in other words it must not be contained within an ObjectMapping's properties.
 * TODO: dynamic_templates
 */
@JsonCodec
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
                                     @JsonKey("_id") id: Option[IdMapping] = None,
                                     @JsonKey("_index") _index: Option[IndexMapping] = None,
                                     @JsonKey("_source") source: Option[SourceMapping] = None,
                                     @JsonKey("_type") _type: Option[TypeMapping] = None,
                                     @JsonKey("_meta") meta: MetaObject = new MetaObject(),
                                     @JsonKey("_routing") routing: Option[RoutingMapping] = None,
                                     @JsonKey("_parent") parent: Option[ParentMapping] = None)
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
   * Make a JSON representation. Must return a JsonObject("field" -> JsonObject(...)).
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

  def hasGraphSupport:Boolean=false //TODO manage check join field

}

object RootDocumentMapping {

  val empty=new RootDocumentMapping()

  implicit val myBooleanDecoder: Decoder[String] = Decoder.decodeString.or(
    Decoder.decodeBoolean.emap {
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

@JsonCodec
case class TextMapping(@JsonNoDefault fielddata: Boolean = false,
                        @JsonKey("position_increment_gap") positionIncrementGap: Option[Int] = None,
                        @JsonKey("eager_global_ordinals") eagerGlobalOrdinals: Option[Boolean] = None,
                        @JsonKey("fielddata_frequency_filter") fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None,
                        @JsonKey("doc_values") docValues: Option[Boolean] = None,
                        @JsonKey("analyzer") analyzer: Option[Analyzer] = None,
                        @JsonKey("search_analyzer") searchAnalyzer: Option[Boolean] = None,
                        @JsonKey("normalizer") normalizer: Option[String] = None,
                        @JsonKey("null_value") nullValue: Option[String] = None,
                        @JsonNoDefault store: Boolean = false,
                        @JsonNoDefault index: Boolean = true,
                        @JsonNoDefault boost: Float = 1.0f,
                        @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                        similarity: Option[Similarity] = None,
                        @JsonKey("copy_to") copyTo: List[String] = Nil,
                        fields: Map[String, Mapping] = Map.empty[String, Mapping],
                        `type`: String = TextMapping.typeName)
                        extends Mapping {}

object TextMapping extends MappingType[TextMapping] {

  implicit val myStringList=Mapping.myListString

  val typeName = "text"

}

@JsonCodec
case class TokenCountMapping(@JsonKey("null_value") nullValue: Option[Int] = None,
                             analyzer: Option[String] = None,
                             @JsonKey("doc_values") docValues: Option[Boolean] = None,
                             @JsonNoDefault store: Boolean = false,
                             @JsonNoDefault index: Boolean = true,
                             @JsonNoDefault boost: Float = 1.0f,
                             @JsonKey("index_options") indexOptions: Option[IndexOptions] = None,
                             similarity: Option[Similarity] = None,
                             @JsonKey("copy_to") copyTo: List[String] = Nil,
                             fields: Map[String, Mapping] = Map.empty[String, Mapping],
                             `type`: String = TextMapping.typeName)
    extends Mapping {

}

object TokenCountMapping extends MappingType[TokenCountMapping] {

  implicit val myStringList=Mapping.myListString

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
   * Make a JSON representation. Must return a JsonObject("field" -> JsonObject(...)).
   */
  override def docValues: Option[Boolean] = None


}

@JsonCodec
final case class BoostMapping(name: Option[String] = None, @JsonKey("null_value") nullValue: Option[Float] = None)
    extends InternalMapping {

  override def NAME = BoostMapping.typeName


}

object BoostMapping extends InternalMappingType {
  val typeName = "_boost"

}

@JsonCodec
final case class IdMapping(name: Option[String] = None) extends InternalMapping {

  def field = "_id"

  override def NAME = IdMapping.typeName

}

object IdMapping extends InternalMappingType {
  val typeName = "_id"

}

@JsonCodec
final case class IndexMapping(enabled: Option[Boolean] = None, path: Option[String] = None) extends InternalMapping {

  def field = "_index"

  override def NAME = IndexMapping.typeName

//  override def toJson: Json = this.asJson

}

object IndexMapping extends InternalMappingType {
  val typeName = "_index"
}

@JsonCodec
final case class ParentMapping(postingsFormat: Option[String] = None, path: Option[String] = None)
    extends InternalMapping {
  def field = "_parent"

  override def NAME = ParentMapping.typeName

//  override def toJson: Json = this.asJson

}

object ParentMapping extends InternalMappingType {
  val typeName = "_parent"

}

@JsonCodec
final case class RoutingMapping(var _required: Option[Boolean] = None, path: Option[String] = None)
    extends InternalMapping {
  def field = "_routing"

  override def NAME = RoutingMapping.typeName

//  override def toJson: Json = this.asJson

}
object RoutingMapping extends InternalMappingType {
  val typeName = "_routing"
}

@JsonCodec
final case class SourceMapping(enabled: Option[Boolean] = None) extends InternalMapping {
  def field = "_source"

  override def NAME = SourceMapping.typeName

//  override def toJson: Json = this.asJson

}

object SourceMapping extends InternalMappingType {
  val typeName = "_source"
}

@JsonCodec
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
