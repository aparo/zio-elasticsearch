/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import io.circe._
import io.circe.derivation.annotations._

/*
 * Allows an arbitrary script to be executed and a result to be returned
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/painless/master/painless-execute-api.html
 *
 * @param body body the body of the call
 */
@JsonCodec
final case class ScriptsPainlessExecuteRequest(body: JsonObject)
    extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_scripts/painless/_execute"

  def queryArgs: Map[String, String] = Map.empty

}
