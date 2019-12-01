/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import java.io.{File => JFile}

import elasticsearch.responses.cluster.{Analysis, ClusterIndexSetting}
import io.circe.Json
import io.circe.derivation.annotations.JsonCodec
import scala.io.Source

@JsonCodec
final case class IndexSettings(number_of_shards: Int = 5,
                               number_of_replicas: Int = 1)
/*@JsonCodec
final case class Analysis(
    analyzer: Map[String, AnalyzerBody]=Map.empty[String, AnalyzerBody],
    filter: Map[String, Map[String, String]] = Map.empty[String, Map[String, String]],
    normalizer:Map[String, Normalizer]=Map.empty[String, Normalizer],
    char_filter : CharFilter = HTMLStrip("")
                         )*/

@JsonCodec
final case class Settings(index: IndexSettings = IndexSettings(),
                          analysis: Analysis = Analysis())

//  def toJson: JsonObject = {
//    var json =
//      Json.obj("index" -> index.asJson)
//    if (options.isDefined) json = json.deepMerge(options.get)
//    json.asObject.get
//  }

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

  //Check with alberto
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

//  implicit val encoderSettings:Encoder.AsObject[Settings] = Encoder.AsObject.instance[Settings] {
//     o => Json.from
//  }

}
