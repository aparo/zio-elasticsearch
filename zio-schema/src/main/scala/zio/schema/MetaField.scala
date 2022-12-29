/*
 * Copyright 2019 Alberto Paro
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

import zio.json.ast.Json
import zio.json._
import zio.json.ast._

@JsonCodec
final case class MetaComputed(script: String)

@JsonCodec
final case class MetaFieldView(var format: Option[String] = None)

@JsonCodec
final case class MetaFieldEdit(
  var `type`: Option[String] = None,
  var `class`: Option[String] = None,
  var format: Option[String] = None,
  var placeholder: Option[String] = None,
  var config: Option[Json] = None,
  var default: Option[Json] = None,
  var options: List[String] = Nil,
  var validators: List[String] = Nil
)

@JsonCodec
final case class MetaField(
  var multiple: Boolean = true,
  var required: Boolean = false,
  var display: Option[String] = None,
  var label: Option[String] = None,
  var image: Option[String] = None,
  var icon: Option[String] = None,
  var editable: Option[Boolean] = None,
  var edit: Option[MetaFieldEdit] = None,
  var view: Option[MetaFieldView] = None,
  @jsonField("auto_update") var auto_update: Option[Boolean] = None, //the field is an autoupdate
  @jsonField("add_date") var add_date: Option[Boolean] = None, //the field is the date during add
  @jsonField("add_datetime") var add_datetime: Option[Boolean] = None, // the field is a datetime during add
  var fk: Option[String] = None, // the field is a foreignkey
  var computed: Option[MetaComputed] = None,
  fields: Map[String, MetaField] = Map.empty[String, MetaField]
) {}
