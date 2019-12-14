/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import enumeratum.EnumEntry.{ Lowercase, Uppercase }
import enumeratum._

sealed trait ClusterHealthStatus extends EnumEntry

object ClusterHealthStatus extends CirceEnum[ClusterHealthStatus] with Enum[ClusterHealthStatus] {

  val values = findValues

  case object green extends ClusterHealthStatus

  case object yellow extends ClusterHealthStatus

  case object red extends ClusterHealthStatus

}

sealed trait GroupBy extends EnumEntry

case object GroupBy extends CirceEnum[GroupBy] with Enum[GroupBy] {

  case object nodes extends GroupBy

  case object parents extends GroupBy

  val values = findValues
}

sealed trait VersionType extends EnumEntry

case object VersionType extends CirceEnum[VersionType] with Enum[VersionType] {

  case object internal extends VersionType

  case object force extends VersionType

  val values = findValues
}

sealed trait Refresh extends EnumEntry

case object Refresh extends CirceEnum[Refresh] with Enum[Refresh] {

  case object `true` extends Refresh

  case object `false` extends Refresh

  case object wait_for extends Refresh

  val values = findValues

  def fromValue(value: Boolean): Refresh =
    if (value) Refresh.`true` else Refresh.`false`

}

sealed trait Conflicts extends EnumEntry

case object Conflicts extends CirceEnum[Conflicts] with Enum[Conflicts] {

  case object abort extends Conflicts

  case object proceed extends Conflicts

  val values = findValues
}

sealed trait Type extends EnumEntry with Lowercase

case object Type extends CirceEnum[Type] with Enum[Type] {

  case object Cpu extends Type

  case object Wait extends Type

  case object Block extends Type

  val values = findValues
}

sealed trait OutputFormat extends EnumEntry

case object OutputFormat extends CirceEnum[OutputFormat] with Enum[OutputFormat] {

  case object detailed extends OutputFormat

  case object text extends OutputFormat

  val values = findValues
}

sealed trait Level extends EnumEntry with Lowercase

case object Level extends CirceEnum[Level] with Enum[Level] {

  case object cluster extends Level

  case object indices extends Level

  case object node extends Level

  case object shards extends Level

  val values = findValues
}

sealed trait OpType extends EnumEntry with Lowercase

case object OpType extends CirceEnum[OpType] with Enum[OpType] {

  case object index extends OpType

  case object create extends OpType

  case object delete extends OpType

  case object update extends OpType

  val values = findValues
}

sealed trait WaitForStatus extends EnumEntry with Lowercase

case object WaitForStatus extends CirceEnum[WaitForStatus] with Enum[WaitForStatus] {

  case object green extends WaitForStatus

  case object yellow extends WaitForStatus

  case object red extends WaitForStatus

  val values = findValues
}

sealed trait DefaultOperator extends EnumEntry with Lowercase

case object DefaultOperator extends CirceEnum[DefaultOperator] with Enum[DefaultOperator] {

  case object AND extends DefaultOperator

  case object OR extends DefaultOperator

  val values = findValues
}

sealed trait ExpandWildcards extends EnumEntry

case object ExpandWildcards extends CirceEnum[ExpandWildcards] with Enum[ExpandWildcards] {

  case object open extends ExpandWildcards

  case object closed extends ExpandWildcards

  case object none extends ExpandWildcards

  case object all extends ExpandWildcards

  val values = findValues
}

sealed trait SearchType extends EnumEntry

case object SearchType extends CirceEnum[SearchType] with Enum[SearchType] {

  case object query_then_fetch extends SearchType

  case object dfs_query_then_fetch extends SearchType

  val values = findValues
}

sealed trait SuggestMode extends EnumEntry

object SuggestMode extends CirceEnum[SuggestMode] with Enum[SuggestMode] {

  case object missing extends SuggestMode

  case object popular extends SuggestMode

  case object always extends SuggestMode

  val values = findValues
}

sealed trait WaitForEvents extends EnumEntry

object WaitForEvents extends CirceEnum[WaitForEvents] with Enum[WaitForEvents] {

  case object immediate extends WaitForEvents

  case object urgent extends WaitForEvents

  case object high extends WaitForEvents

  case object normal extends WaitForEvents

  case object low extends WaitForEvents

  case object languid extends WaitForEvents

  val values = findValues
}

sealed abstract class Size(override val entryName: String) extends EnumEntry

object Size extends CirceEnum[Size] with Enum[Size] {

  case object Empty extends Size("")
  case object K extends Size("k")
  case object M extends Size("m")
  case object G extends Size("g")
  case object T extends Size("t")
  case object P extends Size("p")

  val values = findValues
}

sealed abstract class Time(override val entryName: String) extends EnumEntry

object Time extends CirceEnum[Time] with Enum[Time] {

  case object Days extends Time("d")
  case object Hours extends Time("h")
  case object Minutes extends Time("m")
  case object Seconds extends Time("s")
  case object Milliseconds extends Time("ms")
  case object Microseconds extends Time("micros")
  case object Nanoseconds extends Time("nanos")

  val values = findValues
}

sealed abstract class Bytes(override val entryName: String) extends EnumEntry

object Bytes extends CirceEnum[Bytes] with Enum[Bytes] {

  case object Byte extends Bytes("b")
  case object Kilo extends Bytes("k")
  case object KiloByte extends Bytes("kb")
  case object Mega extends Bytes("m")
  case object MegaByte extends Bytes("mb")
  case object Giga extends Bytes("g")
  case object GigaByte extends Bytes("gb")
  case object Tera extends Bytes("t")
  case object TeraByte extends Bytes("tb")
  case object Peta extends Bytes("p")
  case object PetaByte extends Bytes("pb")

  val values = findValues
}
