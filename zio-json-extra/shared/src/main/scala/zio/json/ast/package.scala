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

package zio.json

import zio.Chunk

package object ast {
  implicit final class JsonOps(private val json: Json) extends AnyVal {

    /**
     * Perform a deep merge of this JSON value with another JSON value.
     *
     * Objects are merged by key, values from the argument JSON take
     * precedence over values from this JSON. Nested objects are
     * recursed.
     *
     * Null, Boolean, String and Number are treated as values,
     * and values from the argument JSON completely replace values
     * from this JSON.
     *
     * `mergeMode` controls the behavior when merging two arrays within JSON.
     * The Default mode treats Array as value, similar to Null, Boolean,
     * String or Number above. The Index mode will replace the elements in
     * this JSON array with the elements in the argument JSON at corresponding
     * position. The Concat mode will concatenate the elements in this JSON array
     * and the argument JSON array.
     */
    def deepMerge(that: Json, mergeMode: MergeMode = MergeMode.Default): Json =
      (json, that) match {
        case (Json.Obj(lhs), Json.Obj(rhs)) =>
          val rhsMap = rhs.toMap
          Json.Obj(
            Chunk.fromIterable(lhs.foldLeft(rhsMap) {
              case (acc, (key, value)) =>
                rhsMap.get(key).fold(acc + (key -> value)) { r =>
                  acc + (key -> value.deepMerge(r, mergeMode))
                }
            })
          )
        case (Json.Arr(lhs), Json.Arr(rhs)) =>
          mergeMode match {
            case MergeMode.Default =>
              that
            case MergeMode.Concat =>
              Json.Arr(lhs ++ rhs)
            case MergeMode.Index =>
              if (rhs.size >= lhs.size)
                that
              else
                Json.Arr(rhs ++ lhs.slice(rhs.size, lhs.size))
          }
        case _ =>
          that
      }

  }
  implicit final class JsonObjOps(private val json: Json.Obj) extends AnyVal {
    def add(name: String, value: Json): Json.Obj = Json.Obj(json.fields ++ Chunk(name -> value))

    def add(name: String, value: Either[String, Json]): Json.Obj = value match {
      case Left(_) => json
      case Right(v)    => Json.Obj(json.fields ++ Chunk(name -> v))
    }
  }

  implicit final class JsonStringOps(private val str: String) extends AnyVal {
    def asJson:Json.Str=Json.Str(str)
  }

  implicit final class JsonIntOps(private val num: Int) extends AnyVal {
    def asJson: Json.Num = Json.Num(num)
  }

  implicit final class JsonBooleanOps(private val bool: Boolean) extends AnyVal {
    def asJson: Json.Bool = Json.Bool(bool)
  }

}
