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

package zio.elasticsearch.indices.rollover
import zio.json._
import zio.json.ast._
final case class RolloverConditions(
  @jsonField("min_age") minAge: Option[String] = None,
  @jsonField("max_age") maxAge: Option[String] = None,
  @jsonField("max_age_millis") maxAgeMillis: Option[Long] = None,
  @jsonField("min_docs") minDocs: Option[Long] = None,
  @jsonField("max_docs") maxDocs: Option[Long] = None,
  @jsonField("max_size") maxSize: Option[String] = None,
  @jsonField("max_size_bytes") maxSizeBytes: Option[Long] = None,
  @jsonField("min_size") minSize: Option[String] = None,
  @jsonField("min_size_bytes") minSizeBytes: Option[Long] = None,
  @jsonField("max_primary_shard_size") maxPrimaryShardSize: Option[String] = None,
  @jsonField("max_primary_shard_size_bytes") maxPrimaryShardSizeBytes: Option[
    Long
  ] = None,
  @jsonField("min_primary_shard_size") minPrimaryShardSize: Option[String] = None,
  @jsonField("min_primary_shard_size_bytes") minPrimaryShardSizeBytes: Option[
    Long
  ] = None,
  @jsonField("max_primary_shard_docs") maxPrimaryShardDocs: Option[Long] = None,
  @jsonField("min_primary_shard_docs") minPrimaryShardDocs: Option[Long] = None
)

object RolloverConditions {
  implicit lazy val jsonCodec: JsonCodec[RolloverConditions] =
    DeriveJsonCodec.gen[RolloverConditions]
}
