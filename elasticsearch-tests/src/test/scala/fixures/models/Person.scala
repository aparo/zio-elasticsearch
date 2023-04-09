/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

package fixures.models

import zio.elasticsearch.orm.ElasticSearchMeta
import zio.json.{ DeriveJsonDecoder, DeriveJsonEncoder, JsonCodec, JsonDecoder, JsonEncoder }
import zio.schema.elasticsearch.annotations.{ IndexName, IndexRequireType, Keyword, PK }
import zio.schema.{ DeriveSchema, Schema }

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
