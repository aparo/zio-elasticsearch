/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._
/*
 * Updates index aliases.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
 *
 * @param body body the body of the call
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Request timeout
 */
@JsonCodec
final case class IndicesUpdateAliasesResponse() {}
