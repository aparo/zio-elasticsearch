/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch

import java.io.{ File => JFile }

import scala.io.Source

import zio.elasticsearch.responses.cluster.{ Analysis, ClusterIndexSetting }
import zio.json._

final case class IndexSettings(number_of_shards: Int = 1, number_of_replicas: Int = 1)
object IndexSettings {
  implicit val jsonDecoder: JsonDecoder[IndexSettings] = DeriveJsonDecoder.gen[IndexSettings]
  implicit val jsonEncoder: JsonEncoder[IndexSettings] = DeriveJsonEncoder.gen[IndexSettings]
}
/*@JsonCodec
final case class Analysis(
    analyzer: Map[String, AnalyzerBody]=Map.empty[String, AnalyzerBody],
    filter: Map[String, Map[String, String]] = Map.empty[String, Map[String, String]],
    normalizer:Map[String, Normalizer]=Map.empty[String, Normalizer],
    char_filter : CharFilter = HTMLStrip("")
                         )*/

final case class Settings(index: IndexSettings = IndexSettings(), analysis: Analysis = Analysis())

object Settings {
  private def readResource(name: String): String = {
    val source = Source.fromInputStream(getClass.getResourceAsStream(name))
    val res = source.mkString
    source.close
    res
  }
  private def readResourceJSON(name: String) =
    readResource(name).toJsonAST
  lazy val SingleShard = (for {
    json <- readResourceJSON("/zio/elasticsearch/settings.json")
    value <- json.as[Settings]
  } yield value).toOption.getOrElse(Settings())
  def ElasticSearchBase: Settings = SingleShard
  def ElasticSearchTestBase: Settings = {
    val base = SingleShard
    val sett = base.copy(
      index = base.index.copy(number_of_replicas = 0, number_of_shards = 1)
    )
    sett
  }
  def fromFile(file: JFile) = {
    val data = Source.fromFile(file).getLines().mkString
    for {
      json <- data.toJsonAST
      value <- json.as[Settings]
    } yield value
  }
  def fromCluster(clusterSettings: ClusterIndexSetting): Settings =
    Settings(analysis = clusterSettings.analysis)
  implicit val jsonDecoder: JsonDecoder[Settings] = DeriveJsonDecoder.gen[Settings]
  implicit val jsonEncoder: JsonEncoder[Settings] = DeriveJsonEncoder.gen[Settings]
}
