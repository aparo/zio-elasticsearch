/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-scroll.html
 *
 * @param scrollId A comma-separated list of scroll IDs to clear
 * @param body body the body of the call
 */
@JsonCodec
case class ClearScrollResponse() {}
