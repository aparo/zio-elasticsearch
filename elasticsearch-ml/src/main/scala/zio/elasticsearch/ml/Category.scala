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

package zio.elasticsearch.ml
import zio._
import zio.json._
import zio.json.ast._
final case class Category(
  @jsonField("category_id") categoryId: Long,
  examples: Chunk[String],
  @jsonField("grok_pattern") grokPattern: Option[String] = None,
  @jsonField("job_id") jobId: String,
  @jsonField("max_matching_length") maxMatchingLength: Long,
  @jsonField("partition_field_name") partitionFieldName: Option[String] = None,
  @jsonField("partition_field_value") partitionFieldValue: Option[String] = None,
  regex: String,
  terms: String,
  @jsonField("num_matches") numMatches: Option[Long] = None,
  @jsonField("preferred_to_categories") preferredToCategories: Option[
    Chunk[String]
  ] = None,
  p: Option[String] = None,
  @jsonField("result_type") resultType: String,
  mlcategory: String
)

object Category {
  implicit val jsonCodec: JsonCodec[Category] = DeriveJsonCodec.gen[Category]
}
