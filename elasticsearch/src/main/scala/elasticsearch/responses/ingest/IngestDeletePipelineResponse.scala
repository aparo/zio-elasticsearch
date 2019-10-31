/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.ingest

import io.circe.derivation.annotations._
/*
 * Deletes a pipeline.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/delete-pipeline-api.html
 *
 * @param id Pipeline ID
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class IngestDeletePipelineResponse() {}
