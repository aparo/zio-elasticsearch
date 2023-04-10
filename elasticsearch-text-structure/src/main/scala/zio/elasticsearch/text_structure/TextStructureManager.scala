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

package zio.elasticsearch.text_structure

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.text_structure.find_structure.FindStructureRequest
import zio.elasticsearch.text_structure.find_structure.FindStructureResponse

object TextStructureManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, TextStructureManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new TextStructureManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait TextStructureManager {
  def httpService: ElasticSearchHttpService

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
  def findStructure(
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
  ): ZIO[Any, FrameworkException, FindStructureResponse] = {
    val request = FindStructureRequest(
      body = body,
      charset = charset,
      columnNames = columnNames,
      delimiter = delimiter,
      ecsCompatibility = ecsCompatibility,
      explain = explain,
      format = format,
      grokPattern = grokPattern,
      hasHeaderRow = hasHeaderRow,
      lineMergeSizeLimit = lineMergeSizeLimit,
      linesToSample = linesToSample,
      quote = quote,
      shouldTrimFields = shouldTrimFields,
      timeout = timeout,
      timestampField = timestampField,
      timestampFormat = timestampFormat
    )

    findStructure(request)

  }

  def findStructure(request: FindStructureRequest): ZIO[Any, FrameworkException, FindStructureResponse] =
    httpService.execute[Array[String], FindStructureResponse](request)

}
