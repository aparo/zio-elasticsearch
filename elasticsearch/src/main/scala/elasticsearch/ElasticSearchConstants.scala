/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

object ElasticSearchConstants {

  def appName = "elasticsearch"

  // Default language used in scripting in SA
  val esDefaultScriptingLanguage = "painless"

  val defaultColumnFamily = "public"
  val defaultColumnVisibility = "public"

  val metaColumnFamily = "_meta"
  val versionColumnQualifier = "version"

  val defaultDB = "default"
  val defaultAuthDB = "default-auth"

  //columnar constants
  final val singleJSONQualifier = "_json"
  final val typeField = "_nttype"

  val version = "6.0.0"
  val versionNum: Int = version.split('.').head.toInt
  final val defaultBulkReaderForValueList = 10000

  val httpPort = 9200
  val nativePort = 9300 //TODO replace everywhere with httpPort
  var defaultAddress = "127.0.0.1"

  val defaultScriptingLanguage: String =
    if (versionNum >= 5) "painless" else "groovy"

  val SEQUENCE_INDEX = "sequence"

  val SEPARATOR = "---"
  val EDGE_IN = "in"
  val EDGE_OUT = "out"
  val EDGE_TYPE = "__edges"
  val EDGE_PATH = "path"
  val EDGE_DIRECT = "direct"
  val EDGE_LABEL = "label"
  val EDGE_IN_INDEX = "in.index"
  val EDGE_IN_TYPE = "in.type"
  val EDGE_IN_ID = "in.id"
  val EDGE_OUT_INDEX = "out.index"
  val EDGE_OUT_TYPE = "out.type"
  val EDGE_OUT_ID = "out.id"

  val MAX_RETURNED_DOCUMENTS = 10000
}
