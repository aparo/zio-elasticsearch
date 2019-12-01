/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.cat

import elasticsearch.Time
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * Returns a concise representation of the cluster health.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-health.html
 *
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param ts Set to false to disable timestamping
 * @param v Verbose mode. Display column headers
 */
@JsonCodec
final case class CatHealthRequest(
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    @JsonKey("master_timeout") masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    ts: Boolean = true,
    v: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_cat/health"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    format.foreach { v =>
      queryArgs += ("format" -> v)
    }
    if (h.nonEmpty) {
      queryArgs += ("h" -> h.toList.mkString(","))
    }
    if (help != false) queryArgs += ("help" -> help.toString)
    local.foreach { v =>
      queryArgs += ("local" -> v.toString)
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
    if (ts != true) queryArgs += ("ts" -> ts.toString)
    if (v != false) queryArgs += ("v" -> v.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
