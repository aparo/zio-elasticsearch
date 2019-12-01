/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import elasticsearch.VersionType
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable

/*
 * Returns information and statistics about terms in the fields of a particular document.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-termvectors.html
 *
 * @param index The index in which the document resides.
 * @param id The id of the document, when not specified a doc param should be supplied.
 * @param body body the body of the call
 * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned.
 * @param fields A comma-separated list of fields to return.
 * @param offsets Specifies if term offsets should be returned.
 * @param payloads Specifies if term payloads should be returned.
 * @param positions Specifies if term positions should be returned.
 * @param preference Specify the node or shard the operation should be performed on (default: random).
 * @param realtime Specifies if request is real-time as opposed to near-real-time (default: true).
 * @param routing Specific routing value.
 * @param termStatistics Specifies if total term frequency and document frequency should be returned.
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 */
@JsonCodec
final case class TermvectorsRequest(
  index: String,
  id: String,
  body: Option[JsonObject] = None,
  @JsonKey("field_statistics") fieldStatistics: Boolean = true,
  fields: Seq[String] = Nil,
  offsets: Boolean = true,
  payloads: Boolean = true,
  positions: Boolean = true,
  preference: Option[String] = None,
  realtime: Option[Boolean] = None,
  routing: Option[String] = None,
  @JsonKey("term_statistics") termStatistics: Boolean = false,
  version: Option[Long] = None,
  @JsonKey("version_type") versionType: Option[VersionType] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(index, "_termvectors", id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    body.foreach { v =>
      queryArgs += ("body" -> v.toString)
    }
    if (fieldStatistics != true)
      queryArgs += ("field_statistics" -> fieldStatistics.toString)
    if (fields.nonEmpty) {
      queryArgs += ("fields" -> fields.toList.mkString(","))
    }
    if (offsets != true) queryArgs += ("offsets" -> offsets.toString)
    if (payloads != true) queryArgs += ("payloads" -> payloads.toString)
    if (positions != true) queryArgs += ("positions" -> positions.toString)
    preference.foreach { v =>
      queryArgs += ("preference" -> v)
    }
    realtime.foreach { v =>
      queryArgs += ("realtime" -> v.toString)
    }
    routing.foreach { v =>
      queryArgs += ("routing" -> v)
    }
    if (termStatistics != false)
      queryArgs += ("term_statistics" -> termStatistics.toString)
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

  // Custom Code On
  // Custom Code Off

}
