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

package zio.resource

import io.circe.{ Decoder, Json }
import zio._

import scala.io.{ Codec, Source }

object ResourceService {

  type ResourceService = Has[Service]

  trait Service {
    def getFileAsString(name: String, codec: Codec = Codec.UTF8): ZIO[Any, Throwable, String]
    def getJson(name: String, codec: Codec = Codec.UTF8): ZIO[Any, Throwable, Json]
    def getJsonEntity[T: Decoder](name: String, codec: Codec = Codec.UTF8): ZIO[Any, Throwable, T]
  }

  def getFileAsString(name: String, codec: Codec = Codec.UTF8): ZIO[ResourceService, Throwable, String] =
    ZIO.accessM(_.get.getFileAsString(name, codec))

  def getJson(name: String, codec: Codec = Codec.UTF8): ZIO[ResourceService, Throwable, Json] =
    ZIO.accessM(_.get.getJson(name, codec))

  def getJsonEntity[T: Decoder](
    name: String,
    codec: Codec = Codec.UTF8
  ): ZIO[ResourceService, Throwable, T] =
    ZIO.accessM(_.get.getJsonEntity[T](name, codec))

  object Live extends ResourceService.Service {
    override def getFileAsString(name: String, codec: Codec = Codec.UTF8): Task[String] = {
      implicit val cdc: Codec = codec
      ZIO.effect {
        val source = Source.fromInputStream(getClass.getResourceAsStream(name))
        val res = source.mkString
        source.close
        res
      }
    }

    override def getJson(name: String, codec: Codec = Codec.UTF8): Task[Json] =
      for {
        str <- getFileAsString(name, codec)
        json <- ZIO.fromEither(io.circe.parser.parse(str))
      } yield json

    override def getJsonEntity[T: Decoder](name: String, codec: Codec = Codec.UTF8): Task[T] =
      for {
        json <- getJson(name, codec)
        obj <- ZIO.fromEither(json.as[T])
      } yield obj
  }

}
