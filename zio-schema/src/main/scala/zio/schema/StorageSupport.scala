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

import zio.schema.annotations._

object StorageSupport {

  private var storages: List[StorageAnnotation] = List(ElasticSearchStorage(), MongoDBStorage())

  def register(storageAnnotation: StorageAnnotation): Unit =
    if (!storages.contains(storageAnnotation)) {
      storages ::= storageAnnotation
    }

  def availables(): List[StorageAnnotation] = storages

  def classNames(): List[String] = storages.map(_.className)

  def getByName(name: String): Option[StorageAnnotation] = storages.find(_.className == name)

}
