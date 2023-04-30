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

package zio.elasticsearch.cluster.reroute
import zio.json._
final case class Command(
  cancel: Option[CommandCancelAction] = None,
  move: Option[CommandMoveAction] = None,
  @jsonField("allocate_replica") allocateReplica: Option[
    CommandAllocateReplicaAction
  ] = None,
  @jsonField("allocate_stale_primary") allocateStalePrimary: Option[
    CommandAllocatePrimaryAction
  ] = None,
  @jsonField("allocate_empty_primary") allocateEmptyPrimary: Option[
    CommandAllocatePrimaryAction
  ] = None
)

object Command {
  implicit lazy val jsonCodec: JsonCodec[Command] = DeriveJsonCodec.gen[Command]
}
