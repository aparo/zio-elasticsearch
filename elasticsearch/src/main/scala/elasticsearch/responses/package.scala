/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.responses.cluster.{ ClusterIndex, IndexTemplate }

import io.circe._

package object responses {

  type SearchResponse = SearchResult[JsonObject]
  type HitResponse = ResultDocument[JsonObject]

  lazy val EmptySearchResponse = SearchResult[JsonObject]()

  type GetIndexTemplatesResponse = Map[String, IndexTemplate]

  type GetMappingsResponse = Map[String, ClusterIndex]

}
