/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.client._
import elasticsearch.requests.{ DeleteRequest, IndexRequest, UpdateRequest }
import elasticsearch.responses._
import io.circe._
import izumi.logstage.api.IzLogger
import zio.{ Ref, ZIO }

import scala.concurrent.duration._

// scalastyle:off

trait BaseElasticSearchSupport extends ExtendedClientManagerTrait with ClientActions with IndexResolverTrait {
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
    implicit qContext: ESNoSqlContext
  ): ZioResponse[Option[Long]] =
    this
      .indexDocument(index, id = Some(id), body = JsonObject.empty)(
        qContext.systemNoSQLContext()
      )
      .map { x =>
        Option(x.version)
      }

  /* Reset the sequence for the id */
  def resetSequence(id: String)(implicit qContext: ESNoSqlContext): ZioResponse[Unit] =
    this
      .delete(ElasticSearchConstants.SEQUENCE_INDEX, id)(
        qContext.systemNoSQLContext()
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

}
// scalastyle:on
