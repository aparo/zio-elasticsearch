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

import elasticsearch.client._
import zio.exception.FrameworkException
import elasticsearch.requests.{ ActionRequest, BulkActionRequest, DeleteRequest, IndexRequest, UpdateRequest }
import elasticsearch.responses._
import io.circe._
import io.circe.syntax._
import zio._
import zio.auth.AuthContext
import zio.logging.{ LogLevel, Logging }
import cats.implicits._
import scala.concurrent.duration._
import scala.util.Random

// scalastyle:off
object BaseElasticSearchService {
  type BaseElasticSearchService = Has[Service]

  trait Service
      extends ExtendedClientManagerTrait
      with ClientActions
      with IndexResolverTrait
      with ClientActionResolver {
    def loggingService: Logging.Service
    def logDebug(s: => String): UIO[Unit] = loggingService.logger.log(LogLevel.Debug)(s)

    def bulkSize: Int
    def applicationName: String

    //activate debug
    /* Managers */

    lazy val dirty = Ref.make(false)

    var defaultTimeout = 1000.seconds
    var creationSleep = 500L
    var connectionLimits = 10
    var maxRetries = 2
    var ignoreLinkInBulk = false
    protected var innerBulkSize: Int = bulkSize
    var maxConcurrentBulk = 10
    var bulkMemMaxSize: Int = 1024 * 1024
    var bulkTimeout = 15.minutes
    //we manage dirty states
    lazy val hosts: Seq[String] = servers.map(_.httpUrl(useSSL))

    def getHost: String = Random.shuffle(hosts).head

    //
    // HTTP Management
    //
    def useSSL: Boolean

    def doCall(
      method: String,
      url: String,
      body: Option[String],
      queryArgs: Map[String, String]
    ): ZioResponse[ESResponse]

    override def convertResponse[T: Encoder: Decoder](request: ActionRequest)(
      eitherResponse: Either[FrameworkException, ESResponse]
    ): Either[FrameworkException, T] =
      for {
        resp <- eitherResponse
        json <- resp.json.leftMap(e => FrameworkException(e))
        res <- json.as[T].leftMap(e => FrameworkException(e))
      } yield res

    override def concreteIndex(index: String): String = index

    override def concreteIndex(index: Option[String]): String =
      index.getOrElse("default")

    def doCall(
      method: String,
      url: String
    ): ZioResponse[ESResponse] =
      doCall(method, url, None, Map.empty[String, String])

    def close(): ZioResponse[Unit] =
      for {
        blk <- this.bulker
        _ <- blk.close()
      } yield ()

    override def doCall(
      request: ActionRequest
    ): ZioResponse[ESResponse] =
      doCall(
        method = request.method,
        url = request.urlPath,
        body = bodyAsString(request.body),
        queryArgs = request.queryArgs
      )

    /* Sequence management */
    /* Get a new value for the id */
    def getSequenceValue(
      id: String,
      index: String = ElasticSearchConstants.SEQUENCE_INDEX,
      docType: String = "sequence"
    )(
      implicit authContext: AuthContext
    ): ZioResponse[Option[Long]] =
      this
        .indexDocument(index, id = Some(id), body = JsonObject.empty)(
          authContext.systemNoSQLContext()
        )
        .map { x =>
          Option(x.version)
        }

    /* Reset the sequence for the id */
    def resetSequence(id: String)(implicit authContext: AuthContext): ZioResponse[Unit] =
      this
        .delete(ElasticSearchConstants.SEQUENCE_INDEX, id)(
          authContext.systemNoSQLContext()
        )
        .map { _ =>
          ()
        }

    def encodeBinary(data: Array[Byte]): String =
      new String(java.util.Base64.getMimeEncoder.encode(data))

    def decodeBinary(data: String): Array[Byte] =
      java.util.Base64.getMimeDecoder.decode(data)

    protected var bulkerStarted: Boolean = false

    private[elasticsearch] lazy val bulker =
      Bulker(this, loggingService, bulkSize = this.innerBulkSize)

    def addToBulk(
      action: IndexRequest
    ): ZioResponse[IndexResponse] =
      for {
        blkr <- bulker
        _ <- blkr.add(action)
      } yield IndexResponse(
        index = action.index,
        id = action.id.getOrElse(""),
        version = 1
      )

    def addToBulk(
      action: DeleteRequest
    ): ZioResponse[DeleteResponse] =
      for {
        blkr <- bulker
        _ <- blkr.add(action)
      } yield DeleteResponse(action.index, action.id)

    def addToBulk(
      action: UpdateRequest
    ): ZioResponse[UpdateResponse] =
      for {
        blkr <- bulker
        _ <- blkr.add(action)
      } yield UpdateResponse(action.index, action.id)

    def executeBulk(body: String, async: Boolean = false): ZioResponse[BulkResponse] =
      if (body.nonEmpty) {
        this.bulk(body)
      } else ZIO.succeed(BulkResponse(0, false, Nil))

    def bulkIndex[T](index: String, items: Seq[T], idFunction: T => Option[String] = { t: T =>
      None
    }, create: Boolean = false)(implicit enc: Encoder.AsObject[T]): ZioResponse[BulkResponse] =
      if (items.isEmpty) ZIO.succeed(BulkResponse(0, false, Nil))
      else {
        this.bulk(
          body = items
            .map(
              i =>
                IndexRequest(
                  index = index,
                  body = i.asJsonObject,
                  id = idFunction(i),
                  opType =
                    if (create) OpType.create
                    else OpType.index
                ).toBulkString
            )
            .mkString
        )
      }

    def bulkDelete[T](index: String, items: Seq[T], idFunction: T => String): ZioResponse[BulkResponse] =
      if (items.isEmpty) ZIO.succeed(BulkResponse(0, false, Nil))
      else {
        this.bulk(
          body = items
            .map(
              i =>
                DeleteRequest(
                  index = index,
                  id = idFunction(i)
                ).toBulkString
            )
            .mkString("\n")
        )
      }

    def bulk(actions: Seq[BulkActionRequest]): ZioResponse[BulkResponse] =
      if (actions.isEmpty)
        ZIO.succeed(BulkResponse(0, false, Nil))
      else {
        this.bulk(
          body = actions.map(_.toBulkString).mkString
        )
      }

    def bulkStream(
      actions: zio.stream.Stream[FrameworkException, BulkActionRequest],
      size: Long = 1000
    ): ZioResponse[Unit] = actions.grouped(size).foreach(b => bulk(b))

  }
// scalastyle:on
}
