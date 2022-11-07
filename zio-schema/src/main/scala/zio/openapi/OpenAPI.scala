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

package zio.openapi

import scala.collection.immutable.ListMap

import cats.implicits._
import zio.json._

@jsonDerive
final case class OpenAPI(
  openapi: String = "3.0.3",
  info: Info,
  tags: List[Tag] = Nil,
  servers: List[Server] = Nil,
  paths: ListMap[String, PathItem] = ListMap.empty,
  components: Option[Components] = None,
  security: List[SecurityRequirement] = Nil
) {

  def addPathItem(path: String, pathItem: PathItem): OpenAPI = {
    val pathItem2 = paths.get(path) match {
      case None           => pathItem
      case Some(existing) => existing.mergeWith(pathItem)
    }

    copy(paths = paths + (path -> pathItem2))
  }

  def setServers(s: List[Server]): OpenAPI = copy(servers = s)

  def setTags(t: List[Tag]): OpenAPI = copy(tags = t)

}

object OpenAPI {
  def fromTraits(info: Info, openAPITraits: OpenAPITrait*): OpenAPI = {
    val tags = openAPITraits.flatMap(_.tags).distinct.sortBy(_.name).toList
    val definitions = ListMap(
      openAPITraits.flatMap(_.definitions).distinct: _*
    )
    val paths: ListMap[String, PathItem] = ListMap(
      openAPITraits.flatMap(_.paths): _*
    )

    OpenAPI(
      info = info,
      tags = tags,
      components = Some(
        Components(
          definitions,
          securitySchemes = ListMap(
            SecurityScheme.BEARER -> Right(SecurityScheme.bearer),
            SecurityScheme.APIKEY -> Right(SecurityScheme.apiKey)
          )
        )
      ),
      paths = paths
    )
  }
}
