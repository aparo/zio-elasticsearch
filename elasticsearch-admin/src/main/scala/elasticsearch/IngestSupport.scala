/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.client.IngestActionResolver
import elasticsearch.managers.IngestManager

trait IngestSupport extends IngestActionResolver {
  lazy val ingest = new IngestManager(this)
}
