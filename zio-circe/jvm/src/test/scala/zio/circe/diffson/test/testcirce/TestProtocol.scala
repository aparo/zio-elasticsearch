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

package zio.circe.diffson

package test

package testcirce

import io.circe._
import io.circe.derivation._

trait TestProtocol {
  implicit def intSeqMarshaller: Encoder[Seq[Int]] =
    Encoder.encodeIterable[Int, Seq]
  implicit def intSeqUnmarshaller: Decoder[Seq[Int]] =
    Decoder[List[Int]].map(_.toSeq)
  implicit def boolMarshaller: Encoder[Boolean] = Encoder.encodeBoolean
  implicit def boolUnmarshaller: Decoder[Boolean] = Decoder.decodeBoolean
  implicit def intMarshaller: Encoder[Int] = Encoder.encodeInt
  implicit def intUnmarshaller: Decoder[Int] = Decoder.decodeInt
  implicit def stringMarshaller: Encoder[String] = Encoder.encodeString
  implicit def testJsonMarshaller: Encoder[test.Json] =
    deriveEncoder[test.Json]
  implicit def testJsonUnmarshaller: Decoder[test.Json] =
    deriveDecoder[test.Json]
}
