/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import com.github.mlangc.slf4zio.api._
import elasticsearch.exception.FrameworkException
import elasticsearch.requests.BulkActionRequest
import elasticsearch.{ ElasticSearch, ZioResponse }
import zio.clock.Clock
import zio.duration._
import zio._

class Bulker(
  client: ElasticSearch,
  val bulkSize: Int,
  flushInterval: Duration = 5.seconds,
  requests: Queue[BulkActionRequest]
) extends LoggingSupport {

  def run() =
    processIfNotEmpty.repeat(Schedule.spaced(flushInterval)).unit.provide(Clock.Live)

  def processIfNotEmpty = for {
    s <- requests.size
    _ <- processRequests.when(s > 0)
  } yield ()

  def add(request: BulkActionRequest): ZioResponse[Unit] =
    requests.offer(request) *> processRequests.whenM {
      for {
        s <- requests.size
      } yield s >= bulkSize
    }

  private def runBulk(items: List[BulkActionRequest]) =
    client.bulk(body = items.map(_.toBulkString).mkString("\n"))
  //TODO check errors

  private val processRequests: ZioResponse[Unit] =
    (for {
      items <- requests.takeUpTo(bulkSize)
      _ <- runBulk(items)
    } yield ()).forever.unit

  def flushBulk(): ZioResponse[Unit] =
    waitForEmpty(requests, 0).unit
  //    if (sinkerInited)
//      sinker.forceIndex()

//  def waitForEmpty[A](queue: Queue[A], size: Int): UIO[Int] =
//    (queue.size <* clock.sleep(10.millis)).repeat(ZSchedule.doWhile(_ != size)).provide(Clock.Live)

  def waitForEmpty[A](queue: Queue[A], size: Int): IO[FrameworkException, Int] =
    (processIfNotEmpty *> queue.size <* clock.sleep(10.millis)).repeat(ZSchedule.doWhile(_ != size)).provide(Clock.Live)

  def close(): ZioResponse[Boolean] =
    for {
      p <- Promise.make[Nothing, Boolean]
      _ <- (requests.awaitShutdown *> (waitForEmpty(requests, 0) *> p.succeed(true))).fork
      res <- p.await
      _ <- requests.shutdown
    } yield res

}

object Bulker {
  def apply(client: ElasticSearch, bulkSize: Int, flushInterval: Duration = 5.seconds) =
    for {
      queue <- Queue.bounded[BulkActionRequest](bulkSize * 10)
      blk = new Bulker(client, bulkSize, flushInterval = flushInterval, requests = queue)
      _ <- blk.run()
    } yield blk

}
