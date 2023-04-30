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

package zio.elasticsearch.ml.get_datafeeds
import zio._
import zio.elasticsearch.ml.Datafeed
import zio.json._
/*
 * Retrieves configuration information for datafeeds.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-datafeed.html
 *
 * @param count

 * @param datafeeds

 */
final case class GetDatafeedsResponse(
  count: Long,
  datafeeds: Chunk[Datafeed] = Chunk.empty[Datafeed]
) {}
object GetDatafeedsResponse {
  implicit lazy val jsonCodec: JsonCodec[GetDatafeedsResponse] =
    DeriveJsonCodec.gen[GetDatafeedsResponse]
}
