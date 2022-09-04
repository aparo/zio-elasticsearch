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

package elasticsearch.fixures.models

import zio.schema.annotations.{ ElasticSearchStorage, IndexName, IndexRequireType, Keyword, PK }
import zio.schema.SchemaDocumentCodec

@SchemaDocumentCodec
@ElasticSearchStorage
final case class Person(@PK @Keyword username: String, name: String, surname: String, age: Option[Int])
@SchemaDocumentCodec
@ElasticSearchStorage
@IndexRequireType
@IndexName("default")
final case class PersonInIndex(@PK @Keyword username: String, name: String, surname: String, age: Option[Int])