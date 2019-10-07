/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
 *
 * @param indices A comma-separated list of index names the alias should point to (supports wildcards); use `_all` to perform the operation on all indices.
 * @param name The name of the alias to be created or updated
 * @param body body the body of the call
 * @param timeout Explicit timestamp for the document
 * @param masterTimeout Specify timeout for connection to master
 */
@JsonCodec
case class IndicesAliasesResponse(acknowledged: Boolean)
