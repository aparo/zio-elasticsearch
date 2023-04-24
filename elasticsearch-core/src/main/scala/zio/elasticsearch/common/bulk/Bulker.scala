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

package zio.elasticsearch.common.bulk

import zio._
import zio.elasticsearch.{ ElasticSearchService }
import zio.exception.FrameworkException

class Bulker(
  client: ElasticSearchService,
  val bulkSize: Int,
  flushInterval: Duration = 5.seconds,
  requests: Queue[BulkActionRequest],
  counter: Ref[Int]
) {

  def run(): ZIO[Any, FrameworkException, Unit] =
    ZIO.logDebug(s"Starter Bulker") *>
      processIfNotEmpty.repeat(Schedule.forever.addDelay(_ => flushInterval)).unit

  def processIfNotEmpty: ZIO[Any, FrameworkException, Unit] =
    for {
      s <- requests.size
      _ <- ZIO.when(s > 0)(processRequests)
    } yield ()

  def add(request: BulkActionRequest): ZIO[Any, FrameworkException, Unit] =
    requests.offer(request) *> ZIO.whenZIO {
      for {
        s <- requests.size
      } yield s >= bulkSize
    }(processRequests).unit

  private def runBulk(items: Chunk[BulkActionRequest]): ZIO[Any, FrameworkException, BulkResponse] = for {
    _ <- ZIO.logDebug(s"Executing bulk with ${items.length} items")
    res <- client.bulk(body = items.map(_.toBulkString))
  } yield res
  //TODO check errors

  private val processRequests: ZIO[Any, FrameworkException, Unit] =
    for {
      s <- requests.size
      counterStart <- counter.get
      _ <- ZIO.logDebug(s"Process bulker start $bulkSize (queue:$s counter$counterStart)")
      items <- requests.takeUpTo(bulkSize)
      _ <- counter.update(_ + items.length)
      _ <- ZIO.when(items.nonEmpty)(runBulk(items))
      sEnd <- requests.size
      counterEnd <- counter.get
      _ <- ZIO.logDebug(s"Process bulker end $bulkSize (queue:$sEnd counter$counterEnd)")
    } yield ()

  def flushBulk(): ZIO[Any, FrameworkException, Unit] =
    waitForEmpty(requests, 0).unit
  //    if (sinkerInited)
//      sinker.forceIndex()

//  def waitForEmpty[A](queue: Queue[A], size: Int): UIO[Int] =
//    (queue.size <* clock.sleep(10.millis)).repeat(ZSchedule.doWhile(_ != size)).provide(Clock.Live)

  def waitForEmpty[A](queue: Queue[A], size: Int): IO[FrameworkException, Int] =
    (processIfNotEmpty *> queue.size <* Clock.sleep(10.millis)).repeat(Schedule.recurWhile(_ != size))

  def close(): ZIO[Any, FrameworkException, Unit] =
    for {
      size <- requests.size
      _ <- ZIO.when(size > 0) {
        for {
          p <- Promise.make[Nothing, Boolean]
          _ <- (requests.awaitShutdown *> (waitForEmpty(requests, 0) *> p.succeed(true))).fork
          _ <- p.await
        } yield ()
      }
      _ <- requests.shutdown
    } yield ()

}

object Bulker {
  def apply(
    client: ElasticSearchService,
    bulkSize: Int,
    flushInterval: Duration = 5.seconds,
    parallelExecutions: Int = 10
  ) =
    for {
      queue <- Queue.bounded[BulkActionRequest](bulkSize * parallelExecutions)
      counter <- Ref.make(0)
      blk = new Bulker(client, bulkSize, flushInterval = flushInterval, requests = queue, counter = counter)
      _ <- blk.run().fork
    } yield blk

}
