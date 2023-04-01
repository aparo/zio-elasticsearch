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
import zio.elasticsearch.{ OpType, Refresh }
import zio.json._
import zio.json.ast.JsonUtils
import zio.json.ast._

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
final case class IndexRequest(
  index: String,
  body: Json.Obj,
  id: Option[String] = None,
  @jsonField("if_primary_term") ifPrimaryTerm: Option[Double] = None,
  @jsonField("if_seq_no") ifSeqNo: Option[Double] = None,
  @jsonField("op_type") opType: OpType = OpType.index,
  refresh: Option[Refresh] = None,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  pipeline: Option[String] = None,
  version: Option[Long] = None,
  @jsonField("wait_for_active_shards") waitForActiveShards: Option[Int] = None
) extends ActionRequest
    with BulkActionRequest {
  def method: String = "POST"
  def urlPath: String = this.makeUrl(index, if (opType == OpType.create) "_create" else "_doc", id)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    ifPrimaryTerm.foreach { v =>
      queryArgs += "if_primary_term" -> v.toString
    }
    ifSeqNo.foreach { v =>
      queryArgs += "if_seq_no" -> v.toString
    }
    pipeline.foreach { v =>
      queryArgs += "pipeline" -> v
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
    waitForActiveShards.foreach { v =>
      queryArgs += "wait_for_active_shards" -> v.toString
    }
    queryArgs.toMap
  }
  override def header: BulkHeader =
    IndexBulkHeader(
      _index = index,
      _id = id,
      pipeline = pipeline,
      routing = routing,
      version = version
    )
  override def toBulkString: String =
    JsonUtils.cleanValue(header.toJsonAST.getOrElse(Json.Null)).toJson + "\n" + body.toJson + "\n"
}
object IndexRequest {
  implicit val jsonDecoder: JsonDecoder[IndexRequest] = DeriveJsonDecoder.gen[IndexRequest]
  implicit val jsonEncoder: JsonEncoder[IndexRequest] = DeriveJsonEncoder.gen[IndexRequest]
}
