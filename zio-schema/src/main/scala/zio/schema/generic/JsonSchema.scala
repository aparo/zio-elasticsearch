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

package zio.schema.generic

import scala.annotation.{ StaticAnnotation, implicitNotFound }
import scala.collection.immutable.ListMap
import scala.collection.mutable
import scala.language.experimental.macros
import scala.util.matching.Regex

import zio.exception.FrameworkException
import zio.openapi.{ MediaType, Reference, ReferenceOr, Response }
import zio.schema.SchemaNames._
import zio.schema.generic.JsonSchema.NamedDefinition
import zio.schema.{ Schema, StorageType }
import enumeratum.values._
import enumeratum.{ Enum, EnumEntry }
import zio.json.ast.Json
import zio.json._
import zio.json._
import zio.json._
import magnolia._

@implicitNotFound(
  "Cannot derive a JsonSchema for ${A}. Please verify that instances can be derived for all its fields"
)
trait JsonSchema[A] extends JsonSchema.HasRef {
  def id: String

  def inline: Boolean

  def relatedDefinitions: Set[JsonSchema.Definition]

  def fieldDefinitions: Set[JsonSchema.NamedDefinition] =
    relatedDefinitions.collect { case d: NamedDefinition => d }

  def jsonObject: Json.Obj

  def swaggerJsonObject: Json.Obj

  lazy val asJson: Json = {
    val jo = jsonObject

    if (jo.contains(ID)) {
      val curId = jo(ID).get.asString.get
      if (curId == UNDEFINED || curId.startsWith("java.") || curId.startsWith("scala.")) {
        Json.fromJsonObject(jo.filterKeys(_ != ID))
      } else
        Json.fromJsonObject(jo)
    } else {
      if (id == UNDEFINED || id.startsWith("java.") || id.startsWith("scala.")) {
        Json.fromJsonObject(jo.filterKeys(_ != ID))
      } else {
        Json.fromJsonObject(jo.add(ID, Json.Str(this.id)))
      }
    }
  }

  def asObjectRef: Json.Obj =
    id match {
      case _: String if id.startsWith("scala.") =>
        id.split('.').last match {
          case "String" => Json.Obj(TYPE -> Json.Str("string"))
          case "Int" =>
            Json.Obj(TYPE -> Json.Str("integer"), "format" -> Json.Str("int32"))
          case "Long" =>
            Json.Obj(TYPE -> Json.Str("integer"), "format" -> Json.Str("int64"))
          case "Short" =>
            Json.Obj(TYPE -> Json.Str("integer"), "format" -> Json.Str("int16"))
          case "Double"  => Json.Obj(TYPE -> Json.Str("double"))
          case "Boolean" => Json.Obj(TYPE -> Json.Str("bool"))
        }
      case UNDEFINED =>
        jsonObject

      case _ =>
        Json.Obj.fromMap(
          Map(
            REFERENCE -> Json.Str(s"#/components/schemas/$id"),
            TYPE -> Json.Str("ref")
          )
        )
    }

  def asJsonRef: Json = asObjectRef.asJson

  def asLeftReference: ReferenceOr[zio.openapi.Schema] = Left(Reference(id))
  def asRightReference: ReferenceOr[zio.openapi.Schema] = {
    val js = this.asJson
    js.as[Schema] match {
      case Left(_) =>
        js.as[zio.openapi.Schema] match {
          case Left(_) =>
            Left(Reference(id)) //TODO check fallback
          case Right(value) =>
            Right(value)
        }
      case Right(value) =>
        Right(value.toOpenApiSchema)
    }
  }

  def asSchema: Either[FrameworkException, Schema] = {
    val sch = this.asJson.as[Schema]
    if (sch.isLeft) {
      println(this.asJson.spaces2)
      println(sch)
    }
    sch.left.map(e => FrameworkException(e))
  }

  //We extract the list of storages
  lazy val storages: List[StorageType] = jsonObject(STORAGES)
    .flatMap(_.asArray)
    .getOrElse(Vector.empty[Json])
    .flatMap(_.asString)
    .map(s => StorageType.withNameInsensitive(s))
    .toList

