/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cluster

import elasticsearch.mappings.RootDocumentMapping
import io.circe.Json
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import elasticsearch.Settings

@JsonCodec
final case class IndexTemplate(
  @JsonKey("index_patterns") indexPatterns: List[String],
  aliases: Map[String, Json] = Map.empty[String, Json],
  settings: Settings = Settings.ElasticSearchBase,
  mappings: Map[String, RootDocumentMapping] = Map.empty[String, RootDocumentMapping],
  order: Int = 1000
)
