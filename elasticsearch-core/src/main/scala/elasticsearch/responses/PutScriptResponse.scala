/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._
/*
 * Creates or updates a script.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
 *
 * @param id Script ID
 * @param body body the body of the call
 * @param context Context name to compile script against
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class PutScriptResponse() {}
