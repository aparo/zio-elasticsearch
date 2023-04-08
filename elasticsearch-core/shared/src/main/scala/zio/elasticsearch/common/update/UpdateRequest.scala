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

package zio.elasticsearch.common.update
import scala.collection.mutable
import zio._
import zio.elasticsearch.common.Refresh
import zio.elasticsearch.common._
import zio.elasticsearch.common.bulk._
import zio.json.ast._
import zio.json._
/*
 * Updates a document with a script or partial document.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update.html
 *
 * @param index The name of the index
 * @param id Document ID
 * @param body body the body of the call
 * @param taskId

 * @param requestsPerSecond

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

 * @param ifPrimaryTerm only perform the update operation if the last operation that has changed the document has the specified primary term
 * @param ifSeqNo only perform the update operation if the last operation that has changed the document has the specified sequence number
 * @param lang The script language (default: painless)
 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param requireAlias When true, requires destination is an alias. Default is false
 * @param retryOnConflict Specify how many times should the operation be retried when a conflict occurs (default: 0)
 * @param routing Specific routing value
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param sourceExcludes A list of fields to exclude from the returned _source field
 * @param sourceIncludes A list of fields to extract and return from the _source field
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the update operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */

final case class UpdateRequest(
  index: String,
  id: String,
  body: Json,
  taskId: String,
  requestsPerSecond: Float,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  ifPrimaryTerm: Option[Double] = None,
  ifSeqNo: Option[Double] = None,
  lang: Option[String] = None,
  pipeline: Option[String] = None,
  refresh: Option[Refresh] = None,
  requireAlias: Option[Boolean] = None,
  retryOnConflict: Option[Double] = None,
  routing: Option[String] = None,
  source: Seq[String] = Nil,
  sourceExcludes: Seq[String] = Nil,
  sourceIncludes: Seq[String] = Nil,
  timeout: Option[String] = None,
  version: Option[Long] = None,
  versionType: Option[VersionType] = None,
  waitForActiveShards: Option[String] = None
) extends ActionRequest[Json]
    with RequestBase
    with BulkActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(index, "_update", id)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    ifPrimaryTerm.foreach { v =>
      queryArgs += ("if_primary_term" -> v.toString)
    }
    ifSeqNo.foreach { v =>
      queryArgs += ("if_seq_no" -> v.toString)
    }
    lang.foreach { v =>
      queryArgs += ("lang" -> v)
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
    retryOnConflict.foreach { v =>
      queryArgs += ("retry_on_conflict" -> v.toString)
    }
    routing.foreach { v =>
      queryArgs += ("routing" -> v)
    }
    if (source.nonEmpty) {
      queryArgs += ("_source" -> source.toList.mkString(","))
    }
    if (sourceExcludes.nonEmpty) {
      queryArgs += ("_source_excludes" -> sourceExcludes.toList.mkString(","))
    }
    if (sourceIncludes.nonEmpty) {
      queryArgs += ("_source_includes" -> sourceIncludes.toList.mkString(","))
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v)
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
    UpdateBulkHeader(_index = index, _id = id, routing = routing, version = version, pipeline = pipeline)

  def addDoc(doc: Json.Obj, docAsUpsert: Boolean = false): UpdateRequest = {
    var toBeAdded: Chunk[(String, Json)] = Chunk("doc" -> doc)
    if (docAsUpsert) {
      toBeAdded ++= Chunk("doc_as_upsert" -> Json.Bool(docAsUpsert))
    }
    body match {
      case Json.Obj(fields) => this.copy(body = Json.Obj(fields ++ toBeAdded))
      case _                => this.copy(body = Json.Obj(toBeAdded))
    }

  }

  def upsert: Boolean = body match {
    case Json.Obj(fields) => fields.find(_._1 == "doc_as_upsert").flatMap(_._2.as[Boolean].toOption).getOrElse(false)
    case _                => false
  }

  def doc: Json.Obj =
    body match {
      case Json.Obj(fields) => fields.find(_._1 == "doc").flatMap(_._2.as[Json.Obj].toOption).getOrElse(Json.Obj())
      case _                => Json.Obj()
    }

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
