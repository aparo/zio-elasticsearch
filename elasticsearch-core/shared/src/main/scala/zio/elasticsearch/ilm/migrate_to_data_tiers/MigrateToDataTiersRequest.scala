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

package zio.elasticsearch.ilm.migrate_to_data_tiers
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ilm.requests.MigrateToDataTiersRequestBody
/*
 * Migrates the indices and ILM policies away from custom node attribute allocation routing to data tiers routing
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-migrate-to-data-tiers.html
 *
 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param human When set to `true` will return statistics in a format suitable for humans.
 * For example `"exists_time": "1h"` for humans and
 * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
 * readable values will be omitted. This makes sense for responses being consumed
 * only by machines.
 * @server_default false

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param body body the body of the call
 * @param dryRun If set to true it will simulate the migration, providing a way to retrieve the ILM policies and indices that need to be migrated. The default is false
 */

final case class MigrateToDataTiersRequest(
  body: MigrateToDataTiersRequestBody = MigrateToDataTiersRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  dryRun: Option[Boolean] = None
) extends ActionRequest[MigrateToDataTiersRequestBody]
    with RequestBase {
  def method: String = "POST"

  def urlPath = "/_ilm/migrate_to_data_tiers"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    dryRun.foreach { v =>
      queryArgs += ("dry_run" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
