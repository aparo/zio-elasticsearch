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

package zio.openapi

import zio.json._
@jsonDerive
final case class SecurityScheme(
  `type`: String,
  description: Option[String] = None,
  name: Option[String] = None,
  in: Option[String] = None,
  scheme: Option[String] = None,
  bearerFormat: Option[String] = None,
  flows: Option[OAuthFlows] = None,
  openIdConnectUrl: Option[String] = None
)

object SecurityScheme {

  val BEARER = "bearerAuth"

  def bearer: SecurityScheme =
    SecurityScheme(
      `type` = "http",
      scheme = Some("bearer"),
      bearerFormat = Some("JWT")
    )

  val APIKEY = "ApiKeyAuth"

  def apiKey: SecurityScheme =
    SecurityScheme(
      `type` = "apiKey",
      in = Some("header"),
      name = Some("X-API-KEY")
    )

}
