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

package zio.circe.diffson

package test

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

abstract class TestArrayDiff[JsValue, Instance <: DiffsonInstance[JsValue]](
  val instance: Instance
) extends Properties("TestArrayDiff") {

  import instance._
  import provider._

  implicit def intSeqMarshaller: Marshaller[Seq[Int]]

  implicit def intSeqUnmarshaller: Unmarshaller[Seq[Int]]

  property("arrayDiff") = forAll { (a: Seq[Int], b: Seq[Int]) =>
    val p = JsonDiff.diff(a, b, false)
    p(a) == b
  }
}
