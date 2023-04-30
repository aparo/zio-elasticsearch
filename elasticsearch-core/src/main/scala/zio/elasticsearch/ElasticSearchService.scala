/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch

import zio._
import zio.elasticsearch.client._
import zio.elasticsearch.common._
import zio.elasticsearch.common.bulk.{ BulkActionRequest, BulkResponse, Bulker }
import zio.elasticsearch.common.create.{ CreateRequest, CreateResponse }
import zio.elasticsearch.common.delete.{ DeleteRequest, DeleteResponse }
import zio.elasticsearch.common.index.{ IndexRequest, IndexResponse }
import zio.elasticsearch.common.search._
import zio.elasticsearch.common.update.{ UpdateRequest, UpdateResponse }
import zio.elasticsearch.queries.Query
import zio.elasticsearch.sort.Sort
import zio.exception.FrameworkException
import zio.json._
import zio.json.ast._
import zio.schema.elasticsearch.annotations.{ CustomId, CustomIndex }
import zio.stream.{ ZSink, ZStream }
trait ElasticSearchService extends CommonManager {
  def httpService: ElasticSearchHttpService
  def config: ElasticSearchConfig

  def applicationName: String

  def dirty: Ref[Boolean]
//  lazy val dirty = Ref.make(false)

  //activate debug
  /* Managers */

  def concreteIndex(index: String): String = config.concreteIndexName(index)

  def concreteIndex(index: Option[String]): String =
    config.concreteIndexName(index.getOrElse("default"))

  def close(): ZIO[Any, FrameworkException, Unit] =
    for {
      blk <- this.bulker
      _ <- blk.close()
    } yield ()

  /* Sequence management */
  /* Get a new value for the id */
  def getSequenceValue(
    id: String,
    index: String = ElasticSearchConstants.SEQUENCE_INDEX
  ): ZIO[Any, FrameworkException, Option[Long]] =
    this.index(index, id = Some(id), body = Json.Obj()).map { x =>
      Option(x.version)
    }

  /* Reset the sequence for the id */
  def resetSequence(id: String): ZIO[Any, FrameworkException, Unit] =
    this.delete(ElasticSearchConstants.SEQUENCE_INDEX, id).unit

  def encodeBinary(data: Array[Byte]): String =
    new String(java.util.Base64.getMimeEncoder.encode(data))

  def decodeBinary(data: String): Array[Byte] =
    java.util.Base64.getMimeDecoder.decode(data)

  private[elasticsearch] lazy val bulker =
    Bulker(this, bulkSize = config.bulkSize)

