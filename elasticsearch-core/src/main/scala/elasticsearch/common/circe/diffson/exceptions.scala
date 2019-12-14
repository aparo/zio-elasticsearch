/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.common.circe.diffson

class DiffsonException(msg: String) extends Exception(msg)

class PatchException(msg: String) extends DiffsonException(msg)

class PointerException(msg: String) extends DiffsonException(msg)

class FormatException(msg: String) extends DiffsonException(msg)
