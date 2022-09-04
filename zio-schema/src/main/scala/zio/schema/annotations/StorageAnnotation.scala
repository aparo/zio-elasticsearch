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

import scala.annotation.StaticAnnotation

// not sealed to allows external storage registration via StorageSupport.register(class)
trait StorageAnnotation extends StaticAnnotation {
  def value: String
  def className: String
  def modelClass: String
  def objectClass: String
  def metaName: String
}

final case class ElasticSearchStorage() extends StorageAnnotation {
  override def value: String = "elasticsearch"
  override def className: String = "ElasticSearchStorage"
  override def modelClass: String = "_root_.elasticsearch.orm.ElasticSearchDocument"
  override def objectClass: String = "_root_.elasticsearch.orm.ElasticSearchMeta"
  def metaName: String = "_es"
}

final case class MongoDBStorage() extends StorageAnnotation {
  override def value: String = "mongodb"
  override def className: String = "MongoDBStorage"
  override def modelClass: String = "_root_.mongodb.orm.MongoDBDocument"
  override def objectClass: String = "_root_.mongodb.orm.MongoDBMeta"
  def metaName: String = "_mongo"
}

final case class IgniteStorage() extends StorageAnnotation {
  override def value: String = "ignite"
  override def className: String = "IgniteStorage"
  override def modelClass: String = "_root_.ignite.orm.IgniteDocument"
  override def objectClass: String = "_root_.ignite.orm.IgniteMeta"
  def metaName: String = "_ignite"
}

final case class ParquetStorage() extends StorageAnnotation {
  override def value: String = "parquet"
  override def className: String = "ParquetStorage"
  override def modelClass: String = "_root_.parquet.orm.ParquetDocument"
  override def objectClass: String = "_root_.parquet.orm.ParquetMeta"
  def metaName: String = "_parquet"
}
