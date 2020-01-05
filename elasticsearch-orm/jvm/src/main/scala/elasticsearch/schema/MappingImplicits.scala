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

package elasticsearch.schema

import elasticsearch.analyzers.Analyzer
import elasticsearch.mappings._
import logstage.IzLogger
import zio.schema._
import zio.auth.AuthContext

import scala.concurrent.Await

object MappingImplicits {

  implicit class SchemaField2ESMapping(val schemaField: SchemaField) {
    private def stringMappingForAnnotation(
      o: StringSchemaField,
      annotationName: String,
      name: String = "",
      subFields: Map[String, Mapping] = Map.empty[String, Mapping]
    ): Map[String, Mapping] =
      annotationName match {
        case "keyword" =>
          Map(
            name -> KeywordMapping(
              index = o.indexProperties.index,
              store = o.indexProperties.stored,
              fields = subFields
            )
          )
        case "text" =>
          Map(
            name -> TextMapping(
              index = o.indexProperties.index,
              store = o.indexProperties.stored,
              fields = subFields
            )
          )
        case "suggest" =>
          Map(
            "tk" -> TextMapping(analyzer = Some(Analyzer.SimpleAnalyzer)),
            "bigram" -> TextMapping(analyzer = Some(Analyzer.BigramAnalyzer)),
            "reverse" -> TextMapping(analyzer = Some(Analyzer.ReverseAnalyzer)),
            "trigram" -> TextMapping(analyzer = Some(Analyzer.TrigramAnalyzer)),
            "quadrigram" -> TextMapping(analyzer = Some(Analyzer.QuadrigramAnalyzer)),
            "gram" -> TextMapping(analyzer = Some(Analyzer.GramAnalyzer))
          )
        case "stem|it" =>
          Map(
            "it" -> TextMapping(
              index = o.indexProperties.index,
              store = o.indexProperties.stored,
              analyzer = Some(Analyzer.ItalianLanguageAnalyzer),
              fields = subFields
            )
          )
        //TODO NLP
        case other =>
          Map(
            name -> TextMapping(
              index = o.indexProperties.index,
              store = o.indexProperties.stored,
              analyzer = Some(Analyzer.byName(other)),
              fields = subFields
            )
          )
      }

    private def getSubType(
      subType: StringSubType,
      o: StringSchemaField,
      subFields: Map[String, Mapping] = Map.empty[String, Mapping]
    ): Mapping =
      subType match {
        case StringSubType.Email =>
          KeywordMapping(
            index = o.indexProperties.index,
            store = o.indexProperties.stored,
            fields = Map("tk" -> TextMapping(analyzer = Some(Analyzer.SimpleAnalyzer)))
          )

        case StringSubType.Password | StringSubType.UserId | StringSubType.Vertex | StringSubType.Crypted =>
          KeywordMapping(
            index = o.indexProperties.index,
            store = o.indexProperties.stored,
            fields = subFields
          )

        case StringSubType.Time =>
          DateTimeMapping(
            index = o.indexProperties.index,
            store = o.indexProperties.stored,
            fields = subFields
          )

        case StringSubType.IP =>
          IpMapping(
            index = o.indexProperties.index,
            store = o.indexProperties.stored,
            fields = subFields
          )

      }

    private def getFirstAnalyzer(analyzers: List[String]): String =
      if (analyzers.contains("text")) {
        "text"
      } else if (analyzers.contains("keyword")) {
        "keyword"
      } else {
        analyzers.head
      }

    private def convertStringSchemaField(
      o: StringSchemaField
    ): Map[String, Mapping] =
      if (o.indexProperties.index) {
        val analyzers = o.indexProperties.analyzers
        if (analyzers.isEmpty) {
          o.subType match {
            case None =>
              Map(
                o.name -> TextMapping(
                  index = o.indexProperties.index,
                  store = o.indexProperties.stored,
                  fields = Map(
                    "keyword" -> KeywordMapping(
                      index = o.indexProperties.index,
                      store = o.indexProperties.stored
                    )
                  )
                )
              )
            case Some(subType) => Map(o.name -> getSubType(subType, o))
          }

        } else {
          if (analyzers.length == 1) {
            o.subType match {
              case None =>
                stringMappingForAnnotation(o, analyzers.head, name = o.name)
              case Some(subType) =>
                Map(
                  o.name -> getSubType(
                    subType,
                    o,
                    subFields = stringMappingForAnnotation(
                      o,
                      analyzers.head,
                      name = o.name
                    )
                  )
                )
            }

          } else {
            o.subType match {
              case None =>
                val firstAnalyzer = getFirstAnalyzer(analyzers)
                val subFields = analyzers
                  .filterNot(_ == firstAnalyzer)
                  .flatMap { annotationName =>
                    stringMappingForAnnotation(
                      o,
                      annotationName = annotationName,
                      name = annotationName
                    ).toList
                  }
                  .toMap
                stringMappingForAnnotation(
                  o,
                  firstAnalyzer,
                  name = o.name,
                  subFields = subFields
                )
              case Some(subType) =>
                val subFields = analyzers.flatMap { annotationName =>
                  stringMappingForAnnotation(
                    o,
                    annotationName = annotationName,
                    name = annotationName
                  ).toList
                }.toMap
                Map(o.name -> getSubType(subType, o, subFields = subFields))
            }
          }
        }
      } else {
        o.subType match {
          case None =>
            Map(
              o.name -> KeywordMapping(
                index = o.indexProperties.index,
                store = o.indexProperties.stored
              )
            )
          case Some(subType) => Map(o.name -> getSubType(subType, o))
        }
      }

    def toMappings(essm: ElasticSearchSchemaManager)(
      implicit schemaManager: SchemaManager,
      AuthContext: AuthContext,
      logger: IzLogger
    ): Map[String, Mapping] = {
      logger.info(s"toMapping processing: ${this.schemaField}")
      internalConversion(this.schemaField, essm)
    }

    private def internalConversion(
      schemaField: SchemaField,
      essm: ElasticSearchSchemaManager
    )(
      implicit schemaManager: SchemaManager,
      AuthContext: AuthContext,
      logger: IzLogger
    ): Map[String, Mapping] = {
      logger.info(s"internalConversion processing: ${schemaField}")

      schemaField match {
        case o: StringSchemaField => convertStringSchemaField(o)
        case o: OffsetDateTimeSchemaField =>
          Map(
            o.name -> DateTimeMapping(
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )
        case o: LocalDateTimeSchemaField =>
          Map(
            o.name -> DateTimeMapping(
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )
        case o: LocalDateSchemaField =>
          Map(
            o.name -> DateTimeMapping(
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )
        case o: DoubleSchemaField =>
          Map(
            o.name -> NumberMapping(
              `type` = NumberType.DOUBLE.entryName,
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )

        case o: BigIntSchemaField =>
          Map(
            o.name -> NumberMapping(
              `type` = NumberType.LONG.entryName,
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )

        case o: IntSchemaField =>
          Map(
            o.name -> NumberMapping(
              `type` = NumberType.INTEGER.entryName,
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )

        case o: BooleanSchemaField =>
          Map(
            o.name -> BooleanMapping(
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )

        case o: LongSchemaField =>
          Map(
            o.name -> NumberMapping(
              `type` = NumberType.LONG.entryName,
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )

        case o: ShortSchemaField =>
          Map(
            o.name -> NumberMapping(
              `type` = NumberType.SHORT.entryName,
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )

        case o: FloatSchemaField =>
          Map(
            o.name -> NumberMapping(
              `type` = NumberType.FLOAT.entryName,
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )

        case o: ByteSchemaField =>
          Map(
            o.name -> NumberMapping(
              `type` = NumberType.BYTE.entryName,
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )
        case o: GeoPointSchemaField =>
          Map(
            o.name -> GeoPointMapping(
              index = o.indexProperties.index,
              store = o.indexProperties.stored
            )
          )

        case o: ListSchemaField => o.items.toMappings(essm)

        case o: SeqSchemaField    => o.items.toMappings(essm)
        case o: SetSchemaField    => o.items.toMappings(essm)
        case o: VectorSchemaField => o.items.toMappings(essm)
        case o: SchemaMetaField =>
          Map(
            o.name -> ObjectMapping(
              properties = o.properties.flatMap(f => internalConversion(f, essm).toList).toMap,
              enabled = o.indexProperties.index
            )
          )
        case o: RefSchemaField =>
          import scala.concurrent.duration._
          Await.result(
            schemaManager.getSchema(o.ref.substring(4)).value,
            5.seconds
          ) match {
            case Left(_) =>
              Map.empty[String, Mapping] //TODO manage better
            case Right(schema) =>
              List(essm.getObjectMappings(schema)).toMap
          }
      }

    }
  }

}
