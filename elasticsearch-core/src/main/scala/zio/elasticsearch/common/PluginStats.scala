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

package zio.elasticsearch.common
import zio._
import zio.json._
final case class PluginStats(
  classname: String,
  description: String,
  @jsonField("elasticsearch_version") elasticsearchVersion: String,
  @jsonField("extended_plugins") extendedPlugins: Chunk[String],
  @jsonField("has_native_controller") hasNativeController: Boolean,
  @jsonField("java_version") javaVersion: String,
  name: String,
  version: String,
  licensed: Boolean
)

object PluginStats {
  implicit lazy val jsonCodec: JsonCodec[PluginStats] =
    DeriveJsonCodec.gen[PluginStats]
}
