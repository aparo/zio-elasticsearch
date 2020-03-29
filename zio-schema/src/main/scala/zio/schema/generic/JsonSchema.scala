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

import enumeratum.{ Enum, EnumEntry }
import io.circe._
import io.circe.syntax._
import zio.schema.generic.JsonSchema.NamedDefinition
import enumeratum.values._
import io.circe.derivation.annotations.JsonCodec
import magnolia.{ CaseClass, Magnolia, SealedTrait }
import zio.schema.{ Schema, StorageType }

import scala.annotation.{ StaticAnnotation, implicitNotFound }
import scala.collection.mutable
import scala.util.matching.Regex
import scala.language.experimental.macros
import zio.schema.SchemaNames._

@implicitNotFound(
  "Cannot derive a JsonSchema for ${A}. Please verify that instances can be derived for all its fields"
)
trait JsonSchema[A] extends JsonSchema.HasRef {
  def id: String

  def inline: Boolean

  def relatedDefinitions: Set[JsonSchema.Definition]

  def fieldDefinitions: Set[JsonSchema.NamedDefinition] =
    relatedDefinitions.collect { case d: NamedDefinition => d }

  def jsonObject: JsonObject

  def swaggerJsonObject: JsonObject

  def asJson: Json =
    if (jsonObject.contains(ID)) {
      val curId = jsonObject(ID).get.asString.get
      if (curId == UNDEFINED || curId.startsWith("java.") || curId.startsWith("scala.")) {
        Json.fromJsonObject(jsonObject.filterKeys(_ != ID))
      } else
        Json.fromJsonObject(jsonObject)
    } else {
      if (id == UNDEFINED || id.startsWith("java.") || id.startsWith("scala.")) {
        Json.fromJsonObject(jsonObject.filterKeys(_ != ID))
      } else {
        Json.fromJsonObject(jsonObject.add(ID, Json.fromString(this.id)))
      }
    }

  //Used for schema
  def asJsonWithRef: Json = {
    var json: JsonObject = if (jsonObject.contains(ID)) {
      val curId = jsonObject("id").get.asString.get
      if (curId.startsWith("java.") || curId.startsWith("scala.")) {
        jsonObject.filterKeys(_ == "id")
      } else jsonObject
    } else {
      if (id.startsWith("java.") || id.startsWith("scala.")) {
        jsonObject.filterKeys(_ == "id")
      } else {
        jsonObject.add(ID, Json.fromString(this.id))
      }
    }
    val properties: Vector[Json] = json(PROPERTIES).get.asArray.get.map { jf =>
      val field = jf.asObject.getOrElse(JsonObject.empty)
      field(TYPE).get.as[String].getOrElse("") match {
        case "object" =>
          Json.obj(
            "$ref" -> Json.fromString(
              s"#/definitions/${field(CLASS_NAME).get.asString.getOrElse("")}"
            ),
            TYPE -> Json.fromString("ref"),
            "name" -> field("name").getOrElse(Json.fromString("ref"))
          )
        case _ => jf
      }
    }
    json = json.add(PROPERTIES, properties.asJson)
    Json.fromJsonObject(json)
  }

  def asObjectRef: JsonObject =
    id match {
      case s: String if id.startsWith("scala.") =>
        id.split('.').last match {
          case "String" => JsonObject(TYPE -> Json.fromString("string"))
          case "Int" =>
            JsonObject(TYPE -> Json.fromString("integer"), "format" -> Json.fromString("int32"))
          case "Long" =>
            JsonObject(TYPE -> Json.fromString("integer"), "format" -> Json.fromString("int64"))
          case "Short" =>
            JsonObject(TYPE -> Json.fromString("integer"), "format" -> Json.fromString("int16"))
          case "Double"  => JsonObject(TYPE -> Json.fromString("double"))
          case "Boolean" => JsonObject(TYPE -> Json.fromString("bool"))
        }
      case UNDEFINED =>
        jsonObject

      case s =>
        JsonObject.fromMap(
          Map(
            REFERENCE -> Json.fromString(s"#/definitions/$id"),
            TYPE -> Json.fromString("ref")
          )
        )
    }

  def asJsonRef: Json = asObjectRef.asJson

  def asSchema: Schema = {
    val sch = this.asJson.as[Schema]
    if (sch.isLeft) {
      println(this.asJson.spaces2)
      println(sch)

    }
    sch.right.get
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
      removeInvalidDefinitionFields(swaggerJsonObject)
    )

  protected val INVALID_DEFINITION_FIELDS =
    List(ID, CLASS_NAME, IS_ROOT, MODULE, NAME, MULTIPLE)

  private def removeInvalidDefinitionFields(json: JsonObject): Json =
    Json.fromFields(
      json.toList.filterNot(v => INVALID_DEFINITION_FIELDS.contains(v._1)).map {
        case (k, v) =>
          k match {
            case PROPERTIES if v.isObject =>
              k -> Json.fromFields(
                v.asObject.get.toList.map(
                  v2 => v2._1 -> removeInvalidDefinitionFields(v2._2.asObject.get)
                )
              )
            case _ => k -> v
          }
      }
    )

  def definitions: Set[JsonSchema.Definition] =
    relatedDefinitions + definition

}

object JsonSchema extends Primitives /* with generic.HListInstances with generic.CoprodInstances */ {

  val jsonUndefString: Json = Json.fromString(UNDEFINED)

  trait HasRef {
    def id: String
    def asRef: Ref = TypeRef(id, None)
    def asArrayRef: Ref = ArrayRef(id, None)
  }

  sealed trait Definition extends HasRef {
    def id: String
    def json: Json
    def relatedRefs: Set[Ref]

