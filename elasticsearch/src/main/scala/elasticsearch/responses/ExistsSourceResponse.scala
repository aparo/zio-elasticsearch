/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._
/*
 * Returns information about whether a document source exists in an index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
 *
 * @param index The name of the index
 * @param id The document ID
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param realtime Specify whether to perform the operation in realtime or search mode
 * @param refresh Refresh the shard containing the document before performing the operation
 * @param routing Specific routing value
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param sourceExcludes A list of fields to exclude from the returned _source field
 * @param sourceIncludes A list of fields to extract and return from the _source field
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 */
@JsonCodec
final case class ExistsSourceResponse() {}
