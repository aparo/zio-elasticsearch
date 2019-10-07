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
 * @param timeout Explicit operation timeout
 * @param masterTimeout Specify timeout for connection to master
 */
@JsonCodec
case class DeleteIndexTemplateResponse() {}
