/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Tests for the UriEncoding object.
 */
class UriEncodingSpec extends AnyFlatSpec with Matchers {
  import UriEncoding._

  sealed trait EncodingResult
  // Good behaviour
  case object NotEncoded extends EncodingResult
  case class PercentEncoded(encoded: String) extends EncodingResult
  // Bad behaviour
  case class NotEncodedButDecodeDifferent(decodedEncoded: String) extends EncodingResult
  case class PercentEncodedButDecodeDifferent(
    encoded: String,
    decodedEncoded: String
  ) extends EncodingResult
  case class PercentEncodedButDecodedInvalid(encoded: String) extends EncodingResult

  def encodingFor(in: String, inCharset: String): EncodingResult = {
    val encoded = encodePathSegment(in, inCharset)
    if (encoded == in) {
      val decodedEncoded = decodePathSegment(encoded, inCharset)
      if (decodedEncoded != in)
        return NotEncodedButDecodeDifferent(decodedEncoded)
      NotEncoded
    } else {
      val decodedEncoded = decodePathSegment(encoded, inCharset)
      if (decodedEncoded != in)
        return PercentEncodedButDecodeDifferent(encoded, decodedEncoded)
      try {
        decodePathSegment(in, inCharset)
        return PercentEncodedButDecodedInvalid(encoded) // Decoding should have failed
      } catch {
        case _: InvalidUriEncodingException => () // This is expected behaviour
      }
      PercentEncoded(encoded)
    }
  }

  "Path segment encoding and decoding" should "percent-encode reserved characters that aren't allowed in a path segment" in {

    /*
RFC 3986 - Uniform Resource Identifier (URI): Generic Syntax

2.2.  Reserved Characters
   ...
      reserved    = gen-delims / sub-delims

      gen-delims  = ":" / "/" / "?" / "#" / "[" / "]" / "@"

      sub-delims  = "!" / "$" / "&" / "'" / "(" / ")"
                  / "*" / "+" / "," / ";" / "="
   ...
   URI producing applications should percent-encode data octets that
   correspond to characters in the reserved set unless these characters
   are specifically allowed by the URI scheme to represent data in that
   component.
...
3.3.  Path
   ...
      segment       = *pchar
      segment-nz    = 1*pchar
      segment-nz-nc = 1*( unreserved / pct-encoded / sub-delims / "@" )
                    ; non-zero-length segment without any colon ":"

      pchar         = unreserved / pct-encoded / sub-delims / ":" / "@"
     */

    // Not allowed (gen-delims, except ":" / "@")
    encodingFor("/", "utf-8") should be(PercentEncoded("%2F"))
    encodingFor("?", "utf-8") should be(PercentEncoded("%3F"))
    encodingFor("#", "utf-8") should be(PercentEncoded("%23"))
    encodingFor("[", "utf-8") should be(PercentEncoded("%5B"))
    encodingFor("]", "utf-8") should be(PercentEncoded("%5D"))
  }

  it should "not percent-encode reserved characters that are allowed in a path segment" in {
    // Allowed (sub-delims / ":" / "@")
    encodingFor("!", "utf-8") should be(NotEncoded)
    encodingFor("$", "utf-8") should be(NotEncoded)
    encodingFor("&", "utf-8") should be(NotEncoded)
    encodingFor("'", "utf-8") should be(NotEncoded)
    encodingFor("(", "utf-8") should be(NotEncoded)
    encodingFor(")", "utf-8") should be(NotEncoded)
    encodingFor("*", "utf-8") should be(NotEncoded)
    encodingFor("+", "utf-8") should be(NotEncoded)
    encodingFor(",", "utf-8") should be(NotEncoded)
    encodingFor(";", "utf-8") should be(NotEncoded)
    encodingFor("=", "utf-8") should be(NotEncoded)
    encodingFor(":", "utf-8") should be(NotEncoded)
    encodingFor("@", "utf-8") should be(NotEncoded)
  }

  /*
2.3.  Unreserved Characters
   ...
      unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
   ... For consistency, percent-encoded octets in the ranges of ALPHA
   (%41-%5A and %61-%7A), DIGIT (%30-%39), hyphen (%2D), period (%2E),
   underscore (%5F), or tilde (%7E) should not be created by URI
   producers and, when found in a URI, should be decoded to their
   corresponding unreserved characters by URI normalizers.
   */
  it should "not percent-encode unreserved characters" in {
    encodingFor("a", "utf-8") should be(NotEncoded)
    encodingFor("z", "utf-8") should be(NotEncoded)
    encodingFor("A", "utf-8") should be(NotEncoded)
    encodingFor("Z", "utf-8") should be(NotEncoded)
    encodingFor("0", "utf-8") should be(NotEncoded)
    encodingFor("9", "utf-8") should be(NotEncoded)
    encodingFor("-", "utf-8") should be(NotEncoded)
    encodingFor(".", "utf-8") should be(NotEncoded)
    encodingFor("_", "utf-8") should be(NotEncoded)
    encodingFor("~", "utf-8") should be(NotEncoded)
  }

