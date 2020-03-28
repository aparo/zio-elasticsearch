package zio
import io.circe.Json
import zio._
import zio.circe.CirceUtils
import zio.console._
import zio.clock._
import zio.logging.Logging.Logging
import zio.logging.log._

package object logging {

  implicit def s(serzio.logging.Logging.Service

  private val structure = LogAnnotation[Map[String, Json]](
    name = "structure",
    initialValue = Map.empty,
    combine = (map, toMerge) => map ++ toMerge,
    render = _.toString()
  )

  def structuredConsole(writer: Map[String, Json] => String): ZLayer[Console with Clock, Nothing, Logging] =
    zio.logging.Logging.make((context, line) =>
      for {
        date   <- currentDateTime.orDie
        level  = context.get(LogAnnotation.Level)
        values = context.get(structure)
        init   = Seq(("date", date.toString()), ("level", level.render), ("message", line))
        _      <- putStrLn(writer(toSValues(init) ++ values))
      } yield ()
    )

  def logContext[A, R <: Logging, E, A1](values: (String, Any)*)(zio: ZIO[R, E, A1]): ZIO[Logging with R, E, A1] =
    locally(structure(toSValues(values)))(zio)


  private def toSValues(values: Seq[(String, Any)]): Map[String, Json] =
    values.map { case (s, v) => (s, CirceUtils.anyToJson(v)) }.toMap

  def logger: URIO[Logging, Logger] =
    ZIO.access[Logging](_.get.logger)

  def debug(line: => String): ZIO[Logging, Nothing, Unit] =
    log(LogLevel.Debug)(line)

  def error(line: => String): ZIO[Logging, Nothing, Unit] =
    log(LogLevel.Error)(line)

  def error(line: => String, cause: Cause[Any]): ZIO[Logging, Nothing, Unit] =
    log(LogLevel.Error)(line + System.lineSeparator() + cause.prettyPrint)

  def info(line: => String): ZIO[Logging, Nothing, Unit] =
    log(LogLevel.Info)(line)


}
