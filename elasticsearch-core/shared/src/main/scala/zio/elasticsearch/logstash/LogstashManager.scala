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

package zio.elasticsearch.logstash

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.logstash.delete_pipeline.DeletePipelineRequest
import zio.elasticsearch.logstash.delete_pipeline.DeletePipelineResponse
import zio.elasticsearch.logstash.get_pipeline.GetPipelineRequest
import zio.elasticsearch.logstash.get_pipeline.GetPipelineResponse
import zio.elasticsearch.logstash.put_pipeline.PutPipelineRequest
import zio.elasticsearch.logstash.put_pipeline.PutPipelineResponse

class LogstashManager(client: ElasticSearchClient) {

  /*
   * Deletes Logstash Pipelines used by Central Management
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/logstash-api-delete-pipeline.html
   *
   * @param id The ID of the Pipeline
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

   */
  def deletePipeline(
    id: String,
    errorTrace: Boolean,
    filterPath: Chunk[String],
    human: Boolean,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, DeletePipelineResponse] = {
    val request = DeletePipelineRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deletePipeline(request)

  }

  def deletePipeline(
    request: DeletePipelineRequest
  ): ZIO[Any, FrameworkException, DeletePipelineResponse] =
    client.execute[Json, DeletePipelineResponse](request)

  /*
   * Retrieves Logstash Pipelines used by Central Management
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/logstash-api-get-pipeline.html
   *
   * @param id A comma-separated list of Pipeline IDs
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

   */
  def getPipeline(
    id: Seq[String] = Nil,
    errorTrace: Boolean,
    filterPath: Chunk[String],
    human: Boolean,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, GetPipelineResponse] = {
    val request = GetPipelineRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getPipeline(request)

  }

  def getPipeline(
    request: GetPipelineRequest
  ): ZIO[Any, FrameworkException, GetPipelineResponse] =
    client.execute[Json, GetPipelineResponse](request)

  /*
   * Adds and updates Logstash Pipelines used for Central Management
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/logstash-api-put-pipeline.html
   *
   * @param id The ID of the Pipeline
   * @param body a Pipeline
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

   */
  def putPipeline(
    id: String,
    body: Pipeline,
    errorTrace: Boolean,
    filterPath: Chunk[String],
    human: Boolean,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, PutPipelineResponse] = {
    val request = PutPipelineRequest(
      id = id,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putPipeline(request)

  }

  def putPipeline(
    request: PutPipelineRequest
  ): ZIO[Any, FrameworkException, PutPipelineResponse] =
    client.execute[Pipeline, PutPipelineResponse](request)

}
