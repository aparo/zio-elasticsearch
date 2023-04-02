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

package zio.elasticsearch.ml.info
import zio.json._
import zio.json.ast._
/*
 * Returns defaults and limits used by machine learning.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-ml-info.html
 *
 * @param defaults

 * @param limits

 * @param upgradeMode

 * @param nativeCode

 */
final case class InfoResponse(
  defaults: Defaults,
  limits: Limits,
  upgradeMode: Boolean = true,
  nativeCode: NativeCode
) {}
object InfoResponse {
  implicit val jsonCodec: JsonCodec[InfoResponse] =
    DeriveJsonCodec.gen[InfoResponse]
}
