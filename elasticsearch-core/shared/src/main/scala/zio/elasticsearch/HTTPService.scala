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

package zio.elasticsearch

import zio.ZIO
import zio.elasticsearch.client.ESResponse
import zio.elasticsearch.common.ActionRequest
import zio.exception.FrameworkException
import zio.json.{ JsonDecoder, JsonEncoder }

trait HTTPService {
  def elasticSearchConfig: ElasticSearchConfig

  def doCall(
    method: String,
    url: String,
    body: Option[String],
    queryArgs: Map[String, String],
    headers: Map[String, String] = Map.empty[String, String]
  ): ZIO[Any, FrameworkException, ESResponse]

  def execute[BODY: JsonEncoder, RESPONSE: JsonDecoder](
    request: ActionRequest[BODY]
  ): ZIO[Any, FrameworkException, RESPONSE]

}
