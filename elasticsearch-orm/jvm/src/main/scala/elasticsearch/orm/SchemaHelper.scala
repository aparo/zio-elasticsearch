package elasticsearch.orm

package zio.schema

import elasticsearch.sort.SortOrder
import io.circe._
import cats.implicits._
import elasticsearch.responses.DeleteResponse
import zio.Task
import zio.auth.AuthContext

import scala.concurrent.Future

trait NoSqlHelper[BaseDocument] {
  implicit def jsonEncoder: Encoder[BaseDocument]
  implicit def jsonDecoder: Decoder[BaseDocument]
  implicit def authContext: AuthContext

  def preLoadHooks: List[(AuthContext, JsonObject) => JsonObject] = Nil
  def postLoadHooks: List[(AuthContext, BaseDocument) => BaseDocument] = Nil

  lazy val moduleId: String = {
    var tokens = fullNamespaceName.split("\\.").toList
    if (tokens.contains("models")) {
      tokens = List(tokens(tokens.indexOf("models") - 1), tokens.last)
    }
    tokens.mkString("")
  }

  /**
    * Convert a class to a Json
    *
    * @param in the document
    * @param processed if processed
    * @return a Json
    */
  def toJson(in: BaseDocument, processed: Boolean): Json

  def fullNamespaceName: String

  def isVertex: Boolean = false

  def databaseName: String = "default"

  def ordering: List[(String, SortOrder)] = List.empty[(String, SortOrder)]

  /* Used to initialize objects in initdata */
  def initData(): Unit = {}

  //Field to be used in create timestamp
  def createField: Option[String] = None

  //Field to be used in modified timestamp
  def modifiedField: Option[String] = None

  //a field that manage user defined object to be saved a custom user instance
  def userDefinedField: Option[String] = None

  def typeName: String = innerTypeName

  protected def innerTypeName: String =
    NameSpaceUtils.namespaceToDocType(fullNamespaceName)

  def applicationDescription =
    s"The ${metaModule}/${modelName} API exposes all operations of ${metaModule}.${modelName} objects."

  def metaModule: String = NameSpaceUtils.getModule(fullNamespaceName)

  def classNamePlural = StringUtils.inflect.plural(className)

  def className = fullNamespaceName.split("\\.").last

  def modelNamePlural = StringUtils.inflect.plural(modelName)

  def modelName: String = NameSpaceUtils.getModelName(fullNamespaceName)

  def restUrl = NameSpaceUtils.namespaceToNameUrl(fullNamespaceName)

  def namespaceName = fullNamespaceName.split("\\.").inits.mkString(".")

  def swaggerDescription = s"The ${metaModule}/${modelName} object."

  //  def fromJson(client: PKFetchableEngine, json: Json): Option[BaseDocument]

  def fromJson(authContext: AuthContext,
               json: Json): ResponseE[BaseDocument] =
    fromJson(authContext, json, None)

  def fromJson(authContext: AuthContext, json: String,
               index: Option[String]): ResponseE[BaseDocument] =
    parser.parse(json) match {
      case Left(x) =>
        Left(FrameworkException(x))
      case Right(value) =>
        fromJson(authContext, value, index)
    }

  def fromJson(authContext: AuthContext, json: Json,
               index: Option[String]): ResponseE[BaseDocument] = {
    var data = json.asObject.get
    preLoadHooks.foreach(f => data = f(authContext, data))
    jsonDecoder.decodeJson(Json.fromJsonObject(data)) match {
      case Right(source) =>
        var obj = source
        postLoadHooks.foreach(f => obj = f(authContext, obj))
        Right(obj)
      case Left(ex) =>
        Left(FrameworkException(ex))
    }
  }

  def updateFields(document: BaseDocument): BaseDocument = {
    document
  }

  /**
    * It should be called before saving
    */
  def processExtraFields(authContext: AuthContext,
                         document: BaseDocument): BaseDocument

  def save(document: BaseDocument, bulk: Boolean = false,
           forceCreate: Boolean = false, index: Option[String] = None,
           docType: Option[String] = None, version: Option[Long] = None,
           refresh: Boolean = false, userId: Option[String] = None,
           id: Option[String] = None): Task[BaseDocument]

  def getByIdHash(id: String): Task[BaseDocument]

  def getByIdSlug(id: String): Task[BaseDocument]

  def getByIds(ids: Seq[String]): Task[List[ResponseE[BaseDocument]]]

  def withErrorHandling[T](f: Future[T], fallback: T): Future[T] = {
    implicit val context = authContext.elasticsearch.executionContext
    f.recover {
      case t: Throwable =>
        fallback
    }
  }

  def getById(id: String): Task[BaseDocument]
  //
  //  def getById(client: PKFetchableEngine,
  //              index: String,
  //              typeName: String,
  //              id: String): Task[BaseDocument]

  def getById(index: String, typeName: String,
              id: String): Task[BaseDocument]

  def exists(id: String): Task[Boolean] = {
    getById(id).map(_ => true)
  }

  def exists(index: String, typeName: String,
             id: String): Task[Boolean] = {
    implicit val context = authContext.elasticsearch.executionContext
    getById(index, typeName, id).map(_ => true)

  }

  def count(): Task[Long]

  /* drop this document collection */
  def drop(index: Option[String] = None): Unit

  /* refresh this document collection */
  def refresh(): Unit

  def deleteById(id: String, bulk: Boolean = false, refresh: Boolean = false,
                 userId: Option[String] = None): Task[DeleteResponse]

  def delete(document: BaseDocument, bulk: Boolean,
             refresh: Boolean): Task[DeleteResponse]

  def find(id: String): Task[BaseDocument] = getById(id)

}

