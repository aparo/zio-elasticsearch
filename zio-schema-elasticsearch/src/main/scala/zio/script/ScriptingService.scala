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

package zio.script

import zio.exception.FrameworkException
import zio.schema.elasticsearch.{ Script, Validator }

trait ScriptingService {

  def execute(script: Script, context: Map[String, Any]): Either[FrameworkException, AnyRef]

  def validate[T](script: Validator, context: Map[String, Any], value: T): Either[FrameworkException, Boolean]

  def removeFromCache(language: String, script: String): Unit

  def cleanCache(): Unit
}
