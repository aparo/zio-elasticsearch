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

package zio.elasticsearch.indices.clear_cache
import zio.elasticsearch.common._
import zio.json._
/*
 * Clears all or specific caches for one or more indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clearcache.html
 *
 * @param shards

 */
final case class ClearCacheResponse(@jsonField("_shards") shards: ShardStatistics) {}
object ClearCacheResponse {
  implicit lazy val jsonCodec: JsonCodec[ClearCacheResponse] =
    DeriveJsonCodec.gen[ClearCacheResponse]
}
