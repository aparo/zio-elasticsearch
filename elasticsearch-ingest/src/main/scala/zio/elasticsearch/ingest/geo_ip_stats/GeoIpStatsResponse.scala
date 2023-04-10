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

package zio.elasticsearch.ingest.geo_ip_stats
import zio.json._
import zio.json.ast._
/*
 * Returns statistical information about geoip databases
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/geoip-stats-api.html
 *
 * @param stats Download statistics for all GeoIP2 databases.

 * @param nodes Downloaded GeoIP2 databases for each node.

 */
final case class GeoIpStatsResponse(
  stats: GeoIpDownloadStatistics,
  nodes: Map[String, GeoIpNodeDatabases] = Map.empty[String, GeoIpNodeDatabases]
) {}
object GeoIpStatsResponse {
  implicit lazy val jsonCodec: JsonCodec[GeoIpStatsResponse] =
    DeriveJsonCodec.gen[GeoIpStatsResponse]
}