  /*
2.1.  Percent-Encoding

   A percent-encoding mechanism is used to represent a data octet in a
   component when that octet's corresponding character is outside the
   allowed set...
   */
  it should "percent-encode any characters that aren't specifically allowed in a path segment" in {
    encodingFor("\u0000", "US-ASCII") should be(PercentEncoded("%00"))
    encodingFor("\u001F", "US-ASCII") should be(PercentEncoded("%1F"))
    encodingFor(" ", "US-ASCII") should be(PercentEncoded("%20"))
    encodingFor("\"", "US-ASCII") should be(PercentEncoded("%22"))
    encodingFor("%", "US-ASCII") should be(PercentEncoded("%25"))
    encodingFor("<", "US-ASCII") should be(PercentEncoded("%3C"))
    encodingFor(">", "US-ASCII") should be(PercentEncoded("%3E"))
    encodingFor("\\", "US-ASCII") should be(PercentEncoded("%5C"))
    encodingFor("^", "US-ASCII") should be(PercentEncoded("%5E"))
    encodingFor("`", "US-ASCII") should be(PercentEncoded("%60"))
    encodingFor("{", "US-ASCII") should be(PercentEncoded("%7B"))
    encodingFor("|", "US-ASCII") should be(PercentEncoded("%7C"))
    encodingFor("}", "US-ASCII") should be(PercentEncoded("%7D"))
    encodingFor("\u007F", "ISO-8859-1") should be(PercentEncoded("%7F"))
    encodingFor("\u00FF", "ISO-8859-1") should be(PercentEncoded("%FF"))
  }

  it should "percent-encode UTF-8 strings by encoding each octet not allowed in a path segment" in {
    encodingFor("£0.25", "UTF-8") should be(PercentEncoded("%C2%A30.25"))
    encodingFor("€100", "UTF-8") should be(PercentEncoded("%E2%82%AC100"))
    encodingFor("«küßî»", "UTF-8") should be(
      PercentEncoded(
        "%C2%ABk%C3%BC%C3%9F%C3%AE%C2%BB"
      )
    )
    encodingFor("“ЌύБЇ”", "UTF-8") should be(
      PercentEncoded(
        "%E2%80%9C%D0%8C%CF%8D%D0%91%D0%87%E2%80%9D"
      )
    )
  }

  /*
2.1.  Percent-Encoding

      ... A percent-encoded octet is encoded as a character
   triplet, consisting of the percent character "%" followed by the two
   hexadecimal digits representing that octet's numeric value. ...

      pct-encoded = "%" HEXDIG HEXDIG

   The uppercase hexadecimal digits 'A' through 'F' are equivalent to
   the lowercase digits 'a' through 'f', respectively.  If two URIs
   differ only in the case of hexadecimal digits used in percent-encoded
   octets, they are equivalent.  For consistency, URI producers and
   normalizers should use uppercase hexadecimal digits for all percent-
   encodings.
   */
  it should "percent-encode to triplets with upper-case hex" in {
    encodingFor("\u0000", "ISO-8859-1") should be(PercentEncoded("%00"))
    encodingFor("\u0099", "ISO-8859-1") should be(PercentEncoded("%99"))
    encodingFor("\u00AA", "ISO-8859-1") should be(PercentEncoded("%AA"))
    encodingFor("\u00FF", "ISO-8859-1") should be(PercentEncoded("%FF"))
  }

  // Misc tests

  it should "handle strings of different lengths" in {
    encodingFor("", "UTF-8") should be(NotEncoded)
    encodingFor("1", "UTF-8") should be(NotEncoded)
    encodingFor("12", "UTF-8") should be(NotEncoded)
    encodingFor("123", "UTF-8") should be(NotEncoded)
    encodingFor("1234567890", "UTF-8") should be(NotEncoded)
  }

  it should "handle strings needing partial percent-encoding" in {
    encodingFor("Hello world", "US-ASCII") should be(PercentEncoded("Hello%20world"))
    encodingFor("/home/foo", "US-ASCII") should be(PercentEncoded("%2Fhome%2Ffoo"))
  }

