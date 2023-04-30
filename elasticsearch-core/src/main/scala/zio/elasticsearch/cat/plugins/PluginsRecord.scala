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

package zio.elasticsearch.cat.plugins
import zio.json._
final case class PluginsRecord(
  id: Option[String] = None,
  name: Option[String] = None,
  component: Option[String] = None,
  version: Option[String] = None,
  description: Option[String] = None,
  @jsonField("type") `type`: Option[String] = None
)

object PluginsRecord {
  implicit lazy val jsonCodec: JsonCodec[PluginsRecord] =
    DeriveJsonCodec.gen[PluginsRecord]
}
