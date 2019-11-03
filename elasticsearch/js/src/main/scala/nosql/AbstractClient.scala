package elasticsearch.nosql

import scala.concurrent.{ Await, Awaitable }
import scala.concurrent.duration._
import scala.scalajs.js.URIUtils.encodeURIComponent

trait AbstractClient {
  def baseUrl: String

  var timeout = 15.seconds
  def awaitResult[T](t: Awaitable[T]) = Await.result(t, timeout)

  val headers = Map("Content-Type" -> "application/json;charset=UTF-8")

  def addGetParam(url: String, name: String, value: String): String = {
    var newUrl = url
    if (url.contains("?")) newUrl += "&" else newUrl += "?"
    newUrl += encodeURIComponent(name) + "=" + encodeURIComponent(value)
    newUrl
  }

  def addGetParam(url: String, args: Map[String, String]): String = {
    if (args.isEmpty)
      return url
    var newUrl = url
    if (url.contains("?")) newUrl += "&" else newUrl += "?"
    newUrl += args
      .map(
        value => encodeURIComponent(value._1) + "=" + encodeURIComponent(value._2)
      )
      .mkString("&")
    newUrl
  }
}