  def NamedDefinition(fieldName: String): NamedDefinition =
    JsonSchema.NamedDefinition(
      id,
      fieldName,
      relatedDefinitions.map(_.asRef),
      asJsonRef
    )

  lazy val definition: JsonSchema.UnnamedDefinition =
    JsonSchema.UnnamedDefinition(
      id,
      relatedDefinitions.map(_.asRef),
      asJson
      //removeInvalidDefinitionFields(swaggerJsonObject)
    )

  lazy val definitionOpenAPI: (String, ReferenceOr[_root_.zio.openapi.Schema]) = id -> asRightReference

  protected val INVALID_DEFINITION_FIELDS =
    List(ID, CLASS_NAME, IS_ROOT, MODULE, NAME, MULTIPLE)

  private def removeInvalidDefinitionFields(json: Json.Obj): Json =
    Json.fromFields(
      json.toList.filterNot(v => INVALID_DEFINITION_FIELDS.contains(v._1)).map {
        case (k, v) =>
          k match {
            case PROPERTIES if v.isObject =>
              k -> Json.fromFields(
                v.asObject.get.toList.map(v2 => v2._1 -> removeInvalidDefinitionFields(v2._2.asObject.get))
              )
            case _ => k -> v
          }
      }
    )

  def definitions: Set[JsonSchema.Definition] =
    relatedDefinitions + definition

  def asResponse(description: String): ReferenceOr[Response] =
    if (asRef.id != "__undefined__")
      Left(Reference(asRef.id))
    else {
      Right(Response(description, content = ListMap("application/json" -> MediaType(schema = Some(asRightReference)))))
    }

}

object JsonSchema extends Primitives /* with generic.HListInstances with generic.CoprodInstances */ {

  val jsonUndefString: Json = Json.Str(UNDEFINED)

  trait HasRef {
    def id: String
    def asRef: Ref = TypeRef(id, None)
    def asArrayRef: Ref = ArrayRef(id, None)
  }

  sealed trait Definition extends HasRef {
    def id: String
    def json: Json
    def relatedRefs: Set[Ref]

    def asSchema: Either[FrameworkException, Schema] = {
      val json1 = Json.fromJsonObject(
        Json.Obj.fromIterable(
          Seq(
            "name" -> Json.Str("undefined")
          ) ++ json.asObject.getOrElse(Json.Obj()).toList
        )
      )
      val sch = json1.as[Schema]
      if (sch.isLeft) {
        println(json1)
        println(sch)
      }
      sch.left.map(e => FrameworkException(e))
    }

    def asRightReference: ReferenceOr[zio.openapi.Schema] =
      asSchema match {
        case Left(value) => Left(Reference(value.toString))
        case Right(value) =>
          Right(value.toOpenApiSchema)
      }

  }

  case class UnnamedDefinition(id: String, relatedRefs: Set[Ref], json: Json) extends Definition

  case class NamedDefinition(
    id: String,
    fieldName: String,
    relatedRefs: Set[Ref],
    json: Json
  ) extends Definition {
    override def asRef = TypeRef(id, Some(fieldName))
    override def asArrayRef = ArrayRef(id, Some(fieldName))
  }

  sealed trait Ref {
    def id: String
    def fieldName: Option[String]

    def isType: Boolean =
      id.startsWith("scala.")
  }

  object Ref {
    implicit final val decodeRef: JsonDecoder[Ref] = {
      JsonDecoder.instance { c =>
        c.as[TypeRef] //We need to decode also array???
      }
    }

