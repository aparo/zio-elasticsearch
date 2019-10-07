/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-get.html
 *
 * @param body body the body of the call
 * @param index The name of the index
 * @param docType The type of the document
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param refresh Refresh the shard containing the document before performing the operation
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param realtime Specify whether to perform the operation in realtime or search mode
 * @param routing Specific routing value
 * @param storedFields A comma-separated list of stored fields to return in the response
 */
@JsonCodec
case class MultiGetResponse(docs: List[GetResponse]) {}
