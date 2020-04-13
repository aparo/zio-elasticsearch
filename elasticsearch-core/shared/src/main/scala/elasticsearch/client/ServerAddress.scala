/*
 * Copyright 2019-2020 Alberto Paro
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

package elasticsearch.client

import scala.util.Try

final case class ServerAddress(host: String = "127.0.0.1", port: Int = 9200) {
  def httpUrl(useSSL: Boolean) =
    s"http${if (useSSL) "s" else ""}://$host:$port"
}

object ServerAddress {

  def fromString(address: String): ServerAddress = {
    val tokens = address.split(":")
    if (tokens.length == 1) {
      ServerAddress(tokens.head)
    } else {
      ServerAddress(tokens.head, Try(tokens(1).toInt).toOption.getOrElse(9200))
    }
  }
}