    implicit final val encodeRef: JsonEncoder[Ref] = {
      JsonEncoder.instance {
        case obj: TypeRef =>
          val id = obj.id
          id match {
            case _: String if id.startsWith("scala.") =>
              id.split('.').last match {
                case "String" => Json.Obj(TYPE -> Json.Str("string"))
                case "Int" =>
                  Json.Obj(TYPE -> Json.Str("integer"), "format" -> Json.Str("int32"))
                case "Long" =>
                  Json.Obj(TYPE -> Json.Str("integer"), "format" -> Json.Str("int64"))
                case "Short" =>
                  Json.Obj(TYPE -> Json.Str("integer"), "format" -> Json.Str("int16"))
                case "Double"  => Json.Obj(TYPE -> Json.Str("double"))
                case "Boolean" => Json.Obj(TYPE -> Json.Str("bool"))
                case "UUID" =>
                  Json.Obj(TYPE -> Json.Str("string"), "format" -> Json.Str("int16"))
              }
            case _ =>
              Json.Obj(REFERENCE -> Json.Str("#/components/schemas/" + id))
          }

        case obj: ArrayRef => //Json.Obj("array" ->obj.asJson)
          obj.asJson
      }
    }
  }

  @JsonCodec
  case class TypeRef(id: String, fieldName: Option[String]) extends Ref

  object TypeRef {
    def apply(definition: Definition): TypeRef = TypeRef(definition.id, None)
    def apply(schema: JsonSchema[_]): TypeRef = TypeRef(schema.id, None)
  }

  @JsonCodec
  case class ArrayRef(id: String, fieldName: Option[String]) extends Ref

  object ArrayRef {
    def apply(definition: Definition): ArrayRef = ArrayRef(definition.id, None)
    def apply(schema: JsonSchema[_]): ArrayRef = ArrayRef(schema.id, None)
  }

  trait PatternProperty[K] {
    def regex: Regex
  }

  object PatternProperty {

    def fromRegex[K](r: Regex): PatternProperty[K] =
      new PatternProperty[K] { override val regex = r }

    implicit def intPatternProp: PatternProperty[Int] =
      fromRegex[Int]("[0-9]*".r)

    implicit def wildcard[K]: PatternProperty[K] =
      fromRegex[K](".*".r)
  }

  def instance[A](
    obj: => Json.Obj
  ): JsonSchema[A] = // (implicit tag: ru.WeakTypeTag[A])
  new JsonSchema[A] {
    override def id =
      if (jsonObject.contains(ID)) {
        jsonObject(ID).get.asString.get
      } else if (jsonObject.contains(CLASS_NAME)) {
        jsonObject(CLASS_NAME).get.asString.get
      } else {
//          tag.tpe.typeSymbol.fullName
        //We disactivate reflection
        UNDEFINED
      }

    override def inline = true
    override lazy val jsonObject = obj

    override def swaggerJsonObject =
      jsonObject.filterKeys(k => !INVALID_DEFINITION_FIELDS.contains(k))

    override def relatedDefinitions = Set.empty
  }

  def functorInstance[F[_], A](
    obj: => Json.Obj
  ): JsonSchema[F[A]] = //(implicit tag: ru.WeakTypeTag[A])
  new JsonSchema[F[A]] {
    override def id =
      if (obj.contains(ID)) {
        obj(ID).get.asString.get
      } else if (obj.contains(CLASS_NAME)) {
        obj(CLASS_NAME).get.asString.get
      } else {
//          tag.tpe.typeSymbol.fullName
        //We disactivate reflection
        UNDEFINED
      }
    override def inline = true
    override def jsonObject = obj
    override def swaggerJsonObject =
      obj.filterKeys(k => !INVALID_DEFINITION_FIELDS.contains(k))
    override def relatedDefinitions = Set.empty
  }

  def instanceAndRelated[A](
    pair: => (Json.Obj, Set[Definition])
  ): JsonSchema[A] = new JsonSchema[A] { //(implicit tag: ru.WeakTypeTag[A])
    override def id =
      if (pair._1.contains(ID)) {
        pair._1(ID).get.asString.get
      } else if (pair._1.contains(CLASS_NAME)) {
        pair._1(CLASS_NAME).get.asString.get
      } else {
//        tag.tpe.typeSymbol.fullName
        //We disactivate reflection
        UNDEFINED
      }
    override def inline = true
    override def jsonObject = pair._1
    override def swaggerJsonObject =
      pair._1.filterKeys(k => !INVALID_DEFINITION_FIELDS.contains(k))
    override def relatedDefinitions = pair._2
  }

