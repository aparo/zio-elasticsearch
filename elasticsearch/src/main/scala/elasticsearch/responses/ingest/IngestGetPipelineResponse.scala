/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.ingest

import io.circe.derivation.annotations._
/*
 * Returns a pipeline.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-pipeline-api.html
 *
 * @param id Comma separated list of pipeline ids. Wildcards supported
 * @param masterTimeout Explicit operation timeout for connection to master node
 */
@JsonCodec
final case class IngestGetPipelineResponse() {}
