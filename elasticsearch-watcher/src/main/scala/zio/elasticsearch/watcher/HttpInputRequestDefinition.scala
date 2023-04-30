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

package zio.elasticsearch.watcher
import zio.json._
final case class HttpInputRequestDefinition(
  auth: Option[HttpInputAuthentication] = None,
  body: Option[String] = None,
  @jsonField("connection_timeout") connectionTimeout: Option[String] = None,
  headers: Option[Map[String, String]] = None,
  host: Option[String] = None,
  method: Option[HttpInputMethod] = None,
  params: Option[Map[String, String]] = None,
  path: Option[String] = None,
  port: Option[Int] = None,
  proxy: Option[HttpInputProxy] = None,
  @jsonField("read_timeout") readTimeout: Option[String] = None,
  scheme: Option[ConnectionScheme] = None,
  url: Option[String] = None
)

object HttpInputRequestDefinition {
  implicit lazy val jsonCodec: JsonCodec[HttpInputRequestDefinition] =
    DeriveJsonCodec.gen[HttpInputRequestDefinition]
}
