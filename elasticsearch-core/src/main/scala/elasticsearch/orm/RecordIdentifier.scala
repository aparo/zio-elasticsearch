/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.orm

final case class RecordIdentifier(datastore: String,
                                  `type`: String,
                                  id: String)
