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

package elasticsearch.orm

import elasticsearch.ZioResponse
import elasticsearch.sort.SortOrder
import io.circe._
import elasticsearch.responses.DeleteResponse
import zio.{ Task, ZIO }
import zio.auth.AuthContext
import zio.common.StringUtils
import zio.exception.FrameworkException
import zio.schema.generic.NameSpaceUtils

trait SchemaHelper[BaseDocument] {
  implicit def jsonEncoder: Encoder[BaseDocument]
  implicit def jsonDecoder: Decoder[BaseDocument]

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

  def classNamePlural: String = StringUtils.inflect.plural(className)

  def className: String = fullNamespaceName.split("\\.").last

  def modelNamePlural: String = StringUtils.inflect.plural(modelName)

  def modelName: String = NameSpaceUtils.getModelName(fullNamespaceName)

  def restUrl: String = NameSpaceUtils.namespaceToNameUrl(fullNamespaceName)

  def namespaceName: String = fullNamespaceName.split("\\.").inits.mkString(".")

  def fromJson(authContext: AuthContext, json: Json): ZioResponse[BaseDocument] =
    fromJson(authContext, json, None)

  def fromJson(authContext: AuthContext, json: String, index: Option[String]): ZioResponse[BaseDocument] =
    for {
      js <- ZIO.fromEither(parser.parse(json)).mapError(e => FrameworkException(e))
      res <- fromJson(authContext, js, index)
    } yield res

  def fromJson(authContext: AuthContext, json: Json, index: Option[String]): ZioResponse[BaseDocument] = {
    var data = json.asObject.get
    preLoadHooks.foreach(f => data = f(authContext, data))
    for {
      source <- ZIO.fromEither(jsonDecoder.decodeJson(Json.fromJsonObject(data))).mapError(e => FrameworkException(e))
      res <- ZIO.effect {
        var obj = source
        postLoadHooks.foreach(f => obj = f(authContext, obj))
        obj
      }.mapError(e => FrameworkException(e))
    } yield res
  }

  def updateFields(document: BaseDocument): BaseDocument =
    document

  /**
   * It should be called before saving
   */
  def processExtraFields(authContext: AuthContext, document: BaseDocument): BaseDocument

  def save(
    document: BaseDocument,
    bulk: Boolean = false,
    forceCreate: Boolean = false,
    index: Option[String] = None,
    docType: Option[String] = None,
    version: Option[Long] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    id: Option[String] = None
  )(implicit authContext: AuthContext): Task[BaseDocument]

  def getByIdHash(id: String)(implicit authContext: AuthContext): Task[BaseDocument]

  def getByIdSlug(id: String)(implicit authContext: AuthContext): Task[BaseDocument]

  def getByIds(ids: Seq[String])(implicit authContext: AuthContext): Task[List[ZioResponse[BaseDocument]]]

  def getById(id: String)(implicit authContext: AuthContext): Task[BaseDocument]
  //
  //  def getById(client: PKFetchableEngine,
  //              index: String,
  //              typeName: String,
  //              id: String): Task[BaseDocument]

  def getById(index: String, typeName: String, id: String)(implicit authContext: AuthContext): ZioResponse[BaseDocument]

  def exists(id: String)(implicit authContext: AuthContext): Task[Boolean] =
    getById(id).map(_ => true)

  def exists(index: String, typeName: String, id: String)(implicit authContext: AuthContext): Task[Boolean] =
    getById(index, typeName, id).map(_ => true)

  def count()(implicit authContext: AuthContext): Task[Long]

  /* drop this document collection */
  def drop(index: Option[String] = None)(implicit authContext: AuthContext): Task[Unit]

  /* refresh this document collection */
  def refresh()(implicit authContext: AuthContext): Task[Unit]

  def deleteById(
    id: String,
    bulk: Boolean = false,
    refresh: Boolean = false,
    userId: Option[String] = None
  )(implicit authContext: AuthContext): Task[DeleteResponse]

  def delete(document: BaseDocument, bulk: Boolean = false, refresh: Boolean = false)(
    implicit authContext: AuthContext
  ): Task[DeleteResponse]

  def find(id: String)(implicit authContext: AuthContext): Task[BaseDocument] = getById(id)

}
