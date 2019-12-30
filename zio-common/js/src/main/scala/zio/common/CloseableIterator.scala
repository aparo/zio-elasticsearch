/*
 * Copyright 2019 - NTTDATA Italia S.P.A. All Rights Reserved.
 */

package zio.common

trait CloseableIterator[+T] extends Iterator[T]

object CloseableIterator {

  val empty: CloseableIterator[Nothing] = new CloseableIterator[Nothing] {
    def hasNext = false
    def next() = Iterator.empty.next()
    def close(): Unit = {}
  }

  def single[T](t: T): CloseableIterator[T] = new CloseableIterator[T] {
    var hasNext = true

    def next() =
      if (hasNext) {
        hasNext = false
        t
      } else {
        Iterator.empty.next()
      }

    def close(): Unit = {}
  }

  def simple[T](it: Iterator[T]) = new CloseableIterator[T] {
    def hasNext = it.hasNext
    def next() = it.next()
    def close(): Unit = {}
  }
}
