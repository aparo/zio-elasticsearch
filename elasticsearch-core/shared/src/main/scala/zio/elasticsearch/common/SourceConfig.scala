package zio.elasticsearch.common

import zio.Chunk
import zio.json._

final case class SourceConfig(includes: Chunk[String] = Chunk.empty, excludes: Chunk[String] = Chunk.empty) {
  def isEmpty: Boolean = includes.isEmpty && excludes.isEmpty
  def nonEmpty: Boolean = !isEmpty
}

object SourceConfig {
  lazy val noSource = SourceConfig(excludes = Chunk("*"))
  lazy val all = SourceConfig()
  implicit val jsonDecoder: JsonDecoder[SourceConfig] = DeriveJsonDecoder.gen[SourceConfig]
  implicit val jsonEncoder: JsonEncoder[SourceConfig] = DeriveJsonEncoder.gen[SourceConfig]
}
