package zio.schema.elasticsearch

import zio.schema.elasticsearch.annotations.{Keyword, Text}
import zio.schema.{DeriveSchema, Schema}

final case class ESSchema1(
  @Keyword
  string: String="tes1",
  @Text
  text: String,
  i: Int=10,
  l: Long=100L,
  d: Double=9.4d,
  b: Boolean=false
)

object ESSchema1 {
  implicit val schema: Schema[ESSchema1] = DeriveSchema.gen[ESSchema1]
}
