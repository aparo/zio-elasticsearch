/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import java.net.URLEncoder

trait ActionRequest {

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
  def body: Any
  def queryArgs: Map[String, String]

}
