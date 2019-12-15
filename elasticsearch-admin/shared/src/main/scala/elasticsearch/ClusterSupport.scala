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

package elasticsearch

import elasticsearch.client.{ ClusterActionResolver, ESCursor, ESCursorRaw, NativeCursor, NativeCursorRaw }
import elasticsearch.managers.ClusterManager
import elasticsearch.mappings.RootDocumentMapping
import elasticsearch.orm.{ QueryBuilder, TypedQueryBuilder }
import elasticsearch.queries.Query
import elasticsearch.requests.{ DeleteRequest, GetRequest, IndexRequest }
import elasticsearch.responses.{ DeleteResponse, GetResponse, HitResponse, IndexResponse, SearchResponse, SearchResult }
import io.circe.{ Decoder, Encoder, JsonObject }
import zio.ZIO

trait ClusterSupport extends ClusterActionResolver with IndicesSupport {
  lazy val cluster = new ClusterManager(this)
  lazy val mappings =
    new elasticsearch.mappings.MappingManager()(logger, this)

  def dropDatabase(index: String): ZioResponse[Unit] =
    for {
      exists <- this.indices.exists(Seq(index))
      _ <- if (exists.isExists)(this.indices
        .delete(Seq(index))
        .andThen(this.cluster.health(waitForStatus = Some(WaitForStatus.yellow))))
      else ZIO.unit
      dir <- dirty
      _ <- dir.set(false)
    } yield ()

  def getIndicesAlias(): ZioResponse[Map[String, List[String]]] =
    this.cluster.state().map { response =>
      response.metadata.indices.map { i =>
        i._1 -> i._2.aliases
      }
    }

  def reindex(index: String)(
    implicit qContext: ESNoSqlContext
  ): Unit = {
    val qb = QueryBuilder(indices = List(index))(
      qContext.systemNoSQLContext(),
      this
    )
    qb.scanHits.foreach { searchHit =>
      this.addToBulk(
        IndexRequest(
          searchHit.index,
          id = Some(searchHit.id),
          body = searchHit.source
        )
      )
    }
    flush(index)

  }

  def copyData(
    queryBuilder: QueryBuilder,
    destIndex: String,
    destType: Option[String] = None,
    callbackSize: Int = 10000,
    callback: Int => Unit = { _ =>
    },
    transformSource: HitResponse => JsonObject = {
      _.source
    }
  ) = {
    val destT = destType.getOrElse(queryBuilder.docTypes.head)
    var size = 0
    queryBuilder.scanHits.foreach { hit =>
      size += 1
      addToBulk(
        IndexRequest(
          destIndex,
          id = Some(hit.id),
          body = transformSource(hit)
        )
      )
      if (size % callbackSize == 0) {
        callback(size)
      }
    }
    if (size > 0)
      callback(size)
    flush(destIndex)
  }

  def getIds(index: String, docType: String)(
    implicit qContext: ESNoSqlContext
  ): Iterator[String] =
    QueryBuilder(
      indices = List(index),
      docTypes = List(docType),
      bulkRead = 5000
    )(qContext.systemNoSQLContext(), this).valueList[String]("_id")

  def countAll(indices: Seq[String], types: Seq[String], filters: List[Query] = Nil)(
    implicit nosqlContext: ESNoSqlContext
  ): ZioResponse[Long] = {
    val qb = QueryBuilder(indices = indices, docTypes = types, size = 0, filters = filters)(nosqlContext, this)
    qb.results.map(_.total.value)
  }

  def countAll(index: String)(implicit nosqlContext: ESNoSqlContext): ZioResponse[Long] =
    countAll(indices = List(index), types = Nil)

  def countAll(index: String, types: Option[String], filters: List[Query])(
    implicit nosqlContext: ESNoSqlContext
  ): ZioResponse[Long] =
    countAll(indices = List(index), types = types.toList)

  def search[T: Encoder: Decoder](
    queryBuilder: TypedQueryBuilder[T]
  ): ZioResponse[SearchResult[T]] =
    this.execute(queryBuilder.toRequest).map(r => SearchResult.fromResponse[T](r))

  /* Get a typed JSON document from an index based on its id. */
  def searchScan[T: Encoder: Decoder](
    queryBuilder: TypedQueryBuilder[T]
  ): ESCursor[T] = {
    implicit val qContext = queryBuilder.nosqlContext
    implicit val client = queryBuilder.client
    new ESCursor(new NativeCursor[T](queryBuilder))
  }

  def search(
    queryBuilder: QueryBuilder
  ): ZioResponse[SearchResponse] =
    this.execute(queryBuilder.toRequest)

  def searchScan(queryBuilder: QueryBuilder): ESCursor[JsonObject] = {
    implicit val qContext = queryBuilder.nosqlContext
    implicit val client = queryBuilder.client
    new ESCursor(
      new NativeCursor[JsonObject](queryBuilder.setScan().toTyped[JsonObject])
    )
  }

  def searchScanRaw(queryBuilder: QueryBuilder): ESCursorRaw =
    new ESCursorRaw(new NativeCursorRaw(queryBuilder.setScan()))

  def searchScroll(queryBuilder: QueryBuilder): ESCursor[JsonObject] = {
    implicit val qContext = queryBuilder.nosqlContext
    implicit val client = queryBuilder.client
    new ESCursor(new NativeCursor[JsonObject](queryBuilder.toTyped[JsonObject]))
  }

