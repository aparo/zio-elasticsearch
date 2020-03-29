/*
 * Copyright 2019-2020 Alberto Paro
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

import io.circe.JsonObject
import zio.auth.AuthContext

trait PreSaveHooks[BaseDocument] {
  def preSaveHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PreSaveJsonHooks {
  def preSaveJsonHooks: List[(AuthContext, JsonObject) => JsonObject]
}

trait PostSaveHooks[BaseDocument] {
  def postSaveHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PreLoadHooks {
  def preLoadHooks: List[(AuthContext, JsonObject) => JsonObject]
}

trait PreUpdateJsonHooks[BaseDocument] {
  def preUpdateJsonHooks: List[(AuthContext, JsonObject) => JsonObject]
}

trait PostLoadHooks[BaseDocument] {
  def postLoadHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PreDeleteHooks[BaseDocument] {
  def preDeleteHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PostDeleteHooks[BaseDocument] {
  def postDeleteHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PreUpdateHooks[BaseDocument] {
  def preUpdateHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PostUpdateHooks[BaseDocument] {
  def postUpdateHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}
