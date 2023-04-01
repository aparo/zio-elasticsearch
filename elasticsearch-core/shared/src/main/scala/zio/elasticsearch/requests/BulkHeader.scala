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

package zio.elasticsearch.requests

import zio.json.{ DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder, jsonHint }

sealed trait BulkHeader
object BulkHeader {
  implicit val jsonDecoder: JsonDecoder[BulkHeader] = DeriveJsonDecoder.gen[BulkHeader]
  implicit val jsonEncoder: JsonEncoder[BulkHeader] = DeriveJsonEncoder.gen[BulkHeader]
}

@jsonHint("index")
final case class IndexBulkHeader(
  _index: String,
  _id: Option[String] = None,
  routing: Option[String] = None,
  version: Option[Long] = None,
  pipeline: Option[String] = None
) extends BulkHeader

@jsonHint("create")
final case class CreateBulkHeader(
  _index: String,
  _id: String,
  routing: Option[String] = None,
  version: Option[Long] = None,
  pipeline: Option[String] = None
) extends BulkHeader

@jsonHint("update")
final case class UpdateBulkHeader(
  _index: String,
  _id: String,
  routing: Option[String] = None,
  version: Option[Long] = None,
  pipeline: Option[String] = None
) extends BulkHeader

@jsonHint("delete")
final case class DeleteBulkHeader(
  _index: String,
  _id: String,
  routing: Option[String] = None,
  version: Option[Long] = None,
  pipeline: Option[String] = None
) extends BulkHeader
