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

package zio.elasticsearch.requests
import zio.Chunk

import scala.collection.mutable
import zio.elasticsearch.{ Refresh, VersionType }
import zio.json._
import zio.json.ast.JsonUtils
import zio.json.ast._

/*
 * Updates a document with a script or partial document.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update.html
 *
 * @param index The name of the index
 * @param id Document ID
 * @param body body the body of the call
 * @param ifPrimaryTerm only perform the update operation if the last operation that has changed the document has the specified primary term
 * @param ifSeqNo only perform the update operation if the last operation that has changed the document has the specified sequence number
 * @param lang The script language (default: painless)
 * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
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
  body: Json.Obj,
  @jsonField("if_primary_term") ifPrimaryTerm: Option[Double] = None,
  @jsonField("if_seq_no") ifSeqNo: Option[Double] = None,
  lang: Option[String] = None,
  refresh: Option[Refresh] = None,
  @jsonField("retry_on_conflict") retryOnConflict: Option[Double] = None,
  routing: Option[String] = None,
  @jsonField("_source") source: Seq[String] = Nil,
  @jsonField("_source_excludes") sourceExcludes: Seq[String] = Nil,
  @jsonField("_source_includes") sourceIncludes: Seq[String] = Nil,
  timeout: Option[String] = None,
  pipeline: Option[String] = None,
  version: Option[Long] = None,
  versionType: Option[VersionType] = None,
  @jsonField("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest
    with BulkActionRequest {
  def method: String = "POST"
  def urlPath: String = this.makeUrl(index, "_update", id)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    ifPrimaryTerm.foreach { v =>
      queryArgs += "if_primary_term" -> v.toString
    }
    ifSeqNo.foreach { v =>
      queryArgs += "if_seq_no" -> v.toString
    }
    lang.foreach { v =>
      queryArgs += "lang" -> v
    }
    refresh.foreach { v =>
      queryArgs += "refresh" -> v.toString
    }
    retryOnConflict.foreach { v =>
      queryArgs += "retry_on_conflict" -> v.toString
    }
    routing.foreach { v =>
      queryArgs += "routing" -> v
    }
    if (source.nonEmpty) {
      queryArgs += "_source" -> source.toList.mkString(",")
    }
    if (sourceExcludes.nonEmpty) {
      queryArgs += "_source_excludes" -> sourceExcludes.toList.mkString(",")
    }
    if (sourceIncludes.nonEmpty) {
      queryArgs += "_source_includes" -> sourceIncludes.toList.mkString(",")
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    waitForActiveShards.foreach { v =>
      queryArgs += "wait_for_active_shards" -> v
    }
    queryArgs.toMap
  }
  override def header: BulkHeader =
    UpdateBulkHeader(_index = index, _id = id, routing = routing, version = version, pipeline = pipeline)

  def addDoc(doc: Json.Obj, docAsUpsert: Boolean = false): UpdateRequest = {
    var toBeAdded: Chunk[(String, Json)] = Chunk("doc" -> doc)
    if (docAsUpsert) {
      toBeAdded ++= Chunk("doc_as_upsert" -> Json.Bool(docAsUpsert))
    }
    this.copy(body = Json.Obj(body.fields ++ toBeAdded))
  }
  def upsert: Boolean =
    this.body.fields.find(_._1 == "doc_as_upsert").flatMap(_._2.as[Boolean].toOption).getOrElse(false)
  def doc: Json.Obj = this.body.fields.find(_._1 == "doc").flatMap(_._2.as[Json.Obj].toOption).getOrElse(Json.Obj())

  override def toBulkString: String =
    JsonUtils.cleanValue(header.toJsonAST.getOrElse(Json.Null)).toJson + "\n" + body.toJson + "\n"
}

object UpdateRequest {
  def toUpdateDoc(index: String, id: String, doc: Json.Obj): UpdateRequest =
    UpdateRequest(index = index, id = id, body = Json.Obj()).addDoc(doc)
  implicit val jsonDecoder: JsonDecoder[UpdateRequest] = DeriveJsonDecoder.gen[UpdateRequest]
  implicit val jsonEncoder: JsonEncoder[UpdateRequest] = DeriveJsonEncoder.gen[UpdateRequest]
}
