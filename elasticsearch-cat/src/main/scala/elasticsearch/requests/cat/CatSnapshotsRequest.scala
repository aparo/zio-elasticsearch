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
 * Returns all snapshots in a specific repository.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-snapshots.html
 *
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param ignoreUnavailable Set to true to ignore unavailable snapshots
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param repository Name of repository from which to fetch the snapshot information
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */
@JsonCodec
final case class CatSnapshotsRequest(
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    @JsonKey("ignore_unavailable") ignoreUnavailable: Boolean = false,
    @JsonKey("master_timeout") masterTimeout: Option[String] = None,
    repository: Option[String] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_cat", "snapshots", repository)

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
    if (ignoreUnavailable != false)
      queryArgs += ("ignore_unavailable" -> ignoreUnavailable.toString)
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
