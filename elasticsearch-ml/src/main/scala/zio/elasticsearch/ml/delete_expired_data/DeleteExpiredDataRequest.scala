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

package zio.elasticsearch.ml.delete_expired_data
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ml.requests.DeleteExpiredDataRequestBody
/*
 * Deletes expired and unused machine learning data.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-expired-data.html
 *
 * @param jobId The ID of the job(s) to perform expired data hygiene for
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
 * @param requestsPerSecond The desired requests per second for the deletion processes.
 * @param timeout How long can the underlying delete processes run until they are canceled
 */

final case class DeleteExpiredDataRequest(
  jobId: String,
  body: DeleteExpiredDataRequestBody = DeleteExpiredDataRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  requestsPerSecond: Option[Double] = None,
  timeout: Option[String] = None
) extends ActionRequest[DeleteExpiredDataRequestBody]
    with RequestBase {
  def method: Method = Method.DELETE

  def urlPath: String = this.makeUrl("_ml", "_delete_expired_data", jobId)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    requestsPerSecond.foreach { v =>
      queryArgs += ("requests_per_second" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
