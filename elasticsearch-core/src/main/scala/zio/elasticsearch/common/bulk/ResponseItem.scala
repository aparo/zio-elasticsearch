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

package zio.elasticsearch.common.bulk
import zio.elasticsearch.common._
import zio.json._
import zio.json.ast._
final case class ResponseItem(
  @jsonField("_id") id: Option[String] = None,
  @jsonField("_index") index: String,
  status: Int,
  error: Option[ErrorCause] = None,
  @jsonField("_primary_term") primaryTerm: Option[Long] = None,
  result: Option[String] = None,
  @jsonField("_seq_no") seqNo: Option[Int] = None,
  @jsonField("_shards") shards: Option[ShardStatistics] = None,
  @jsonField("_version") version: Option[Int] = None,
  @jsonField("forced_refresh") forcedRefresh: Option[Boolean] = None,
  get: Option[TDocument] = None
)

object ResponseItem {
  implicit lazy val jsonCodec: JsonCodec[ResponseItem] =
    DeriveJsonCodec.gen[ResponseItem]
}
