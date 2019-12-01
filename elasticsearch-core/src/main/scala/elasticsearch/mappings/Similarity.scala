/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.mappings

import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Lowercase

sealed trait Similarity extends EnumEntry with Lowercase

object Similarity extends Enum[Similarity] with CirceEnum[Similarity] {

  case object Classic extends Similarity

  case object BM25 extends Similarity

  val values = findValues

}
