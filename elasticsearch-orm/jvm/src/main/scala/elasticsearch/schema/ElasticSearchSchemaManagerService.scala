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
import elasticsearch.orm.ORMService
import logstage.IzLogger
import zio._
import zio.auth.AuthContext
import zio.exception._
import zio.schema._
import zio.schema.generic.JsonSchema

trait ElasticSearchSchemaManagerService extends ORMService {
  val elasticSearchSchemaManagerService: ElasticSearchSchemaManagerService.Service[Any]
}

object ElasticSearchSchemaManagerService {
  trait Service[R] {
    def iLogger: IzLogger
    def registerSchema[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, Unit]
    def getMapping(schema: Schema): ZIO[Any, FrameworkException, RootDocumentMapping]
    def createMapping[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, Unit]
    def createIndicesFromRegisteredSchema(): ZIO[Any, FrameworkException, Unit]
  }

  trait Live extends ElasticSearchSchemaManagerService {
    val elasticSearchSchemaManagerService: ElasticSearchSchemaManagerService.Service[Any] =
      new ElasticSearchSchemaManagerService.Service[Any] {
        override def iLogger: IzLogger = schemaService.iLogger
        implicit val authContext = AuthContext.System

        def registerSchema[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, Unit] =
          schemaService
            .registerSchema(jsonSchema)
            .mapError(e => UnableToRegisterSchemaException(jsonSchema.toString))
            .unit

        def createMapping[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, Unit] = {
          val schema = jsonSchema.asSchema
          for {
            _ <- schemaService.registerSchema(schema)
            root <- getMapping(schema)
            index <- indices.createWithSettingsAndMappings(getIndexFromSchema(schema), mappings = Some(root)).unit
          } yield index
        }

        private def getIndexFromSchema(schema: Schema): String =
          schema.index.indexName.getOrElse(schema.name)

        override def createIndicesFromRegisteredSchema(): ZIO[Any, FrameworkException, Unit] = {
          def mergeSchemas(
            schemas: List[Schema],
            mappings: List[RootDocumentMapping]
          ): ZIO[Any, FrameworkException, List[(String, RootDocumentMapping)]] = {
            val mappingMerger = new MappingMerger(iLogger)
            val merged = schemas
              .map(s => getIndexFromSchema(s) -> s)
              .zip(mappings)
              .groupBy(_._1._1)
              .map {
                case (name, mps) =>
                  val schemaMappings = mps.map { v =>
                    v._1._2.className.getOrElse(v._1._2.name) -> v._2
                  }
                  name -> mappingMerger.merge(schemaMappings)
              }
              .toList

            for {
              maps <- ZIO.foreach(merged) {
                case (index, eithermapping) =>
                  ZIO
                    .fromEither(eithermapping)
                    .map(m => index -> m.asInstanceOf[RootDocumentMapping])
                    .mapError(e => FrameworkMultipleExceptions(e))
              }
//              mainMappings <- ZIO.sequence(maps)
            } yield maps

          }

          for {
            schemas <- schemaService.schemas
            mappings <- ZIO.foreach(schemas)(getMapping)
            merged <- ZIO.effect(mergeSchemas(schemas, mappings)).mapError(e => FrameworkException(e))
            finalMappings <- merged
            _ <- ZIO.foreach(finalMappings) {
              case (name, mapping) =>
                indices.createWithSettingsAndMappings(name, mappings = Some(mapping))
            }
          } yield ()

        }

        def getMapping(schema: Schema): ZIO[Any, FrameworkException, RootDocumentMapping] = {
          for {
            esProperties <- ZIO.foreach(schema.properties.filter(_.name != "_id"))(f => internalConversion(f))
          } yield RootDocumentMapping(properties = esProperties.flatten.toMap)

        }.mapError(e => FrameworkException(e))

        def getObjectMappings(schema: Schema): Task[List[(String, Mapping)]] =
          for {
            esProperties <- ZIO.foreach(schema.properties.filter(_.name != "_id"))(f => internalConversion(f))
          } yield List(schema.name -> ObjectMapping(properties = esProperties.flatten.toMap))

//      private def mergedMapping(
//                                 schemas: Seq[Schema]
//                               ): (Seq[MergeMappingException], RootDocumentMapping) = {
//        val documentMappings = schemas.map(s => getMapping(s))
//
//        var totalErrors = Seq.empty[MergeMappingException]
//
//        documentMappings.drop(1).foreach {
//          case (newName, newObject) =>
//            val (errors, optMapping) =
//              resultObject.merge(resultObjectName, newName, newObject)
//            totalErrors ++= errors
//            optMapping.foreach(v => resultObject = v.asInstanceOf[RootDocumentMapping])
//        }
//        totalErrors -> resultObject
//      }

        private def stringMappingForAnnotation(
          o: StringSchemaField,
          annotationName: String,
          name: String = "",
          subFields: Map[String, Mapping] = Map.empty[String, Mapping]
        ): List[(String, Mapping)] =
          annotationName match {
            case "keyword" =>
              List(
                name -> KeywordMapping(
                  index = o.indexProperties.index,
                  store = o.indexProperties.stored,
                  fields = subFields
                )
              )
            case "text" =>
              List(
                name -> TextMapping(
                  index = o.indexProperties.index,
                  store = o.indexProperties.stored,
                  fields = subFields
                )
              )
            case "suggest" =>
              List(
                "tk" -> TextMapping(analyzer = Some(Analyzer.SimpleAnalyzer)),
                "bigram" -> TextMapping(analyzer = Some(Analyzer.BigramAnalyzer)),
                "reverse" -> TextMapping(analyzer = Some(Analyzer.ReverseAnalyzer)),
                "trigram" -> TextMapping(analyzer = Some(Analyzer.TrigramAnalyzer)),
                "quadrigram" -> TextMapping(analyzer = Some(Analyzer.QuadrigramAnalyzer)),
                "gram" -> TextMapping(analyzer = Some(Analyzer.GramAnalyzer))
              )
            case "stem|it" =>
              List(
                "it" -> TextMapping(
                  index = o.indexProperties.index,
                  store = o.indexProperties.stored,
                  analyzer = Some(Analyzer.ItalianLanguageAnalyzer),
                  fields = subFields
                )
              )
            //TODO NLP
            case other =>
              List(
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
        ): List[(String, Mapping)] =
          if (o.indexProperties.index) {
            val analyzers = o.indexProperties.analyzers
            if (analyzers.isEmpty) {
              o.subType match {
                case None =>
                  List(
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
                case Some(subType) => List(o.name -> getSubType(subType, o))
              }

            } else {
              if (analyzers.length == 1) {
                o.subType match {
                  case None =>
                    stringMappingForAnnotation(o, analyzers.head, name = o.name)
                  case Some(subType) =>
                    List(
                      o.name -> getSubType(
                        subType,
                        o,
                        subFields = stringMappingForAnnotation(
                          o,
                          analyzers.head,
                          name = o.name
                        ).toMap
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
                    List(o.name -> getSubType(subType, o, subFields = subFields))
                }
              }
            }
          } else {
            o.subType match {
              case None =>
                List(
                  o.name -> KeywordMapping(
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              case Some(subType) => List(o.name -> getSubType(subType, o))
            }
          }

        private def internalConversion(
          schemaField: SchemaField
        ): Task[List[(String, Mapping)]] = {
          iLogger.debug(s"internalConversion processing: ${schemaField}")

          schemaField match {
            case o: StringSchemaField => ZIO.succeed(convertStringSchemaField(o))
            case o: OffsetDateTimeSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> DateTimeMapping(
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )
            case o: LocalDateTimeSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> DateTimeMapping(
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )
            case o: LocalDateSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> DateTimeMapping(
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )
            case o: DoubleSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> NumberMapping(
                    `type` = NumberType.DOUBLE.entryName,
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )

            case o: BigIntSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> NumberMapping(
                    `type` = NumberType.LONG.entryName,
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )

            case o: IntSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> NumberMapping(
                    `type` = NumberType.INTEGER.entryName,
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )

            case o: BooleanSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> BooleanMapping(
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )

            case o: LongSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> NumberMapping(
                    `type` = NumberType.LONG.entryName,
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )

            case o: ShortSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> NumberMapping(
                    `type` = NumberType.SHORT.entryName,
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )

            case o: FloatSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> NumberMapping(
                    `type` = NumberType.FLOAT.entryName,
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )

            case o: ByteSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> NumberMapping(
                    `type` = NumberType.BYTE.entryName,
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )
            case o: GeoPointSchemaField =>
              ZIO.succeed(
                List(
                  o.name -> GeoPointMapping(
                    index = o.indexProperties.index,
                    store = o.indexProperties.stored
                  )
                )
              )

            case o: ListSchemaField => internalConversion(o.items)

            case o: SeqSchemaField    => internalConversion(o.items)
            case o: SetSchemaField    => internalConversion(o.items)
            case o: VectorSchemaField => internalConversion(o.items)
            case o: SchemaMetaField =>
              for {
                parameters <- ZIO.foreach(o.properties) { f =>
                  internalConversion(f)
                }
              } yield o.indexProperties.nesting match {
                case NestingType.Nested =>
                  List(
                    o.name -> NestedMapping(
                      properties = parameters.flatten.toMap,
                      enabled = o.indexProperties.index
                    )
                  )
                case NestingType.Embedded =>
                  List(
                    o.name -> ObjectMapping(
                      properties = parameters.flatten.toMap,
                      enabled = o.indexProperties.index
                    )
                  )
              }

            case o: RefSchemaField =>
              for {
                schema <- schemaService.getSchema(o.ref.substring(4))
                res <- getObjectMappings(schema)
              } yield res
          }

        }
      }
  }
}
