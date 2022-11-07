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

package zio.elasticsearch.geo

object SpatialStrategy extends Enumeration {
  type SpatialStrategy = Value
  val term, recursive = Value
}

/**
 * Utilities for encoding and decoding geohashes. Based on
 * http://en.wikipedia.org/wiki/Geohash.
 */
object GeoHashUtils {

  private final val BASE_32: Array[Char] =
    Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p',
      'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
  final val PRECISION: Int = 12
  private final val BITS: Array[Int] = Array(16, 8, 4, 2, 1)

  def encode(latitude: Double, longitude: Double): String =
    encode(latitude, longitude, PRECISION)

  /**
   * Encodes the given latitude and longitude into a geohash
   *
   * @param latitude
   *   Latitude to encode
   * @param longitude
   *   Longitude to encode
   * @return
   *   Geohash encoding of the longitude and latitude
   */
  def encode(latitude: Double, longitude: Double, precision: Int): String = {
    var latInterval0: Double = -90.0
    var latInterval1: Double = 90.0
    var lngInterval0: Double = -180.0
    var lngInterval1: Double = 180.0
    val geohash: StringBuilder = new StringBuilder
    var isEven: Boolean = true
    var bit: Int = 0
    var ch: Int = 0
    while (geohash.length < precision) {
      var mid: Double = 0.0
      if (isEven) {
        mid = (lngInterval0 + lngInterval1) / 2d
        if (longitude > mid) {
          ch |= BITS(bit)
          lngInterval0 = mid
        } else {
          lngInterval1 = mid
        }
      } else {
        mid = (latInterval0 + latInterval1) / 2d
        if (latitude > mid) {
          ch |= BITS(bit)
          latInterval0 = mid
        } else {
          latInterval1 = mid
        }
      }
      isEven = !isEven
      if (bit < 4) {
        bit += 1
      } else {
        geohash.append(BASE_32(ch))
        bit = 0
        ch = 0
      }
    }
    geohash.toString
  }

  private final def encode(x: Int, y: Int): Char =
    BASE_32(
      ((x & 1) + ((y & 1) * 2) + ((x & 2) * 2) + ((y & 2) * 4) + ((x & 4) * 4)) % 32
    )

  /**
   * Calculate all neighbors of a given geohash cell.
   *
   * @param geohash
   *   Geohash of the defines cell
   * @return
   *   geohashes of all neighbor cells
   */
  def neighbors(geohash: String): List[String] =
    addNeighbors(geohash, geohash.length, Nil)

  /**
   * Calculate the geohash of a neighbor of a geohash
   *
   * @param geohash
   *   the geohash of a cell
   * @param level
   *   level of the geohash
   * @param dx
   *   delta of the first grid coordinate (must be -1, 0 or +1)
   * @param dy
   *   delta of the second grid coordinate (must be -1, 0 or +1)
   * @return
   *   geohash of the defined cell
   */
  private def neighbor(
    geohash: String,
    level: Int,
    dx: Int,
    dy: Int
  ): Option[String] = {
    val cell: Int = decode(geohash.charAt(level - 1))
    val x0: Int = cell & 1
    val y0: Int = cell & 2
    val x1: Int = cell & 4
    val y1: Int = cell & 8
    val x2: Int = cell & 16
    val x: Int = x0 + (x1 / 2) + (x2 / 4)
    val y: Int = (y0 / 2) + (y1 / 4)
    if (level == 1) {
      if ((dy < 0 && y == 0) || (dy > 0 && y == 3)) {
        return None
      } else {
        return Some(Character.toString(encode(x + dx, y + dy)))
      }
    } else {
      val nx: Int = if (((level % 2) == 1)) (x + dx) else (x + dy)
      val ny: Int = if (((level % 2) == 1)) (y + dy) else (y + dx)
      val xLimit: Int = if (((level % 2) == 0)) 7 else 3
      val yLimit: Int = if (((level % 2) == 0)) 3 else 7
      if (nx >= 0 && nx <= xLimit && ny >= 0 && ny < yLimit) {
        return Some(geohash.substring(0, level - 1) + encode(nx, ny))
      } else {
        val nr = neighbor(geohash, (level - 1), dx, dy)
        if (nr.isDefined) {
          return Some(nr.get + encode(nx, ny))
        } else {
          return None
        }
      }
    }
  }

