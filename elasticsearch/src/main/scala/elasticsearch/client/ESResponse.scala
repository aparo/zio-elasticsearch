/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import io.circe.parser.parse

final case class ESResponse(status: Int, body: String) {

  def json: Either[io.circe.ParsingFailure, io.circe.Json] = parse(body)
}
