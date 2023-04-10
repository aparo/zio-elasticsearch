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
import zio.Chunk

import scala.collection.mutable
import zio.elasticsearch.common._
import zio.elasticsearch.text_structure.Format
/*
 * Finds the structure of a text file. The text file must contain data that is suitable to be ingested into Elasticsearch.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/find-structure.html
 *
 * @param body body the body of the call
 * @param charset Optional parameter to specify the character set of the file
 * @param columnNames Optional parameter containing a comma separated list of the column names for a delimited file
 * @param delimiter Optional parameter to specify the delimiter character for a delimited file - must be a single character
 * @param ecsCompatibility Optional parameter to specify the compatibility mode with ECS Grok patterns - may be either 'v1' or 'disabled'
 * @param explain Whether to include a commentary on how the structure was derived
 * @param format Optional parameter to specify the high level file format
 * @param grokPattern Optional parameter to specify the Grok pattern that should be used to extract fields from messages in a semi-structured text file
 * @param hasHeaderRow Optional parameter to specify whether a delimited file includes the column names in its first row
 * @param lineMergeSizeLimit Maximum number of characters permitted in a single message when lines are merged to create messages.
 * @param linesToSample How many lines of the file should be included in the analysis
 * @param quote Optional parameter to specify the quote character for a delimited file - must be a single character
 * @param shouldTrimFields Optional parameter to specify whether the values between delimiters in a delimited file should have whitespace trimmed from them
 * @param timeout Timeout after which the analysis will be aborted
 * @param timestampField Optional parameter to specify the timestamp field in the file
 * @param timestampFormat Optional parameter to specify the timestamp format in the file - may be either a Joda or Java time format
 */

final case class FindStructureRequest(
  body: Array[String],
  charset: Option[String] = None,
  columnNames: Chunk[String] = Chunk.empty,
  delimiter: Option[String] = None,
  ecsCompatibility: Option[String] = None,
  explain: Boolean = false,
  format: Option[Format] = None,
  grokPattern: Option[String] = None,
  hasHeaderRow: Option[Boolean] = None,
  lineMergeSizeLimit: Int = 10000,
  linesToSample: Int = 1000,
  quote: Option[String] = None,
  shouldTrimFields: Option[Boolean] = None,
  timeout: String = "25s",
  timestampField: Option[String] = None,
  timestampFormat: Option[String] = None
) extends ActionRequest[Array[String]] {
  def method: Method = Method.POST

  def urlPath = "/_text_structure/find_structure"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    charset.foreach { v =>
      queryArgs += ("charset" -> v)
    }
    if (columnNames.nonEmpty) {
      queryArgs += ("column_names" -> columnNames.toList.mkString(","))
    }
    delimiter.foreach { v =>
      queryArgs += ("delimiter" -> v)
    }
    ecsCompatibility.foreach { v =>
      queryArgs += ("ecs_compatibility" -> v)
    }
    if (explain != false) queryArgs += ("explain" -> explain.toString)
    format.foreach { v =>
      queryArgs += ("format" -> v.toString)
    }
    grokPattern.foreach { v =>
      queryArgs += ("grok_pattern" -> v)
    }
    hasHeaderRow.foreach { v =>
      queryArgs += ("has_header_row" -> v.toString)
    }
    if (lineMergeSizeLimit != 10000)
      queryArgs += ("line_merge_size_limit" -> lineMergeSizeLimit.toString)
    if (linesToSample != 1000)
      queryArgs += ("lines_to_sample" -> linesToSample.toString)
    quote.foreach { v =>
      queryArgs += ("quote" -> v)
    }
    shouldTrimFields.foreach { v =>
      queryArgs += ("should_trim_fields" -> v.toString)
    }
    if (timeout != "25s") queryArgs += ("timeout" -> timeout.toString)
    timestampField.foreach { v =>
      queryArgs += ("timestamp_field" -> v)
    }
    timestampFormat.foreach { v =>
      queryArgs += ("timestamp_format" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
