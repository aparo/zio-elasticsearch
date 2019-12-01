/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.mappings

import io.circe._
import io.circe.derivation.annotations._

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
  @JsonKey("auto_update") var auto_update: Option[Boolean] = None, //the field is an autoupdate
  @JsonKey("add_date") var add_date: Option[Boolean] = None, //the field is the date during add
  @JsonKey("add_datetime") var add_datetime: Option[Boolean] = None, // the field is a datetime during add
  var fk: Option[String] = None, // the field is a foreignkey
  var computed: Option[MetaComputed] = None,
  fields: Map[String, MetaField] = Map.empty[String, MetaField]
) {}