  // Path segment encoding differs from query string encoding, which is
  // "application/x-www-form-urlencoded". One difference is the encoding
  // of the "+" and space characters.
  it should "percent-encode spaces, but not + characters" in {
    encodingFor(" ", "US-ASCII") should be(PercentEncoded("%20")) // vs "+" for query strings
    encodingFor("+", "US-ASCII") should be(NotEncoded) // vs "%2B" for query strings
    encodingFor(" +", "US-ASCII") should be(PercentEncoded("%20+")) // vs "+%2B" for query strings
    encodingFor("1+2=3", "US-ASCII") should be(NotEncoded)
    encodingFor("1 + 2 = 3", "US-ASCII") should be(PercentEncoded("1%20+%202%20=%203"))
  }

  it should "decode characters percent-encoded with upper and lowercase hex digits" in {
    decodePathSegment("%aa", "ISO-8859-1") should be("\u00AA")
    decodePathSegment("%aA", "ISO-8859-1") should be("\u00AA")
    decodePathSegment("%Aa", "ISO-8859-1") should be("\u00AA")
    decodePathSegment("%AA", "ISO-8859-1") should be("\u00AA")
    decodePathSegment("%ff", "ISO-8859-1") should be("\u00FF")
    decodePathSegment("%fF", "ISO-8859-1") should be("\u00FF")
    decodePathSegment("%Ff", "ISO-8859-1") should be("\u00FF")
    decodePathSegment("%FF", "ISO-8859-1") should be("\u00FF")
  }

  it should "decode percent-encoded characters that don't really need to be encoded" in {
    decodePathSegment("%21", "utf-8") should be("!")
    decodePathSegment("%61", "utf-8") should be("a")
    decodePathSegment("%31%32%33", "UTF-8") should be("123")
    // Encoded by MIME type "application/x-www-form-urlencoded"
    decodePathSegment("%2b", "US-ASCII") should be("+")
    decodePathSegment("%7e", "US-ASCII") should be("~")
  }

  "Path decoding" should "decode basic paths" in {
    decodePath("", "utf-8") should be("")
    decodePath("/", "utf-8") should be("/")
    decodePath("/abc", "utf-8") should be("/abc")
    decodePath("/css/stylesheet.css", "utf-8") should be("/css/stylesheet.css")
  }

  it should "decode paths with encoded characters" in {
    decodePath("/hello%20world", "utf-8") should be("/hello world")
  }

  it should "decode encoded slashes (although they can't be distinguished from unencoded slashes)" in {
    decodePath("/a%2fb", "utf-8") should be("/a/b")
    decodePath("/a%2fb/c%2fd", "utf-8") should be("/a/b/c/d")
  }

  it should "not decode badly encoded paths" in {
    assertThrows[InvalidUriEncodingException] {
      decodePath("/a|b/", "utf-8")
    }
    assertThrows[InvalidUriEncodingException] {
      decodePath("/hello world", "utf-8")
    }
  }

  it should "not perform normalization of dot-segments" in {
    decodePath("a/..", "utf-8") should be("a/..")
    decodePath("a/.", "utf-8") should be("a/.")
  }

  it should "not perform normalization of duplicate slashes" in {
    decodePath("//a", "utf-8") should be("//a")
    decodePath("a//b", "utf-8") should be("a//b")
    decodePath("a//", "utf-8") should be("a//")
  }

  it should "decode complex UTF-8 octets" in {
    decodePath("/path/%C2%ABk%C3%BC%C3%9F%C3%AE%C2%BB", "UTF-8") should be("/path/«küßî»")
    decodePath("/path/%E2%80%9C%D0%8C%CF%8D%D0%91%D0%87%E2%80%9D", "UTF-8") should be("/path/“ЌύБЇ”")
  }

  // Internal methods

  "Internal UriEncoding methods" should "know how to split strings" in {
    splitString("", '/') should be(Seq(""))
    splitString("/", '/') should be(Seq("", ""))
    splitString("a", '/') should be(Seq("a"))
    splitString("a/b", '/') should be(Seq("a", "b"))
    splitString("a//b", '/') should be(Seq("a", "", "b"))
    splitString("/a", '/') should be(Seq("", "a"))
    splitString("/a/b", '/') should be(Seq("", "a", "b"))
    splitString("/a/b/", '/') should be(Seq("", "a", "b", ""))
    splitString("/abc/xyz", '/') should be(Seq("", "abc", "xyz"))
  }

}
