/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
 *
 * @param lang Script language
 * @param id Script ID
 * @param body body the body of the call
 */
@JsonCodec
case class PutStoredScriptResponse() {}
