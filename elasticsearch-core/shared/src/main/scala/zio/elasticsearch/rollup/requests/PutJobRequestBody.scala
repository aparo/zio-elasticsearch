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

package zio.elasticsearch.rollup.requests
import zio._
import zio.elasticsearch.rollup.{ FieldMetric, Groupings }
import zio.json._
import zio.json.ast._

final case class PutJobRequestBody(
  cron: String,
  groups: Groupings,
  @jsonField("index_pattern") indexPattern: String,
  metrics: Option[Chunk[FieldMetric]] = None,
  @jsonField("page_size") pageSize: Int,
  @jsonField("rollup_index") rollupIndex: String,
  timeout: Option[String] = None,
  headers: Option[Json] = None
)

object PutJobRequestBody {
  implicit val jsonCodec: JsonCodec[PutJobRequestBody] =
    DeriveJsonCodec.gen[PutJobRequestBody]
}