  /**
   * Add all geohashes of the cells next to a given geohash to a list.
   *
   * @param geohash
   *   Geohash of a specified cell
   * @param length
   *   level of the given geohash
   * @param neighbors
   *   list to add the neighbors to
   * @return
   *   the given list
   */
  private final def addNeighbors(
    geohash: String,
    length: Int,
    neighbors: List[String]
  ): List[String] = {
    var result: List[String] = neighbors
    val south = neighbor(geohash, length, 0, -1)
    val north = neighbor(geohash, length, 0, +1)
    if (north.isDefined) {
      result = neighbor(north.get, length, -1, 0).get :: north.get :: neighbor(
        north.get,
        length,
        +1,
        0
      ).get :: result
    }
    result = neighbor(geohash, length, -1, 0).get :: neighbor(
      geohash,
      length,
      +1,
      0
    ).get :: result
    if (south != null) {
      result = neighbor(south.get, length, -1, 0).get :: north.get :: neighbor(
        south.get,
        length,
        +1,
        0
      ).get :: result
    }
    result
  }

  private final def decode(geo: Char): Int =
    geo match {
      case '0' =>
        return 0
      case '1' =>
        return 1
      case '2' =>
        return 2
      case '3' =>
        return 3
      case '4' =>
        return 4
      case '5' =>
        return 5
      case '6' =>
        return 6
      case '7' =>
        return 7
      case '8' =>
        return 8
      case '9' =>
        return 9
      case 'b' =>
        return 10
      case 'c' =>
        return 11
      case 'd' =>
        return 12
      case 'e' =>
        return 13
      case 'f' =>
        return 14
      case 'g' =>
        return 15
      case 'h' =>
        return 16
      case 'j' =>
        return 17
      case 'k' =>
        return 18
      case 'm' =>
        return 19
      case 'n' =>
        return 20
      case 'p' =>
        return 21
      case 'q' =>
        return 22
      case 'r' =>
        return 23
      case 's' =>
        return 24
      case 't' =>
        return 25
      case 'u' =>
        return 26
      case 'v' =>
        return 27
      case 'w' =>
        return 28
      case 'x' =>
        return 29
      case 'y' =>
        return 30
      case 'z' =>
        return 31
    }

  /**
   * Decodes the given geohash into a latitude and longitude
   *
   * @param geohash
   *   Geohash to deocde
   * @return
   *   Array with the latitude at index 0, and longitude at index 1
   */
  def decode(geohash: String): GeoPoint = {
    val interval: Array[Double] = decodeCell(geohash)
    GeoPoint((interval(0) + interval(1)) / 2d, (interval(2) + interval(3)) / 2d)
  }

  /**
   * Decodes the given geohash into a geohash cell defined by the points
   * nothWest and southEast
   *
   * @param geohash
   *   Geohash to deocde
   * @param northWest
   *   the point north/west of the cell
   * @param southEast
   *   the point south/east of the cell
   */
  def decodeCell(
    geohash: String,
    northWest: GeoPoint,
    southEast: GeoPoint
  ): (GeoPoint, GeoPoint) = {
    val interval: Array[Double] = decodeCell(geohash)
    (GeoPoint(interval(1), interval(2)), GeoPoint(interval(0), interval(3)))
  }

  private def decodeCell(geohash: String): Array[Double] = {
    val interval: Array[Double] = Array(-90.0, 90.0, -180.0, 180.0)
    var isEven: Boolean = true

    geohash.toStream.foreach { ch =>
      val cd = decode(ch)
      BITS.foreach { mask =>
        if (isEven) {
          if ((cd & mask) != 0) {
            interval(2) = (interval(2) + interval(3)) / 2d
          } else {
            interval(3) = (interval(2) + interval(3)) / 2d
          }
        } else {
          if ((cd & mask) != 0) {
            interval(0) = (interval(0) + interval(1)) / 2d
          } else {
            interval(1) = (interval(0) + interval(1)) / 2d
          }
        }
        isEven = !isEven
      }
    }
    interval
  }
}
