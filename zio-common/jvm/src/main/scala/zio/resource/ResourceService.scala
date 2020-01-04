/*
 * Copyright 2019 - Paro Consulting. All Rights Reserved.
 */

package zio.resource

import io.circe.{Decoder, Json}
import zio._

import scala.io.{Codec, Source}

trait ResourceService {
  val resourceService: ResourceService.Service[Any]
}

object ResourceService {

  trait Service[R] {
    def getFileAsString(name: String, codec: Codec = Codec.UTF8): Task[String]
    def getJson(name: String, codec: Codec = Codec.UTF8): Task[Json]
    def getJsonEntity[T: Decoder](name: String, codec: Codec = Codec.UTF8): Task[T]
  }

  trait Live extends ResourceService {
    override val resourceService: Service[Any] = new Service[Any] {
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
}
