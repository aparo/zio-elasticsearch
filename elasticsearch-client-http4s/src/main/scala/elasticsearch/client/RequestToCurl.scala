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

package elasticsearch.client

import org.http4s.{ EmptyBody, Request }
import zio.{ DefaultRuntime, Task }

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import zio.interop.catz._

object RequestToCurl {
  implicit lazy val runtime = new DefaultRuntime {}

  implicit def toCurl(request: Request[Task]): String = {
    val parts = new ListBuffer[String]()
    parts += s"curl -L --max-redirs 32 -X ${request.method.name} '${request.uri.toString()}'"
    request.headers.foreach { header =>
      parts += s"-H '${header.toString()}'"
    }
    request.body match {
      case EmptyBody =>
      case body =>
        val str = new String(runtime.unsafeRun(body.compile.toList).toArray).replace("\'", "\\\'")
        parts += s"-H 'Content-Length: ${str.length}' --data '${str}'"
    }
    parts.mkString(" ")
  }
}
