/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.client.ClusterActionResolver
import elasticsearch.managers.ClusterManager
import zio.ZIO

trait ClusterSupport extends ClusterActionResolver with IndicesSupport {
  lazy val cluster = new ClusterManager(this)

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

}
