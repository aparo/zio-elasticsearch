/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

import scala.collection.mutable
import elasticsearch.VersionType
import elasticsearch.Refresh

import scala.collection.mutable.ListBuffer
import elasticsearch.common.circe.CirceUtils

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update.html
 *
 * @param index The name of the index
 * @param docType The type of the document
 * @param id Document ID
 * @param body body the body of the call
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param parent ID of the parent document. Is is only used for routing and when for the upsert request
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param timestamp Explicit timestamp for the document
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param version Explicit version number for concurrency control
 * @param retryOnConflict Specify how many times should the operation be retried when a conflict occurs (default: 0)
 * @param versionType Specific version type
 * @param fields A list of fields to return in the response
 * @param routing Specific routing value
 * @param lang The script language (default: painless)
 * @param ttl Expiration time for the document
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the update operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
@JsonCodec
final case class UpdateRequest(
  index: String,
  docType: String,
  id: String,
  body: JsonObject = JsonObject.empty,
  @JsonKey("_source_include") sourceInclude: Seq[String] = Nil,
  parent: Option[String] = None,
  @JsonKey("_source") source: Seq[String] = Nil,
  refresh: Refresh = Refresh.`false`,
  timestamp: Option[String] = None,
  @JsonKey("_source_exclude") sourceExclude: Seq[String] = Nil,
  version: Option[Long] = None,
  pipeline: Option[String] = None,
  @JsonKey("retry_on_conflict") retryOnConflict: Int = 0,
  @JsonKey("version_type") versionType: Option[VersionType] = None,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Int = 1
) extends ActionRequest
    with BulkActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(index, docType, id, "_update")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (sourceInclude.nonEmpty) {
      queryArgs += ("_source_include" -> sourceInclude.toList.mkString(","))
    }
    parent.map { v =>
      queryArgs += ("parent" -> v)
    }
    if (source.nonEmpty) {
      queryArgs += ("_source" -> source.toList.mkString(","))
    }
    if (refresh != Refresh.`false`) {
      queryArgs += ("refresh" -> refresh.toString)
    }
    timestamp.map { v =>
      queryArgs += ("timestamp" -> v.toString)
    }
    if (sourceExclude.nonEmpty) {
      queryArgs += ("_source_exclude" -> sourceExclude.toList.mkString(","))
    }
    version.map { v =>
      queryArgs += ("version" -> v.toString)
    }
    if (retryOnConflict != 0)
      queryArgs += ("retry_on_conflict" -> retryOnConflict.toString)
    versionType.map { v =>
      queryArgs += ("version_type" -> v.toString)
    }
    routing.map { v =>
      queryArgs += ("routing" -> v)
    }
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    if (waitForActiveShards != 1)
      queryArgs += ("wait_for_active_shards" -> waitForActiveShards.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  override def header: JsonObject = {
    val headerBody = new ListBuffer[(String, Json)]
    headerBody ++= Seq(
      "_index" -> Json.fromString(index),
      "_type" -> Json.fromString(docType),
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
    parent.foreach { v =>
      headerBody += ("parent" -> Json.fromString(v))
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
