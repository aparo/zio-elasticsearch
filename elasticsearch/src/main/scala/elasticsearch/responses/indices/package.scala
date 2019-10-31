/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import elasticsearch.responses.cluster.{ ClusterIndex, IndexTemplate }

package object indices {
  type IndicesGetMappingResponse = Map[String, ClusterIndex]
  type IndicesGetTemplateResponse = Map[String, IndexTemplate]
  type IndicesExistsTemplateResponse = Map[String, IndexTemplate]

}
