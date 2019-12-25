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
import elasticsearch.managers.IndicesManager
import elasticsearch.responses.indices._

trait IndicesSupport
    extends IndicesActionResolver
    with BaseElasticSearchSupport {
  lazy val indices = new IndicesManager(this)
  var defaultSettings = Settings.ElasticSearchBase
  var defaultTestSettings = Settings.ElasticSearchTestBase
  //default settings to build index
  //  var defaultIndex = "default"
  var alias = Set.empty[String]

  def refresh(): ZioResponse[Unit] =
    for {
      _ <- flushBulk(false)
      _ <- indices.refresh()
      dir <- dirty
      _ <- dir.set(false)
    } yield ()

  def exists(
      indices: String*
  ): ZioResponse[IndicesExistsResponse] =
    this.indices.exists(indices)

  def flush(
      indices: String*
  ): ZioResponse[IndicesFlushResponse] =
    this.indices.flush(indices)

  def refresh(
      indices: String*
  ): ZioResponse[IndicesRefreshResponse] =
    this.indices.refresh(indices)

  def open(
      index: String
  ): ZioResponse[IndicesOpenResponse] =
    this.indices.open(index)

  def flushBulk(
      async: Boolean = false
  ): ZioResponse[IndicesFlushResponse] =
    for {
      blkr <- bulker
      _ <- blkr.flushBulk()
    } yield IndicesFlushResponse()

}
