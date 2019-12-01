/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.common.circe.diffson

import scala.annotation.tailrec

import scala.language.implicitConversions

trait JsonPointerSupport[JsValue] { this: DiffsonInstance[JsValue] =>

  import provider._

  type PointerErrorHandler =
    PartialFunction[(JsValue, String, JsonPointer), JsValue]

  /** A class to work with Json pointers according to http://tools.ietf.org/html/rfc6901.
    *  The behavior in case of invalid pointer is customizable by passing an error handler
    *  when instantiating.
    *
    *  @author Lucas Satabin
    */
  case class JsonPointer(path: Pointer) {

    /** Evaluates the given path in the given JSON object.
      *  Upon missing elements in value, the error handler is called with the current value and element
      */
    final def evaluate(
        value: JsValue
    )(implicit handler: PointerErrorHandler): JsValue =
      evaluate(value, this, Pointer.Root, handler)

    def /(key: String): JsonPointer = path :+ Left(key)

    def /(idx: Int): JsonPointer = path :+ Right(idx)

    def +:(part: Part): JsonPointer = copy(part +: path)

    def :+(part: Part): JsonPointer = copy(path :+ part)

    def serialize: String =
      if (path.isEmpty) ""
      else
        "/" + path
          .map {
            case Left(l) => l.replace("~", "~0").replace("/", "~1")
            case Right(r) => r.toString
          }
          .mkString("/")

    @tailrec
    private def evaluate(
        value: JsValue,
        path: JsonPointer,
        parent: JsonPointer,
        handler: PointerErrorHandler
    ): JsValue =
      (value, path.path) match {
        case (JsObject(obj), Left(elem) +: tl) =>
          evaluate(
            obj.getOrElse(elem, JsNull),
            tl,
            parent :+ Left(elem),
            handler
          )
        case (JsArray(arr), Right(idx) +: tl) =>
          if (idx >= arr.size)
            // we know (by construction) that the index is greater or equal to zero
            evaluate(
              handler(Tuple3(value, idx.toString, parent)),
              tl,
              parent :+ Right(idx),
              handler
            )
          else
            evaluate(arr(idx), tl, parent :+ Right(idx), handler)
        case (arr @ JsArray(_), Left("-") +: tl) =>
          evaluate(
            handler(Tuple3(value, "-", parent)),
            tl,
            parent / "-",
            handler
          )
        case (_, Pointer.Root) =>
          value
        case (_, Left(elem) +: tl) =>
          evaluate(
            handler(Tuple3(value, elem, parent)),
            tl,
            parent / elem,
            handler
          )
      }

  }

  object JsonPointer {
    implicit def fromPointer(p: Pointer): JsonPointer = JsonPointer(p)

    /** Parses a JSON pointer and returns the resolved path. */
    def parse(input: String): JsonPointer = JsonPointer {
      if (input == null || input.isEmpty)
        // shortcut if input is empty
        Pointer.Root
      else if (!input.startsWith("/")) {
        // a pointer MUST start with a '/'
        throw new PointerException("A JSON pointer must start with '/'")
      } else {
        // first gets the different parts of the pointer
        val parts = input
          .split("/")
          // the first element is always empty as the path starts with a '/'
          .drop(1)
        if (parts.length == 0) {
          // the pointer was simply "/"
          Pointer("")
        } else {
          // check that an occurrence of '~' is followed by '0' or '1'
          if (parts.exists(_.matches(".*~(?![01]).*"))) {
            throw new PointerException(
              "Occurrences of '~' must be followed by '0' or '1'"
            )
          } else {
            val allParts = if (input.endsWith("/")) parts :+ "" else parts

            val elems = allParts
            // transform the occurrences of '~1' into occurrences of '/'
            // transform the occurrences of '~0' into occurrences of '~'
              .map(_.replace("~1", "/").replace("~0", "~"))
            Pointer(elems: _*)
          }
        }
      }
    }
  }

}
