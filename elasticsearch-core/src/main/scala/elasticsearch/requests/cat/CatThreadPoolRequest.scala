/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.cat

import elasticsearch.Size
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * Returns cluster-wide thread pool statistics per node.
By default the active, queue and rejected statistics are returned for all thread pools.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-thread-pool.html
 *
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param size The multiplier in which to display values
 * @param threadPoolPatterns A comma-separated list of regular-expressions to filter the thread pools in the output
 * @param v Verbose mode. Display column headers
 */
@JsonCodec
final case class CatThreadPoolRequest(
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    @JsonKey("master_timeout") masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    size: Option[Size] = None,
    @JsonKey("thread_pool_patterns") threadPoolPatterns: Seq[String] = Nil,
    v: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_cat", "thread_pool", threadPoolPatterns)

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
    size.foreach { v =>
      queryArgs += ("size" -> v.toString)
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
