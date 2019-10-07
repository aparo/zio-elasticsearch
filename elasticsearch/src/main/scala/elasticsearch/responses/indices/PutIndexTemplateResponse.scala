/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
 *
 * @param name The name of the template
 * @param body body the body of the call
 * @param create Whether the index template should only be added if new or can also replace an existing one
 * @param order The order for this template when merging multiple matching ones (higher numbers are merged later, overriding the lower numbers)
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 * @param flatSettings Return settings in flat format (default: false)
 */
@JsonCodec
case class PutIndexTemplateResponse() {}
