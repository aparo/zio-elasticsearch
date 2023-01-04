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

package zio.elasticsearch.orm

import java.time.OffsetDateTime
import zio.auth.AuthContext
import zio.common.UUID
import zio.exception.{ BulkException, FrameworkException }
import zio.elasticsearch._
import zio.elasticsearch.client.Bulker
import zio.elasticsearch.mappings.MetaUser
import zio.elasticsearch.orm.models.TimeStampedModel
import zio.elasticsearch.queries.{ IdsQuery, TermQuery }
import zio.elasticsearch.requests.{ IndexRequest, UpdateRequest }
import zio.elasticsearch.responses.{ DeleteResponse, GetResponse, UpdateResponse }
import zio.elasticsearch.schema.FieldHelpers
import zio.json.ast._
import zio.json._
import zio.ZIO
import zio.stream.Stream
import zio.schema.Schema
import zio.schema.elasticsearch.{ ElasticSearchSchema, FieldModifier, ParentMeta, SchemaField }
import zio.schema.elasticsearch.annotations._
private[orm] class ESHelper[Document](
  schema: ElasticSearchSchema,
  metaUser: Option[MetaUser],
  parentMeta: Option[ParentMeta] = None,
  preSaveHooks: List[(AuthContext, Document) => Document] = Nil,
  preSaveJsonHooks: List[(AuthContext, Json.Obj) => Json.Obj] = Nil,
  postSaveHooks: List[(AuthContext, Document) => Document] = Nil,
  preDeleteHooks: List[(AuthContext, Document) => Document] = Nil,
  postDeleteHooks: List[(AuthContext, Document) => Document] = Nil,
  preUpdateHooks: List[(AuthContext, Document) => Document] = Nil,
  preUpdateJsonHooks: List[(AuthContext, Json.Obj) => Json.Obj] = Nil,
  postUpdateHooks: List[(AuthContext, Document) => Document] = Nil
)(
  implicit
  val jsonEncoder: JsonEncoder[Document],
  val jsonDecoder: JsonDecoder[Document],
  val elasticsearchClient: ClusterService
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
   * @param in
   *   the document
   * @param processed
   *   if processed
   * @return
   *   a Json
   */
  override def toJson(in: Document, processed: Boolean): Either[String, Json] =
    jsonEncoder.toJsonAST(in)

  override def fullNamespaceName: String = schema.id

  /**
   * It should be called before saving
   */
  override def processExtraFields(AuthContext: AuthContext, document: Document): Document = document

  def save(id: String, item: Document)(implicit authContext: AuthContext): ZioResponse[Document] =
    save(document = item, id = Some(id))

  def createMany(
    documents: Iterable[Document],
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    pipeline: Option[String] = None,
    skipExisting: Boolean = true
  )(implicit authContext: AuthContext): ZioResponse[List[Document]] = {
    val buildRequests = documents.map { document =>
      toIndexRequest(
        document,
        forceCreate = true,
        index = index,
        userId = userId,
        pipeline = pipeline
      )
    }

    for {
      resultBulkInitial <- elasticsearchClient.baseElasticSearchService.bulk(buildRequests.map(_._2).toList)
      requests <- if (!skipExisting) ZIO.succeed(buildRequests)
      else
        ZIO.attempt {
          val results =
            resultBulkInitial.items.filterNot(_.isConflict).map(r => r.index -> r.id)
          // fast path results empty
          if (results.isEmpty) Nil
          else if (results.length == buildRequests.size) {
            buildRequests
          } else
            buildRequests.filter {
              case (_, request) =>
                request.id match {
                  case Some(value) =>
                    results.contains(request.index -> value)
                  case None =>
                    true
                }
            }
        }.mapError(e => FrameworkException(e))

      resultBulk <- ZIO.succeed(if (skipExisting) resultBulkInitial.removeAlreadyExist else resultBulkInitial)
      _ <- ZIO.when(resultBulk.errors) {
        ZIO.fail(BulkException(resultBulk, message = "In Create Many"))
      }
      //TODO clean the request
      result <- if (postSaveHooks.isEmpty) ZIO.succeed(requests.map(_._1).toList)
      else
        ZIO.succeed {
          requests
            .map(_._1)
            .map { doc =>
              var d = doc
              /*Post Saving record */
              postSaveHooks.foreach(f => d = f(authContext, d))
              d
            }
            .toList
        }
      _ <- ZIO.when(refresh) {
        elasticsearchClient.indicesService.refresh(requests.map(_._2.index).toList.distinct)
      }

    } yield result
  }

  def saveMany(
    documents: Iterable[Document],
    forceCreate: Boolean = false,
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    pipeline: Option[String] = None
  )(implicit authContext: AuthContext): ZioResponse[List[Document]] = {
    val buildRequests = documents.map { document =>
      toIndexRequest(
        document,
        forceCreate = forceCreate,
        index = index,
        userId = userId,
        pipeline = pipeline
      )
    }

    for {
      resultBulk <- elasticsearchClient.baseElasticSearchService.bulk(buildRequests.map(_._2).toList)
      _ <- ZIO.when(resultBulk.errors)(ZIO.fail(BulkException(resultBulk, message = "In Save Many")))
      result <- if (postSaveHooks.isEmpty) ZIO.succeed(buildRequests.map(_._1).toList)
      else
        ZIO.succeed {
          buildRequests
            .map(_._1)
            .map { doc =>
              var d = doc
              /*Post Saving record */
              postSaveHooks.foreach(f => d = f(authContext, d))
              d
            }
            .toList
        }
      _ <- ZIO.when(refresh) {
        elasticsearchClient.indicesService.refresh(buildRequests.map(_._2.index).toList.distinct)
      }

    } yield result
  }

  def save(
    document: Document,
    bulk: Boolean = false,
    forceCreate: Boolean = false,
    index: Option[String] = None,
    version: Option[Long] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    id: Option[String] = None,
    pipeline: Option[String] = None
  )(implicit authContext: AuthContext): ZioResponse[Document] = {
    val (obj, indexRequest) = toIndexRequest(
      document,
      forceCreate = forceCreate,
      index = index,
      version = version,
      refresh = refresh,
      userId = userId,
      id = id,
      pipeline = pipeline
    )

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
    forceCreate: Boolean,
    index: Option[String],
    version: Option[Long] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    id: Option[String] = None,
    pipeline: Option[String] = None
  )(implicit authContext: AuthContext): (Document, IndexRequest) = {
    var obj = document
    obj = updateFields(obj)

    if (obj.isInstanceOf[WithHiddenId]) {
      obj.asInstanceOf[WithHiddenId]._version = document.asInstanceOf[WithHiddenId]._version
    }
    preSaveHooks.foreach(f => obj = f(authContext, obj))

    var source = toJsValue(obj, true)

    source = source.add(ElasticSearchConstants.TYPE_FIELD, Json.Str(concreteIndex(index)))

    if (obj.isInstanceOf[TimeStampedModel]) {
      source = source.add("modified", Json.Str(OffsetDateTime.now().toString))
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
      IndexRequest(index = realIndex, body = source, pipeline = pipeline)

    //we set id
    id match {
      case Some(value) =>
        // we need to check if has a schema with single index
        indexRequest = indexRequest.copy(id = Some(schema.resolveId(source, id)))

      case None =>
        // no id configured we need to calculate it
        obj match {
          case c: zio.schema.elasticsearch.annotations.CustomId =>
            indexRequest = indexRequest.copy(id = Some(c.calcId()))

          case c: WithId =>
            if (c.id.isEmpty)
              indexRequest = indexRequest.copy(id = Some(UUID.randomBase64UUID()))
            else
              indexRequest = indexRequest.copy(id = Some(c.id))

          case mWI: WithHiddenId if mWI._id.isDefined =>
            indexRequest = indexRequest.copy(id = mWI._id)

          case _ =>
            // we check if there are PK in Schema
            indexRequest = indexRequest.copy(id = Some(schema.resolveId(source, id)))

        }
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
        source.getOption[String](pmeta.field).orElse(indexRequest.id)
      indexRequest = indexRequest.copy(routing = parent)
    }

    if (forceCreate) {
      indexRequest = indexRequest.copy(opType = OpType.create)
    }
    if (refresh)
      indexRequest = indexRequest.copy(refresh = Some(Refresh.`true`))
    (obj, indexRequest)
  }

  override def refresh()(implicit authContext: AuthContext): ZioResponse[Unit] = {
    val index = concreteIndex()
    elasticsearchClient.indicesService.refresh(index).unit
  }

  /**
   * Bulk a stream of documents
   *
   * @param documents
   * @param size
   * @param index
   * @param refresh
   * @param userId
   * @return
   */
  def bulkStream(
    documents: zio.stream.Stream[FrameworkException, Document],
    size: Int = 1000,
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    forceCreate: Boolean = false,
    pipeline: Option[String] = None
  )(implicit authContext: AuthContext): ZIO[Any, FrameworkException, Bulker] =
    for {
      bulker <- Bulker(elasticsearchClient.baseElasticSearchService, bulkSize = size)
      _ <- documents.foreach { document =>
        bulker.add(
          toIndexRequest(
            document,
            forceCreate = forceCreate,
            index = index,
            refresh = refresh,
            userId = userId,
            pipeline = pipeline
          )._2
        )
      }
    } yield bulker

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
    fastDelete(concreteIndex(), id)

  def delete(document: Document, bulk: Boolean = false, refresh: Boolean = false)(
    implicit
    authContext: AuthContext
  ): ZioResponse[DeleteResponse] = {
    val (index, docType, id) = extractIndexMeta(document)

    if (preDeleteHooks.isEmpty && postDeleteHooks.isEmpty) {
      fastDelete(index, id, bulk = bulk, refresh = refresh)
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
    val index = concreteIndex()
    elasticsearchClient.indicesService.delete(Seq(index)).unit
//
//    var qs = query
//    index.foreach(name => qs = qs.copy(indices = Seq(name)))
//    qs.delete()
  }

  def fastDelete(
    index: String,
    id: String,
    bulk: Boolean = false,
    refresh: Boolean = false
  )(implicit authContext: AuthContext): ZioResponse[DeleteResponse] =
    elasticsearchClient.delete(
      index,
      schema.validateId(id),
      bulk = bulk,
      refresh = Some(Refresh.fromValue(refresh))
    )

  //TODO bulk by default
  def deleteByIds(ids: Seq[String])(implicit authContext: AuthContext) =
    query.filterF(IdsQuery(values = ids.toList)).delete()

  def deleteAll()(implicit authContext: AuthContext) =
    query.delete()

  def deleteMany(
    documents: Iterable[Document],
    bulk: Boolean = false,
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None
  )(implicit authContext: AuthContext): ZioResponse[List[DeleteResponse]] = // TODO manage final refresh
  ZIO.foreach(documents.toList) { document =>
    delete(
      document = document,
      bulk = bulk,
//          index=index,
      refresh = false //,
//          userId=userId
    )
  }

  def deleteById(
    id: String,
    bulk: Boolean = false,
    refresh: Boolean = false,
    userId: Option[String] = None
  )(implicit authContext: AuthContext): ZioResponse[DeleteResponse] =
    if (preDeleteHooks.isEmpty && postDeleteHooks.isEmpty) {
      fastDelete(
        concreteIndex(),
        id,
        bulk = bulk,
        refresh = refresh
      )
    } else {
      for {
        item <- getById(id)
        res <- delete(item, bulk = bulk, refresh = refresh)
      } yield res
    }

  // convert class to a JsObject
  def toJsValue(in: Document, processed: Boolean)(implicit authContext: AuthContext): Json.Obj = {
    val toBeProcessed = processed

    val withExtra = if (toBeProcessed) processExtraFields(in) else in
    processExtraFields(withExtra.asJson.asInstanceOf[Json.Obj])
  }

  def getFieldByName(name: String): Option[SchemaField] =
    schema.properties.find(_.name == name)

  def concreteIndex(index: Option[String] = None)(implicit authContext: AuthContext): String =
    index match {
      //        case _ if ElasticSearchConstants.testMode => Constants.defaultTestIndex
      case None    => authContext.resolveContext(this.schema.index.indexName.getOrElse(this.schema.id))
      case Some(i) => authContext.resolveContext(i)
    }

  def update(
    id: String,
    document: Document,
    values: Json.Obj,
    bulk: Boolean = false,
    refresh: Boolean = false,
    storageNamespace: Option[String] = None,
    docType: Option[String] = None,
    userId: Option[String] = None
  )(implicit authContext: AuthContext): ZioResponse[UpdateResponse] = {
    var updateJson = values
    //TODO add
    //preUpdateHooks

    if (schema.indexRequireType)
      updateJson = updateJson.add(ElasticSearchConstants.TYPE_FIELD, Json.Str(schema.id))

    if (document.isInstanceOf[TimeStampedModel]) {
      updateJson = updateJson.add(
        "modified",
        Json.Str(OffsetDateTime.now().toString)
      )
    }

    val realIndex: String = storageNamespace match {
      case Some(v) => v
      case None =>
        document match {
          case obj: CustomIndex =>
            obj.calcIndex()
          case _ =>
            authContext.resolveContext(schema.index.indexName.getOrElse(schema.id))
        }
    }

    preUpdateJsonHooks.foreach(f => updateJson = f(authContext, updateJson))

    var updateAction = UpdateRequest(
      realIndex,
      schema.validateId(id),
      body = Json.Obj("doc" -> updateJson),
      refresh = Some(Refresh.`false`)
    )

    //      preUpdateActionHooks.foreach(f => updateAction = f(authContext, updateAction))

    //TODO add user data on change if required

    def processPostUpdate(req: UpdateResponse): ZioResponse[UpdateResponse] =
      if (postUpdateHooks.nonEmpty) {
        getById(index = realIndex, schema.validateId(id)).map { x =>
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
    getById(concreteIndex(), UUID.nameUUIDFromString(id))

  def getByIdSlug(id: String)(implicit authContext: AuthContext): ZioResponse[Document] = {
    import zio.common.StringUtils._
    getById(concreteIndex(), id.slug)
  }

  def getByIds(ids: Seq[String])(implicit authContext: AuthContext): ZioResponse[List[ZioResponse[Document]]] =
    for {
      response <- elasticsearchClient.baseElasticSearchService.mget(
        ids.map(id => (concreteIndex(), schema.validateId(id)))
      )
    } yield response.docs.map(d => processGetResponse(d))

  def getById(id: String)(implicit authContext: AuthContext): ZioResponse[Document] =
    getById(concreteIndex(), id)

  def get(id: String)(implicit authContext: AuthContext): ZioResponse[Document] =
    getById(concreteIndex(), id)

  def processGetResponse(response: GetResponse)(implicit authContext: AuthContext): ZioResponse[Document] = {
    //TODO notity broken json
    val resp = response.source
    this.fromJson(authContext, resp, None)
  }

  def getById(index: String, id: String)(implicit authContext: AuthContext): ZioResponse[Document] =
    elasticsearchClient.get(index, schema.validateId(id)).flatMap { response =>
      processGetResponse(response)
    }

  def count()(implicit authContext: AuthContext): ZioResponse[Long] =
    if (schema.indexRequireType) {
      elasticsearchClient.countAll(Seq(concreteIndex()), filters = List(typeFilter))
    } else {
      elasticsearchClient.countAll(concreteIndex())
    }

  //TODO write the code for non-dynamic
  def processExtraFields(document: Document): Document = document

  def processExtraFields(json: Json.Obj)(implicit authContext: AuthContext): Json.Obj = {
    var result = json
    if (!result.keys.exists(_ == ElasticSearchConstants.TYPE_FIELD)) {
      result = result.add(ElasticSearchConstants.TYPE_FIELD, Json.Str(concreteIndex()))
    }
    var process = false
    this.heatMapColumns.foreach { column =>
      if (result.keys.exists(_ == column.name)) {
        process = true
        result = Json.Obj(
          result.fields ++ FieldHelpers.expandHeapMapValues(
            column.name,
            result.getOption[String](column.name).get
          )
        )

      }
    }
    if (process) result else json
  }

  def query(implicit authContext: AuthContext): TypedQueryBuilder[Document] =
    if (schema.indexRequireType) {
      //TODO add autofilter if user managed
      new TypedQueryBuilder[Document](
        filters = List(typeFilter),
        indices = List(concreteIndex()),
        sort = ordering.map(v => zio.elasticsearch.sort.FieldSort(v._1, order = v._2)),
        isSingleIndex = false,
        docTypes = List(schema.name)
      )
    } else {
      //TODO add autofilter if user managed
      new TypedQueryBuilder[Document](
        //      filters=typeFilter,
        indices = List(concreteIndex()),
        sort = ordering.map(v => zio.elasticsearch.sort.FieldSort(v._1, order = v._2))
      )

    }

  def keys(implicit authContext: AuthContext): Stream[FrameworkException, String] =
    query.noSource.scan.map(res => cleanId(res.id))

  def values(implicit authContext: AuthContext): Stream[FrameworkException, Document] =
    query.scan.map(_.source.toOption.get)

  def getAll(items: Seq[String])(implicit authContext: AuthContext): ZioResponse[Map[String, Document]] =
    query.multiGet(items.toList).map(_.map(v => v.id -> v.source.toOption.get).toMap)

  /* Special Fields */
  //TODO stub we need to finish annotation propagation in schema
  lazy val heatMapColumns =
    this.schema.properties.filter(v => v.modifiers.contains(FieldModifier.HeatMap))
  //    this.columns.filter(_.annotations.exists(_.isInstanceOf[HeatMap]))

  lazy val typeFilter = TermQuery(ElasticSearchConstants.TYPE_FIELD, schema.id)

  lazy val cleanId: String => String = schema.cleanId(_)

  // not remove used for compatibility with schema helper
  override def getById(index: String, typeName: String, id: String)(
    implicit
    authContext: AuthContext
  ): ZioResponse[Document] = getById(index, id)
}
