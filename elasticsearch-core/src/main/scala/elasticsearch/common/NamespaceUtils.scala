/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.common

object NamespaceUtils {
  final val defaultIndex = "_index"
  final val defaultConnection = "default"
  final val defaultQDBBulkReaderForValueList = 10000

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
    if (tokens.contains("app")) {
      tokens(tokens.indexOf("app") + 1)
    } else if (tokens.contains("app")) {
      tokens(tokens.indexOf("app") + 1)
    } else if (tokens.contains("app")) {
      tokens(tokens.indexOf("app") + 1)
    } else if (tokens.contains("elasticsearch")) {
      tokens(tokens.indexOf("elasticsearch") + 1)
    } else if (tokens.contains("elasticsearch")) {
      tokens(tokens.indexOf("elasticsearch") + 1)
    } else if (tokens.contains("elasticsearch")) {
      tokens(tokens.indexOf("elasticsearch") + 1)
    } else if (tokens.contains("models")) {
      tokens(tokens.indexOf("models") - 1)
    } else if (tokens.contains("client")) {
      tokens(tokens.indexOf("client") + 2)
    } else {
      tokens = tokens.dropWhile(v => INVALID_PACKAGES.contains(v))

      tokens.headOption.getOrElse("undefined")
    }
  }

  def getModelName(name: String): String = name.split("\\.").last

}
