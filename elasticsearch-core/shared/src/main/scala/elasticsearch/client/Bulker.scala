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

package elasticsearch.client

import elasticsearch.requests.BulkActionRequest
import elasticsearch.{ElasticSearchService, ZioResponse}
import zio._
import zio.clock.Clock
import zio.duration._
import zio.exception.FrameworkException
import zio.logging.Logging

class Bulker(
    client: ElasticSearchService.Service,
    loggingService: Logging.Service,
    val bulkSize: Int,
    flushInterval: Duration = 5.seconds,
    requests: Queue[BulkActionRequest]
) {

  def run() =
    processIfNotEmpty
      .repeat(Schedule.forever.addDelay(_ => flushInterval))
      .unit
      .provideLayer(Clock.live)

  def processIfNotEmpty =
    for {
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

  def waitForEmpty[A](queue: Queue[A],
                      size: Int): IO[FrameworkException, Int] =
    (processIfNotEmpty *> queue.size <* clock.sleep(10.millis))
      .repeat(Schedule.doWhile(_ != size))
      .provideLayer(Clock.live)

  def close(): ZioResponse[Boolean] =
    for {
      p <- Promise.make[Nothing, Boolean]
      _ <- (requests.awaitShutdown *> (waitForEmpty(requests, 0) *> p.succeed(
        true))).fork
      res <- p.await
      _ <- requests.shutdown
    } yield res

}

object Bulker {
  def apply(
      client: ElasticSearchService.Service,
      loggingService: Logging.Service,
      bulkSize: Int,
      flushInterval: Duration = 5.seconds
  ) =
    for {
      queue <- Queue.bounded[BulkActionRequest](bulkSize * 10)
      blk = new Bulker(client,
                       loggingService,
                       bulkSize,
                       flushInterval = flushInterval,
                       requests = queue)
      _ <- blk.run()
    } yield blk

}
