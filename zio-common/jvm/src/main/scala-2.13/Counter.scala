/*
 * Copyright 2018-2022 - Alberto Paro on Apache 2 Licence. All Rights Reserved.
 */

package zio.common

class Counter[A, B: Numeric](counter: Map[A, B]) {
  import Numeric.Implicits._

  def +(key: A)(implicit num: Numeric[B]): Counter[A, B] =
    this.change(key, num.fromInt(1))

  def -(key: A)(implicit num: Numeric[B]): Counter[A, B] =
    this.change(key, num.fromInt(-1))
  def *(by: B): Counter[A, B] = Counter(counter.view.mapValues(value => value * by).toMap)

  def change(key: A, by: B): Counter[A, B] =
    Counter((counter + (key -> { by.+(apply(key)): B })))

  def ++(other: Counter[A, B]): Counter[A, B] =
    other.iterator.foldLeft(this) {
      case (counter, (key, count)) =>
        counter.change(key, count)
    }
  def get(key: A): Option[B] = counter.get(key)

  def apply(key: A)(implicit num: Numeric[B]): B =
    counter.getOrElse(key, num.fromInt(0))
  def toMap: Map[A, B] = counter
  def iterator: Iterator[Tuple2[A, B]] = counter.iterator
  def toList: List[Tuple2[A, B]] = counter.toList
  def toSeq: Seq[Tuple2[A, B]] = counter.toSeq
  def empty: Counter[A, B] = Counter[A, B]()
  def size: Int = counter.size
  override def equals(other: Any): Boolean = other match {
    case (other: Counter[A, B]) => counter.toMap.equals(other.toMap)
    case _                      => false
  }
  override def hashCode = counter.hashCode
  override def toString: String =
    counter.toString.replaceFirst("Map", "Counter") // cheap but effective
  def mapValues[Num: Numeric](
    fun: B => Num
  )(implicit num: Numeric[Num]): Counter[A, Num] =
    toCounter(num, fun)

  def toCounter[Num: Numeric](implicit ev: B => Num): Counter[A, Num] =
    Counter(counter.view.mapValues(ev).toMap)
  def max: A = counter.maxBy(_._2)._1
  def sum: B = counter.values.sum

  // Normalizes the counter such that the sum of all values is one.
  def normalize(): Counter[A, Double] =
    mapValues(value => value.toDouble / sum.toDouble)
  def keys(): Iterable[A] = counter.keys
}

object Counter {
  def apply[A, B: Numeric](): Counter[A, B] = apply(Map[A, B]())

  def apply[A, B: Numeric](counter: Map[A, B]): Counter[A, B] =
    new Counter[A, B](counter)
  def apply[A](count: Iterable[A]): Counter[A, Int] = apply(count.toIterator)

  def apply[A](count: Iterator[A]): Counter[A, Int] =
    count.foldLeft(apply[A, Int]()) {
      case (counter, element) =>
        counter + element
    }
}
