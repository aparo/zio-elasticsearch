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
import elasticsearch.exception.FrameworkException
import elasticsearch.requests.{
  BulkActionRequest,
  DeleteRequest,
  IndexRequest,
  UpdateRequest
}
import elasticsearch.responses._
import io.circe._
import io.circe.syntax._
import izumi.logstage.api.IzLogger
import zio.{Ref, ZIO}

import scala.concurrent.duration._

// scalastyle:off

trait BaseElasticSearchSupport
    extends ExtendedClientManagerTrait
    with ClientActions
    with IndexResolverTrait {
  implicit def logger: IzLogger
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

  def hosts: Seq[String]

  def close(): ZioResponse[Unit]

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
  def resetSequence(id: String)(
      implicit authContext: AuthContext): ZioResponse[Unit] =
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

  protected lazy val bulker =
    Bulker(this, logger, bulkSize = this.innerBulkSize)

  def addToBulk(
      action: IndexRequest
  ): ZioResponse[IndexResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield
      IndexResponse(
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

  def executeBulk(body: String,
                  async: Boolean = false): ZioResponse[BulkResponse] =
    if (body.nonEmpty) {
      this.bulk(body)
    } else ZIO.succeed(BulkResponse(0, false, Nil))

  def bulkIndex[T](index: String,
                   items: Seq[T],
                   idFunction: T => Option[String] = { _ =>
                     None
                   },
                   create: Boolean = false)(
      implicit enc: Encoder[T]): ZioResponse[BulkResponse] =
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
          .mkString("\n")
      )
    }

  def bulkDelete[T](index: String,
                    items: Seq[T],
                    idFunction: T => String): ZioResponse[BulkResponse] =
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
        body = actions.map(_.toBulkString).mkString("\n")
      )
    }

  def bulkStream(
      actions: zio.stream.Stream[FrameworkException, BulkActionRequest],
      size: Int = 1000
  ): ZioResponse[Unit] = actions.grouped(size).foreach(b => bulk(b))

}
// scalastyle:on
