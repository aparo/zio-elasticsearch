/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-count.html
 *
 */
@JsonCodec
case class CountResponse(@JsonKey("_shards") shards: Shards, count: Long)
