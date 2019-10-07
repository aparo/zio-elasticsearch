/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import elasticsearch.VersionType
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

import scala.collection.mutable

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-termvectors.html
 *
 * @param body body the body of the call
 * @param index The index in which the document resides.
 * @param docType The type of the document.
 * @param parent Parent id of documents. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param preference Specify the node or shard the operation should be performed on (default: random) .Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param payloads Specifies if term payloads should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param offsets Specifies if term offsets should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param termStatistics Specifies if total term frequency and document frequency should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param version Explicit version number for concurrency control
 * @param positions Specifies if term positions should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param versionType Specific version type
 * @param fields A list of fields to return. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param realtime Specifies if requests are real-time as opposed to near-real-time (default: true).
 * @param routing Specific routing value. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param ids A list of documents ids. You must define ids as parameter or set "ids" or "docs" in the request body
 */
@JsonCodec
final case class MultiTermVectorsRequest(
  body: Json,
  index: Option[String] = None,
  docType: Option[String] = None,
  parent: Option[String] = None,
  preference: String = "random",
  @JsonKey("field_statistics") fieldStatistics: Boolean = true,
  payloads: Boolean = true,
  offsets: Boolean = true,
  @JsonKey("term_statistics") termStatistics: Boolean = false,
  version: Option[Double] = None,
  positions: Boolean = true,
  @JsonKey("version_type") versionType: Option[VersionType] = None,
  fields: Seq[String] = Nil,
  realtime: Boolean = true,
  routing: Option[String] = None,
  ids: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(index, docType, "_mtermvectors")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    parent.map { v =>
      queryArgs += ("parent" -> v)
    }
    if (preference != "random") queryArgs += ("preference" -> preference)
    if (fieldStatistics != true)
      queryArgs += ("field_statistics" -> fieldStatistics.toString)
    if (payloads != true) queryArgs += ("payloads" -> payloads.toString)
    if (offsets != true) queryArgs += ("offsets" -> offsets.toString)
    if (termStatistics != false)
      queryArgs += ("term_statistics" -> termStatistics.toString)
    version.map { v =>
      queryArgs += ("version" -> v.toString)
    }
    if (positions != true) queryArgs += ("positions" -> positions.toString)
    versionType.map { v =>
      queryArgs += ("version_type" -> v.toString)
    }
    if (!fields.isEmpty) {
      queryArgs += ("fields" -> fields.toList.mkString(","))
    }
    if (realtime != true) queryArgs += ("realtime" -> realtime.toString)
    routing.map { v =>
      queryArgs += ("routing" -> v)
    }
    if (!ids.isEmpty) {
      queryArgs += ("ids" -> ids.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
