/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import scala.util.Try

final case class ServerAddress(host: String = "127.0.0.1", port: Int = 9200) {
  def httpUrl(useSSL: Boolean) = s"http${if (useSSL) "s" else ""}://$host:$port"
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
