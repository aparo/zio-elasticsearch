/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

// /*
//  * Copyright 2018-2022 - Alberto Paro on Apache 2 Licence. All Rights Reserved.
//  */

// package zio.elasticsearch.client

// import org.http4s._
// import org.http4s.headers.`Content-Type`
// import org.scalatest.matchers.should.Matchers
// import org.scalatest.wordspec.AnyWordSpec
// import zio._

// class RequestToCurlSpec extends AnyWordSpec with Matchers {
//   import RequestToCurl._
//   private val jsonContent =
//     `Content-Type`(MediaType.application.json, Charset.`UTF-8`)
//   private val ndjsonContent =
//     `Content-Type`(new MediaType("application", "x-ndjson"), Charset.`UTF-8`)

//   "RequestToCurlSpec" should {
//     "process simple request" in {
//       val request = Request[Task](
//         Method.GET,
//         Uri.unsafeFromString("http://localhost:9200"),
//         headers = Headers.of(Header("Accept", "application/json"))
//       ).withContentType(jsonContent)
//       toCurl(request) should be(
//         "curl -L --max-redirs 32 -X GET 'http://localhost:9200' -H 'Accept: application/json' -H 'Content-Type: application/json; charset=UTF-8'"
//       )
//     }

//     "process body request" in {
//       val request = Request[Task](
//         Method.POST,
//         Uri.unsafeFromString("http://localhost:9200"),
//         body = fs2.Stream("""{"name":"Brian O'Connor"}""").through(fs2.text.utf8Encode),
//         headers = Headers.of(Header("Accept", "application/json"))
//       ).withContentType(jsonContent)
//       toCurl(request) should be(
//         """curl -L --max-redirs 32 -X POST 'http://localhost:9200' -H 'Accept: application/json' -H 'Content-Type: application/json; charset=UTF-8' -H 'Content-Length: 26' --data '{"name":"Brian O\'Connor"}'"""
//       )
//     }
//   }
// }
