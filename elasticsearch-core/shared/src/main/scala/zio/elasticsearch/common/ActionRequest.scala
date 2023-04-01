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

package zio.elasticsearch.common

import java.net.URLEncoder

trait ActionRequest[BODY] {

  protected def makeUrl(parts: Any*): String = {
    val values = parts.toList.collect {
      case s: String => URLEncoder.encode(s, "UTF-8")
      case s: Seq[_] =>
        s.toList.map(s => URLEncoder.encode(s.toString, "UTF-8")).mkString(",")
      case Some(s) => URLEncoder.encode(s.toString, "UTF-8")
    }
    values.toList.mkString("/")
  }

  def urlPath: String
  def method: String
  def body: BODY
  def queryArgs: Map[String, String]

}
