/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-termvectors.html
 *
 * @param index The index in which the document resides.
 * @param docType The type of the document.
 * @param id The id of the document, when not specified a doc param should be supplied.
 * @param body body the body of the call
 * @param parent Parent id of documents.
 * @param preference Specify the node or shard the operation should be performed on (default: random).
 * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned.
 * @param payloads Specifies if term payloads should be returned.
 * @param offsets Specifies if term offsets should be returned.
 * @param termStatistics Specifies if total term frequency and document frequency should be returned.
 * @param version Explicit version number for concurrency control
 * @param positions Specifies if term positions should be returned.
 * @param versionType Specific version type
 * @param fields A comma-separated list of fields to return.
 * @param realtime Specifies if request is real-time as opposed to near-real-time (default: true).
 * @param routing Specific routing value.
 */
@JsonCodec
case class TermVectorsResponse() {}
