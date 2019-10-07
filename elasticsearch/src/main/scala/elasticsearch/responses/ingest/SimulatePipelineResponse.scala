/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.ingest

import io.circe.derivation.annotations._

/*
 * https://www.elastic.co/guide/en/elasticsearch/plugins/master/ingest.html
 *
 * @param body body the body of the call
 * @param id Pipeline ID
 * @param verbose Verbose mode. Display data output for each processor in executed pipeline
 */
@JsonCodec
case class SimulatePipelineResponse() {}
