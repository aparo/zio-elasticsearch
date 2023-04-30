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

package zio.elasticsearch.ml.flush_job
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ml.requests.FlushJobRequestBody
/*
 * Forces any buffered data to be processed by the job.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-flush-job.html
 *
 * @param jobId The name of the job to flush
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

 * @param advanceTime Advances time to the given value generating results and updating the model for the advanced interval
 * @param body body the body of the call
 * @param calcInterim Calculates interim results for the most recent bucket or all buckets within the latency period
 * @param end When used in conjunction with calc_interim, specifies the range of buckets on which to calculate interim results
 * @param skipTime Skips time to the given value without generating results or updating the model for the skipped interval
 * @param start When used in conjunction with calc_interim, specifies the range of buckets on which to calculate interim results
 */

final case class FlushJobRequest(
  jobId: String,
  body: FlushJobRequestBody = FlushJobRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  advanceTime: Option[String] = None,
  calcInterim: Option[Boolean] = None,
  end: Option[String] = None,
  skipTime: Option[String] = None,
  start: Option[String] = None
) extends ActionRequest[FlushJobRequestBody]
    with RequestBase {
  def method: Method = Method.POST

  def urlPath: String =
    this.makeUrl("_ml", "anomaly_detectors", jobId, "_flush")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    advanceTime.foreach { v =>
      queryArgs += ("advance_time" -> v)
    }
    calcInterim.foreach { v =>
      queryArgs += ("calc_interim" -> v.toString)
    }
    end.foreach { v =>
      queryArgs += ("end" -> v)
    }
    skipTime.foreach { v =>
      queryArgs += ("skip_time" -> v)
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
