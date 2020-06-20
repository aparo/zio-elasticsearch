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

package elasticsearch

import elasticsearch.ElasticSearch.ElasticSearch
import elasticsearch.client.ZioHTTP4SClient
import zio.ZLayer
import zio.logging.slf4j.Slf4jLogger
import zio.logging.LogAnnotation._
import zio.blocking.Blocking

trait EmbeededElasticSearchSupport {

  val logLayer = Slf4jLogger.makeWithAnnotationsAsMdc(List(Level, Name, Throwable))

  lazy val esLayer = {
    val esEmbedded: ZLayer[Any, Throwable, ElasticSearch] = EmbeddedClusterService.embedded
    ZioHTTP4SClient.buildFromElasticsearch(logLayer, esEmbedded)
  }
}
