/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.nodes

import io.circe.derivation.annotations._
/*
 * Reloads secure settings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/secure-settings.html#reloadable-secure-settings
 *
 * @param nodeId A comma-separated list of node IDs to span the reload/reinit call. Should stay empty because reloading usually involves all cluster nodes.
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class NodesReloadSecureSettingsResponse() {}
