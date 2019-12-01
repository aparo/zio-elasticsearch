/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import io.circe._
import io.circe.derivation.annotations.{JsonCodec, JsonKey}
import scala.collection.mutable

/*
 * Allows to copy documents from one index to another, optionally filtering the source
documents by a query, changing the destination index settings, or fetching the
documents from a remote cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
 *
 * @param body body the body of the call
 * @param maxDocs Maximum number of documents to process (default: all documents)
 * @param refresh Should the effected indexes be refreshed?
 * @param requestsPerSecond The throttle to set on this request in sub-requests per second. -1 means no throttle.
 * @param scroll Control how long to keep the search context alive
 * @param slices The number of slices this task should be divided into. Defaults to 1 meaning the task isn't sliced into subtasks.
 * @param timeout Time each individual bulk request should wait for shards that are unavailable.
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the reindex operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 * @param waitForCompletion Should the request should block until the reindex is complete.
 */
@JsonCodec
final case class ReindexRequest(
    body: JsonObject,
    @JsonKey("max_docs") maxDocs: Option[Double] = None,
    refresh: Option[Boolean] = None,
    @JsonKey("requests_per_second") requestsPerSecond: Int = 0,
    scroll: String = "5m",
    slices: Double = 1,
    timeout: String = "1m",
    @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] =
      None,
    @JsonKey("wait_for_completion") waitForCompletion: Boolean = true
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = "/_reindex"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    maxDocs.foreach { v =>
      queryArgs += ("max_docs" -> v.toString)
    }
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    if (requestsPerSecond != 0)
      queryArgs += ("requests_per_second" -> requestsPerSecond.toString)
    if (scroll != "5m") queryArgs += ("scroll" -> scroll.toString)
    if (slices != 1) queryArgs += ("slices" -> slices.toString)
    if (timeout != "1m") queryArgs += ("timeout" -> timeout.toString)
    waitForActiveShards.foreach { v =>
      queryArgs += ("wait_for_active_shards" -> v)
    }
    if (waitForCompletion != true)
      queryArgs += ("wait_for_completion" -> waitForCompletion.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
