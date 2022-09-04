/*
 * Copyright 2018-2022 - Alberto Paro on Apache 2 Licence. All Rights Reserved.
 */

package elasticsearch.client

import org.http4s.{EmptyBody, Request, Response}
import zio.interop.catz._
import zio.{Runtime, Task}

import scala.collection.mutable.ListBuffer

object RequestToCurl {

  implicit def toCurl(request: Request[Task]): String = {
    val parts = new ListBuffer[String]()
    parts += s"curl -L --max-redirs 32 -X ${request.method.name} '${request.uri.toString()}'"
    request.headers.foreach { header =>
      parts += s"-H '${header.toString()}'"
    }
    request.body match {
      case EmptyBody =>
      case body =>
        val str =
          new String(Runtime.default.unsafeRun(body.compile.toList).toArray).replace("\'", "\\\'")
        parts += s"-H 'Content-Length: ${str.length}' --data '$str'"
    }
    parts.mkString(" ")
  }

  def responseToString(response: Response[Task], body: String): String =
    s"""Response(status=${response.status.code}, headers=${response.headers.redactSensitive()}, body=$body)"""
}
