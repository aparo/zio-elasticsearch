/*
 * Copyright 2019-2020 Alberto Paro
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

package elasticsearch.requests
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

import scala.collection.mutable
import elasticsearch.{ Refresh, VersionType }

import scala.collection.mutable.ListBuffer
import zio.circe.CirceUtils

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
@JsonCodec
final case class UpdateRequest(
  index: String,
  id: String,
  body: JsonObject,
  @JsonKey("if_primary_term") ifPrimaryTerm: Option[Double] = None,
  @JsonKey("if_seq_no") ifSeqNo: Option[Double] = None,
  lang: Option[String] = None,
  refresh: Option[Refresh] = None,
  @JsonKey("retry_on_conflict") retryOnConflict: Option[Double] = None,
  routing: Option[String] = None,
  @JsonKey("_source") source: Seq[String] = Nil,
  @JsonKey("_source_excludes") sourceExcludes: Seq[String] = Nil,
  @JsonKey("_source_includes") sourceIncludes: Seq[String] = Nil,
  timeout: Option[String] = None,
  pipeline: Option[String] = None,
  version: Option[Long] = None,
  versionType: Option[VersionType] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest
    with BulkActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(index, "_update", id)

  def queryArgs: Map[String, String] = {
    //managing parameters
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
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
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
      queryArgs += ("timeout" -> v.toString)
    }
    waitForActiveShards.foreach { v =>
      queryArgs += ("wait_for_active_shards" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  override def header: JsonObject = {
    val headerBody = new ListBuffer[(String, Json)]
    headerBody ++= Seq(
      "_index" -> Json.fromString(index),
      "_id" -> Json.fromString(id)
    )
    version.foreach { v =>
      headerBody += ("version" -> Json.fromString(v.toString))
    }
    pipeline.foreach { v =>
      headerBody += ("pipeline" -> Json.fromString(v))
    }
    routing.foreach { v =>
      headerBody += ("routing" -> Json.fromString(v))
    }
    routing.foreach { v =>
      headerBody += ("routing" -> Json.fromString(v))
    }
    JsonObject.fromMap(Map("update" -> Json.fromFields(headerBody.toMap)))
  }

//  def addScript(script:qdb.script.Script):UpdateRequest={
//
//  }

  def addDoc(doc: JsonObject, docAsUpsert: Boolean = false): UpdateRequest = {
    var toBeAdded = List("doc" -> Json.fromJsonObject(doc))
    if (docAsUpsert) {
      toBeAdded ::= ("doc_as_upsert" -> Json.fromBoolean(docAsUpsert))
    }

    this.copy(body = JsonObject.fromIterable(body.toList ++ toBeAdded))

  }

  def upsert: Boolean =
    if (this.body.contains("doc_as_upsert")) {
      body("doc_as_upsert").get.asBoolean.get
    } else false

  def doc: JsonObject =
    if (this.body.contains("doc")) {
      body("doc").get.asObject.getOrElse(JsonObject.empty)
    } else JsonObject.empty

  // Custom Code On
  // Custom Code Off
  override def toBulkString: String =
    CirceUtils.printer.print(Json.fromJsonObject(header)) + "\n" +
      CirceUtils.printer.print(Json.fromJsonObject(body)) + "\n"
}
