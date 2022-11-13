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

package zio.elasticsearch.requests
import scala.collection.mutable

import zio.elasticsearch.Refresh
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

/*
 * Allows to perform multiple index/update/delete operations in a single request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-bulk.html
 *
 * @param body body the body of the call
 * @param docType Default document type for items which don't provide one
 * @param index Default index for items which don't provide one
 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param routing Specific routing value
 * @param source True or false to return the _source field or not, or default list of fields to return, can be overridden on each sub-request
 * @param sourceExcludes Default list of fields to exclude from the returned _source field, can be overridden on each sub-request
 * @param sourceIncludes Default list of fields to extract and return from the _source field, can be overridden on each sub-request
 * @param timeout Explicit operation timeout
 * @param `type` Default document type for items which don't provide one
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the bulk operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
final case class BulkRequest(
  body: String,
  docType: Option[String] = None,
  index: Option[String] = None,
  pipeline: Option[String] = None,
  refresh: Option[Refresh] = None,
  routing: Option[String] = None,
  @jsonField("_source") source: Seq[String] = Nil,
  @jsonField("_source_excludes") sourceExcludes: Seq[String] = Nil,
  @jsonField("_source_includes") sourceIncludes: Seq[String] = Nil,
  timeout: Option[String] = None,
  @jsonField("type") `type`: Option[String] = None,
  @jsonField("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"
  def urlPath: String = this.makeUrl(index, docType, "_bulk")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    pipeline.foreach { v =>
      queryArgs += "pipeline" -> v
    }
    refresh.foreach { v =>
      queryArgs += "refresh" -> v.toString
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
    `type`.foreach { v =>
      queryArgs += "type" -> v
    }
    waitForActiveShards.foreach { v =>
      queryArgs += "wait_for_active_shards" -> v
    }
    queryArgs.toMap
  }
}
object BulkRequest {
  implicit val jsonDecoder: JsonDecoder[BulkRequest] = DeriveJsonDecoder.gen[BulkRequest]
  implicit val jsonEncoder: JsonEncoder[BulkRequest] = DeriveJsonEncoder.gen[BulkRequest]
}
