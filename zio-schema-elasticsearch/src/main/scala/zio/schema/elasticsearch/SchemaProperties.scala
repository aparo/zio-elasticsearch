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

package zio.schema.elasticsearch

import zio.Chunk
import zio.json._

/**
 * This class defines a GlobalIndexProperties entity
 * @param active
 *   if this entity is active
 */
final case class GlobalIndexProperties(
  @JsonNoDefault active: Boolean = true,
  @JsonNoDefault requireType: Boolean = false,
  @JsonNoDefault indexSharding: IndexSharding = IndexSharding.NONE,
  @JsonNoDefault indexName: Option[String] = None,
  @JsonNoDefault indexPrefix: Option[String] = None,
  @JsonNoDefault nesting: NestingType = NestingType.Embedded
)
object GlobalIndexProperties {
  implicit val jsonDecoder: JsonDecoder[GlobalIndexProperties] = DeriveJsonDecoder.gen[GlobalIndexProperties]
  implicit val jsonEncoder: JsonEncoder[GlobalIndexProperties] = DeriveJsonEncoder.gen[GlobalIndexProperties]
}

/**
 * This class defines a IndexingProperties entity
 * @param analyzers
 *   a list of analyzers
 * @param index
 *   if the field should be indexed
 * @param stored
 *   if the field should be stored
 */
final case class IndexingProperties(
  @JsonNoDefault analyzers: Chunk[String] = Chunk.empty[String],
  @JsonNoDefault index: Boolean = true,
  @JsonNoDefault stored: Boolean = false,
  @JsonNoDefault nesting: NestingType = NestingType.Embedded,
  @JsonNoDefault es_type: EsType = EsType.none
)

object IndexingProperties {
  lazy val empty: IndexingProperties = IndexingProperties()
  implicit val jsonDecoder: JsonDecoder[IndexingProperties] = DeriveJsonDecoder.gen[IndexingProperties]
  implicit val jsonEncoder: JsonEncoder[IndexingProperties] = DeriveJsonEncoder.gen[IndexingProperties]
}
