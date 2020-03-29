/*
 * Copyright 2019-2020 Alberto Paro
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

package zio.exception

object ErrorCode {
  val Continue = 100 // "Continue", "The server has received the request headers, and the client should proceed to send the request body."
  val SwitchingProtocols = 101 // "Switching Protocols", "The server is switching protocols, because the client requested the switch."
  val Processing = 102 // "Processing", "The server is processing the request, but no response is available yet."

  val EarlyHints = 103 // "Early Hints", "The server is likely to send a final response with the header fields included in the response." // RFC 8297

  val OK = 200 // "OK", "OK"
  val Created = 201 // "Created", "The request has been fulfilled and resulted in a new resource being created."
  val Accepted = 202 // "Accepted", "The request has been accepted for processing, but the processing has not been completed."
  val NonAuthoritativeInformation = 203 // "Non-Authoritative Information", "The server successfully processed the request, but is returning information that may be from another source."
  val NoContent = 204 // "No Content", "The server successfully processed the request and is not returning any content.", allowsEntity = false
  val ResetContent = 205 // "Reset Content", "The server successfully processed the request, but is not returning any content."
  val PartialContent = 206 // "Partial Content", "The server is delivering only part of the resource due to a range header sent by the client."
  val MultiStatus = 207 // "Multi-Status", "The message body that follows is an XML message and can contain a number of separate response codes, depending on how many sub-requests were made."
  val AlreadyReported = 208 // "Already Reported", "The members of a DAV binding have already been enumerated in a previous reply to this request, and are not being included again."
  val IMUsed = 226 // "IM Used", "The server has fulfilled a GET request for the resource, and the response is a representation of the result of one or more instance-manipulations applied to the current instance."

  val MultipleChoices = 300 // "Multiple Choices", "There are multiple options for the resource that the client may follow.", "There are multiple options for the resource that the client may follow. The preferred one is <a href=\"%s\">this URI</a>."
  val MovedPermanently = 301 // "Moved Permanently", "This and all future requests should be directed to the given URI.", "This and all future requests should be directed to <a href=\"%s\">this URI</a>."
  val Found = 302 // "Found", "The resource was found, but at a different URI.", "The requested resource temporarily resides under <a href=\"%s\">this URI</a>."
  val SeeOther = 303 // "See Other", "The response to the request can be found under another URI using a GET method.", "The response to the request can be found under <a href=\"%s\">this URI</a> using a GET method."
  val NotModified = 304 // "Not Modified", "The resource has not been modified since last requested.", "", allowsEntity = false
  val UseProxy = 305 // "Use Proxy", "This single request is to be repeated via the proxy given by the Location field.", "This single request is to be repeated via the proxy under <a href=\"%s\">this URI</a>."
  val TemporaryRedirect = 307 // "Temporary Redirect", "The request should be repeated with another URI, but future requests can still use the original URI.", "The request should be repeated with <a href=\"%s\">this URI</a>, but future requests can still use the original URI."
  val PermanentRedirect = 308 // "Permanent Redirect", "The request, and all future requests should be repeated using another URI.", "The request, and all future requests should be repeated using <a href=\"%s\">this URI</a>."

  val BadRequest = 400 // "Bad Request", "The request contains bad syntax or cannot be fulfilled."
  val Unauthorized = 401 // "Unauthorized", "Authentication is possible but has failed or not yet been provided."
  val PaymentRequired = 402 // "Payment Required", "Reserved for future use."
  val Forbidden = 403 // "Forbidden", "The request was a legal request, but the server is refusing to respond to it."
  val NotFound = 404 // "Not Found", "The requested resource could not be found but may be available again in the future."
  val MethodNotAllowed = 405 // "Method Not Allowed", "A request was made of a resource using a request method not supported by that resource;"
  val NotAcceptable = 406 // "Not Acceptable", "The requested resource is only capable of generating content not acceptable according to the Accept headers sent in the request."
  val ProxyAuthenticationRequired = 407 // "Proxy Authentication Required", "Proxy authentication is required to access the requested resource."
  val RequestTimeout = 408 // "Request Timeout", "The server timed out waiting for the request."
  val Conflict = 409 // "Conflict", "The request could not be processed because of conflict in the request, such as an edit conflict."
  val Gone = 410 // "Gone", "The resource requested is no longer available and will not be available again."
  val LengthRequired = 411 // "Length Required", "The request did not specify the length of its content, which is required by the requested resource."
  val PreconditionFailed = 412 // "Precondition Failed", "The server does not meet one of the preconditions that the requester put on the request."
  val RequestEntityTooLarge = 413 // "Request Entity Too Large", "The request is larger than the server is willing or able to process."
  val RequestUriTooLong = 414 // "Request-URI Too Long", "The URI provided was too long for the server to process."
  val UnsupportedMediaType = 415 // "Unsupported Media Type", "The request entity has a media type which the server or resource does not support."
  val RequestedRangeNotSatisfiable = 416 // "Requested Range Not Satisfiable", "The client has asked for a portion of the file, but the server cannot supply that portion."
  val ExpectationFailed = 417 // "Expectation Failed", "The server cannot meet the requirements of the Expect request-header field."
  val ImATeapot = 418 // "I'm a teapot", "The resulting entity body MAY be short and stout."
  val EnhanceYourCalm = 420 // "Enhance Your Calm", "You are being rate-limited." // Twitter only
  val MisdirectedRequest = 421 // "Misdirected Request", "The request was directed at a server that is not able to produce a response." // HTTP/2 only. https://tools.ietf.org/html/rfc7540#section-9.1.2
  val UnprocessableEntity = 422 // "Unprocessable Entity", "The request was well-formed but was unable to be followed due to semantic errors."
  val Locked = 423 // "Locked", "The resource that is being accessed is locked."
  val FailedDependency = 424 // "Failed Dependency", "The request failed due to failure of a previous request."
  val UnorderedCollection = 425 // "Unordered Collection", "The collection is unordered."
  val UpgradeRequired = 426 // "Upgrade Required", "The client should switch to a different protocol."
  val PreconditionRequired = 428 // "Precondition Required", "The server requires the request to be conditional."
  val TooManyRequests = 429 // "Too Many Requests", "The user has sent too many requests in a given amount of time."
  val RequestHeaderFieldsTooLarge = 431 // "Request Header Fields Too Large", "The server is unwilling to process the request because either an individual header field, or all the header fields collectively, are too large."
  val RetryWith = 449 // "Retry With", "The request should be retried after doing the appropriate action."
  val BlockedByParentalControls = 450 // "Blocked by Windows Parental Controls", "Windows Parental Controls are turned on and are blocking access to the given webpage."
  val UnavailableForLegalReasons = 451 // "Unavailable For Legal Reasons", "Resource access is denied for legal reasons."

  val InternalServerError = 500 // "Internal Server Error", "There was an internal server error."
  val NotImplemented = 501 // "Not Implemented", "The server either does not recognize the request method, or it lacks the ability to fulfill the request."
  val BadGateway = 502 // "Bad Gateway", "The server was acting as a gateway or proxy and received an invalid response from the upstream server."
  val ServiceUnavailable = 503 // "Service Unavailable", "The server is currently unavailable (because it is overloaded or down for maintenance)."
  val GatewayTimeout = 504 // "Gateway Timeout", "The server was acting as a gateway or proxy and did not receive a timely response from the upstream server."
  val HTTPVersionNotSupported = 505 // "HTTP Version Not Supported", "The server does not support the HTTP protocol version used in the request."
  val VariantAlsoNegotiates = 506 // "Variant Also Negotiates", "Transparent content negotiation for the request, results in a circular reference."
  val InsufficientStorage = 507 // "Insufficient Storage", "Insufficient storage to complete the request."
  val LoopDetected = 508 // "Loop Detected", "The server detected an infinite loop while processing the request."
  val BandwidthLimitExceeded = 509 // "Bandwidth Limit Exceeded", "Bandwidth limit has been exceeded."
  val NotExtended = 510 // "Not Extended", "Further extensions to the request are required for the server to fulfill it."
  val NetworkAuthenticationRequired = 511 // "Network Authentication Required", "The client needs to authenticate to gain network access."
  val NetworkReadTimeout = 598 // "Network read timeout error", ""
  val NetworkConnectTimeout = 599 // "Network connect timeout error", ""
}
