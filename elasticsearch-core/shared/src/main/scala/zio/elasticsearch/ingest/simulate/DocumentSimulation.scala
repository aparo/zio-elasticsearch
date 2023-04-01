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

package zio.elasticsearch.ingest.simulate
import zio.json._
import zio.json.ast._
import zio.elasticsearch.common._

final case class DocumentSimulation(
  @jsonField("_id") id: String,
  @jsonField("_index") index: String,
  @jsonField("_ingest") ingest: Ingest,
  @jsonField("_routing") routing: Option[String] = None,
  @jsonField("_source") source: Map[String, Json],
  @jsonField("_version") version: Option[Int] = None,
  @jsonField("_version_type") versionType: Option[VersionType] = None
)

object DocumentSimulation {
  implicit val jsonCodec: JsonCodec[DocumentSimulation] =
    DeriveJsonCodec.gen[DocumentSimulation]
}
