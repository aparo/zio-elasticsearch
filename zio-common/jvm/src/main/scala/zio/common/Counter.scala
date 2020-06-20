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

package zio.common

class Counter[A, B: Numeric](counter: Map[A, B]) {
  import Numeric.Implicits._

  def +(key: A)(implicit num: Numeric[B]): Counter[A, B] =
    this.change(key, num.fromInt(1))

  def -(key: A)(implicit num: Numeric[B]): Counter[A, B] =
    this.change(key, num.fromInt(-1))
  def *(by: B): Counter[A, B] = Counter(counter.mapValues(value => value * by).toMap)

  def change(key: A, by: B): Counter[A, B] =
    Counter((counter + (key -> { by.+(apply(key)): B })))

  def ++(other: Counter[A, B]): Counter[A, B] =
    other.iterator.foldLeft(this)({
      case (counter, (key, count)) => counter.change(key, count)
    })
  def get(key: A): Option[B] = counter.get(key)

  def apply(key: A)(implicit num: Numeric[B]): B =
    counter.getOrElse(key, num.fromInt(0))
  def toMap(): Map[A, B] = counter
  def iterator(): Iterator[Tuple2[A, B]] = counter.iterator
  def toList(): List[Tuple2[A, B]] = counter.toList
  def toSeq(): Seq[Tuple2[A, B]] = counter.toSeq
  def empty(): Counter[A, B] = Counter[A, B]()
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
    Counter(counter.mapValues(ev).toMap)
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
    count.foldLeft(apply[A, Int]())({
      case (counter, element) => counter + element
    })
}
