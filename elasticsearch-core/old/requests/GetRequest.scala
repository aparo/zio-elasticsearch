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

import zio.elasticsearch.common.VersionType
import zio.json._
import zio.json.ast._

/*
 * Returns a document.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
 *
 * @param index The name of the index
 * @param id The document ID
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param realtime Specify whether to perform the operation in realtime or search mode
 * @param refresh Refresh the shard containing the document before performing the operation
 * @param routing Specific routing value
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param storedFields A comma-separated list of stored fields to return in the response
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 */
final case class GetRequest(
  index: String,
  id: String,
  preference: Option[String] = None,
  realtime: Option[Boolean] = None,
  refresh: Option[Boolean] = None,
  routing: Option[String] = None,
  @jsonField("_source") source: Seq[String] = Nil,
  @jsonField("_source_exclude") sourceExclude: Seq[String] = Nil,
  @jsonField("_source_include") sourceInclude: Seq[String] = Nil,
  @jsonField("stored_fields") storedFields: Seq[String] = Nil,
  version: Option[Long] = None,
  @jsonField("version_type") versionType: Option[VersionType] = None
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl(index, "_doc", id)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    preference.foreach { v =>
      queryArgs += "preference" -> v
    }
    realtime.foreach { v =>
      queryArgs += "realtime" -> v.toString
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
    if (sourceExclude.nonEmpty) {
      queryArgs += "_source_exclude" -> sourceExclude.toList.mkString(",")
    }
    if (sourceInclude.nonEmpty) {
      queryArgs += "_source_include" -> sourceInclude.toList.mkString(",")
    }
    if (storedFields.nonEmpty) {
      queryArgs += "stored_fields" -> storedFields.toList.mkString(",")
    }
    version.foreach { v =>
      queryArgs += "version" -> v.toString
    }
    versionType.foreach { v =>
      queryArgs += "version_type" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object GetRequest {
  implicit val jsonDecoder: JsonDecoder[GetRequest] = DeriveJsonDecoder.gen[GetRequest]
  implicit val jsonEncoder: JsonEncoder[GetRequest] = DeriveJsonEncoder.gen[GetRequest]
}
