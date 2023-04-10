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

package zio.elasticsearch.cat.ml_datafeeds
import zio.elasticsearch.ml.DatafeedState
import zio.json._

final case class DatafeedsRecord(
  id: Option[String] = None,
  state: Option[DatafeedState] = None,
  @jsonField("assignment_explanation") assignmentExplanation: Option[String] = None,
  @jsonField("buckets.count") `buckets.count`: Option[String] = None,
  @jsonField("search.count") `search.count`: Option[String] = None,
  @jsonField("search.time") `search.time`: Option[String] = None,
  @jsonField("search.bucket_avg") `search.bucketAvg`: Option[String] = None,
  @jsonField("search.exp_avg_hour") `search.expAvgHour`: Option[String] = None,
  @jsonField("node.id") `node.id`: Option[String] = None,
  @jsonField("node.name") `node.name`: Option[String] = None,
  @jsonField("node.ephemeral_id") `node.ephemeralId`: Option[String] = None,
  @jsonField("node.address") `node.address`: Option[String] = None
)

object DatafeedsRecord {
  implicit lazy val jsonCodec: JsonCodec[DatafeedsRecord] =
    DeriveJsonCodec.gen[DatafeedsRecord]
}
