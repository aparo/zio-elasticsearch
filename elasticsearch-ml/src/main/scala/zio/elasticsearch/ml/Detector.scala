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
final case class Detector(
  @jsonField("by_field_name") byFieldName: Option[String] = None,
  @jsonField("custom_rules") customRules: Option[Chunk[DetectionRule]] = None,
  @jsonField("detector_description") detectorDescription: Option[String] = None,
  @jsonField("detector_index") detectorIndex: Option[Int] = None,
  @jsonField("exclude_frequent") excludeFrequent: Option[ExcludeFrequent] = None,
  @jsonField("field_name") fieldName: Option[String] = None,
  function: Option[String] = None,
  @jsonField("over_field_name") overFieldName: Option[String] = None,
  @jsonField("partition_field_name") partitionFieldName: Option[String] = None,
  @jsonField("use_null") useNull: Option[Boolean] = None
)

object Detector {
  implicit val jsonCodec: JsonCodec[Detector] = DeriveJsonCodec.gen[Detector]
}
