/*
 * Copyright 2019 Alberto Paro
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

package zio.schema

import io.circe.Json.Obj
import zio.json._

@jsonDerive
final case class Script(
  name: String,
  script: String,
  language: String,
  parameters: Json.Obj = Json.Obj(),
  description: String = "",
  scriptParameters: List[ScriptParameter] = Nil,
  returnType: Option[ScriptParameter] = None
) {
  def id: String = name
}

@jsonDerive
final case class Argument(name: String, `type`: String) {
  def id: String = name
}

@jsonDerive
final case class Validator(
  name: String,
  script: String,
  language: String,
  parameters: Json.Obj = Json.Obj(),
  description: String = "",
  scriptParameters: List[ScriptParameter] = Nil
) {
  def id: String = name
}
