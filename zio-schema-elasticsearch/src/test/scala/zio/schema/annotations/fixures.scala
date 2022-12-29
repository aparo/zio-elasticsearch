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

package zio.schema.elasticsearch

import zio.schema.elasticsearch.annotations.{ Keyword, Text }
import zio.schema.{ DeriveSchema, Schema }

final case class ESSchema1(
  @Keyword
  string: String = "tes1",
  @Text
  text: String,
  i: Int = 10,
  l: Long = 100L,
  d: Double = 9.4d,
  b: Boolean = false
)

object ESSchema1 {
  implicit val schema: Schema[ESSchema1] = DeriveSchema.gen[ESSchema1]
}
