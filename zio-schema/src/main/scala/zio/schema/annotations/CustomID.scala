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

package zio.schema.annotations

trait WithIndex {
  def index: String

  def index_=(value: String): Unit
}

trait WithType {
  def `type`: String

  def type_=(value: String): Unit
}

trait WithVersion {
  def version: Long

  def version_=(value: Long): Unit
}

trait FullId extends WithId with WithType with WithIndex

trait CustomId {

  def calcId(): String

}
