/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._
/*
 * Performs the analysis process on a text and return the tokens breakdown of the text.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-analyze.html
 *
 * @param body body the body of the call
 * @param index The name of the index to scope the operation
 */
@JsonCodec
final case class IndicesAnalyzeResponse() {}
