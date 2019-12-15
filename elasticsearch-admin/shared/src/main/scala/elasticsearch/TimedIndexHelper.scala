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

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import zio.ZIO

object TimedIndexHelper {
  val lastest = "logstash-latest"
  val lastWeek = "logstash-last-week"
  val lastMonth = "logstash-last-month"
  val fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd")
  val fmtMonth = DateTimeFormatter.ofPattern("yyyyMM")

  //return today data index
  def getLogstashIndex = {
    val date = OffsetDateTime.now()
    s"logstash-${fmt.format(date)}"
  }

  def getLogstashIndex(date: OffsetDateTime) = s"logstash-${fmt.format(date)}"

  def getMonthIndex(prefix: String, date: OffsetDateTime) =
    s"$prefix-${fmt.format(date)}"

  def updateTimedAlias(
    client: BaseElasticSearchSupport with ClusterSupport,
    datastores: List[String]
  ): ZioResponse[Unit] = {
    def processDatastore(name: String, indices: Map[String, List[String]]): ZioResponse[Unit] = {
      val prefix = name.stripSuffix("-")
      val names =
        indices.filter(_._1.startsWith(name)).toList.sortBy(_._1)
      val toAdd =
        names.filterNot(_._2.contains(prefix)).map(_._1)
      client.indices.addAlias(prefix, toAdd).when(toAdd.nonEmpty)
    }

    for {
      indices <- client.getIndicesAlias()
      _ <- ZIO.foreach(datastores)(n => processDatastore(n, indices))
    } yield ()
  }

//  def updateLogStashAlias(client: ElasticSearch): Unit = {
//
//    val indices = client.awaitResult(client.getIndicesAlias())
//    val logs = indices.filter(_._1.startsWith("logstash")).toList.sortBy(_._1)
//
//    List((TimedIndexHelper.lastest, 1), (lastWeek, 7), (lastMonth, 30)).foreach {
//      case (alias, rangeSize) =>
//        val toDelete =
//          logs.dropRight(rangeSize).filter(_._2.contains(alias)).map(_._1)
//        if (toDelete.nonEmpty) {
//          val r = client.indices.deleteAlias(alias, toDelete)
//          client.awaitResult(r.value)
//        }
//
//        val toAdd =
//          logs.takeRight(rangeSize).filterNot(_._2.contains(alias)).map(_._1)
//        if (toAdd.nonEmpty) {
//          val r =
//            client.indices.addAlias(alias, logs.takeRight(rangeSize).map(_._1))
//          client.awaitResult(r.value)
//        }
//    }
//
//    //clean obsolete marvel,
//    val marvels =
//      indices.filter(_._1.startsWith(".marvel")).toList.sortBy(_._1)
//    if (marvels.length > 10) {
//      marvels.dropRight(7).foreach { marverlIndex =>
//        logger.info(s"Removing ${marverlIndex._1}")
//        client.awaitResult(client.indices.delete(marverlIndex._1).value)
//      }
//    }
//
//    //close older indices,
//    //    val logstashs=indices.filter(_._1.startsWith("logstash-")).toList.sortBy(_._1)
//    //    if(logstashs.length>32){
//    //      logstashs.dropRight(32).foreach{
//    //        logstashIndex =>
//    //          logger.info(s"Deleting ${logstashIndex._1}")
//    //          client.awaitResult(client.indices.delete(logstashIndex._1))
//    //      }
//    //
//    //    }
//
//  }

}