  def inlineInstance[A](
    obj: => Json.Obj
  ): JsonSchema[A] = //(implicit tag: ru.WeakTypeTag[A])
  new JsonSchema[A] {
    override def id =
      if (obj.contains(ID)) {
        obj(ID).get.asString.get
      } else if (obj.contains(CLASS_NAME)) {
        obj(CLASS_NAME).get.asString.get
      } else {
//          tag.tpe.typeSymbol.fullName
        //We disactivate reflection
        UNDEFINED
      }
    override def inline = true
    override def relatedDefinitions = Set.empty
    override def jsonObject = obj
    override def swaggerJsonObject =
      obj.filterKeys(k => !INVALID_DEFINITION_FIELDS.contains(k))
  }

  /* Enum management */

  def enum[A](values: Seq[String]): JsonSchema[A] =
    inlineInstance(
      Map(TYPE -> Json.Str("string"), "enum" -> values.asJson).asJsonObject
    )

  def enumChar[A](values: Seq[Char]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.Str("string"),
        "format" -> Json.Str("char"),
        "enum" -> values.asJson
      ).asJsonObject
    )

  def enumByte[A](values: Seq[Byte]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.Str("string"),
        "format" -> Json.Str("byte"),
        "enum" -> values.asJson
      ).asJsonObject
    )

  def enumShort[A](values: Seq[Short]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.Str("integer"),
        "format" -> Json.Str("int16"),
        "enum" -> values.asJson
      ).asJsonObject
    )

  def enumInt[A](values: Seq[Int]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.Str("integer"),
        "format" -> Json.Str("int32"),
        "enum" -> values.asJson
      ).asJsonObject
    )

  def enumLong[A](values: Seq[Long]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.Str("integer"),
        "format" -> Json.Str("int64"),
        "enum" -> values.asJson
      ).asJsonObject
    )

  def enum[A <: Enumeration](a: A): JsonSchema[A] =
    enum[A](a.values.map(_.toString).toList)

  def enum[E <: EnumEntry](
    e: Enum[E]
  ): JsonSchema[E] =
    enum[E](e.values.map(_.entryName))

  def enum[E <: ShortEnumEntry](
    e: ShortEnum[E]
  ): JsonSchema[E] =
    enumShort[E](e.values.map(_.value))

  def enum[E <: CharEnumEntry](
    e: CharEnum[E]
  ): JsonSchema[E] =
    enumChar[E](e.values.map(_.value))

  def enum[E <: ByteEnumEntry](
    e: ByteEnum[E]
  ): JsonSchema[E] =
    enumByte[E](e.values.map(_.value))

  def enum[E <: IntEnumEntry](
    e: IntEnum[E]
  ): JsonSchema[E] =
    enumInt[E](e.values.map(_.value))

  def enum[E <: LongEnumEntry](
    e: LongEnum[E]
  ): JsonSchema[E] =
    enumLong[E](e.values.map(_.value))

  implicit def deriveEnum[A](
    implicit
    ev: PlainEnum[A]
  ): JsonSchema[A] =
    enum[A](ev.ids)

  type Typeclass[T] = JsonSchema[T]

  private val cache = new mutable.HashMap[String, JsonSchema[_]]()
  private val cacheJson = new mutable.HashMap[String, Json]()

  def combine[T](ctx: CaseClass[JsonSchema, T]): JsonSchema[T] =
    //    if (classOf[Some[_]].getName == ctx.typeName.full) {
    //      JsonSchema.optSchema[T]
    //    } else {
    JsonSchema.instanceAndRelated[T] {
      val classAnnotationManager = new ClassAnnotationManager(
        fullname = ctx.typeName.full,
        annotations = ctx.annotations.collect {
          case s: StaticAnnotation =>
            s
        }.toList
      )

      val defaultMap = new mutable.LinkedHashMap[String, Any]
      val fieldsMapping = new mutable.LinkedHashMap[String, Json]
      val annotationsMap =
        new mutable.LinkedHashMap[String, List[StaticAnnotation]]
//      val requiredFields = new mutable.ListBuffer[String]

      ctx.parameters.foreach { p =>
        val tc = p.typeclass
        if (p.default.isDefined) {
          defaultMap.put(p.label, p.default.get)
        }
        annotationsMap.put(
          p.label,
          p.annotations.collect {
            case s: StaticAnnotation =>
              s
          }.toList
        )
        if (tc != null)
          fieldsMapping.put(p.label, tc.asJson)
      }

      val fieldDescriptions: Json.Obj = Json.Obj.fromIterable(fieldsMapping)

      _root_.scala.Tuple2.apply[_root_.io.circe.Json.Obj, Set[
        _root_.zio.schema.generic.JsonSchema.Definition
      ]](
        classAnnotationManager.buildMainFields(
          fieldDescriptions,
          defaultMap = defaultMap.toMap,
          annotationsMap = annotationsMap.toMap
        ),
        Set.empty[_root_.zio.schema.generic.JsonSchema.Definition]
      )
    }

  def dispatch[T](sealedTrait: SealedTrait[JsonSchema, T]): JsonSchema[T] =
    if (classOf[Option[_]].getName == sealedTrait.typeName.full) {
      sealedTrait.subtypes.find(_.typeclass.isInstanceOf[JsonSchema[_]]).get.typeclass.asInstanceOf[JsonSchema[T]]
    } else {
//      println(s"${sealedTrait.typeName.full} children ${sealedTrait.subtypes.map(_.typeName.full).mkString("|")}")
      if (cache.contains(sealedTrait.typeName.full))
        cache(sealedTrait.typeName.full).asInstanceOf[JsonSchema[T]]
      else {
        val result = JsonSchema.instanceAndRelated[T] {
          Json.Obj.fromIterable(
            Seq(
              TYPE -> Json.Str("object"),
              "oneOf" -> Json.Arr(sealedTrait.subtypes.flatMap { subtype =>
                subtype.typeName.full match {
                  case "zio.schema.VectorSchemaField"              => None
                  case "zio.schema.ListSchemaField"                => None
                  case "zio.schema.SetSchemaField"                 => None
                  case "zio.schema.SeqSchemaField"                 => None
                  case "zio.schema.SchemaMetaField"                => None
                  case "scala.collection.immutable.::"             => None
                  case s: String if s == sealedTrait.typeName.full => None
                  case _ =>
                    if (cacheJson.contains(subtype.typeName.full))
                      cacheJson.get(sealedTrait.typeName.full)
                    else {
//                    println(s"oneof ==> ${subtype.typeName.full}")
                      val result = subtype.typeclass.asJson
                      cacheJson += subtype.typeName.full -> result
                      Some(result)
                    }
                }
              })
            )
          ) -> Set.empty[_root_.zio.schema.generic.JsonSchema.Definition]
        }
        cache += sealedTrait.typeName.full -> result
        result
      }
    }

  //implicit def gen[T]: JsonSchema[T] = macro Magnolia.gen[T]

  implicit def deriveFor[T]: JsonSchema[T] = macro Magnolia.gen[T]

  def schemaAsJsonObject[T]: JsonSchema[T] = inlineInstance[T](
    Map(
      TYPE -> "object",
      "format" -> "json"
    ).asJsonObject
  )

  def schemaAsSeqJsonObject[A]: JsonSchema[List[A]] =
    inlineInstance[List[A]](
      Json.Obj.fromMap(
        Map(
          TYPE -> Json.Str("array"),
          "format" -> Json.Str("list"),
          "multiple" -> Json.Bool(true),
          "required" -> Json.Bool(false),
          "items" -> Json.Obj(
            TYPE -> Json.Str("object"),
            "format" -> Json.Str("json")
          )
        )
      )
    )
}
