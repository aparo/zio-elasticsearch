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
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import zio.elasticsearch.{ Refresh, VersionType }
import zio.json._
import zio.json.ast.JsonUtils
import zio.json.ast._

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
final case class DeleteRequest(
  index: String,
  id: String,
  @jsonField("if_primary_term") ifPrimaryTerm: Option[Double] = None,
  @jsonField("if_seq_no") ifSeqNo: Option[Double] = None,
  refresh: Option[Refresh] = None,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  version: Option[Long] = None,
  @jsonField("version_type") versionType: Option[VersionType] = None,
  @jsonField("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest
    with BulkActionRequest {
  def method: String = "DELETE"
  def urlPath: String = this.makeUrl(index, "_doc", id)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    ifPrimaryTerm.foreach { v =>
      queryArgs += "if_primary_term" -> v.toString
    }
    ifSeqNo.foreach { v =>
      queryArgs += "if_seq_no" -> v.toString
    }
    refresh.foreach { v =>
      queryArgs += "refresh" -> v.toString
    }
    routing.foreach { v =>
      queryArgs += "routing" -> v
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    version.foreach { v =>
      queryArgs += "version" -> v.toString
    }
    versionType.foreach { v =>
      queryArgs += "version_type" -> v.toString
    }
    waitForActiveShards.foreach { v =>
      queryArgs += "wait_for_active_shards" -> v
    }
    queryArgs.toMap
  }
  def body: Json.Obj = Json.Obj()
  override def header: BulkHeader =
    DeleteBulkHeader(_index = index, _id = id, routing = routing, version = version /*, pipeline = pipeline*/ )
  override def toBulkString: String = header.toJson + "\n"
}
object DeleteRequest {
  implicit val jsonDecoder: JsonDecoder[DeleteRequest] = DeriveJsonDecoder.gen[DeleteRequest]
  implicit val jsonEncoder: JsonEncoder[DeleteRequest] = DeriveJsonEncoder.gen[DeleteRequest]
}
