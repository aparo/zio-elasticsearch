/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.common.circe

import scala.collection.immutable.Queue

/** This package contains an implementation of Json JsonPatch, according to [RFC-6902](http://tools.ietf.org/html/rfc6902)
 */
package object diffson {

  type Part = Either[String, Int]
  type Pointer = Queue[Part]

  object Pointer {
    val Root: Pointer = Queue.empty

    private val IsDigit = "(0|[1-9][0-9]*)".r

    def apply(elems: String*): Pointer =
      Queue(elems.map {
        case IsDigit(idx) => Right(idx.toInt)
        case key          => Left(key)
      }: _*)

    def unapplySeq(pointer: Pointer): Option[Queue[Part]] =
      Queue.unapplySeq(pointer)

  }

}
