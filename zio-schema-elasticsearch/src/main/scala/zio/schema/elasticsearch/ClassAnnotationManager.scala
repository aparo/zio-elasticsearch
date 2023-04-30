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

package zio.schema.elasticsearch

import scala.annotation.{ Annotation, StaticAnnotation }
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import zio.Chunk
import zio.common.{ NamespaceUtils, StringUtils }
import zio.exception.InvalidJsonException
import zio.json._
import zio.json.ast.{ Json, JsonCursor, JsonUtils }
import zio.schema.elasticsearch.SchemaNames._
import zio.schema.elasticsearch.annotations._

private class ClassAnnotationManager(
  val fullname: String,
  val annotations: List[StaticAnnotation]
) {

  lazy val name: String = {
    import StringUtils._
    NamespaceUtils.getModelName(fullname).convertCamelToSnakeCase
  }

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
    source: Json.Obj,
    defaultMap: Map[String, Any],
    annotationsMap: Map[String, List[StaticAnnotation]]
  ): Json.Obj = {
    val (properties, fieldKeyParts) = this.injectProperties(
      source,
      defaultMap = defaultMap, //        defaultMapJson = defaultMapJson,
      annotationsMap = annotationsMap
    )

    val mainFields: Chunk[(String, Json)] = Chunk(
      TYPE -> Json.Str(extractType),
      NAME -> Json.Str(this.name),
      MODULE -> Json.Str(this.module),
      CLASS_NAME -> Json.Str(this.fullname),
      IS_ROOT -> Json.Bool(true),
      PROPERTIES -> properties.toJsonAST.getOrElse(Json.Obj())
    ) ++ getMainAnnotations(fieldKeyParts)

    Json.Obj(mainFields)

  }

  /**
   * Extract index properties
   * @param annotations
   *   index annotations
   * @return
   *   a index Json.Obj with columnar data
   */
  def extractIndexDescription(
    annotations: List[IndexAnnotation],
    isMainClass: Boolean = false
  ): Json.Obj = {

    var fields = List.empty[(String, Json)]
    var textAnnotations = List.empty[String]

    annotations.foreach {
      case _: Embedded   => fields ::= NESTING -> Json.Str(EMBEDDED)
      case _: Nested     => fields ::= NESTING -> Json.Str(NESTED)
      case Parent(value) => fields ::= PARENT -> Json.Str(value)
      case _: Index      => fields ::= INDEX -> Json.Bool(true)
      case _: NoIndex =>
        if (isMainClass)
          fields ::= ACTIVE -> Json.Bool(false)
        else
          fields ::= INDEX -> Json.Bool(false)
      case Store(value)      => fields ::= STORE -> Json.Bool(value)
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
          fields ::= "indexName" -> Json.Str(value)
      case IndexPrefix(value) =>
        if (isMainClass)
          fields ::= "indexPrefix" -> Json.Str(value)
      case _: IndexRequireType =>
        if (isMainClass)
          fields ::= "requireType" -> Json.Bool(true)
    }

    if (textAnnotations.nonEmpty)
      fields ::= ANALYZERS -> Json.Arr(textAnnotations.map(s => Json.Str(s)): _*)

    Json.Obj(Chunk.fromIterable(fields))
  }

  /**
   * Extract version if defined in annotation
   * @return
   *   a option inve value about the version
   */
  def getVersion(): Option[Int] =
    annotations.find(_.isInstanceOf[Version]).map(_.asInstanceOf[Version].version)

  /**
   * Extract id if defined in annotation
   * @return
   *   a option inve value about the version
   */
  def getId(): Option[String] =
    annotations.find(_.isInstanceOf[SchemaId]).map(_.asInstanceOf[SchemaId].id)

  /**
   * Extract the given annotation if exists
   * @return
   *   a option value
   */
  def getMainAnnotations(fieldKeyParts: List[KeyPart]): List[(String, Json)] = {
    val mainFields = new ListBuffer[(String, Json)]()
    getVersion().foreach { version =>
      mainFields += VERSION -> Json.Num(version)
    }

    getId().foreach { value =>
      mainFields += ID -> Json.Str(value)
    }

    annotations
      .find(_.isInstanceOf[Description])
      .map(_.asInstanceOf[Description].description)
      .foreach(ann => mainFields += DESCRIPTION -> Json.Str(ann))

    annotations
      .find(_.isInstanceOf[Label])
      .map(_.asInstanceOf[Label].label)
      .foreach(ann => mainFields += LABEL -> Json.Str(ann))

    annotations.find(_.isInstanceOf[AutoOwner]).foreach(_ => mainFields += AUTO_OWNER -> Json.Bool(true))

    val index = extractIndexDescription(
      annotations.collect {
        case a: IndexAnnotation =>
          a
      },
      isMainClass = true
    )

    if (index.fields.nonEmpty)
      mainFields += INDEX -> index

    //key mananagement
    var keymanager = annotations.collectFirst {
      case a: KeyManagement =>
        a
    }.getOrElse(KeyManagement.empty)

    if (fieldKeyParts.nonEmpty)
      keymanager = keymanager.copy(parts = keymanager.parts ++ fieldKeyParts)

    //Key annotations
    annotations.collect {
      case ann: PKAnnotation =>
        ann
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
      mainFields += KEY -> keymanager.toJsonAST.getOrElse(Json.Arr())

    mainFields.toList
  }

  def injectProperties(
    source: Json.Obj,
    defaultMap: Map[String, Any],
    //                       defaultMapJson: Map[String, Json],
    annotationsMap: Map[String, List[StaticAnnotation]]
  ): (List[Json], List[KeyPart]) = {

    val keyParts = new mutable.ListBuffer[KeyPart]()

    val fields: List[Json] = source.fields.map {
      case (fname, json) =>
        var j = json.asInstanceOf[Json.Obj]
        if (defaultMap.contains(fname)) {
          try {
            JsonUtils.anyToJson(defaultMap(fname)) match {
              case Json.Null =>
              case x         => j = j.add("default", x)
            }
          } catch {
            case _: InvalidJsonException =>
            //logger.warning("")
          }

        }

        val annotations = annotationsMap.getOrElse(fname, Nil)
        val realName = extractRealName(fname, annotations)

        //IndexingAnnotation
        val indexing = this.extractIndexDescription(annotations.collect {
          case a: IndexAnnotation =>
            a
        })
        if (!indexing.fields.isEmpty)
          j = j.add(INDEX, indexing)

        //Subtype annotations
        annotations.find(_.isInstanceOf[SubTypeAnnotation]).foreach { ann =>
          ann.asInstanceOf[SubTypeAnnotation] match {
            case _: Email =>
              j = j.add(SUB_TYPE, StringSubType.Email.asInstanceOf[StringSubType].toJsonAST)
            case _: Ip =>
              j = j.add(SUB_TYPE, StringSubType.IP.asInstanceOf[StringSubType].toJsonAST)
            case _: Password =>
              j = j.add(
                SUB_TYPE,
                StringSubType.Password.asInstanceOf[StringSubType].toJsonAST
              )
            case _: UserId =>
              j = j.add(
                SUB_TYPE,
                StringSubType.UserId.asInstanceOf[StringSubType].toJsonAST
              )
            case _: Vertex =>
              j = j.add(
                SUB_TYPE,
                StringSubType.Vertex.asInstanceOf[StringSubType].toJsonAST
              )
            case _: Binary =>
              j = j.add(
                SUB_TYPE,
                StringSubType.Binary.asInstanceOf[StringSubType].toJsonAST
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
          j = j.add("unique", Json.Bool(true))
        //        else
        //          j=j.add("unique" , Json.Bool(false))

        if (annotations.exists(_.isInstanceOf[Created]))
          j = j.add("created", Json.Bool(true))
        //        else
        //          j=j.add("created" , Json.Bool(false))

        if (annotations.exists(_.isInstanceOf[Modified]))
          j = j.add("modified", Json.Bool(true))
        //        else
        //          j=j.add("modified" , Json.Bool(false))

        annotations
          .find(_.isInstanceOf[Label])
          .map(_.asInstanceOf[Label].label)
          .foreach(ann => j = j.add(LABEL, Json.Str(ann)))

        annotations
          .find(_.isInstanceOf[Description])
          .map(_.asInstanceOf[Description].description)
          .foreach(ann => j = j.add(DESCRIPTION, Json.Str(ann)))

        //we cook items
        val items = j.get(JsonCursor.field("items")).map { itemObj =>
          itemObj.asInstanceOf[Json.Obj].add(NAME, Json.Str(realName))
        }
        if (items.isRight) {
          j = j.add("items", items)
        }

        if (realName != fname) {
          j = j.add(CLASS_NAME, Json.Str(fname))
          j = j.add("name", Json.Str(realName))

          j
        } else {
          j = j.add(NAME, Json.Str(fname))
          j
        }

    }.toList
    (fields, keyParts.toList)
  }

  /**
   * * Extract the realName for a field
   *
   * @param currentName
   *   the field RealName
   * @param annotations
   *   a list of field annotations
   * @return
   *   the realName
   */
  def extractRealName(
    currentName: String,
    annotations: List[Annotation]
  ): String =
    if (annotations.isEmpty) currentName
    else {
      val annots = annotations.collect { case s: jsonField => s.name }
      annots.headOption.getOrElse(currentName)
    }
}
