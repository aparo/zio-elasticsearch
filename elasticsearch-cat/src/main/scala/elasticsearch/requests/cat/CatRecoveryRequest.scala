/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.cat

import elasticsearch.{ Bytes, Time }
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * Returns information about index shard recoveries, both on-going completed.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-recovery.html
 *
 * @param activeOnly If `true`, the response only includes ongoing shard recoveries
 * @param bytes The unit in which to display byte values
 * @param detailed If `true`, the response includes detailed information about shard recoveries
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param index Comma-separated list or wildcard expression of index names to limit the returned information
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */
@JsonCodec
final case class CatRecoveryRequest(
  @JsonKey("active_only") activeOnly: Boolean = false,
  bytes: Option[Bytes] = None,
  detailed: Boolean = false,
  format: Option[String] = None,
  h: Seq[String] = Nil,
  help: Boolean = false,
  index: Seq[String] = Nil,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  s: Seq[String] = Nil,
  time: Option[Time] = None,
  v: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_cat", "recovery", index)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (activeOnly != false)
      queryArgs += ("active_only" -> activeOnly.toString)
    bytes.foreach { v =>
      queryArgs += ("bytes" -> v.toString)
    }
    if (detailed != false) queryArgs += ("detailed" -> detailed.toString)
    format.foreach { v =>
      queryArgs += ("format" -> v)
    }
    if (h.nonEmpty) {
      queryArgs += ("h" -> h.toList.mkString(","))
    }
    if (help != false) queryArgs += ("help" -> help.toString)
    if (index.nonEmpty) {
      queryArgs += ("index" -> index.toList.mkString(","))
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    if (s.nonEmpty) {
      queryArgs += ("s" -> s.toList.mkString(","))
    }
    time.foreach { v =>
      queryArgs += ("time" -> v.toString)
    }
    if (v != false) queryArgs += ("v" -> v.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
