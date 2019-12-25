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

import elasticsearch.VersionType
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable

/*
 * Returns information about whether a document source exists in an index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
 *
 * @param index The name of the index
 * @param id The document ID
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param realtime Specify whether to perform the operation in realtime or search mode
 * @param refresh Refresh the shard containing the document before performing the operation
 * @param routing Specific routing value
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param sourceExcludes A list of fields to exclude from the returned _source field
 * @param sourceIncludes A list of fields to extract and return from the _source field
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 */
@JsonCodec
final case class ExistsSourceRequest(
    index: String,
    id: String,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    @JsonKey("_source") source: Seq[String] = Nil,
    @JsonKey("_source_excludes") sourceExcludes: Seq[String] = Nil,
    @JsonKey("_source_includes") sourceIncludes: Seq[String] = Nil,
    version: Option[Long] = None,
    @JsonKey("version_type") versionType: Option[VersionType] = None
) extends ActionRequest {
  def method: String = "HEAD"

  def urlPath: String = this.makeUrl(index, "_source", id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    preference.foreach { v =>
      queryArgs += ("preference" -> v)
    }
    realtime.foreach { v =>
      queryArgs += ("realtime" -> v.toString)
    }
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    routing.foreach { v =>
      queryArgs += ("routing" -> v)
    }
    if (source.nonEmpty) {
      queryArgs += ("_source" -> source.toList.mkString(","))
    }
    if (sourceExcludes.nonEmpty) {
      queryArgs += ("_source_excludes" -> sourceExcludes.toList.mkString(","))
    }
    if (sourceIncludes.nonEmpty) {
      queryArgs += ("_source_includes" -> sourceIncludes.toList.mkString(","))
    }
    version.foreach { v =>
      queryArgs += ("version" -> v.toString)
    }
    versionType.foreach { v =>
      queryArgs += ("version_type" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
