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

package zio.common

object NamespaceUtils {
  val defaultIndex = "_index"
  val defaultConnection = "default"
  val defaultBulkReaderForValueList = 10000

  val specialNamespace = List("models", "engines")

  def namespaceToDocType(name: String): String = {
    var tokens = name.toLowerCase().split("\\.").toList
    specialNamespace.foreach { ns =>
      if (tokens.contains(ns)) {
        tokens = List(tokens(tokens.indexOf(ns) - 1), tokens.last)
      }
    }
    tokens.mkString("_")
  }

  def namespaceToNameUrl(name: String): String = {
    var tokens = name.toLowerCase().split("\\.").toList
    specialNamespace.foreach { ns =>
      if (tokens.contains(ns)) {
        tokens = List(tokens(tokens.indexOf(ns) - 1), tokens.last)
      }
    }
    tokens.mkString("/")
  }

  def getModuleOriginal(name: String): String = {
    val tokens = name.toLowerCase().split("\\.").toList
    specialNamespace.foreach { ns =>
      if (tokens.contains(ns)) {
        return tokens(tokens.indexOf(ns) - 1)
      }
    }
    if (tokens.length - 3 > 0) {
      tokens(tokens.length - 3)
    } else if (tokens.length - 2 > 0) {
      tokens(tokens.length - 2)
    } else {
      tokens.head
    }
  }

  val INVALID_PACKAGES = Set("com", "net", "tech")

  def getModule(name: String): String = {
    var tokens = name.toLowerCase().split("\\.").toList
    if (tokens.contains("models")) {
      tokens(tokens.indexOf("models") - 1)
    } else if (tokens.contains("app")) {
      tokens(tokens.indexOf("app") + 1)
    } else if (tokens.contains("zio")) {
      tokens(tokens.indexOf("zio") + 1)
    } else if (tokens.contains("elasticsearch")) {
      tokens(tokens.indexOf("elasticsearch") + 1)
    } else if (tokens.contains("client")) {
      tokens(tokens.indexOf("client") + 2)
    } else {
      tokens = tokens.dropWhile(v => INVALID_PACKAGES.contains(v))

      tokens.headOption.getOrElse("undefined")
    }
  }

  def getModelName(name: String): String = name.split("\\.").last

}
