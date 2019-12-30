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

import scala.annotation.StaticAnnotation

sealed trait StorageAnnotation extends StaticAnnotation {
  def value: String
}

final case class IgniteStorage() extends StorageAnnotation {
  override def value: String = "ignite"
}

final case class ElasticSearchStorage() extends StorageAnnotation {
  override def value: String = "elasticsearch"
}

final case class ColumnarStorage() extends StorageAnnotation {
  override def value: String = "columnar"
}

final case class MongoDBStorage() extends StorageAnnotation {
  override def value: String = "mongodb"
}
