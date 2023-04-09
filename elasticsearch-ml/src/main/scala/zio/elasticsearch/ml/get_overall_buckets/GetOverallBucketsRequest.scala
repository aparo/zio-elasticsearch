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

package zio.elasticsearch.ml.get_overall_buckets
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ml.requests.GetOverallBucketsRequestBody
/*
 * Retrieves overall bucket results that summarize the bucket results of multiple anomaly detection jobs.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-overall-buckets.html
 *
 * @param jobId The job IDs for which to calculate overall bucket results
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

 * @param allowNoMatch Whether to ignore if a wildcard expression matches no jobs. (This includes `_all` string or when no jobs have been specified)
 * @param body body the body of the call
 * @param bucketSpan The span of the overall buckets. Defaults to the longest job bucket_span
 * @param end Returns overall buckets with timestamps earlier than this time
 * @param excludeInterim If true overall buckets that include interim buckets will be excluded
 * @param overallScore Returns overall buckets with overall scores higher than this value
 * @param start Returns overall buckets with timestamps after this time
 * @param topN The number of top job bucket scores to be used in the overall_score calculation
 */

final case class GetOverallBucketsRequest(
  jobId: String,
  body: GetOverallBucketsRequestBody = GetOverallBucketsRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  allowNoMatch: Option[Boolean] = None,
  bucketSpan: Option[String] = None,
  end: Option[String] = None,
  excludeInterim: Option[Boolean] = None,
  overallScore: Option[Double] = None,
  start: Option[String] = None,
  topN: Option[Int] = None
) extends ActionRequest[GetOverallBucketsRequestBody]
    with RequestBase {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(
    "_ml",
    "anomaly_detectors",
    jobId,
    "results",
    "overall_buckets"
  )

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoMatch.foreach { v =>
      queryArgs += ("allow_no_match" -> v.toString)
    }
    bucketSpan.foreach { v =>
      queryArgs += ("bucket_span" -> v)
    }
    end.foreach { v =>
      queryArgs += ("end" -> v)
    }
    excludeInterim.foreach { v =>
      queryArgs += ("exclude_interim" -> v.toString)
    }
    overallScore.foreach { v =>
      queryArgs += ("overall_score" -> v.toString)
    }
    start.foreach { v =>
      queryArgs += ("start" -> v)
    }
    topN.foreach { v =>
      queryArgs += ("top_n" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
