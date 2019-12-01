/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import io.circe._
import io.circe.derivation.annotations.JsonCodec

/*
 * Allows to use the Mustache language to pre-render a search definition.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html#_validating_templates
 *
 * @param body body the body of the call
 * @param id The id of the stored search template
 */
@JsonCodec
final case class RenderSearchTemplateRequest(
    body: JsonObject,
    id: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_render", "template", id)

  def queryArgs: Map[String, String] = Map.empty[String, String]

}
