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
import java.time.OffsetDateTime

import cats.data._
import cats.implicits._
import elasticsearch.mappings.RootDocumentMapping
import elasticsearch.orm._
import elasticsearch.orm.models.TimeStampedModel
import elasticsearch.queries.{IdsQuery, Query}
import elasticsearch.requests.{IndexRequest, UpdateRequest}
import elasticsearch.responses.{DeleteResponse, GetResponse, UpdateResponse}
import elasticsearch.{ClusterSupport, ElasticSearchConstants, OpType, Refresh, queries}
import io.circe
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json, JsonObject}
import zio.common.UUID
import elasticsearch.schema.FieldHelpers
import zio.auth.AuthContext
import zio.exception.FrameworkException
import zio.schema.annotations.{WithHiddenId, WithId, WithIndex, WithType, WithVersion}
import zio.schema.generic._
import zio.schema._

import scala.concurrent.Future

trait ElasticSearchDocument[Document] extends SchemaDocument[Document] {
  //  self: Document =>
  //  def esMeta: ElasticSearchMeta[Document]

}

trait ElasticSearchMeta[Document] extends SchemaMeta[Document] {
  self =>

  def es(
          implicit AuthContext: AuthContext,
          elasticsearch: ClusterSupport,
          encoder: Encoder[Document],
          decoder: Decoder[Document]
  ): ESHelper[Document] =
    new ESHelper[Document](
      schema,
      _schema,
      metaUser = metaUser,
      parentMeta = parentMeta,
      preSaveHooks = self match {
        case value: PreSaveHooks[Document] => value.preSaveHooks
        case _                             => Nil
      },
      preSaveJsonHooks = self match {
        case value: PreSaveJsonHooks => value.preSaveJsonHooks
        case _                       => Nil
      },
      postSaveHooks = self match {
        case value: PostSaveHooks[Document] => value.postSaveHooks
        case _                              => Nil
      },
      preDeleteHooks = self match {
        case value: PreDeleteHooks[Document] => value.preDeleteHooks
        case _                               => Nil
      },
      postDeleteHooks = self match {
        case value: PreSaveHooks[Document] => value.preSaveHooks
        case _                             => Nil
      },
      preUpdateHooks = self match {
        case value: PreUpdateHooks[Document] => value.preUpdateHooks
        case _                               => Nil
      },
      preUpdateJsonHooks = self match {
        case value: PreUpdateJsonHooks[Document] => value.preUpdateJsonHooks
        case _                                   => Nil
      },
      postUpdateHooks = self match {
        case value: PostUpdateHooks[Document] => value.postUpdateHooks
        case _                                => Nil
      }
    )

}

