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

package zio.elasticsearch.requests

import scala.collection.mutable

import zio.elasticsearch.common.VersionType
import zio.json._
import zio.json.ast._

/*
 * Returns multiple termvectors in one request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-termvectors.html
 *
 * @param body body the body of the call
 * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param fields A comma-separated list of fields to return. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param ids A comma-separated list of documents ids. You must define ids as parameter or set "ids" or "docs" in the request body
 * @param index The index in which the document resides.
 * @param offsets Specifies if term offsets should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param payloads Specifies if term payloads should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param positions Specifies if term positions should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param preference Specify the node or shard the operation should be performed on (default: random) .Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param realtime Specifies if requests are real-time as opposed to near-real-time (default: true).
 * @param routing Specific routing value. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param termStatistics Specifies if total term frequency and document frequency should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
 * @param version Explicit version number for concurrency control
 * @param versionType Specific version type
 */
final case class MultiTermVectorsRequest(
  body: Option[Json.Obj] = None,
  @jsonField("field_statistics") fieldStatistics: Boolean = true,
  fields: Seq[String] = Nil,
  ids: Seq[String] = Nil,
  index: Option[String] = None,
  offsets: Boolean = true,
  payloads: Boolean = true,
  positions: Boolean = true,
  preference: Option[String] = None,
  realtime: Option[Boolean] = None,
  routing: Option[String] = None,
  @jsonField("term_statistics") termStatistics: Boolean = false,
  version: Option[Long] = None,
  @jsonField("version_type") versionType: Option[VersionType] = None
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl(index, "_mtermvectors")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    body.foreach { v =>
      queryArgs += "body" -> v.toString
    }
    if (fieldStatistics != true) queryArgs += "field_statistics" -> fieldStatistics.toString
    if (fields.nonEmpty) {
      queryArgs += "fields" -> fields.toList.mkString(",")
    }
    if (ids.nonEmpty) {
      queryArgs += "ids" -> ids.toList.mkString(",")
    }
    if (offsets != true) queryArgs += "offsets" -> offsets.toString
    if (payloads != true) queryArgs += "payloads" -> payloads.toString
    if (positions != true) queryArgs += "positions" -> positions.toString
    preference.foreach { v =>
      queryArgs += "preference" -> v
    }
    realtime.foreach { v =>
      queryArgs += "realtime" -> v.toString
    }
    routing.foreach { v =>
      queryArgs += "routing" -> v
    }
    if (termStatistics != false) queryArgs += "term_statistics" -> termStatistics.toString
    version.foreach { v =>
      queryArgs += "version" -> v.toString
    }
    versionType.foreach { v =>
      queryArgs += "version_type" -> v.toString
    }
    queryArgs.toMap
  }
}
object MultiTermVectorsRequest {
  implicit val jsonDecoder: JsonDecoder[MultiTermVectorsRequest] = DeriveJsonDecoder.gen[MultiTermVectorsRequest]
  implicit val jsonEncoder: JsonEncoder[MultiTermVectorsRequest] = DeriveJsonEncoder.gen[MultiTermVectorsRequest]
}
