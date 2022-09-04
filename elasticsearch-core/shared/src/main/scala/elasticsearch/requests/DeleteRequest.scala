/*
 * Copyright 2019 Alberto Paro
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
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import zio.circe.CirceUtils
import elasticsearch.{ Refresh, VersionType }
import io.circe._
import io.circe.derivation.annotations._

/*
 * Removes a document from the index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete.html
 *
 * @param index The name of the index
 * @param id The document ID
 * @param ifPrimaryTerm only perform the delete operation if the last operation that has changed the document has the specified primary term
 * @param ifSeqNo only perform the delete operation if the last operation that has changed the document has the specified sequence number
 * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param routing Specific routing value
 * @param timeout Explicit operation timeout
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
@JsonCodec
final case class DeleteRequest(
  index: String,
  id: String,
  @JsonKey("if_primary_term") ifPrimaryTerm: Option[Double] = None,
  @JsonKey("if_seq_no") ifSeqNo: Option[Double] = None,
  refresh: Option[Refresh] = None,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  version: Option[Long] = None,
  @JsonKey("version_type") versionType: Option[VersionType] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest
    with BulkActionRequest {
  def method: String = "DELETE"

  def urlPath: String = this.makeUrl(index, "_doc", id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    ifPrimaryTerm.foreach { v =>
      queryArgs += ("if_primary_term" -> v.toString)
    }
    ifSeqNo.foreach { v =>
      queryArgs += ("if_seq_no" -> v.toString)
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
    JsonObject.fromMap(Map("delete" -> Json.fromFields(headerBody)))
  }

  // Custom Code On
  // Custom Code Off
  override def toBulkString: String =
    CirceUtils.printer.print(Json.fromJsonObject(header)) + "\n"
}
