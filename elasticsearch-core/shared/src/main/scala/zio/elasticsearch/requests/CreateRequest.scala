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

import zio.elasticsearch.{ Refresh, VersionType }
import zio.json._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

/*
 * Creates a new document in the index.

Returns a 409 response when a document with a same ID already exists in the index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
 *
 * @param index The name of the index
 * @param id Document ID
 * @param body body the body of the call
 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param routing Specific routing value
 * @param timeout Explicit operation timeout
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
final case class CreateRequest(
  index: String,
  id: String,
  body: Json.Obj,
  pipeline: Option[String] = None,
  refresh: Option[Refresh] = None,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  version: Option[Long] = None,
  @jsonField("version_type") versionType: Option[VersionType] = None,
  @jsonField("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest {
  def method: String = "PUT"
  def urlPath: String = this.makeUrl(index, "_create", id)
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
}
object CreateRequest {
  implicit val jsonDecoder: JsonDecoder[CreateRequest] = DeriveJsonDecoder.gen[CreateRequest]
  implicit val jsonEncoder: JsonEncoder[CreateRequest] = DeriveJsonEncoder.gen[CreateRequest]
}
