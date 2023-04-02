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

package zio.elasticsearch.common.search_mvt
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.common.requests.SearchMvtRequestBody
/*
 * Searches a vector tile for geospatial values. Returns results as a binary Mapbox vector tile.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-vector-tile-api.html
 *
 * @param index Comma-separated list of data streams, indices, or aliases to search
 * @param field Field containing geospatial data to return
 * @param zoom Zoom level for the vector tile to search
 * @param x X coordinate for the vector tile to search
 * @param y Y coordinate for the vector tile to search
 * @param gridAgg Aggregation used to create a grid for `field`.

 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param human When set to `true` will return statistics in a format suitable for humans.
 * For example `"exists_time": "1h"` for humans and
 * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
 * readable values will be omitted. This makes sense for responses being consumed
 * only by machines.
 * @server_default false

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param body body the body of the call
 * @param exactBounds If false, the meta layer's feature is the bounding box of the tile. If true, the meta layer's feature is a bounding box resulting from a `geo_bounds` aggregation.
 * @param extent Size, in pixels, of a side of the vector tile.
 * @param gridPrecision Additional zoom levels available through the aggs layer. Accepts 0-8.
 * @param gridType Determines the geometry type for features in the aggs layer.
 * @param size Maximum number of features to return in the hits layer. Accepts 0-10000.
 * @param trackTotalHits Indicate if the number of documents that match the query should be tracked. A number can also be specified, to accurately track the total hit count up to the number.
 * @param withLabels If true, the hits and aggs layers will contain additional point features with suggested label positions for the original features.
 */

final case class SearchMvtRequest(
  index: String,
  field: String,
  zoom: String,
  x: String,
  y: String,
  body: SearchMvtRequestBody,
  gridAgg: GridAggregationType,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  exactBounds: Boolean = false,
  extent: Int = 4096,
  gridPrecision: Int = 8,
  gridType: GridType = "grid",
  size: Int = 10000,
  trackTotalHits: Option[Long] = None,
  withLabels: Boolean = false
) extends ActionRequest[SearchMvtRequestBody]
    with RequestBase {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(index, "_mvt", field, zoom, x, y)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (exactBounds != false)
      queryArgs += ("exact_bounds" -> exactBounds.toString)
    if (extent != 4096) queryArgs += ("extent" -> extent.toString)
    if (gridPrecision != 8)
      queryArgs += ("grid_precision" -> gridPrecision.toString)
    if (gridType != "grid")
      queryArgs += ("grid_type" -> gridType.toString)
    if (size != 10000) queryArgs += ("size" -> size.toString)
    trackTotalHits.foreach { v =>
      queryArgs += ("track_total_hits" -> v.toString)
    }
    if (withLabels != false) queryArgs += ("with_labels" -> withLabels.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
