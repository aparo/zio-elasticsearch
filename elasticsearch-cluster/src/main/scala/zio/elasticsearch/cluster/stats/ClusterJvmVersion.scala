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

package zio.elasticsearch.cluster.stats
import zio.json._
final case class ClusterJvmVersion(
  @jsonField("bundled_jdk") bundledJdk: Boolean,
  count: Int,
  @jsonField("using_bundled_jdk") usingBundledJdk: Boolean,
  version: String,
  @jsonField("vm_name") vmName: String,
  @jsonField("vm_vendor") vmVendor: String,
  @jsonField("vm_version") vmVersion: String
)

object ClusterJvmVersion {
  implicit lazy val jsonCodec: JsonCodec[ClusterJvmVersion] =
    DeriveJsonCodec.gen[ClusterJvmVersion]
}
