package zio.schema.elasticsearch

import zio.schema.{ DeriveSchema, Schema }

final case class ESSchema1(
  @Keyword
  string: String,
  @Text
  text: String,
  i: Int,
  l: Long,
  d: Double,
  b: Boolean
)

object ESSchema1 {
  implicit val schema: Schema[ESSchema1] = DeriveSchema.gen[ESSchema1]
}
