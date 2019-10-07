/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import scala.collection.mutable
import elasticsearch.Refresh
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-bulk.html
 *
 * @param body body the body of the call
 * @param index Default index for items which don't provide one
 * @param sourceInclude Default list of fields to extract and return from the _source field, can be overridden on each sub-request
 * @param source True or false to return the _source field or not, or default list of fields to return, can be overridden on each sub-request
 * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param sourceExclude Default list of fields to exclude from the returned _source field, can be overridden on each sub-request
 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param fields Default comma-separated list of fields to return in the response for updates, can be overridden on each sub-request
 * @param routing Specific routing value
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the bulk operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
@JsonCodec
final case class BulkRequest(
  body: String,
  index: Option[String] = None,
  @JsonKey("_source_include") sourceInclude: Seq[String] = Nil,
  @JsonKey("_source") source: Seq[String] = Nil,
  refresh: Option[Refresh] = None,
  @JsonKey("_source_exclude") sourceExclude: Seq[String] = Nil,
  pipeline: Option[String] = None,
  fields: Seq[String] = Nil,
  routing: Option[String] = None,
  timeout: Option[String] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(index, "_bulk")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!sourceInclude.isEmpty) {
      queryArgs += ("_source_include" -> sourceInclude.toList.mkString(","))
    }
    if (!source.isEmpty) {
      queryArgs += ("_source" -> source.toList.mkString(","))
    }
    refresh.map { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    if (!sourceExclude.isEmpty) {
      queryArgs += ("_source_exclude" -> sourceExclude.toList.mkString(","))
    }
    pipeline.map { v =>
      queryArgs += ("pipeline" -> v)
    }
    if (!fields.isEmpty) {
      queryArgs += ("fields" -> fields.toList.mkString(","))
    }
    routing.map { v =>
      queryArgs += ("routing" -> v)
    }
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    waitForActiveShards.map { v =>
      queryArgs += ("wait_for_active_shards" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
