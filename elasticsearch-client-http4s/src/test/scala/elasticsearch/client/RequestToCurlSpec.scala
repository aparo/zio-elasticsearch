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

import org.http4s._
import org.http4s.headers.`Content-Type`
import org.scalatest.{ Matchers, WordSpec }
import zio._

class RequestToCurlSpec extends WordSpec with Matchers {
  import RequestToCurl._
  private val jsonContent = `Content-Type`(MediaType.application.json, Charset.`UTF-8`)
  private val ndjsonContent = `Content-Type`(new MediaType("application", "x-ndjson"), Charset.`UTF-8`)

  "RequestToCurlSpec" should {
    "process simple request" in {
      val request = Request[Task](
        Method.GET,
        Uri.unsafeFromString("http://localhost:9200"),
        headers = Headers.of(Header("Accept", "application/json"))
      ).withContentType(jsonContent)
      toCurl(request) should be(
        "curl -L --max-redirs 32 -X GET 'http://localhost:9200' -H 'Accept: application/json' -H 'Content-Type: application/json; charset=UTF-8'"
      )
    }

    "process body request" in {
      val request = Request[Task](
        Method.POST,
        Uri.unsafeFromString("http://localhost:9200"),
        body = fs2.Stream("""{"name":"Brian O'Connor"}""").through(fs2.text.utf8Encode),
        headers = Headers.of(Header("Accept", "application/json"))
      ).withContentType(jsonContent)
      toCurl(request) should be(
        """curl -L --max-redirs 32 -X POST 'http://localhost:9200' -H 'Accept: application/json' -H 'Content-Type: application/json; charset=UTF-8' -H 'Content-Length: 26' --data '{"name":"Brian O\'Connor"}'"""
      )
    }
  }
}
