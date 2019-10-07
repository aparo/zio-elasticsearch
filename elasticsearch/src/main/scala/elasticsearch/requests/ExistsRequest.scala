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

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
 *
 * @param index The name of the index
 * @param docType The type of the document (use `_all` to fetch the first document matching the ID across all types)
 * @param id The document ID
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param parent The ID of the parent document
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param refresh Refresh the shard containing the document before performing the operation
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 * @param realtime Specify whether to perform the operation in realtime or search mode
 * @param routing Specific routing value
 * @param storedFields A list of stored fields to return in the response
 */
@JsonCodec
final case class ExistsRequest(
  index: String,
  id: String,
  @JsonKey("_source_include") sourceInclude: Seq[String] = Nil,
  parent: Option[String] = None,
  @JsonKey("_source") source: Seq[String] = Nil,
  refresh: Option[Boolean] = None,
  preference: String = "random",
  @JsonKey("_source_exclude") sourceExclude: Seq[String] = Nil,
  version: Option[Double] = None,
  @JsonKey("version_type") versionType: Option[VersionType] = None,
  realtime: Option[Boolean] = None,
  routing: Option[String] = None,
  @JsonKey("stored_fields") storedFields: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "HEAD"

  def urlPath: String = this.makeUrl(index, id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!sourceInclude.isEmpty) {
      queryArgs += ("_source_include" -> sourceInclude.toList.mkString(","))
    }
    parent.map { v =>
      queryArgs += ("parent" -> v)
    }
    if (!source.isEmpty) {
      queryArgs += ("_source" -> source.toList.mkString(","))
    }
    refresh.map { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    if (preference != "random") queryArgs += ("preference" -> preference)
    if (!sourceExclude.isEmpty) {
      queryArgs += ("_source_exclude" -> sourceExclude.toList.mkString(","))
    }
    version.map { v =>
      queryArgs += ("version" -> v.toString)
    }
    versionType.map { v =>
      queryArgs += ("version_type" -> v.toString)
    }
    realtime.map { v =>
      queryArgs += ("realtime" -> v.toString)
    }
    routing.map { v =>
      queryArgs += ("routing" -> v)
    }
    if (!storedFields.isEmpty) {
      queryArgs += ("stored_fields" -> storedFields.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
