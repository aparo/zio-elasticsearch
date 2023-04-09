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

package zio.elasticsearch.snapshot.requests
import zio.elasticsearch.snapshot.{ Repository, RepositorySettings }
import zio.json._
import zio.json.ast._

final case class CreateRepositoryRequestBody(
  repository: Option[Repository] = None,
  @jsonField("type") `type`: String,
  settings: RepositorySettings
)

object CreateRepositoryRequestBody {
  implicit val jsonCodec: JsonCodec[CreateRepositoryRequestBody] =
    DeriveJsonCodec.gen[CreateRepositoryRequestBody]
}
