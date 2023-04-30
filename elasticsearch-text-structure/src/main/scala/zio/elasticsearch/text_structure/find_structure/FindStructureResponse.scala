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

package zio.elasticsearch.text_structure.find_structure
import zio._
import zio.json._
/*
 * Finds the structure of a text file. The text file must contain data that is suitable to be ingested into Elasticsearch.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/find-structure.html
 *
 * @param charset

 * @param hasHeaderRow

 * @param hasByteOrderMarker

 * @param format

 * @param fieldStats

 * @param sampleStart

 * @param numMessagesAnalyzed

 * @param mappings

 * @param quote

 * @param delimiter

 * @param needClientTimezone

 * @param numLinesAnalyzed

 * @param columnNames

 * @param explanation

 * @param grokPattern

 * @param multilineStartPattern

 * @param excludeLinesPattern

 * @param javaTimestampFormats

 * @param jodaTimestampFormats

 * @param timestampField

 * @param shouldTrimFields

 * @param ingestPipeline

 */
final case class FindStructureResponse(
  charset: String,
  hasHeaderRow: Boolean = true,
  hasByteOrderMarker: Boolean = true,
  format: String,
  fieldStats: Map[String, FieldStat] = Map.empty[String, FieldStat],
  sampleStart: String,
  numMessagesAnalyzed: Int,
//  mappings: TypeMapping,
  quote: String,
  delimiter: String,
  needClientTimezone: Boolean = true,
  numLinesAnalyzed: Int,
  columnNames: Chunk[String] = Chunk.empty[String],
  explanation: Chunk[String] = Chunk.empty[String],
  grokPattern: String,
  multilineStartPattern: String,
  excludeLinesPattern: String,
  javaTimestampFormats: Chunk[String] = Chunk.empty[String],
  jodaTimestampFormats: Chunk[String] = Chunk.empty[String],
  timestampField: String,
  shouldTrimFields: Boolean = true
//  ingestPipeline: PipelineConfig
) {}
object FindStructureResponse {
  implicit lazy val jsonCodec: JsonCodec[FindStructureResponse] =
    DeriveJsonCodec.gen[FindStructureResponse]
}
