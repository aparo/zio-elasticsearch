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

package zio.schema

object SchemaNames {
  final val NAME: String = "name"
  final val MODULE: String = "module"
  final val TYPE: String = "type"
  final val REFERENCE: String = "$ref"
  final val REQUIRED: String = "required"
  final val ACTIVE: String = "active"
  final val IS_ROOT: String = "is_root"
  final val STORAGES: String = "storages"
  final val AUTO_OWNER: String = "auto_owner"
  final val CLASS_NAME: String = "class_name"
  final val PROPERTIES: String = "properties"
  final val KEY: String = "key"
  final val LABEL: String = "label"
  final val DESCRIPTION: String = "description"
  final val CREATION_DATE: String = "creation_date"
  final val CREATION_USER: String = "creation_user"
  final val MODIFICATION_DATE: String = "modification_date"
  final val MODIFICATION_USER: String = "modification_user"
  final val ID: String = "id"
  final val VERSION: String = "version"
  final val SCHEMA: String = "schema"
  final val DATASTORE: String = "datastore"
  final val SUB_TYPE: String = "sub_type"
  final val IS_SENSITIVE: String = "is_sensitive"
  final val MULTIPLE: String = "multiple"
  final val UNDEFINED: String = "__undefined__"

  // Columnar
  final val COLUMNAR: String = "columnar"
  final val VISIBILITY: String = "visibility"
  final val FAMILY: String = "family"
  final val QUALIFIER: String = "qualifier"
  final val TABLE: String = "table"
  final val NAMESPACE: String = "namespace"
  final val IS_SINGLE_JSON: String = "is_single_json"
  final val SINGLE_STORAGE: String = "single_storage"
  final val SINGLE_STORAGE_SEPARATOR: String = ":" //used to build id i.e. type<SINGLE_STORAGE_SEPARATOR>id => type:id

  // Indexing
  final val ANALYZERS: String = "analyzers"
  final val NESTING: String = "nesting"
  final val EMBEDDED: String = "embedded"
  final val NESTED: String = "nested"
  final val PARENT: String = "parent"
  final val INDEX: String = "index"
  final val STORE: String = "store"
  final val HEATMAP: String = "heatmap"
  final val TEXT_KEYWORD: String = "keyword"
  final val TEXT_TEXT: String = "text"
  final val TEXT_SUGGEST: String = "suggest"
  final val TEXT_STEM: String = "stem"
  final val TEXT_NLP: String = "nlp"
  final val SCORE: String = "score"
  final val SOURCE: String = "source"
  final val HIGHLIGHT: String = "highlight"

}
