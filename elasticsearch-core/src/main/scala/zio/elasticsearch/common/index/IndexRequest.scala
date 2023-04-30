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

package zio.elasticsearch.common.index
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.common.bulk._
import zio.json._
import zio.json.ast._
/*
 * Creates or updates a document in an index.
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

 * @param ifPrimaryTerm only perform the index operation if the last operation that has changed the document has the specified primary term
 * @param ifSeqNo only perform the index operation if the last operation that has changed the document has the specified sequence number
 * @param opType Explicit operation type. Defaults to `index` for requests with an explicit document ID, and to `create`for requests without an explicit document ID
 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param requireAlias When true, requires destination to be an alias. Default is false
 * @param routing Specific routing value
 * @param timeout Explicit operation timeout
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */

final case class IndexRequest(
  index: String,
  body: TDocument,
  id: Option[String] = None,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  ifPrimaryTerm: Option[Double] = None,
  ifSeqNo: Option[Double] = None,
  opType: OpType = OpType.index,
  pipeline: Option[String] = None,
  refresh: Option[Refresh] = None,
  requireAlias: Option[Boolean] = None,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  version: Option[Long] = None,
  versionType: Option[VersionType] = None,
  waitForActiveShards: Option[String] = None
) extends ActionRequest[TDocument]
    with RequestBase
    with BulkActionRequest {
  def method: Method = if (opType == OpType.create) Method.PUT else Method.POST

  def urlPath: String = this.makeUrl(index, if (opType == OpType.create) "_create" else "_doc", id)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    ifPrimaryTerm.foreach { v =>
      queryArgs += ("if_primary_term" -> v.toString)
    }
    ifSeqNo.foreach { v =>
      queryArgs += ("if_seq_no" -> v.toString)
    }
    if (opType != OpType.index) {
      queryArgs += ("op_type" -> opType.toString)
    }
    pipeline.foreach { v =>
      queryArgs += ("pipeline" -> v)
    }
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    requireAlias.foreach { v =>
      queryArgs += ("require_alias" -> v.toString)
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
    IndexBulkHeader(
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
