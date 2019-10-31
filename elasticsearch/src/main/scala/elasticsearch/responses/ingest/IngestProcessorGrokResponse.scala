/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.ingest

import io.circe.derivation.annotations._
/*
 * Returns a list of the built-in patterns.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/grok-processor.html#grok-processor-rest-get
 *

 */
@JsonCodec
final case class IngestProcessorGrokResponse() {}
