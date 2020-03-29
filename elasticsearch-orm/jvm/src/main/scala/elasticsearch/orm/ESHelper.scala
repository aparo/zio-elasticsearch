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

package elasticsearch.orm

import java.time.OffsetDateTime

import elasticsearch.orm.models.TimeStampedModel
import elasticsearch._
import elasticsearch.queries.{ IdsQuery }
import elasticsearch.requests.{ IndexRequest, UpdateRequest }
import elasticsearch.responses.{ DeleteResponse, GetResponse, UpdateResponse }
import elasticsearch.schema.FieldHelpers
import io.circe
import io.circe._
import zio.ZIO
import zio.auth.AuthContext
import zio.common.UUID
import zio.exception.FrameworkException
import zio.schema._
import zio.schema.annotations._
import zio.schema.generic.JsonSchema
import zio.stream.Stream
import io.circe.syntax._

private[orm] class ESHelper[Document](
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
)(
  implicit val jsonEncoder: Encoder[Document],
  val jsonDecoder: Decoder[Document],
  val elasticsearchClient: ClusterService.Service
) extends SchemaHelper[Document] {

  override def typeName: String = "_doc"

  //  private val getClientForReader: ElasticSearch = {
  //    AuthContext.nosqlModule.elaticsearchModule.getConnection(index = concreteIndex())
  //  }

//  val indexName: String = concreteIndex()
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

  def save(id: String, item: Document)(implicit authContext: AuthContext): ZioResponse[Document] =
    save(document = item, id = Some(id))

  def save(items: Iterator[Document])(implicit authContext: AuthContext): Iterator[ZioResponse[Document]] =
    items.map(item => save(item))

  def save(items: Seq[Document])(implicit authContext: AuthContext): Seq[ZioResponse[Document]] =
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
  )(implicit authContext: AuthContext): ZioResponse[Document] = {
    var obj = document
    obj = updateFields(obj)

    if (obj.isInstanceOf[WithHiddenId]) {
      obj.asInstanceOf[WithHiddenId]._version = document.asInstanceOf[WithHiddenId]._version
    }
    preSaveHooks.foreach(f => obj = f(authContext, obj))

    var source = toJsValue(obj, true).asObject.get

    source = source.add(ElasticSearchConstants.typeField, Json.fromString(concreteIndex(index)))

    if (obj.isInstanceOf[TimeStampedModel]) {
      source = source.add("modified", Json.fromString(OffsetDateTime.now().toString))
    }

    preSaveJsonHooks.foreach(f => source = f(authContext, source))
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

    var indexRequest =
      IndexRequest(index = realIndex, body = source)

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
      indexRequest = indexRequest.copy(routing = parent)
    }

    if (forceCreate) {
      indexRequest = indexRequest.copy(opType = OpType.create)
    }
    if (refresh)
      indexRequest = indexRequest.copy(refresh = Some(Refresh.`true`))

    val response = bulk match {
      case true =>
        elasticsearchClient.baseElasticSearchService.addToBulk(indexRequest) *> ZIO.succeed(obj)
      case false =>
        val respEither =
          elasticsearchClient.baseElasticSearchService.indexDocument(indexRequest)
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
          obj
        }
    }

    response.map { doc =>
      var d = doc
      /*Post Saving record */
      postSaveHooks.foreach(f => d = f(authContext, d))
      d
    }
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
  )(implicit authContext: AuthContext): (Document, IndexRequest) = {
    var obj = document
    obj = updateFields(obj)

    if (obj.isInstanceOf[WithHiddenId]) {
      obj.asInstanceOf[WithHiddenId]._version = document.asInstanceOf[WithHiddenId]._version
    }
    preSaveHooks.foreach(f => obj = f(authContext, obj))

    var source = toJsValue(obj, true).asObject.get
    source = source.add(ElasticSearchConstants.typeField, Json.fromString(concreteIndex(index)))

    if (obj.isInstanceOf[TimeStampedModel]) {
      source = source.add("modified", Json.fromString(OffsetDateTime.now().toString))
    }

    preSaveJsonHooks.foreach(f => source = f(authContext, source))
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
    var indexRequest =
      IndexRequest(index = realIndex, body = source)

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
      indexRequest = indexRequest.copy(routing = parent)
    }

    if (forceCreate) {
      indexRequest = indexRequest.copy(opType = OpType.create)
    }
    if (refresh)
      indexRequest = indexRequest.copy(refresh = Some(Refresh.`true`))

    postSaveHooks.foreach(f => obj = f(authContext, obj))
    (obj, indexRequest)
  }

  override def refresh()(implicit authContext: AuthContext): ZioResponse[Unit] = {
    val index = concreteIndex()
    elasticsearchClient.indicesService.refresh(index).unit
  }

  def extractIndexMeta(document: Document)(implicit authContext: AuthContext): (String, String, String) = {
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

  def delete(id: String)(implicit authContext: AuthContext): ZioResponse[DeleteResponse] =
    fastDelete(concreteIndex(), this.typeName, id)

  def delete(document: Document, bulk: Boolean = false, refresh: Boolean = false)(
    implicit authContext: AuthContext
  ): ZioResponse[DeleteResponse] = {
    val (index, docType, id) = extractIndexMeta(document)

    if (preDeleteHooks.isEmpty && postDeleteHooks.isEmpty) {
      fastDelete(index, docType, id, bulk = bulk, refresh = refresh)
    } else {
      var obj = document
      preDeleteHooks.foreach(f => obj = f(authContext, obj))

      val res = elasticsearchClient.delete(
        index,
        id,
        refresh = Some(Refresh.fromValue(refresh)),
        bulk = bulk
      )
      postDeleteHooks.foreach(f => obj = f(authContext, obj))
      res
    }
  }

  /* drop this document collection */
  override def drop(index: Option[String])(implicit authContext: AuthContext): ZioResponse[Unit] = {
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
  )(implicit authContext: AuthContext): ZioResponse[DeleteResponse] =
    elasticsearchClient.delete(
      index,
      id,
      bulk = bulk,
      refresh = Some(Refresh.fromValue(refresh))
    )

  //TODO bulk by default
  def deleteByIds(ids: Seq[String])(implicit authContext: AuthContext) =
    query.filterF(IdsQuery(values = ids.toList)).delete()

  def deleteAll()(implicit authContext: AuthContext) =
    query.delete()

  def deleteById(
    id: String,
    bulk: Boolean = false,
    refresh: Boolean = false,
    userId: Option[String] = None
  )(implicit authContext: AuthContext): ZioResponse[DeleteResponse] =
    if (preDeleteHooks.isEmpty && postDeleteHooks.isEmpty) {
      fastDelete(
        concreteIndex(),
        this.typeName,
        id,
        bulk = bulk,
        refresh = refresh
      )
    } else {
      for {
        item <- getById(id);
        res <- delete(item, bulk = bulk, refresh = refresh)
      } yield res
    }

  // convert class to a JsObject
  def toJsValue(in: Document, processed: Boolean)(implicit authContext: AuthContext): Json = {
    val toBeProcessed = processed

    val withExtra = if (toBeProcessed) processExtraFields(in) else in
    processExtraFields(withExtra.asJson)
  }

  def getFieldByName(name: String): Option[SchemaField] =
    schema.properties.find(_.name == name)

  def concreteIndex(index: Option[String] = None)(implicit authContext: AuthContext): String =
    index match {
      //        case _ if ElasticSearchConstants.testMode => Constants.defaultTestIndex
      case None    => authContext.resolveContext(this.schema.tableName)
      case Some(i) => authContext.resolveContext(i)
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
  )(implicit authContext: AuthContext): ZioResponse[UpdateResponse] = {
    var updateJson = values
    //TODO add
    //preUpdateHooks

    updateJson = updateJson.add(ElasticSearchConstants.typeField, Json.fromString(concreteIndex()))

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
            authContext.resolveContext(schema.tableName)
        }
    }

    preUpdateJsonHooks.foreach(f => updateJson = f(authContext, updateJson))

    var updateAction = UpdateRequest(
      realIndex,
      id,
      body = circe.JsonObject.fromIterable(Seq("doc" -> Json.fromJsonObject(updateJson))),
      refresh = Some(Refresh.`false`)
    )

    //      preUpdateActionHooks.foreach(f => updateAction = f(authContext, updateAction))

    //TODO add user data on change if required

    def processPostUpdate(req: UpdateResponse): ZioResponse[UpdateResponse] =
      if (postUpdateHooks.nonEmpty) {
        getById(index = realIndex, docType.getOrElse("_doc"), id).map { x =>
          var y = x
          postUpdateHooks.foreach(f => y = f(authContext, y))
          y
        } *> ZIO.succeed(req)
      } else ZIO.succeed(req)

    for {
      result <- if (bulk) {
        elasticsearchClient.baseElasticSearchService.addToBulk(updateAction)
      } else elasticsearchClient.baseElasticSearchService.update(updateAction)
      resPost <- processPostUpdate(result)
    } yield resPost

  }

  def getByIdHash(id: String)(implicit authContext: AuthContext): ZioResponse[Document] =
    getById(concreteIndex(), this.typeName, UUID.nameUUIDFromString(id))

  def getByIdSlug(id: String)(implicit authContext: AuthContext): ZioResponse[Document] = {
    import zio.common.StringUtils._
    getById(concreteIndex(), this.typeName, id.slug)
  }

  def getByIds(ids: Seq[String])(implicit authContext: AuthContext): ZioResponse[List[ZioResponse[Document]]] =
    for {
      response <- elasticsearchClient.baseElasticSearchService.mget(
        ids.map(id => (concreteIndex(), this.typeName, id))
      )
    } yield response.docs.map(d => processGetResponse(d))

  def getById(id: String)(implicit authContext: AuthContext): ZioResponse[Document] =
    //    val (client, index) = getClient
    getById(concreteIndex(), this.typeName, id)

  def get(id: String)(implicit authContext: AuthContext): ZioResponse[Document] =
    //    val (client, index) = getClient
    getById(concreteIndex(), this.typeName, id)

  def processGetResponse(response: GetResponse)(implicit authContext: AuthContext): ZioResponse[Document] = {
    //TODO notity broken json
    val resp = Json.fromJsonObject(response.source)
    this.fromJson(authContext, resp, None)
  }

  def getById(index: String, typeName: String, id: String)(implicit authContext: AuthContext): ZioResponse[Document] =
    elasticsearchClient.get(index, id).flatMap { response =>
      processGetResponse(response)
    }

  def count()(implicit authContext: AuthContext): ZioResponse[Long] =
    elasticsearchClient.countAll(List(concreteIndex()), List(this.typeName))

  //TODO write the code for non-dynamic
  def processExtraFields(document: Document): Document = document

  def processExtraFields(json: Json)(implicit authContext: AuthContext): Json = {
    var result = json.asObject.get
    if (!result.keys.exists(_ == ElasticSearchConstants.typeField)) {
      result = result.add(ElasticSearchConstants.typeField, Json.fromString(concreteIndex()))
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

  def query(implicit authContext: AuthContext): TypedQueryBuilder[Document] =
    //TODO add autofilter if user managed
    new TypedQueryBuilder[Document](
      //      filters=typeFilter,
      indices = List(concreteIndex()),
      docTypes = List(typeName),
      sort = ordering.map(v => elasticsearch.sort.FieldSort(v._1, order = v._2))
    )

  def keys(implicit authContext: AuthContext): Stream[FrameworkException, String] = query.noSource.scan.map(_.id)

  def values(implicit authContext: AuthContext): Stream[FrameworkException, Document] = query.scan.map(_.source)

  def getAll(items: Seq[String])(implicit authContext: AuthContext): ZioResponse[Map[String, Document]] =
    query.multiGet(items.toList).map(_.map(v => v.id -> v.source).toMap)

  /* Special Fields */
  //TODO stub we need to finish annotation propagation in schema
  lazy val heatMapColumns =
    this.schema.properties.filter(v => v.modifiers.contains(FieldModifier.HeatMap))
  //    this.columns.filter(_.annotations.exists(_.isInstanceOf[HeatMap]))

}
