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

package elasticsearch

import io.circe._
import io.circe.parser._

import scala.io.Source

trait SpecHelper {
  import logstage._

  implicit lazy val logger: IzLogger = {
    val textSink = ConsoleSink.text(colored = true)

    val sinks = List(textSink)
    IzLogger(Debug, sinks)
  }

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
