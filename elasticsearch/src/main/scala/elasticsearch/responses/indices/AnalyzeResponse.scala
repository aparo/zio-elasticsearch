/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-analyze.html
 *
 * @param body body the body of the call
 * @param index The name of the index to scope the operation
 * @param preferLocal With `true`, specify that a local shard should be used if available, with `false`, use a random shard (default: true)
 * @param format Format of the output
 */
@JsonCodec
case class AnalyzeResponse() {}
