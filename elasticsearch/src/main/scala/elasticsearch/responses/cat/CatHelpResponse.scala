/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cat

import io.circe.derivation.annotations._
/*
 * Returns help for the Cat APIs.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat.html
 *
 * @param help Return help information
 * @param s Comma-separated list of column names or column aliases to sort by
 */
@JsonCodec
final case class CatHelpResponse() {}
