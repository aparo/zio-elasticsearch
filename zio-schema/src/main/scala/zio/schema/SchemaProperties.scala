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

import io.circe.derivation.annotations._

/**
  * This class defines parameter to map the entity on a Columnar datastore
  *
  * @param active if this entity is active
  * @param namespace the columnar datastore
  * @param table an optional datastore table
  * @param family an optional column family
  * @param qualifier an optional column qualifier
  * @param visibility a list of Visibility properties
  * @param isSingleJson if the entity is a SingleJson Object
  * @param singleStorage the name of the storage for a single storage entity
  */
@JsonCodec
final case class GlobalColumnProperties(
    active: Boolean = true,
    @JsonNoDefault @JsonKey(SchemaNames.NAMESPACE) namespace: Option[String] =
      None,
    @JsonNoDefault @JsonKey(SchemaNames.TABLE) table: Option[String] = None,
    @JsonNoDefault family: Option[String] = None,
    @JsonNoDefault qualifier: Option[String] = None,
    @JsonNoDefault visibility: List[Visibility] = Nil,
    @JsonNoDefault @JsonKey(SchemaNames.IS_SINGLE_JSON) isSingleJson: Boolean =
      false,
    @JsonNoDefault @JsonKey(SchemaNames.SINGLE_STORAGE) singleStorage: Option[
      String
    ] = None
)

/**
  * This class defines a ColumnProperties entity
  * @param family an optional column family
  * @param qualifier an optional column qualifier
  * @param visibility a list of Visibility properties
  */
@JsonCodec
final case class ColumnProperties(
    @JsonNoDefault family: Option[String] = None,
    @JsonNoDefault qualifier: Option[String] = None,
    @JsonNoDefault visibility: List[Visibility] = Nil
)

object ColumnProperties {
  lazy val empty: ColumnProperties = ColumnProperties()
}

/**
  * This class defines a GlobalIndexProperties entity
  * @param active if this entity is active
  */
@JsonCodec
final case class GlobalIndexProperties(
    @JsonNoDefault active: Boolean = true,
    @JsonNoDefault indexSharding: IndexSharding = IndexSharding.NONE,
    @JsonNoDefault indexName: Option[String] = None,
    @JsonNoDefault indexPrefix: Option[String] = None,
    @JsonNoDefault nesting: NestingType = NestingType.Embedded
)

/**
  * This class defines a IndexingProperties entity
  * @param analyzers a list of analyzers
  * @param index if the field should be indexed
  * @param stored if the field should be stored
  */
@JsonCodec
final case class IndexingProperties(
    @JsonNoDefault analyzers: List[String] = Nil,
    @JsonNoDefault index: Boolean = true,
    @JsonNoDefault stored: Boolean = false,
    @JsonNoDefault nesting: NestingType = NestingType.Embedded
)

object IndexingProperties {
  lazy val empty: IndexingProperties = IndexingProperties()
}
