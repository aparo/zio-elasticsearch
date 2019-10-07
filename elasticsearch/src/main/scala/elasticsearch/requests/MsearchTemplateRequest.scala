/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import elasticsearch.SearchType
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

import scala.collection.mutable

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/current/search-multi-search.html
 *
 * @param body body the body of the call
 * @param indices A list of index names to use as default
 * @param docTypes A list of document types to use as default
 * @param searchType Search operation type
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 */
@JsonCodec
final case class MsearchTemplateRequest(
  body: Seq[String] = Nil,
  indices: Seq[String] = Nil,
  docTypes: Seq[String] = Nil,
  @JsonKey("search_type") searchType: Option[SearchType] = None,
  @JsonKey("typed_keys") typedKeys: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, docTypes, "_msearch", "template")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    searchType.map { v =>
      queryArgs += ("search_type" -> v.toString)
    }
    typedKeys.map { v =>
      queryArgs += ("typed_keys" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
