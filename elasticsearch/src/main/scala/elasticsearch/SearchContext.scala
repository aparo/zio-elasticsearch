/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.nosql

import elasticsearch.mappings.RootDocumentMapping

/**
 * A search context to be used in building query, filter and aggregations
 *
 * @param indices        a list of indices
 * @param mappings       a list of mappings to be used
 * @param defaultMapping the current mapping
 */
final case class SearchContext(
  indices: List[String],
  mappings: Map[String, RootDocumentMapping],
  defaultMapping: Option[(String, RootDocumentMapping)] = None
) {
  lazy val types = mappings.keySet

  def mapping = defaultMapping.getOrElse(mappings.head)

}

object SearchContext {
  val INVALID_FIELD = "Invalid Field"
}
