/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import io.circe._
import io.circe.parser._

import scala.io.Source

trait SpecHelper {

  def readResource(name: String): String = {
    val source = Source.fromInputStream(getClass.getResourceAsStream(name))
    val res = source.mkString
    source.close
    res
  }

  def readResourceJSON(name: String): Json = {
    val parsed = parse(readResource(name))
    if (parsed.isLeft) println(parsed)
    parsed.right.get
  }

}
