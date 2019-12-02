/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.client.SnapshotActionResolver
import elasticsearch.managers.SnapshotManager

trait SnapshotSupport extends SnapshotActionResolver {
  lazy val snapshot = new SnapshotManager(this)
}
