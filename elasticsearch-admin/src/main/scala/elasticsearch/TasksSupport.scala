/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.client.TasksActionResolver
import elasticsearch.managers.TasksManager

trait TasksSupport extends TasksActionResolver {
  lazy val tasks = new TasksManager(this)

}
