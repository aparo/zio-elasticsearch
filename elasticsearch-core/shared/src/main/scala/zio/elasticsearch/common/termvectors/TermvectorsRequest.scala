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

package zio.elasticsearch.common.termvectors
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.common.requests.TermvectorsRequestBody
/*
 * Returns information and statistics about terms in the fields of a particular document.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-termvectors.html
 *
 * @param index The index in which the document resides.
 * @param id The id of the document, when not specified a doc param should be supplied.
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
 * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned.
 * @param fields A comma-separated list of fields to return.
 * @param offsets Specifies if term offsets should be returned.
 * @param payloads Specifies if term payloads should be returned.
 * @param positions Specifies if term positions should be returned.
 * @param preference Specify the node or shard the operation should be performed on (default: random).
 * @param realtime Specifies if request is real-time as opposed to near-real-time (default: true).
 * @param routing Specific routing value.
 * @param termStatistics Specifies if total term frequency and document frequency should be returned.
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 */

final case class TermvectorsRequest(
  index: String,
  id: String,
  body: TermvectorsRequestBody,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  fieldStatistics: Boolean = true,
  fields: Seq[String] = Nil,
  offsets: Boolean = true,
  payloads: Boolean = true,
  positions: Boolean = true,
  preference: Option[String] = None,
  realtime: Option[Boolean] = None,
  routing: Option[String] = None,
  termStatistics: Boolean = false,
  version: Option[Long] = None,
  versionType: Option[VersionType] = None
) extends ActionRequest[TermvectorsRequestBody]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl(index, "_termvectors", id)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (fieldStatistics != true)
      queryArgs += ("field_statistics" -> fieldStatistics.toString)
    if (fields.nonEmpty) {
      queryArgs += ("fields" -> fields.toList.mkString(","))
    }
    if (offsets != true) queryArgs += ("offsets" -> offsets.toString)
    if (payloads != true) queryArgs += ("payloads" -> payloads.toString)
    if (positions != true) queryArgs += ("positions" -> positions.toString)
    preference.foreach { v =>
      queryArgs += ("preference" -> v)
    }
    realtime.foreach { v =>
      queryArgs += ("realtime" -> v.toString)
    }
    routing.foreach { v =>
      queryArgs += ("routing" -> v)
    }
    if (termStatistics != false)
      queryArgs += ("term_statistics" -> termStatistics.toString)
    version.foreach { v =>
      queryArgs += ("version" -> v.toString)
    }
    versionType.foreach { v =>
      queryArgs += ("version_type" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
