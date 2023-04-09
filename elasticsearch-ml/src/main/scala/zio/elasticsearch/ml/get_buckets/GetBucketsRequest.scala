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

package zio.elasticsearch.ml.get_buckets
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ml.requests.GetBucketsRequestBody
/*
 * Retrieves anomaly detection job results for one or more buckets.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-bucket.html
 *
 * @param jobId ID of the job to get bucket results from
 * @param timestamp The timestamp of the desired single bucket result
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

 * @param anomalyScore Filter for the most anomalous buckets
 * @param body body the body of the call
 * @param desc Set the sort direction
 * @param end End time filter for buckets
 * @param excludeInterim Exclude interim results
 * @param expand Include anomaly records
 * @param from skips a number of buckets
 * @param size specifies a max number of buckets to get
 * @param sort Sort buckets by a particular field
 * @param start Start time filter for buckets
 */

final case class GetBucketsRequest(
  jobId: String,
  body: GetBucketsRequestBody = GetBucketsRequestBody(),
  timestamp: String,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  anomalyScore: Option[Double] = None,
  desc: Option[Boolean] = None,
  end: Option[String] = None,
  excludeInterim: Option[Boolean] = None,
  expand: Option[Boolean] = None,
  from: Option[Int] = None,
  size: Option[Int] = None,
  sort: Option[String] = None,
  start: Option[String] = None
) extends ActionRequest[GetBucketsRequestBody]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl(
    "_ml",
    "anomaly_detectors",
    jobId,
    "results",
    "buckets",
    timestamp
  )

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    anomalyScore.foreach { v =>
      queryArgs += ("anomaly_score" -> v.toString)
    }
    desc.foreach { v =>
      queryArgs += ("desc" -> v.toString)
    }
    end.foreach { v =>
      queryArgs += ("end" -> v)
    }
    excludeInterim.foreach { v =>
      queryArgs += ("exclude_interim" -> v.toString)
    }
    expand.foreach { v =>
      queryArgs += ("expand" -> v.toString)
    }
    from.foreach { v =>
      queryArgs += ("from" -> v.toString)
    }
    size.foreach { v =>
      queryArgs += ("size" -> v.toString)
    }
    sort.foreach { v =>
      queryArgs += ("sort" -> v)
    }
    start.foreach { v =>
      queryArgs += ("start" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
