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

package zio.elasticsearch.ml.get_model_snapshots
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ml.requests.GetModelSnapshotsRequestBody
/*
 * Retrieves information about model snapshots.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-snapshot.html
 *
 * @param jobId The ID of the job to fetch
 * @param snapshotId The ID of the snapshot to fetch
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
 * @param desc True if the results should be sorted in descending order
 * @param end The filter 'end' query parameter
 * @param from Skips a number of documents
 * @param size The default number of documents returned in queries as a string.
 * @param sort Name of the field to sort on
 * @param start The filter 'start' query parameter
 */

final case class GetModelSnapshotsRequest(
  jobId: String,
  snapshotId: String,
  body: GetModelSnapshotsRequestBody = GetModelSnapshotsRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  desc: Option[Boolean] = None,
  end: Option[java.time.LocalDate] = None,
  from: Option[Int] = None,
  size: Option[Int] = None,
  sort: Option[String] = None,
  start: Option[java.time.LocalDate] = None
) extends ActionRequest[GetModelSnapshotsRequestBody]
    with RequestBase {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(
    "_ml",
    "anomaly_detectors",
    jobId,
    "model_snapshots",
    snapshotId
  )

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    desc.foreach { v =>
      queryArgs += ("desc" -> v.toString)
    }
    end.foreach { v =>
      queryArgs += ("end" -> v.toString)
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
      queryArgs += ("start" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
