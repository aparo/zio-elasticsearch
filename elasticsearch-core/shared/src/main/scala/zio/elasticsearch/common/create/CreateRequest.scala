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

package zio.elasticsearch.common.create
import scala.collection.mutable
import zio._
import zio.json._
import zio.elasticsearch.common.Refresh
import zio.elasticsearch.common._
import zio.elasticsearch.common.bulk._
import zio.json.ast.{ Json, JsonUtils }
/*
 * Creates a new document in the index.

Returns a 409 response when a document with a same ID already exists in the index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
 *
 * @param index The name of the index
 * @param id Document ID
 * @param body body the body of the call
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

 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param routing Specific routing value
 * @param timeout Explicit operation timeout
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */

final case class CreateRequest(
  index: String,
  id: String,
  body: TDocument,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  pipeline: Option[String] = None,
  refresh: Option[Refresh] = None,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  version: Option[Long] = None,
  versionType: Option[VersionType] = None,
  waitForActiveShards: Option[String] = None
) extends ActionRequest[TDocument]
    with RequestBase
    with BulkActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl(index, "_create", id)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    pipeline.foreach { v =>
      queryArgs += ("pipeline" -> v)
    }
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    routing.foreach { v =>
      queryArgs += ("routing" -> v)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    version.foreach { v =>
      queryArgs += ("version" -> v.toString)
    }
    versionType.foreach { v =>
      queryArgs += ("version_type" -> v.toString)
    }
    waitForActiveShards.foreach { v =>
      queryArgs += ("wait_for_active_shards" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  override def header: BulkHeader =
    CreateBulkHeader(
      _index = index,
      _id = id,
      pipeline = pipeline,
      routing = routing,
      version = version
    )

  override def toBulkString: String = {
    val sb = new StringBuilder()
    sb.append(JsonUtils.cleanValue(header.toJsonAST.getOrElse(Json.Null)).toJson)
    sb.append("\n")
    sb.append(body.toJson)
    sb.append("\n")
    sb.toString()
  }
  // Custom Code Off

}
