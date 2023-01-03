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

package zio.elasticsearch.fixures.models

import zio.elasticsearch.orm.ElasticSearchMeta
import zio.json.{ DeriveJsonDecoder, DeriveJsonEncoder, JsonCodec, JsonDecoder, JsonEncoder }
import zio.schema.{ DeriveSchema, Schema }
import zio.schema.elasticsearch.annotations.{ IndexName, IndexRequireType, Keyword, PK }

final case class Person(@PK @Keyword username: String, name: String, surname: String, age: Option[Int])
object Person extends ElasticSearchMeta[Person] {
  implicit final val decoder: JsonDecoder[Person] =
    DeriveJsonDecoder.gen[Person]
  implicit final val encoder: JsonEncoder[Person] =
    DeriveJsonEncoder.gen[Person]
  implicit final val codec: JsonCodec[Person] = JsonCodec(encoder, decoder)
  implicit val schema: Schema[Person] = DeriveSchema.gen[Person]
}
@IndexRequireType
@IndexName("default")
final case class PersonInIndex(@PK @Keyword username: String, name: String, surname: String, age: Option[Int])
object PersonInIndex extends ElasticSearchMeta[PersonInIndex] {
  implicit final val decoder: JsonDecoder[PersonInIndex] =
    DeriveJsonDecoder.gen[PersonInIndex]
  implicit final val encoder: JsonEncoder[PersonInIndex] =
    DeriveJsonEncoder.gen[PersonInIndex]
  implicit final val codec: JsonCodec[PersonInIndex] = JsonCodec(encoder, decoder)
  implicit val schema: Schema[PersonInIndex] = DeriveSchema.gen[PersonInIndex]
}
