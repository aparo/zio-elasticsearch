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

package zio.elasticsearch.ml.forecast
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ml.requests.ForecastRequestBody
/*
 * Predicts the future behavior of a time series by using its historical behavior.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-forecast.html
 *
 * @param jobId The ID of the job to forecast for
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
 * @param duration The duration of the forecast
 * @param expiresIn The time interval after which the forecast expires. Expired forecasts will be deleted at the first opportunity.
 * @param maxModelMemory The max memory able to be used by the forecast. Default is 20mb.
 */

final case class ForecastRequest(
  jobId: String,
  body: ForecastRequestBody = ForecastRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  duration: Option[String] = None,
  expiresIn: Option[String] = None,
  maxModelMemory: Option[String] = None
) extends ActionRequest[ForecastRequestBody]
    with RequestBase {
  def method: Method = Method.POST

  def urlPath: String =
    this.makeUrl("_ml", "anomaly_detectors", jobId, "_forecast")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    duration.foreach { v =>
      queryArgs += ("duration" -> v.toString)
    }
    expiresIn.foreach { v =>
      queryArgs += ("expires_in" -> v.toString)
    }
    maxModelMemory.foreach { v =>
      queryArgs += ("max_model_memory" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