  def addToBulk(
    action: IndexRequest
  ): ZIO[Any, FrameworkException, IndexResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield IndexResponse(
      index = action.index,
      id = action.id.getOrElse(""),
      version = 1
    )

  def addToBulk(
    action: CreateRequest
  ): ZIO[Any, FrameworkException, CreateResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield CreateResponse(
      index = action.index,
      id = action.id,
      version = 1
    )

  def addToBulk(
    action: DeleteRequest
  ): ZIO[Any, FrameworkException, DeleteResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield DeleteResponse(action.index, action.id)

  def addToBulk(
    action: UpdateRequest
  ): ZIO[Any, FrameworkException, UpdateResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield UpdateResponse(action.index, action.id)

//  def executeBulk(body: String, async: Boolean = false): ZIO[Any, FrameworkException, BulkResponse] =
//    if (body.nonEmpty) {
//      this.bulk(body)
//    } else ZIO.succeed(BulkResponse(took=0, errors = false, items=Chunk.empty))

  def makeBulker(
    bulkSize: Int = 500,
    flushInterval: zio.Duration = zio.Duration.fromSeconds(5),
    parallelExecutions: Int = 10
  ): ZIO[Any with Any with Scope,Nothing,Bulker] = ZIO.acquireRelease {
    for {
      bulker <- Bulker(
        this,
        bulkSize = bulkSize,
        flushInterval = flushInterval,
        parallelExecutions = parallelExecutions
      )
    } yield bulker
  } { bulker =>
    (for {
      _ <- ZIO.logDebug("Flushing sink bulker")
      _ <- bulker.flushBulk()
      _ <- ZIO.logDebug("Closing sink bulker")
      _ <- bulker.close()
      _ <- ZIO.logDebug("Closed sink bulker")
    } yield ()).ignore
  }

  def sink[T](
    index: String,
    bulker: Bulker,
    create: Boolean = false,
    idFunction: Option[IDFunction[T]] = None,
    indexFunction: Option[T => String] = None,
    pipeline: Option[String] = None,
    extra: Json.Obj = Json.Obj()
  )(implicit encoder: JsonEncoder[T]): ZSink[Any, FrameworkException, T, T, Long] = {

    val writer: ZSink[Any, FrameworkException, T, T, Unit] =
      ZSink.foreachChunk[Any, FrameworkException, T] { byteChunk =>
        val items = byteChunk.map(
          o =>
            buildIndexRequest(
              index = index,
              item = o,
              idFunction = idFunction.getOrElse { _: T =>
                None
              },
              indexFunction = indexFunction,
              create = create,
              pipeline = pipeline,
              extra = extra
            )
        )
        ZIO.foreachParDiscard(items)(i => bulker.add(i))

      }

    writer &> ZSink.count

  }

  def buildIndexRequest[T](
    index: String,
    item: T,
    idFunction: IDFunction[T] = IdFunctions.typeToNone[T],
    indexFunction: Option[T => String] = None,
    create: Boolean = false,
    pipeline: Option[String] = None,
    extra: Json.Obj = Json.Obj()
  )(
    implicit
    enc: JsonEncoder[T]
  ): IndexRequest = {
    val id = item match {
      case c: CustomId => Some(c.calcId())
      case _           => idFunction(item)
    }

    val realIndex = item match {
      case c: CustomIndex               => c.calcIndex()
      case _ if indexFunction.isDefined => indexFunction.get.apply(item)
      case _                            => index
    }

    IndexRequest(
      index = realIndex,
      body =
        if (extra.fields.nonEmpty)
          Json.Obj(item.toJsonAST.getOrElse(Json.Obj()).asInstanceOf[Json.Obj].fields ++ extra.fields)
        else item.toJsonAST.map(_.asInstanceOf[Json.Obj]).getOrElse(Json.Obj()),
      id = id,
      opType =
        if (create) OpType.create
        else OpType.index,
      pipeline = pipeline
    )
  }

  def bulkIndex[T](
    index: String,
    items: Chunk[T],
    idFunction: IDFunction[T] = IdFunctions.typeToNone[T],
    create: Boolean = false,
    pipeline: Option[String] = None,
    extra: Json.Obj = Json.Obj()
  )(
    implicit
    enc: JsonEncoder[T]
  ): ZIO[Any, FrameworkException, BulkResponse] =
    if (items.isEmpty) ZIO.succeed(BulkResponse.empty)
    else {
      this.bulk(
        body = items.map(
          i =>
            buildIndexRequest[T](
              index = index,
              item = i,
              idFunction = idFunction,
              create = create,
              pipeline = pipeline,
              extra = extra
            ).toBulkString
        )
      )
    }

  def bulkDelete[T](
    index: String,
    items: Chunk[T],
    idFunction: T => String
  ): ZIO[Any, FrameworkException, BulkResponse] =
    if (items.isEmpty) ZIO.succeed(BulkResponse.empty)
    else {
      this.bulk(
        body = items.map(
          i =>
            DeleteRequest(
              index = index,
              id = idFunction(i)
            ).toBulkString
        )
      )
    }

  def bulk(actions: Chunk[BulkActionRequest]): ZIO[Any, FrameworkException, BulkResponse] =
    if (actions.isEmpty)
      ZIO.succeed(BulkResponse.empty)
    else {
      this.bulk(
        body = actions.map(_.toBulkString)
      )
    }

  def bulkStream(
    actions: zio.stream.Stream[FrameworkException, BulkActionRequest],
    size: Int = 1000,
    parallel: Int = 5
  ): ZIO[Any, FrameworkException, Int] =
    actions.grouped(size).mapZIOPar(parallel)(b => bulk(b).as(b.length)).runSum

  private def processStep(
    state: StreamState
  ): ZIO[Any, FrameworkException, (Chunk[ResultDocument], Option[StreamState])] = {

    def getPit: ZIO[Any, FrameworkException, Option[String]] = if (!state.usePit) ZIO.succeed(None)
    else if (state.pit.isDefined) ZIO.succeed(state.pit)
    else
      for {
        result <- openPointInTime(keepAlive = state.keepAlive, indices = state.indices)
      } yield Some(result.id)

    for {
      pit <- getPit
      searchBody = SearchRequestBody(
        trackScores = Some(false),
        size = state.size,
        query = Some(state.query),
        sort = Some(state.sort),
        search_after = if (state.nextAfter.nonEmpty) Some(state.nextAfter) else None,
        pit = pit.map(p => PointInTimeReference(id = p, keepAlive = Some(state.keepAlive))),
        _source = if (state.sourceConfig.nonEmpty) Some(state.sourceConfig) else None
      )
      resp <- search(indices = state.indices, body = searchBody)
      _ <- ZIO.when(state.usePit && pit.nonEmpty && resp.hits.hits.length < state.size)(closePointInTime(pit.get))
    } yield (
      resp.hits.hits,
      if (resp.hits.hits.length < state.size) None
      else
        Some(
          state.copy(
            response = Some(resp),
            scrollId = resp.scrollId,
            nextAfter = resp.hits.hits.lastOption.map(_.sort).getOrElse(Chunk.empty[Json]),
            pit = pit
          )
        )
    )
  }

  def searchStream(
    indices: Chunk[String],
    sort: Sort.Sort,
    query: Query = Query.matchAllQuery,
    size: Int = 100,
    searchAfter: Chunk[Json] = Chunk.empty,
    keepAlive: String = "5m",
    sourceConfig: SourceConfig = SourceConfig.all
  ): ZStream[Any, FrameworkException, ResultDocument] = {
    val usePit = searchAfter.isEmpty && (sort.isEmpty || !sort.exists(s => s.isShardDoc))
    var finalSort = sort
    if (usePit) {
      if (sort.isEmpty) {
        finalSort = List(Sort.shardDoc)
      } else if (!finalSort.contains(Sort.shardDoc)) {
        finalSort = finalSort ::: Sort.shardDoc :: Nil
      }
    }
    ZStream
      .paginateZIO[Any, FrameworkException, Chunk[ResultDocument], StreamState](
        StreamState(
          indices = indices,
          query = query,
          sort = finalSort,
          size = size,
          nextAfter = searchAfter,
          usePit = usePit,
          keepAlive = keepAlive,
          sourceConfig = sourceConfig
        )
      )(
        processStep
      )
      .mapConcat(_.toList)
  }

  def searchStream(searchRequest: SearchRequest): ZStream[Any, FrameworkException, ResultDocument] =
    searchStream(
      indices = searchRequest.indices,
      sort = searchRequest.body.sort.getOrElse(Sort.EmptySort),
      query = searchRequest.body.query.getOrElse(Query.matchAllQuery),
      size = searchRequest.body.size,
      searchAfter = searchRequest.body.search_after.getOrElse(Chunk.empty[Json]),
      keepAlive = searchRequest.scroll.getOrElse("1m"),
      sourceConfig = searchRequest.body._source.getOrElse(SourceConfig.all)
    )

  def searchStreamTyped[R: JsonDecoder](
    searchRequest: SearchRequest
  ): zio.stream.Stream[FrameworkException, R] =
    searchStream(searchRequest).mapConcat { r =>
      r.source.flatMap(_.as[R].toOption)
    }

  def searchStreamTypedWIthId[R: JsonDecoder](
    searchRequest: SearchRequest
  ): zio.stream.Stream[FrameworkException, (String, R)] =
    searchStream(searchRequest).mapConcat { r =>
      r.source.flatMap(_.as[R].toOption).map(v => r.id -> v)
    }

  def searchStreamIdField[R: JsonDecoder](
    searchRequest: SearchRequest,
    field: String
  ): zio.stream.Stream[FrameworkException, (String, R)] =
    searchStream(searchRequest).mapConcat { r =>
      val id = r.id
      ResultDocument.getValues[R](field, r).map(v => id -> v)
    }

  def searchStreamFields(searchRequest: SearchRequest): zio.stream.Stream[FrameworkException, Json.Obj] =
    searchStream(searchRequest).map(_.source.getOrElse(Json.Obj()))

  def searchStreamField[R: JsonDecoder](
    searchRequest: SearchRequest,
    field: String
  ): zio.stream.Stream[FrameworkException, R] =
    searchStream(searchRequest).mapConcat { r =>
      ResultDocument.getValues[R](field, r)
    }

  def searchStreamField[R1: JsonDecoder, R2: JsonDecoder](
    searchRequest: SearchRequest,
    field1: String,
    field2: String
  ): zio.stream.Stream[FrameworkException, (R1, R2)] =
    searchStream(searchRequest).mapConcat { record =>
      for {
        v1 <- ResultDocument.getValues[R1](field1, record)
        v2 <- ResultDocument.getValues[R2](field2, record)
      } yield (v1, v2)
    }

  def searchStreamField[R1: JsonDecoder, R2: JsonDecoder, R3: JsonDecoder](
    searchRequest: SearchRequest,
    field1: String,
    field2: String,
    field3: String
  ): zio.stream.Stream[FrameworkException, (R1, R2, R3)] =
    searchStream(searchRequest).mapConcat { record =>
      for {
        v1 <- ResultDocument.getValues[R1](field1, record)
        v2 <- ResultDocument.getValues[R2](field2, record)
        v3 <- ResultDocument.getValues[R3](field3, record)
      } yield (v1, v2, v3)
    }

  def searchStreamField[R1: JsonDecoder, R2: JsonDecoder, R3: JsonDecoder, R4: JsonDecoder](
    searchRequest: SearchRequest,
    field1: String,
    field2: String,
    field3: String,
    field4: String
  ): zio.stream.Stream[FrameworkException, (R1, R2, R3, R4)] =
    searchStream(searchRequest).mapConcat { record =>
      for {
        v1 <- ResultDocument.getValues[R1](field1, record)
        v2 <- ResultDocument.getValues[R2](field2, record)
        v3 <- ResultDocument.getValues[R3](field3, record)
        v4 <- ResultDocument.getValues[R4](field4, record)
      } yield (v1, v2, v3, v4)
    }

}
object ElasticSearchService {

  // services

  val live: ZLayer[ElasticSearchHttpService, Nothing, ElasticSearchService] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
        dirtyBulk <- Ref.make(false)
      } yield new ElasticSearchService {
        override def httpService: ElasticSearchHttpService = httpServiceBase

        override def config: ElasticSearchConfig = httpService.elasticSearchConfig

        override def applicationName: DateMath = httpService.elasticSearchConfig.applicationName.getOrElse("default")

        override def dirty: Ref[TrackHits] = dirtyBulk

      }
    }

}
