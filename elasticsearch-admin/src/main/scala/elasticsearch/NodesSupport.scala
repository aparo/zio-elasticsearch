/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.client.NodesActionResolver
import elasticsearch.managers.NodesManager

trait NodesSupport extends NodesActionResolver {
  lazy val nodes = new NodesManager(this)
}
