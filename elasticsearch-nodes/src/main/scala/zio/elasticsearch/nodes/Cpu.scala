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

package zio.elasticsearch.nodes
import zio.json._
import zio.json.ast._
final case class Cpu(
  percent: Option[Int] = None,
  sys: Option[String] = None,
  @jsonField("sys_in_millis") sysInMillis: Option[Long] = None,
  total: Option[String] = None,
  @jsonField("total_in_millis") totalInMillis: Option[Long] = None,
  user: Option[String] = None,
  @jsonField("user_in_millis") userInMillis: Option[Long] = None,
  @jsonField("load_average") loadAverage: Option[Map[String, Double]] = None
)

object Cpu {
  implicit lazy val jsonCodec: JsonCodec[Cpu] = DeriveJsonCodec.gen[Cpu]
}
