/*
 * Copyright 2019-2023 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.elasticsearch

import zio.json.{ JsonCodec, _ }

sealed trait ClusterHealthStatus

object ClusterHealthStatus {

  implicit final val decoder: JsonDecoder[ClusterHealthStatus] =
    DeriveJsonDecoderEnum.gen[ClusterHealthStatus]
  implicit final val encoder: JsonEncoder[ClusterHealthStatus] =
    DeriveJsonEncoderEnum.gen[ClusterHealthStatus]
  implicit final val codec: JsonCodec[ClusterHealthStatus] = JsonCodec(encoder, decoder)

  case object green extends ClusterHealthStatus

  case object yellow extends ClusterHealthStatus

  case object red extends ClusterHealthStatus

}

sealed trait GroupBy

object GroupBy {

  case object nodes extends GroupBy

  case object parents extends GroupBy

  implicit final val decoder: JsonDecoder[GroupBy] =
    DeriveJsonDecoderEnum.gen[GroupBy]
  implicit final val encoder: JsonEncoder[GroupBy] =
    DeriveJsonEncoderEnum.gen[GroupBy]
  implicit final val codec: JsonCodec[GroupBy] = JsonCodec(encoder, decoder)

}

sealed trait VersionType

object VersionType {

  case object internal extends VersionType

  case object force extends VersionType

  implicit final val decoder: JsonDecoder[VersionType] =
    DeriveJsonDecoderEnum.gen[VersionType]
  implicit final val encoder: JsonEncoder[VersionType] =
    DeriveJsonEncoderEnum.gen[VersionType]
  implicit final val codec: JsonCodec[VersionType] = JsonCodec(encoder, decoder)
}

sealed trait Refresh

case object Refresh {

  case object `true` extends Refresh

  case object `false` extends Refresh

  case object wait_for extends Refresh

  implicit final val decoder: JsonDecoder[Refresh] =
    DeriveJsonDecoderEnum.gen[Refresh]
  implicit final val encoder: JsonEncoder[Refresh] =
    DeriveJsonEncoderEnum.gen[Refresh]
  implicit final val codec: JsonCodec[Refresh] = JsonCodec(encoder, decoder)

  def fromValue(value: Boolean): Refresh =
    if (value) Refresh.`true` else Refresh.`false`

}

sealed trait Conflicts

object Conflicts {

  case object abort extends Conflicts

  case object proceed extends Conflicts

  implicit final val decoder: JsonDecoder[Conflicts] =
    DeriveJsonDecoderEnum.gen[Conflicts]
  implicit final val encoder: JsonEncoder[Conflicts] =
    DeriveJsonEncoderEnum.gen[Conflicts]
  implicit final val codec: JsonCodec[Conflicts] = JsonCodec(encoder, decoder)
}
@jsonEnumLowerCase
sealed trait Type extends EnumLowerCase

object Type {

  case object Cpu extends Type

  case object Wait extends Type

  case object Block extends Type

  implicit final val decoder: JsonDecoder[Type] =
    DeriveJsonDecoderEnum.gen[Type]
  implicit final val encoder: JsonEncoder[Type] =
    DeriveJsonEncoderEnum.gen[Type]
  implicit final val codec: JsonCodec[Type] = JsonCodec(encoder, decoder)
}

sealed trait OutputFormat

object OutputFormat {

  case object detailed extends OutputFormat

  case object text extends OutputFormat

  implicit final val decoder: JsonDecoder[OutputFormat] =
    DeriveJsonDecoderEnum.gen[OutputFormat]
  implicit final val encoder: JsonEncoder[OutputFormat] =
    DeriveJsonEncoderEnum.gen[OutputFormat]
  implicit final val codec: JsonCodec[OutputFormat] = JsonCodec(encoder, decoder)
}
@jsonEnumLowerCase
sealed trait Level extends EnumLowerCase

case object Level {

  case object cluster extends Level

  case object indices extends Level

  case object node extends Level

  case object shards extends Level

  implicit final val decoder: JsonDecoder[Level] =
    DeriveJsonDecoderEnum.gen[Level]
  implicit final val encoder: JsonEncoder[Level] =
    DeriveJsonEncoderEnum.gen[Level]
  implicit final val codec: JsonCodec[Level] = JsonCodec(encoder, decoder)
}

@jsonEnumLowerCase sealed trait OpType extends EnumLowerCase

object OpType {

  case object index extends OpType

  case object create extends OpType

  case object delete extends OpType

  case object update extends OpType

  implicit final val decoder: JsonDecoder[OpType] =
    DeriveJsonDecoderEnum.gen[OpType]
  implicit final val encoder: JsonEncoder[OpType] =
    DeriveJsonEncoderEnum.gen[OpType]
  implicit final val codec: JsonCodec[OpType] = JsonCodec(encoder, decoder)

}
@jsonEnumLowerCase
sealed trait WaitForStatus extends EnumLowerCase

object WaitForStatus {

  case object green extends WaitForStatus

  case object yellow extends WaitForStatus

  case object red extends WaitForStatus

  implicit final val decoder: JsonDecoder[WaitForStatus] =
    DeriveJsonDecoderEnum.gen[WaitForStatus]
  implicit final val encoder: JsonEncoder[WaitForStatus] =
    DeriveJsonEncoderEnum.gen[WaitForStatus]
  implicit final val codec: JsonCodec[WaitForStatus] = JsonCodec(encoder, decoder)
}
@jsonEnumLowerCase
sealed trait DefaultOperator extends EnumLowerCase

object DefaultOperator {

  case object AND extends DefaultOperator

  case object OR extends DefaultOperator

  implicit final val decoder: JsonDecoder[DefaultOperator] =
    DeriveJsonDecoderEnum.gen[DefaultOperator]
  implicit final val encoder: JsonEncoder[DefaultOperator] =
    DeriveJsonEncoderEnum.gen[DefaultOperator]
  implicit final val codec: JsonCodec[DefaultOperator] = JsonCodec(encoder, decoder)

}

sealed trait ExpandWildcards

object ExpandWildcards {

  case object open extends ExpandWildcards

  case object closed extends ExpandWildcards

  case object none extends ExpandWildcards

  case object all extends ExpandWildcards

  implicit final val decoder: JsonDecoder[ExpandWildcards] =
    DeriveJsonDecoderEnum.gen[ExpandWildcards]
  implicit final val encoder: JsonEncoder[ExpandWildcards] =
    DeriveJsonEncoderEnum.gen[ExpandWildcards]
  implicit final val codec: JsonCodec[ExpandWildcards] = JsonCodec(encoder, decoder)
}

sealed trait SearchType

object SearchType {

  case object query_then_fetch extends SearchType

  case object dfs_query_then_fetch extends SearchType

  implicit final val decoder: JsonDecoder[SearchType] =
    DeriveJsonDecoderEnum.gen[SearchType]
  implicit final val encoder: JsonEncoder[SearchType] =
    DeriveJsonEncoderEnum.gen[SearchType]
  implicit final val codec: JsonCodec[SearchType] = JsonCodec(encoder, decoder)
}

sealed trait SuggestMode

object SuggestMode {

  case object missing extends SuggestMode

  case object popular extends SuggestMode

  case object always extends SuggestMode

  implicit final val decoder: JsonDecoder[SuggestMode] =
    DeriveJsonDecoderEnum.gen[SuggestMode]
  implicit final val encoder: JsonEncoder[SuggestMode] =
    DeriveJsonEncoderEnum.gen[SuggestMode]
  implicit final val codec: JsonCodec[SuggestMode] = JsonCodec(encoder, decoder)
}

sealed trait WaitForEvents

object WaitForEvents {

  case object immediate extends WaitForEvents

  case object urgent extends WaitForEvents

  case object high extends WaitForEvents

  case object normal extends WaitForEvents

  case object low extends WaitForEvents

  case object languid extends WaitForEvents

  implicit final val decoder: JsonDecoder[WaitForEvents] =
    DeriveJsonDecoderEnum.gen[WaitForEvents]
  implicit final val encoder: JsonEncoder[WaitForEvents] =
    DeriveJsonEncoderEnum.gen[WaitForEvents]
  implicit final val codec: JsonCodec[WaitForEvents] = JsonCodec(encoder, decoder)
}

sealed abstract class Size(override val entryName: String) extends EnumEntryName

object Size {

  case object Empty extends Size("")
  case object K extends Size("k")
  case object M extends Size("m")
  case object G extends Size("g")
  case object T extends Size("t")
  case object P extends Size("p")

  implicit final val decoder: JsonDecoder[Size] =
    DeriveJsonDecoderEnum.gen[Size]
  implicit final val encoder: JsonEncoder[Size] =
    DeriveJsonEncoderEnum.gen[Size]
  implicit final val codec: JsonCodec[Size] = JsonCodec(encoder, decoder)
}

sealed abstract class Time(override val entryName: String) extends EnumEntryName

object Time {

  case object Days extends Time("d")
  case object Hours extends Time("h")
  case object Minutes extends Time("m")
  case object Seconds extends Time("s")
  case object Milliseconds extends Time("ms")
  case object Microseconds extends Time("micros")
  case object Nanoseconds extends Time("nanos")

  implicit final val decoder: JsonDecoder[Time] =
    DeriveJsonDecoderEnum.gen[Time]
  implicit final val encoder: JsonEncoder[Time] =
    DeriveJsonEncoderEnum.gen[Time]
  implicit final val codec: JsonCodec[Time] = JsonCodec(encoder, decoder)
}

sealed abstract class Bytes(override val entryName: String) extends EnumEntryName

object Bytes {

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

  implicit final val decoder: JsonDecoder[Bytes] =
    DeriveJsonDecoderEnum.gen[Bytes]
  implicit final val encoder: JsonEncoder[Bytes] =
    DeriveJsonEncoderEnum.gen[Bytes]
  implicit final val codec: JsonCodec[Bytes] = JsonCodec(encoder, decoder)
}
