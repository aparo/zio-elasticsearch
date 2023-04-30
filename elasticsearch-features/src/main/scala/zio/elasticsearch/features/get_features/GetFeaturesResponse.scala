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

package zio.elasticsearch.features.get_features
import zio._
import zio.elasticsearch.features.Feature
import zio.json._ /*
 * Gets a list of features which can be included in snapshots using the feature_states field when creating a snapshot
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-features-api.html
 *
 * @param features

 */
final case class GetFeaturesResponse(
  features: Chunk[Feature] = Chunk.empty[Feature]
) {}
object GetFeaturesResponse {
  implicit lazy val jsonCodec: JsonCodec[GetFeaturesResponse] =
    DeriveJsonCodec.gen[GetFeaturesResponse]
}
