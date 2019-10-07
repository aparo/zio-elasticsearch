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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete.html
 *
 * @param index The name of the index
 * @param docType The type of the document
 * @param id The document ID
 * @param parent ID of parent document
 * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 * @param routing Specific routing value
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
@JsonCodec
final case class DeleteRequest(
  index: String,
  id: String,
  parent: Option[String] = None,
  refresh: Option[Refresh] = None,
  version: Option[Long] = None,
  @JsonKey("version_type") versionType: Option[VersionType] = None,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest
    with BulkActionRequest {
  def method: String = "DELETE"

  def urlPath: String = this.makeUrl(index, id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    parent.map { v =>
      queryArgs += ("parent" -> v)
    }
    refresh.map { v =>
      queryArgs += ("refresh" -> v.toString)
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
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    waitForActiveShards.map { v =>
      queryArgs += ("wait_for_active_shards" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: JsonObject = JsonObject.empty

  override def header: JsonObject = {
    val headerBody = new ListBuffer[(String, Json)]
    headerBody ++= Seq(
      "_index" -> Json.fromString(index),
      "_id" -> Json.fromString(id)
    )
    routing.foreach { v =>
      headerBody += ("routing" -> Json.fromString(v))
    }
    version.foreach { v =>
      headerBody += ("version" -> Json.fromString(v.toString))
    }
    parent.foreach { v =>
      headerBody += ("parent" -> Json.fromString(v))
    }
    JsonObject.fromMap(Map("delete" -> Json.fromFields(headerBody)))
  }

  // Custom Code On
  // Custom Code Off
  override def toBulkString: String =
    CirceUtils.printer.print(Json.fromJsonObject(header)) + "\n"
}
