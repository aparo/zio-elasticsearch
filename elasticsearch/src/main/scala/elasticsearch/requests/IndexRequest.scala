/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import elasticsearch.{ OpType, Refresh, VersionType }
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import elasticsearch.common.circe.CirceUtils

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
 *
 * @param index The name of the index
 * @param docType The type of the document
 * @param id Document ID
 * @param body body the body of the call
 * @param parent ID of the parent document
 * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param timestamp Explicit timestamp for the document
 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 * @param routing Specific routing value
 * @param ttl Expiration time for the document
 * @param opType Explicit operation type
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
@JsonCodec
final case class IndexRequest(
  index: String,
  id: Option[String] = None,
  body: JsonObject,
  parent: Option[String] = None,
  refresh: Option[Refresh] = None,
  timestamp: Option[String] = None,
  pipeline: Option[String] = None,
  version: Option[Long] = None,
  @JsonKey("version_type") versionType: Option[VersionType] = None,
  routing: Option[String] = None,
  ttl: Option[Long] = None,
  @JsonKey("op_type") opType: OpType = OpType.index,
  timeout: Option[String] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[Double] = None
) extends ActionRequest
    with BulkActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(index, if (opType == OpType.create) "_create" else "_doc", id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    parent.map { v =>
      queryArgs += ("parent" -> v)
    }
    refresh.map { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    timestamp.map { v =>
      queryArgs += ("timestamp" -> v.toString)
    }
    pipeline.map { v =>
      queryArgs += ("pipeline" -> v)
    }
    version.map { v =>
      queryArgs += ("version" -> v.toString)
    }
    versionType.map { v =>
      queryArgs += ("version_type" -> v.toString)
    }
    routing.map { v =>
      queryArgs += ("routing" -> v)
    }
    ttl.map { v =>
      queryArgs += ("ttl" -> v.toString)
    }
    if (opType != OpType.index)
      queryArgs += ("op_type" -> opType.toString)
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    waitForActiveShards.map { v =>
      queryArgs += ("wait_for_active_shards" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  override def header: JsonObject = {
    val headerBody = new ListBuffer[(String, Json)]
    headerBody ++= Seq(
      "_index" -> Json.fromString(index)
    )
    id.foreach(i => headerBody += "_id" -> Json.fromString(i))
    routing.foreach { v =>
      headerBody += ("routing" -> Json.fromString(v))
    }
    version.foreach { v =>
      headerBody += ("version" -> Json.fromLong(v))
    }
    pipeline.foreach { v =>
      headerBody += ("pipeline" -> Json.fromString(v))
    }
    parent.foreach { v =>
      headerBody += ("parent" -> Json.fromString(v))
    }

    opType match {
      case OpType.index =>
        JsonObject.fromMap(Map("index" -> Json.fromFields(headerBody)))
      case OpType.create =>
        JsonObject.fromMap(Map("create" -> Json.fromFields(headerBody)))
      case _ => JsonObject.fromMap(Map("index" -> Json.fromFields(headerBody)))
    }
  }

  // Custom Code On
  // Custom Code Off

  override def toBulkString: String =
    CirceUtils.printer.print(Json.fromJsonObject(header)) + "\n" + CirceUtils.printer.print(
      Json.fromJsonObject(body)
    ) + "\n"
}
