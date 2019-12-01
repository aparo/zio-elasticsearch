/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.exception

final case class InvalidJsonValue(val msg: String) extends Exception
