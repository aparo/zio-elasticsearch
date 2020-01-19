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

package elasticsearch

import java.io.{ File => JFile }

import elasticsearch.responses.cluster.{ Analysis, ClusterIndexSetting }
import io.circe.Json
import io.circe.derivation.annotations.JsonCodec
import scala.io.Source

@JsonCodec
final case class IndexSettings(number_of_shards: Int = 1, number_of_replicas: Int = 1)
/*@JsonCodec
final case class Analysis(
    analyzer: Map[String, AnalyzerBody]=Map.empty[String, AnalyzerBody],
    filter: Map[String, Map[String, String]] = Map.empty[String, Map[String, String]],
    normalizer:Map[String, Normalizer]=Map.empty[String, Normalizer],
    char_filter : CharFilter = HTMLStrip("")
                         )*/

@JsonCodec
final case class Settings(index: IndexSettings = IndexSettings(), analysis: Analysis = Analysis())

object Settings {
  private def readResource(name: String): String = {
    val source = Source.fromInputStream(getClass.getResourceAsStream(name))
    val res = source.mkString
    source.close
    res
  }

  private def readResourceJSON(name: String): Json =
    io.circe.parser.parse(readResource(name)).right.get

  lazy val SingleShard = {
    readResourceJSON("/elasticsearch/settings.json").as[Settings].right.get
  }

  def ElasticSearchBase: Settings = {
    val base = SingleShard
    base
  }

  def ElasticSearchTestBase: Settings = {
    val base = SingleShard
    val sett = base.copy(
      index = base.index.copy(number_of_replicas = 0, number_of_shards = 1)
    )
    sett
  }

  def fromFile(file: JFile) = {
    val data = Source.fromFile(file).getLines().mkString
    import io.circe.parser._
    parse(data).map(_.as[Settings].right.get)
  }

  def fromCluster(clusterSettings: ClusterIndexSetting): Settings =
    Settings(analysis = clusterSettings.analysis)

}
