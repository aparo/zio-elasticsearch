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

package zio.elasticsearch.cat.nodeattrs
import zio.json._
final case class NodeAttributesRecord(
  node: Option[String] = None,
  id: Option[String] = None,
  pid: Option[String] = None,
  host: Option[String] = None,
  ip: Option[String] = None,
  port: Option[String] = None,
  attr: Option[String] = None,
  value: Option[String] = None
)

object NodeAttributesRecord {
  implicit lazy val jsonCodec: JsonCodec[NodeAttributesRecord] =
    DeriveJsonCodec.gen[NodeAttributesRecord]
}
