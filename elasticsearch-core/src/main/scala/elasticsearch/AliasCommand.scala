/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

final case class AliasCommand(command: String, index: String, alias: String)
