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

package zio.elasticsearch.watcher.deactivate_watch
import zio.elasticsearch.watcher.ActivationStatus
import zio.json._
import zio.json.ast._
/*
 * Deactivates a currently active watch.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-deactivate-watch.html
 *
 * @param status

 */
final case class DeactivateWatchResponse(status: ActivationStatus) {}
object DeactivateWatchResponse {
  implicit lazy val jsonCodec: JsonCodec[DeactivateWatchResponse] =
    DeriveJsonCodec.gen[DeactivateWatchResponse]
}