    def asSchema: Schema = {
      val sch = json.as[Schema]
      sch.right.get
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
    implicit final val decodeRef: Decoder[Ref] = {
      Decoder.instance { c =>
        c.as[TypeRef] //We need to decode also array???
      }
    }

    implicit final val encodeRef: Encoder[Ref] = {
      Encoder.instance {
        case obj: TypeRef =>
          val id = obj.id
          id match {
            case s: String if id.startsWith("scala.") =>
              id.split('.').last match {
                case "String" => Json.obj(TYPE -> Json.fromString("string"))
                case "Int" =>
                  Json.obj(TYPE -> Json.fromString("integer"), "format" -> Json.fromString("int32"))
                case "Long" =>
                  Json.obj(TYPE -> Json.fromString("integer"), "format" -> Json.fromString("int64"))
                case "Short" =>
                  Json.obj(TYPE -> Json.fromString("integer"), "format" -> Json.fromString("int16"))
                case "Double"  => Json.obj(TYPE -> Json.fromString("double"))
                case "Boolean" => Json.obj(TYPE -> Json.fromString("bool"))
              }
            case s =>
              Json.obj(REFERENCE -> Json.fromString("#/definitions/" + id))
          }

        case obj: ArrayRef => //Json.obj("array" ->obj.asJson)
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
    obj: => JsonObject
  ): JsonSchema[A] = // (implicit tag: ru.WeakTypeTag[A])
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
    override def jsonObject = obj

    override def swaggerJsonObject =
      obj.filterKeys(k => !INVALID_DEFINITION_FIELDS.contains(k))

    override def relatedDefinitions = Set.empty
  }

  def functorInstance[F[_], A](
    obj: => JsonObject
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
    pair: => (JsonObject, Set[Definition])
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
    obj: => JsonObject
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
      Map(TYPE -> Json.fromString("string"), "enum" -> values.asJson).asJsonObject
    )

  def enumChar[A](values: Seq[Char]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.fromString("string"),
        "format" -> Json.fromString("char"),
        "enum" -> values.asJson
      ).asJsonObject
    )

  def enumByte[A](values: Seq[Byte]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.fromString("string"),
        "format" -> Json.fromString("byte"),
        "enum" -> values.asJson
      ).asJsonObject
    )

  def enumShort[A](values: Seq[Short]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.fromString("integer"),
        "format" -> Json.fromString("int16"),
        "enum" -> values.asJson
      ).asJsonObject
    )

  def enumInt[A](values: Seq[Int]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.fromString("integer"),
        "format" -> Json.fromString("int32"),
        "enum" -> values.asJson
      ).asJsonObject
    )

  def enumLong[A](values: Seq[Long]): JsonSchema[A] =
    inlineInstance(
      Map(
        TYPE -> Json.fromString("integer"),
        "format" -> Json.fromString("int64"),
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
    implicit ev: PlainEnum[A]
  ): JsonSchema[A] =
    enum[A](ev.ids)

  type Typeclass[T] = JsonSchema[T]

  def combine[T](ctx: CaseClass[JsonSchema, T]): JsonSchema[T] =
    //    if (classOf[Some[_]].getName == ctx.typeName.full) {
    //      JsonSchema.optSchema[T]
    //    } else {
    JsonSchema.instanceAndRelated[T] {
      val classAnnotationManager = new ClassAnnotationManager(
        fullname = ctx.typeName.full,
        annotations = ctx.annotations.collect {
          case s: StaticAnnotation => s
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
        annotationsMap.put(p.label, p.annotations.collect {
          case s: StaticAnnotation => s
        }.toList)
        fieldsMapping.put(p.label, tc.asJson)
      }

      val fieldDescriptions: JsonObject = JsonObject.fromIterable(fieldsMapping)

      _root_.scala.Tuple2.apply[_root_.io.circe.JsonObject, Set[
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
      JsonSchema.instanceAndRelated[T] {
        JsonObject.fromIterable(
          Seq(
            TYPE -> Json.fromString("object"),
            "oneOf" -> Json.fromValues(sealedTrait.subtypes.map { subtype =>
              subtype.typeclass.asJson
            })
          )
        ) -> Set.empty[_root_.zio.schema.generic.JsonSchema.Definition]
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
      JsonObject.fromMap(
        Map(
          TYPE -> Json.fromString("array"),
          "format" -> Json.fromString("list"),
          "multiple" -> Json.fromBoolean(true),
          "required" -> Json.fromBoolean(false),
          "items" -> Json.obj(
            TYPE -> Json.fromString("object"),
            "format" -> Json.fromString("json")
          )
        )
      )
    )

  //  def deriveFor[A]: JsonSchema[A] =  SchemaDerivation.gen[A] //macro JsonSchemaMacro.jsonSchemaAndRelatedImpl[A]

//  def deriveForShapeless[A](implicit ev: JsonSchema[A]): JsonSchema[A] = {
//    ev
//  }

  //  def deriveFor[A]: JsonSchema[A] = JsonSchemaMacros.jsonSchemaAndRelated[A]
//
//  def deriveFor[A]: JsonSchema[A] = {
//        instanceAndRelated[A] {
//          jsonSchemaAndRelated[A]
//        }
//      }
//
//  def jsonSchemaAndRelated[A]:(JsonObject, Set[zio.schema.generic.JsonSchema.Definition])=macro jsonSchemaAndRelatedImpl[A]
//
//  def jsonSchemaAndRelatedImpl[T: c.WeakTypeTag](c: Context): c.Expr[(JsonObject, Set[zio.schema.generic.JsonSchema.Definition])] = {
//    import c.universe._
//    c.Expr[(JsonObject, Set[zio.schema.generic.JsonSchema.Definition])](q"""_root_.io.circe.JsonObject.empty -> Set.empty[_root_.zio.schema.generic.JsonSchema.Definition]""")
//
//  }

}