class ESHelper[Document](
  schema: Schema,
  jsonSchema: JsonSchema[Document],
  metaUser: Option[MetaUser],
  parentMeta: Option[ParentMeta] = None,
  preSaveHooks: List[(AuthContext, Document) => Document] = Nil,
  preSaveJsonHooks: List[(AuthContext, JsonObject) => JsonObject] = Nil,
  postSaveHooks: List[(AuthContext, Document) => Document] = Nil,
  preDeleteHooks: List[(AuthContext, Document) => Document] = Nil,
  postDeleteHooks: List[(AuthContext, Document) => Document] = Nil,
  preUpdateHooks: List[(AuthContext, Document) => Document] = Nil,
  preUpdateJsonHooks: List[(AuthContext, JsonObject) => JsonObject] = Nil,
  postUpdateHooks: List[(AuthContext, Document) => Document] = Nil
)(implicit val AuthContext: AuthContext, val jsonEncoder: Encoder[Document], val jsonDecoder: Decoder[Document],
  val elasticsearch: ClusterSupport)
    extends NoSqlHelper[Document] {

  override def typeName: String = "_doc"

  private val getClientForReader: ElasticSearch = {
    AuthContext.nosqlModule.elaticsearchModule.getConnection(index = concreteIndex())
  }

  val indexName: String = concreteIndex()
  //  implicit val executor=AuthContext.elasticsearch.executionContext

  //  def fullNamespaceName = mf.runtimeClass.getName
  //
  //  def module: String = NameSpaceUtils.getModule(fullNamespaceName)

  /**
   * Convert a class to a Json
   *
   * @param in the document
   * @param processed if processed
   * @return a Json
   */
  override def toJson(in: Document, processed: Boolean): Json =
    jsonEncoder.apply(in)

  override def fullNamespaceName: String = jsonSchema.id

  /**
   * It should be called before saving
   */
  override def processExtraFields(AuthContext: AuthContext, document: Document): Document = document

  def save(id: String, item: Document): ResponseETF[Document] =
    save(document = item, id = Some(id))

  def save(items: Iterator[Document]): Iterator[ResponseETF[Document]] =
    items.map(item => save(item))

  def save(items: Seq[Document]): Seq[ResponseETF[Document]] =
    items.map(item => save(item))

  def save(
    document: Document,
    bulk: Boolean = false,
    forceCreate: Boolean = false,
    index: Option[String] = None,
    docType: Option[String] = None,
    version: Option[Long] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    id: Option[String] = None
  ): ResponseETF[Document] = {
    var obj = document
    obj = updateFields(obj)

    if (obj.isInstanceOf[WithHiddenId]) {
      obj.asInstanceOf[WithHiddenId]._version = document.asInstanceOf[WithHiddenId]._version
    }
    preSaveHooks.foreach(f => obj = f(AuthContext, obj))

    var source = toJsValue(obj, true).asObject.get

    source = source.add(ElasticSearchConstants.typeField, Json.fromString(this.indexName))

    if (obj.isInstanceOf[TimeStampedModel]) {
      source = source.add("modified", Json.fromString(OffsetDateTime.now().toString))
    }

    preSaveJsonHooks.foreach(f => source = f(AuthContext, source))
    if (userId.isDefined) {
      metaUser.foreach { mu =>
        source = mu.processPreSaveMetaUser(source, userId.get)
      }
    }

    /*Saving record */
    val realIndex = index match {
      case Some(s) => concreteIndex(Some(s))
      case _ =>
        document match {
          case mWI: CustomIndex =>
            mWI.calcIndex()
          case mWI: WithIndex =>
            concreteIndex(Some(mWI.index))
          case mWI: WithHiddenId => mWI._index.getOrElse(concreteIndex())
          case _                 => concreteIndex()
        }
    }
    val realClient: ElasticSearch =
      AuthContext.nosqlModule.elaticsearchModule.getConnection(index = concreteIndex(Some(realIndex)))
    //    val realDocType = docType match {
    //      case Some(s) => s
    //      case _ =>
    //        document match {
    //          case mWI: WithType =>
    //            var typ = mWI.`type`
    //            if (typ.isEmpty)
    //              typ = this.typeName
    //            typ
    //          case mWI: WithHiddenId => mWI._type.getOrElse(this.typeName)
    //          case _                 => this.typeName
    //        }
    //
    //    }
    //    println(src)
    var indexRequest =
      IndexRequest(index = realIndex, docType = "_doc", body = source)

    //we set id
    if (id.isDefined)
      indexRequest = indexRequest.copy(id = id)
    else
      obj match {
        case c: CustomID =>
          indexRequest = indexRequest.copy(id = Some(c.id))

        case c: WithId =>
          if (c.id.isEmpty)
            indexRequest = indexRequest.copy(id = Some(UUID.randomBase64UUID()))
          else
            indexRequest = indexRequest.copy(id = Some(c.id))

        case mWI: WithHiddenId if mWI._id.isDefined =>
          indexRequest = indexRequest.copy(id = mWI._id)

        case _ =>
      }

    //we set version
    obj match {
      case o: WithVersion =>
        indexRequest = indexRequest.copy(version = Some(o.version))
      case o: WithHiddenId if o._version.isDefined =>
        indexRequest = indexRequest.copy(version = o._version)
      case _ =>
    }

    //we set parent if required
    parentMeta.foreach { pmeta =>
      val parent: Option[String] =
        source(pmeta.field).flatMap(_.asString).orElse(indexRequest.id)
      indexRequest = indexRequest.copy(parent = parent)
    }

    if (forceCreate) {
      indexRequest = indexRequest.copy(opType = OpType.create)
    }
    if (refresh)
      indexRequest = indexRequest.copy(refresh = Some(Refresh.`true`))

    bulk match {
      case true =>
        realClient.addToBulk(indexRequest)
      case false =>
        val respEither =
          realClient.awaitResult(realClient.indexDocument(indexRequest).value)
        respEither.map { resp =>
          if (obj.isInstanceOf[WithId])
            obj.asInstanceOf[WithId].id = resp.id
          if (obj.isInstanceOf[WithType])
            obj.asInstanceOf[WithType].`type` = resp.docType
          if (obj.isInstanceOf[WithIndex])
            obj.asInstanceOf[WithIndex].index = resp.index
          if (obj.isInstanceOf[WithVersion])
            obj.asInstanceOf[WithVersion].version = resp.version

          if (obj.isInstanceOf[WithHiddenId]) {
            obj.asInstanceOf[WithHiddenId]._id = Some(resp.id)
            obj.asInstanceOf[WithHiddenId]._type = Some(resp.docType)
            obj.asInstanceOf[WithHiddenId]._index = Some(resp.index)
            obj.asInstanceOf[WithHiddenId]._version = Some(resp.version)
          }
        }
    }

    /*Post Saving record */

    postSaveHooks.foreach(f => obj = f(AuthContext, obj))
    EitherT(Future.successful(Right(obj).asInstanceOf[Either[FrameworkException, Document]]))
  }

  def toIndexRequest(
    document: Document,
    bulk: Boolean,
    forceCreate: Boolean,
    index: Option[String],
    docType: Option[String],
    version: Option[Long] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    id: Option[String] = None
  ): (Document, IndexRequest) = {
    var obj = document
    obj = updateFields(obj)

    if (obj.isInstanceOf[WithHiddenId]) {
      obj.asInstanceOf[WithHiddenId]._version = document.asInstanceOf[WithHiddenId]._version
    }
    preSaveHooks.foreach(f => obj = f(AuthContext, obj))

    var source = toJsValue(obj, true).asObject.get
    source = source.add(ElasticSearchConstants.typeField, Json.fromString(this.indexName))

    if (obj.isInstanceOf[TimeStampedModel]) {
      source = source.add("modified", Json.fromString(OffsetDateTime.now().toString))
    }

    preSaveJsonHooks.foreach(f => source = f(AuthContext, source))
    if (userId.isDefined) {
      metaUser.foreach { mu =>
        source = mu.processPreSaveMetaUser(source, userId.get)
      }
    }

    /*Saving record */
    val realIndex = index match {
      case Some(s) => concreteIndex(Some(s))
      case _ =>
        document match {
          case mWI: CustomIndex =>
            mWI.calcIndex()
          case mWI: WithIndex =>
            concreteIndex(Some(mWI.index))
          case mWI: WithHiddenId => mWI._index.getOrElse(concreteIndex())
          case _                 => concreteIndex()
        }
    }
    val realClient =
      AuthContext.nosqlModule.elaticsearchModule.getConnection(realIndex)
    //    val realDocType = docType match {
    //      case Some(s) => s
    //      case _ =>
    //        document match {
    //          case mWI: WithType =>
    //            var typ = mWI.`type`
    //            if (typ.isEmpty)
    //              typ = this.typeName
    //            typ
    //          case mWI: WithHiddenId => mWI._type.getOrElse(this.typeName)
    //          case _                 => this.typeName
    //        }
    //
    //    }
    //    println(src)
    var indexRequest =
      IndexRequest(index = realIndex, docType = "_doc", body = source)

    //we set id
    if (id.isDefined)
      indexRequest = indexRequest.copy(id = id)
    else
      obj match {
        case c: CustomID =>
          indexRequest = indexRequest.copy(id = Some(c.id))

        case c: WithId =>
          if (c.id.isEmpty)
            indexRequest = indexRequest.copy(id = Some(UUID.randomBase64UUID()))
          else
            indexRequest = indexRequest.copy(id = Some(c.id))

        case mWI: WithHiddenId if mWI._id.isDefined =>
          indexRequest = indexRequest.copy(id = mWI._id)

        case _ =>
      }

    //we set version
    obj match {
      case o: WithVersion =>
        indexRequest = indexRequest.copy(version = Some(o.version))
      case o: WithHiddenId if o._version.isDefined =>
        indexRequest = indexRequest.copy(version = o._version)
      case _ =>
    }

    //we set parent if required
    parentMeta.foreach { pmeta =>
      val parent: Option[String] =
        source(pmeta.field).flatMap(_.asString).orElse(indexRequest.id)
      indexRequest = indexRequest.copy(parent = parent)
    }

    if (forceCreate) {
      indexRequest = indexRequest.copy(opType = OpType.create)
    }
    if (refresh)
      indexRequest = indexRequest.copy(refresh = Some(Refresh.`true`))

    postSaveHooks.foreach(f => obj = f(AuthContext, obj))
    (obj, indexRequest)
  }

  override def refresh(): Unit = {
    implicit val executionContext = AuthContext.elasticsearch.executionContext
    val index = concreteIndex()
    val client =
      AuthContext.nosqlModule.elaticsearchModule.getConnection(index)
    client.refresh(index).value.getWithBlocking
  }

  def extractIndexMeta(document: Document): (String, String, String) = {
    var index = concreteIndex()
    var docType = this.typeName
    var id = ""
    if (document.isInstanceOf[WithId]) {
      id = document.asInstanceOf[WithId].id
    }
    if (document.isInstanceOf[WithType]) {
      docType = document.asInstanceOf[WithType].`type`
    }
    if (document.isInstanceOf[WithIndex]) {
      index = document.asInstanceOf[WithIndex].index
    }
    if (document.isInstanceOf[WithHiddenId]) {
      val o = document.asInstanceOf[WithHiddenId]
      index = o._index.getOrElse(index)
      docType = o._type.getOrElse(docType)
      id = o._id.getOrElse(id)
    }
    (index, docType, id)
  }

  def delete(id: String): ResponseETF[DeleteResponse] =
    fastDelete(concreteIndex(), this.typeName, id)

  def delete(document: Document, bulk: Boolean, refresh: Boolean): ResponseETF[DeleteResponse] = {
    val (index, docType, id) = extractIndexMeta(document)

    if (preDeleteHooks.isEmpty && postDeleteHooks.isEmpty) {
      fastDelete(index, docType, id, bulk = bulk, refresh = refresh)
    } else {
      var obj = document
      val realClient = getClientForReader
      preDeleteHooks.foreach(f => obj = f(AuthContext, obj))

      val res = realClient.delete(
        index,
        docType,
        id,
        refresh = Some(Refresh.fromValue(refresh)),
        bulk = bulk
      )
      postDeleteHooks.foreach(f => obj = f(AuthContext, obj))
      res
    }
  }

  /* drop this document collection */
  override def drop(index: Option[String]): Unit = {
    var qs = query
    index.foreach(name ⇒ qs = qs.copy(indices = Seq(name)))
    qs.delete()
  }

  def fastDelete(
    index: String,
    docType: String,
    id: String,
    bulk: Boolean = false,
    refresh: Boolean = false
  ): ResponseETF[DeleteResponse] = {
    val client = getClientForReader
    client.delete(
      index,
      docType,
      id,
      bulk = bulk,
      refresh = Some(Refresh.fromValue(refresh))
    )
  }

  //TODO bulk by default
  def deleteByIds(ids: Seq[String]) =
    query.filterF(IdsQuery(values = ids.toList)).delete()

  def deleteAll() =
    query.delete()

  def deleteById(
    id: String,
    bulk: Boolean = false,
    refresh: Boolean = false,
    userId: Option[String] = None
  ): ResponseETF[DeleteResponse] =
    if (preDeleteHooks.isEmpty && postDeleteHooks.isEmpty) {
      fastDelete(
        concreteIndex(),
        this.typeName,
        id,
        bulk = bulk,
        refresh = refresh
      )
    } else {
      implicit val executionContext =
        AuthContext.elasticsearch.executionContext
      for {
        item <- getById(id);
        res <- delete(item, bulk = bulk, refresh = refresh)
      } yield res
    }

  def getMapping: RootDocumentMapping =
    AuthContext.nosqlModule.elaticsearchModule.elasticSearchSchemaManager.getMappings(schema)._2

  //  def validateFields(m: Map[String, Any],
  //                     index: Option[String]): (Map[String, Any], List[ValidationError]) = {
  //    var errors = List.empty[ValidationError]
  //    var data = m
  //    //Check if all the required filters are available
  //    columns.foreach { field =>
  //      if (!m.contains(field.name)) {
  //        if (field.hasDefault) {
  //          data += (field.name -> field.default.get)
  //        } else if (field.required) {
  //          errors ::= RequiredField(field)
  //        }
  //      }
  //    }
  //    (data, errors)
  //  }

  // convert class to a JsObject
  def toJsValue(in: Document, processed: Boolean): Json = {
    val toBeProcessed = processed

    val withExtra = if (toBeProcessed) processExtraFields(in) else in
    processExtraFields(withExtra.asJson)
  }

  def getFieldByName(name: String): Option[SchemaField] =
    schema.properties.find(_.name == name)

  def concreteIndex(index: Option[String] = None): String =
    index match {
      //        case _ if ElasticSearchConstants.testMode => Constants.defaultTestIndex
      case None    => AuthContext.cookTableName(this.schema.tableName)
      case Some(i) => AuthContext.cookTableName(i)
    }

  def update(
    id: String,
    document: Document,
    values: JsonObject,
    bulk: Boolean = false,
    refresh: Boolean = false,
    storageNamespace: Option[String] = None,
    docType: Option[String] = None,
    userId: Option[String] = None
  ): ResponseETF[UpdateResponse] = {
    implicit val executionContext = AuthContext.elasticsearch.executionContext
    var updateJson = values
    //TODO add
    //preUpdateHooks

    updateJson = updateJson.add(ElasticSearchConstants.typeField, Json.fromString(this.indexName))

    if (document.isInstanceOf[TimeStampedModel]) {
      updateJson = updateJson.add(
        "modified",
        Json.fromString(OffsetDateTime.now().toString)
      )
    }

    val realIndex: String = storageNamespace match {
      case Some(v) => v
      case None =>
        document match {
          case obj: CustomIndex =>
            obj.calcIndex()
          case _ =>
            AuthContext.cookTableName(schema.tableName)
        }
    }

    preUpdateJsonHooks.foreach(f => updateJson = f(AuthContext, updateJson))

    var updateAction = UpdateRequest(
      realIndex,
      docType.getOrElse("_doc"),
      id,
      body = circe.JsonObject.fromIterable(Seq("doc" -> Json.fromJsonObject(updateJson))),
      refresh = Refresh.`false`
    )

    //      preUpdateActionHooks.foreach(f => updateAction = f(AuthContext, updateAction))

    //TODO add user data on change if required
    val clientReal =
      AuthContext.nosqlModule.elaticsearchModule.getConnection(realIndex)
    val result = if (bulk) {
      clientReal.addToBulk(updateAction)
    } else clientReal.update(updateAction)

    if (postUpdateHooks.nonEmpty) {
      implicit val executionContext =
        AuthContext.elasticsearch.executionContext
      val res = AuthContext.elasticsearch.awaitResultBulk(result.value)
      getById(index = realIndex, docType.getOrElse("_doc"), id).map { x =>
        var y = x
        postUpdateHooks.foreach(f => y = f(AuthContext, y))
        y
      }
    }
    result
  }

  def getByIdHash(id: String): ResponseETF[Document] =
    getById(concreteIndex(), this.typeName, UUID.nameUUIDFromString(id))

  def getByIdSlug(id: String): ResponseETF[Document] = {
    import nttdata.common.StringUtils._
    getById(concreteIndex(), this.typeName, id.slug)
  }

  def getByIds(ids: Seq[String]): ResponseETF[List[ResponseE[Document]]] = {
    implicit val executionContext = AuthContext.elasticsearch.executionContext
    val client =
      AuthContext.nosqlModule.elaticsearchModule.getConnection(concreteIndex())
    for {
      response <- client.mget(
        ids.map(id => (concreteIndex(), this.typeName, id))
      )
    } yield response.docs.map(d => processGetResponse(d))

  }

  def getById(id: String): ResponseETF[Document] =
    //    val (client, index) = getClient
    getById(concreteIndex(), this.typeName, id)

  def get(id: String): ResponseETF[Document] =
    //    val (client, index) = getClient
    getById(concreteIndex(), this.typeName, id)

  def processGetResponse(response: GetResponse): ResponseE[Document] = {
    //TODO notity broken json
    val resp = Json.fromJsonObject(response.source)
    this.fromJson(AuthContext, resp, None)
  }
  //    def getById(client: ElasticSearch,
  //                index: String,
  //                typeName: String,
  //                id: String): ResponseETF[Document] = {
  //      implicit val realClient = client.asInstanceOf[ElasticSearch].getConnection(index)
  //      implicit val manager = ElasticSearchContext(realClient, user = ElasticSearchSystemUser)
  //      val response = realClient.get(index, typeName, id)
  //      val realManager = ElasticSearchContext(realClient, user = ElasticSearchSystemUser)
  //      withErrorHandling[Option[Document]](response.map(p => processGetResponse(realManager, p)),
  //        None)
  //    }

  def getById(index: String, typeName: String, id: String): ResponseETF[Document] = {
    implicit val executionContext = AuthContext.elasticsearch.executionContext
    val realClient =
      AuthContext.nosqlModule.elaticsearchModule.getConnection(index)
    realClient.get(index, typeName, id).flatMap { response =>
      EitherT.fromEither(processGetResponse(response))
    }

    //      response.flatMap{
    //        resp =>
    //          EitherF[Future]()
    //      }
    //      withErrorHandling[Option[Document]](response.map{p =>
    //        processGetResponse(p)
    //      },
    //        None)
  }

  def count(): ResponseETF[Long] = {
    val index = concreteIndex()
    val realClient =
      AuthContext.nosqlModule.elaticsearchModule.getConnection(index)
    realClient.countAll(List(concreteIndex()), List(this.typeName) /*, filters=typeFilter*/ )

  }

  //TODO write the code for non-dynamic
  def processExtraFields(document: Document): Document = document

  def processExtraFields(json: Json): Json = {
    var result = json.asObject.get
    if (!result.keys.exists(_ == ElasticSearchConstants.typeField)) {
      result = result.add(ElasticSearchConstants.typeField, Json.fromString(this.indexName))
    }
    var process = false
    this.heatMapColumns.foreach { column =>
      if (result.keys.exists(_ == column.name)) {
        process = true
        result = JsonObject.fromIterable(
          result.toList ++ FieldHelpers.expandHeapMapValues(
            column.name,
            result(column.name).get.asString.get
          )
        )

      }
    }
    if (process) Json.fromJsonObject(result) else json
  }

  def typeFilter: List[Query] =
    List(queries.TermQuery(ElasticSearchConstants.typeField, this.indexName))

  def query: TypedQueryBuilder[Document] =
    //TODO add autofilter if user managed
    new TypedQueryBuilder[Document](
      //      filters=typeFilter,
      indices = List(concreteIndex()),
      docTypes = List(typeName),
      sort = ordering.map(v => elasticsearch.sort.FieldSort(v._1, order = v._2))
    )

  def keys: Iterator[String] = query.noSource.scan.map(_.id)

  def values = query.scan.map(_.source)

  def getAll(items: Seq[String]): Map[String, Document] = {
    import nttdata.common.FutureUtils._
    query.multiGet(items.toList).value.getWithBlocking.toOption.getOrElse(Nil).map(v => v.id -> v.source).toMap
  }

  /* Special Fields */
  //TODO stub we need to finish annotation propagation in schema
  lazy val heatMapColumns =
    this.schema.properties.filter(v => v.modifiers.contains(FieldModifier.HeatMap))
  //    this.columns.filter(_.annotations.exists(_.isInstanceOf[HeatMap]))
}