  /**
   * We ovveride methods t powerup user management0
   */
  /*
   * Returns a document.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param storedFields A comma-separated list of stored fields to return in the response
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   */
  override def get(
    index: String,
    id: String,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExclude: Seq[String] = Nil,
    sourceInclude: Seq[String] = Nil,
    storedFields: Seq[String] = Nil,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None
  )(implicit context: ESNoSqlContext): ZioResponse[GetResponse] = {
    // Custom Code On
    //alias expansion
    val ri = concreteIndex(Some(index))
    logger.debug(s"get($ri, $id)")

    var request = GetRequest(
      index = concreteIndex(Some(index)),
      id = id,
      preference = preference,
      realtime = realtime,
      refresh = refresh,
      routing = routing,
      source = source,
      sourceExclude = sourceExclude,
      sourceInclude = sourceInclude,
      storedFields = storedFields,
      version = version,
      versionType = versionType
    )

    context.user match {
      case user if user.id == ESSystemUser.id =>
        get(request)
      case user =>
        //TODO add user to the request
        val mapping = context.environment.unsafeRun(this.mappings.get(concreteIndex(Some(index))))
        val metaUser = mapping.meta.user
        //we manage auto_owner objects
        if (metaUser.auto_owner) {
          request = request.copy(id = metaUser.processAutoOwnerId(id, user.id))
          get(request).flatMap { result =>
            if (result.found) {
              ZIO.succeed(result)
            } else {
              get(request.copy(id = id)) //TODO exception in it' missing
            }
          }

        } else {
          get(request)
        }
    }
  }

  /*
   * Removes a document from the index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param ifPrimaryTerm only perform the delete operation if the last operation that has changed the document has the specified primary term
   * @param ifSeqNo only perform the delete operation if the last operation that has changed the document has the specified sequence number
   * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param routing Specific routing value
   * @param timeout Explicit operation timeout
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  override def delete(
    index: String,
    id: String,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    refresh: Option[_root_.elasticsearch.Refresh] = None,
    routing: Option[String] = None,
    timeout: Option[String] = None,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None,
    waitForActiveShards: Option[String] = None,
    bulk: Boolean = false
  )(implicit context: ESNoSqlContext): ZioResponse[DeleteResponse] = {
    //alias expansion
    //    val realDocType = this.mappings.expandAliasType(concreteIndex(Some(index)))
    val ri = concreteIndex(Some(index))
    logger.debug(s"delete($ri, $id)")

    var request = DeleteRequest(
      index = concreteIndex(Some(index)),
      id = id,
      ifPrimaryTerm = ifPrimaryTerm,
      ifSeqNo = ifSeqNo,
      refresh = refresh,
      version = version,
      versionType = versionType,
      routing = routing,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    if (context.user.id != ESSystemUser.id) {
      val upUser = for {
        map <- this.mappings.get(concreteIndex(Some(index)))
      } yield {
        //we manage auto_owner objects
        val metaUser = map.meta.user
        if (metaUser.auto_owner) {
          request = request.copy(id = metaUser.processAutoOwnerId(id, context.user.id))
        }
      }
      context.environment.unsafeRun(upUser)
    }

    if (bulk) {
      this.addToBulk(request) *>
        ZIO.succeed(DeleteResponse(index = request.index, id = request.id))

    } else delete(request)
  }

  /*
   * Creates or updates a document in an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
   *
   * @param index The name of the index
   * @param id Document ID
   * @param body body the body of the call
   * @param ifPrimaryTerm only perform the index operation if the last operation that has changed the document has the specified primary term
   * @param ifSeqNo only perform the index operation if the last operation that has changed the document has the specified sequence number
   * @param opType Explicit operation type. Defaults to `index` for requests with an explicit document ID, and to `create`for requests without an explicit document ID
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param routing Specific routing value
   * @param timeout Explicit operation timeout
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  override def indexDocument(
    index: String,
    body: JsonObject,
    id: Option[String] = None,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    opType: OpType = OpType.index,
    pipeline: Option[String] = None,
    refresh: Option[_root_.elasticsearch.Refresh] = None,
    routing: Option[String] = None,
    timeout: Option[String] = None,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None,
    waitForActiveShards: Option[Int] = None,
    bulk: Boolean = false
  )(implicit noSQLContextManager: ESNoSqlContext): ZioResponse[IndexResponse] = {
    val request = IndexRequest(
      index = index,
      body = body,
      id = id,
      ifPrimaryTerm = ifPrimaryTerm,
      ifSeqNo = ifSeqNo,
      opType = opType,
      pipeline = pipeline,
      refresh = refresh,
      routing = routing,
      timeout = timeout,
      version = version,
      //versionType = versionType,
      waitForActiveShards = waitForActiveShards
    )

    def applyMappingChanges(mapping: RootDocumentMapping, request: IndexRequest): IndexRequest =
      if (id.isDefined) {
        noSQLContextManager.user match {
          case u if u.id == ESSystemUser.id => request
          case u =>
            val metaUser = mapping.meta.user
            if (metaUser.auto_owner) {
              request.copy(id = Some(metaUser.processAutoOwnerId(id.get, u.id)))
            } else request
        }
      } else {
        noSQLContextManager.user match {
          case user if user.id == ESSystemUser.id => request
          case u =>
            val metaUser = mapping.meta.user
            if (metaUser.auto_owner) {
              request.copy(id = Some(u.id))
            } else request
        }
      }

    def applyReqOrBulk(request: IndexRequest, bulk: Boolean): ZioResponse[IndexResponse] =
      if (bulk) {
        this.addToBulk(request) *>
          ZIO.succeed(
            IndexResponse(
              shards = elasticsearch.responses.Shards.empty,
              index = request.index,
              id = request.id.getOrElse(""),
              version = 0
            )
          )

      } else
        indexDocument(request)

    for {
      req <- this.mappings
        .get(concreteIndex(Some(index)))
        .fold[IndexRequest](_ => request, m => applyMappingChanges(m, request))
      res <- applyReqOrBulk(req, bulk)
    } yield res

  }
}
