/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import elasticsearch.common.circe.CirceUtils
import elasticsearch.{ OpType, Refresh }
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/*
 * Creates or updates a document in an index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
 *
 * @param index The name of the index
 * @param id Document ID
 * @param body body the body of the call
 * @param ifPrimaryTerm only perform the index operation if the last operation that has changed the document has the specified primary term
 * @param ifSeqNo only perform the index operation if the last operation that has changed the document has the specified sequence number
 * @param opType Explicit operation type. Defaults to `index` for requests with an explicit document ID, and to `create`for requests without an explicit document ID
 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param routing Specific routing value
 * @param timeout Explicit operation timeout
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
@JsonCodec
final case class IndexRequest(
  index: String,
  body: JsonObject,
  id: Option[String] = None,
  @JsonKey("if_primary_term") ifPrimaryTerm: Option[Double] = None,
  @JsonKey("if_seq_no") ifSeqNo: Option[Double] = None,
  @JsonKey("op_type") opType: OpType = OpType.index,
  refresh: Option[Refresh] = None,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  pipeline: Option[String] = None,
  version: Option[Long] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[Int] = None
) extends ActionRequest
    with BulkActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(index, if (opType == OpType.create) "_create" else "_doc", id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    ifPrimaryTerm.foreach { v =>
      queryArgs += ("if_primary_term" -> v.toString)
    }
    ifSeqNo.foreach { v =>
      queryArgs += ("if_seq_no" -> v.toString)
    }
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
//    versionType.foreach { v =>
//      queryArgs += ("version_type" -> v.toString)
//    }
    waitForActiveShards.foreach { v =>
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
