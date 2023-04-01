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

package zio.elasticsearch.client
import zio.json.ast._
import zio.json._
import zio.exception._
final case class ESResponse(status: Int, body: String) {
  lazy val asJson: Either[ParsingFailureException, Json] = body.fromJson[Json] match {
    case Left(value)  => Left(ParsingFailureException(value, value))
    case Right(value) => Right(value)
  }
}
