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
 * @param id Comma separated list of pipeline ids. Wildcards supported
 * @param masterTimeout Explicit operation timeout for connection to master node
 */
@JsonCodec
case class GetPipelineResponse() {}
