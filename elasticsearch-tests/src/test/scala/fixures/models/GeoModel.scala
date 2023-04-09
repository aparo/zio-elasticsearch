/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

package fixures.models

import zio.elasticsearch.geo.GeoPointLatLon
import zio.elasticsearch.orm.ElasticSearchMeta
import zio.json._
import zio.schema.elasticsearch.annotations.{ Keyword, PK }
import zio.schema.{ DeriveSchema, Schema }
final case class GeoModel(@PK @Keyword username: String, geoPoint: GeoPointLatLon)

object GeoModel extends ElasticSearchMeta[GeoModel] {
  implicit final val decoder: JsonDecoder[GeoModel] =
    DeriveJsonDecoder.gen[GeoModel]
  implicit final val encoder: JsonEncoder[GeoModel] =
    DeriveJsonEncoder.gen[GeoModel]
  implicit final val codec: JsonCodec[GeoModel] = JsonCodec(encoder, decoder)
  implicit val schema: Schema[GeoModel] = DeriveSchema.gen[GeoModel]
}
