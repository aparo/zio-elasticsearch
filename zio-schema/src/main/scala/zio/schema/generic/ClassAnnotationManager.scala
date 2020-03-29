/*
 * Copyright 2019-2020 Alberto Paro
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

package zio.schema.generic

import zio.circe.CirceUtils
import io.circe._
import io.circe.syntax._
import zio.common.{ NamespaceUtils, StringUtils }
import zio.schema.StringSubType
import zio.schema.annotations._
import io.circe.derivation.annotations.JsonKey
import zio.exception.InvalidJsonValue

import scala.annotation.StaticAnnotation
import scala.collection.immutable.Map
import scala.collection.mutable

private class ClassAnnotationManager(
  val fullname: String,
  val annotations: List[StaticAnnotation]
) {

  import zio.schema.SchemaNames._

  lazy val name: String =
    StringUtils.convertCamelToSnakeCase(NamespaceUtils.getModelName(fullname))

  lazy val module: String = NamespaceUtils.getModule(fullname)

  // map a fullname to special types
  private val speciaFieldType =
    Map(
      "elasticsearch.geo.GeoHash" -> "geo_point",
      "elasticsearch.geo.GeoPoint" -> "geo_point",
      "elasticsearch.geo.GeoPointLatLon" -> "geo_point"
    )

  private def extractType: String = speciaFieldType.getOrElse(this.fullname, "object")

  def buildMainFields(
    source: JsonObject,
    defaultMap: Map[String, Any],
    annotationsMap: Map[String, List[StaticAnnotation]]
  ): JsonObject = {
    val (properties, fieldKeyParts) = this.injectProperties(
      source,
      defaultMap = defaultMap, //        defaultMapJson = defaultMapJson,
      annotationsMap = annotationsMap
    )

    val mainFields: List[(String, Json)] = List(
      TYPE -> Json.fromString(extractType),
      NAME -> Json.fromString(this.name),
      MODULE -> Json.fromString(this.module),
      CLASS_NAME -> Json.fromString(this.fullname),
      IS_ROOT -> Json.fromBoolean(true),
      PROPERTIES -> properties.asJson
    ) ++ getMainAnnotations(fieldKeyParts)

    JsonObject.fromIterable(mainFields)

  }

  /**
   * Extract columnar properties
   * @param annotations columnar annotations
   * @return a columnar JsonObject with columnar data
   */
  def extractColumnarDescription(
    annotations: List[ColumnarAnnotation]
  ): JsonObject = {

    var fields = List.empty[(String, Json)]
    var visibilities: List[Json] = this.annotations.collect {
      case c: ColumnVisibility           => c.asJson
      case c: ColumnVisibilityExpression => c.asJson
      case c: ColumnVisibilityScript     => c.asJson
    }
    annotations.foreach {
      case nc: NoColumnar      => fields ::= ACTIVE -> Json.fromBoolean(false)
      case ColumnFamily(value) => fields ::= FAMILY -> Json.fromString(value)
      case ColumnQualifier(value) =>
        fields ::= QUALIFIER -> Json.fromString(value)
      case TableName(value) => fields ::= TABLE -> Json.fromString(value)
      case NamespaceName(value) =>
        fields ::= NAMESPACE -> Json.fromString(value)
//      case c:ColumnVisibilityAnnotation => visibilities ::= c.asJson
      case c: ColumnVisibilityExpression => visibilities ::= c.asJson
      case c: ColumnVisibilityScript     => visibilities ::= c.asJson
      case c: ColumnVisibility           => visibilities ::= c.asJson

      case _: ColumnarSingleJson =>
        fields ::= IS_SINGLE_JSON -> Json.fromBoolean(true)
      case SingleStorage(singleStorageName) =>
        fields ::= SINGLE_STORAGE -> Json.fromString(singleStorageName)
    }

    if (visibilities.nonEmpty) {
      fields ::= VISIBILITY -> visibilities.distinct.asJson
    }
    JsonObject.fromIterable(fields)
  }

  /**
   * Extract index properties
   * @param annotations index annotations
   * @return a index JsonObject with columnar data
   */
  def extractIndexDescription(
    annotations: List[IndexAnnotation],
    isMainClass: Boolean = false
  ): JsonObject = {

    var fields = List.empty[(String, Json)]
    var textAnnotations = List.empty[String]
    var hasVisibility = false
    annotations.foreach {
      case _: Embedded   => fields ::= NESTING -> Json.fromString(EMBEDDED)
      case _: Nested     => fields ::= NESTING -> Json.fromString(NESTED)
      case Parent(value) => fields ::= PARENT -> Json.fromString(value)
      case _: Index      => fields ::= INDEX -> Json.fromBoolean(true)
      case _: NoIndex =>
        if (isMainClass)
          fields ::= ACTIVE -> Json.fromBoolean(false)
        else
          fields ::= INDEX -> Json.fromBoolean(false)
      case Store(value)      => fields ::= STORE -> Json.fromBoolean(value)
      case _: Keyword        => textAnnotations ::= TEXT_KEYWORD
      case _: Text           => textAnnotations ::= TEXT_TEXT
      case _: NLP            => textAnnotations ::= TEXT_NLP
      case _: Suggest        => textAnnotations ::= TEXT_SUGGEST
      case Stem(language)    => textAnnotations ::= s"${TEXT_STEM}|$language"
      case _: HeatMap        => textAnnotations ::= HEATMAP
      case _: TimeSerieField => //TODO manage timeseries if required
      case _: TimeSerieIndex => //TODO manage timeseries if required
      case IndexName(value) =>
        if (isMainClass)
          fields ::= "indexName" -> Json.fromString(value)
      case IndexPrefix(value) =>
        if (isMainClass)
          fields ::= "indexPrefix" -> Json.fromString(value)
    }

    if (textAnnotations.nonEmpty)
      fields ::= ANALYZERS -> textAnnotations.asJson

    JsonObject.fromIterable(fields)
  }

  /**
   * Extract version if defined in annotation
   * @return a option inve value about the version
   */
  def getVersion(): Option[Int] =
    annotations.find(_.isInstanceOf[Version]).map(_.asInstanceOf[Version].version)

  /**
   * Extract id if defined in annotation
   * @return a option inve value about the version
   */
  def getId(): Option[String] =
    annotations.find(_.isInstanceOf[SchemaId]).map(_.asInstanceOf[SchemaId].id)

  /**
   * Extract the given annotation if exists
   * @return a option value
   */
  def getMainAnnotations(fieldKeyParts: List[KeyPart]): List[(String, Json)] = {
    var mainFields: List[(String, Json)] = Nil
    getVersion().foreach { version =>
      mainFields ::= VERSION -> Json.fromInt(version)
    }

    getId().foreach { value =>
      mainFields ::= ID -> Json.fromString(value)
    }

    annotations
      .find(_.isInstanceOf[Description])
      .map(_.asInstanceOf[Description].description)
      .foreach(ann => mainFields ::= DESCRIPTION -> Json.fromString(ann))

    annotations
      .find(_.isInstanceOf[Label])
      .map(_.asInstanceOf[Label].label)
      .foreach(ann => mainFields ::= LABEL -> Json.fromString(ann))

    annotations.find(_.isInstanceOf[AutoOwner]).foreach(ann => mainFields ::= AUTO_OWNER -> Json.fromBoolean(true))

    val storages = annotations.collect {
      case a: StorageAnnotation => a
    }.map(_.value)
//    if (storages.isEmpty)
//      logger.warn(
//          s"Undefined data storage for ${fullname}: Use one of more of IgniteStorage, ElasticSearchStorage, or ColumnarStorage")

    if (storages.nonEmpty)
      mainFields ::= STORAGES -> storages.asJson

    val columnar = extractColumnarDescription(annotations.collect {
      case a: ColumnarAnnotation => a
    })

    if (columnar.nonEmpty)
      mainFields ::= COLUMNAR -> Json.fromJsonObject(columnar)

    val index = extractIndexDescription(annotations.collect {
      case a: IndexAnnotation => a
    }, isMainClass = true)

    if (index.nonEmpty)
      mainFields ::= INDEX -> Json.fromJsonObject(index)

    //key mananagement
    var keymanager = annotations.collectFirst {
      case a: KeyManagement => a
    }.getOrElse(KeyManagement.empty)

    if (fieldKeyParts.nonEmpty)
      keymanager = keymanager.copy(parts = keymanager.parts ++ fieldKeyParts)

    //Key annotations
    annotations.collect {
      case ann: PKAnnotation => ann
    }.foreach {
      case _: PK =>
      case pksep: PKSeparator =>
        keymanager = keymanager.copy(separator = Some(pksep.text))
      case _: PKLowercase =>
        keymanager = keymanager.copy(postProcessing = List(KeyPostProcessing.LowerCase))
      case _: PKHash =>
        keymanager = keymanager.copy(postProcessing = List(KeyPostProcessing.Hash))
      case _: PKLowercaseHash =>
        keymanager = keymanager.copy(
          postProcessing = List(KeyPostProcessing.LowerCase, KeyPostProcessing.Hash)
        )
    }

    if (keymanager != KeyManagement.empty)
      mainFields ::= KEY -> keymanager.asJson

    mainFields
  }

  def injectProperties(
    source: JsonObject,
    defaultMap: Map[String, Any],
    //                       defaultMapJson: Map[String, Json],
    annotationsMap: Map[String, List[StaticAnnotation]]
  ): (List[Json], List[KeyPart]) = {

    import zio.schema.annotations._

    val keyParts = new mutable.ListBuffer[KeyPart]()

    val fields: List[Json] = source.toMap.map {
      case (fname, json) =>
        var j = json.asObject.get
        if (defaultMap.contains(fname)) {
          try {
            CirceUtils.anyToJson(defaultMap(fname)) match {
              case Json.Null =>
              case x         => j = j.add("default", x)
            }
          } catch {
            case ex: InvalidJsonValue =>
            //logger.warning("")
          }

        }

        val annotations = annotationsMap.getOrElse(fname, Nil)
        val realName = extractRealName(fname, annotations)

        //ColumnarAnnotation
        val columnar = this.extractColumnarDescription(annotations.collect {
          case a: ColumnarAnnotation => a
        })
        if (!columnar.isEmpty)
          j = j.add(COLUMNAR, Json.fromJsonObject(columnar))

        //IndexingAnnotation
        val indexing = this.extractIndexDescription(annotations.collect {
          case a: IndexAnnotation => a
        })
        if (!indexing.isEmpty)
          j = j.add(INDEX, Json.fromJsonObject(indexing))

        //Subtype annotations
        annotations.find(_.isInstanceOf[SubTypeAnnotation]).foreach { ann =>
          ann.asInstanceOf[SubTypeAnnotation] match {
            case _: Email =>
              j = j.add(SUB_TYPE, Json.fromString(StringSubType.Email.entryName))
            case _: Ip =>
              j = j.add(SUB_TYPE, Json.fromString(StringSubType.IP.entryName))
            case _: Password =>
              j = j.add(
                SUB_TYPE,
                Json.fromString(StringSubType.Password.entryName)
              )
            case _: UserId =>
              j = j.add(
                SUB_TYPE,
                Json.fromString(StringSubType.UserId.entryName)
              )
            case _: Vertex =>
              j = j.add(
                SUB_TYPE,
                Json.fromString(StringSubType.Vertex.entryName)
              )
          }
        }

        //Key annotations
        val keyAnnotations = annotations.filter(_.isInstanceOf[PKAnnotation])
        if (keyAnnotations.nonEmpty) {
          val name = realName
          val postProcessings = new mutable.ListBuffer[KeyPostProcessing]()
          keyAnnotations.foreach { ann =>
            ann.asInstanceOf[PKAnnotation] match {
              case _: PKSeparator =>
              case _: PK          =>
              case _: PKLowercase =>
                postProcessings += KeyPostProcessing.LowerCase
              case _: PKHash => postProcessings += KeyPostProcessing.Hash
              case _: PKLowercaseHash =>
                postProcessings ++= List(
                  KeyPostProcessing.LowerCase,
                  KeyPostProcessing.Hash
                )
            }
          }
          keyParts += KeyField(name, postProcessing = postProcessings.toList)

        }

        //special field annotations
        if (annotations.exists(_.isInstanceOf[Unique]))
          j = j.add("unique", Json.fromBoolean(true))
        //        else
        //          j=j.add("unique" , Json.fromBoolean(false))

        if (annotations.exists(_.isInstanceOf[Created]))
          j = j.add("created", Json.fromBoolean(true))
        //        else
        //          j=j.add("created" , Json.fromBoolean(false))

        if (annotations.exists(_.isInstanceOf[Modified]))
          j = j.add("modified", Json.fromBoolean(true))
        //        else
        //          j=j.add("modified" , Json.fromBoolean(false))

        annotations
          .find(_.isInstanceOf[Label])
          .map(_.asInstanceOf[Label].label)
          .foreach(ann => j = j.add(LABEL, Json.fromString(ann)))

        annotations
          .find(_.isInstanceOf[Description])
          .map(_.asInstanceOf[Description].description)
          .foreach(ann => j = j.add(DESCRIPTION, Json.fromString(ann)))

        //we cook items
        val items = j("items").map { itemObj =>
          itemObj.asObject.get.add(NAME, Json.fromString(realName))
        }
        if (items.isDefined) {
          j = j.add("items", Json.fromJsonObject(items.get))
        }

        if (realName != fname) {
          j = j.add(CLASS_NAME, Json.fromString(fname))
          j = j.add("name", Json.fromString(realName))

          Json.fromJsonObject(j)
        } else {
          j = j.add(NAME, Json.fromString(fname))
          Json.fromJsonObject(j)
        }

    }.toList
    (fields, keyParts.toList)
  }

  /** *
   * Extract the realName for a field
   *
   * @param currentName the field RealName
   * @param annotations a list of field annotations
   * @return the realName
   */
  def extractRealName(
    currentName: String,
    annotations: List[StaticAnnotation]
  ): String =
    if (annotations.isEmpty) currentName
    else {
      val annots = annotations.collect { case JsonKey(name) => name }
      annots.headOption.getOrElse(currentName)
    }
}
