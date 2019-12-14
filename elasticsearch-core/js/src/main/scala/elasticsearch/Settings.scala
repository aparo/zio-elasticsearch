package elasticsearch

import elasticsearch.responses.cluster.ClusterIndexSetting
import io.circe._
import io.circe.derivation.annotations.JsonCodec
import io.circe.syntax._

object Settings {

  def ElasticSearchBase: Settings = Settings()

  def ElasticSearchTestBase: Settings = {
    val sett = ElasticSearchBase.copy(replicas = 0, shards = 1)
    sett.copy(
      options = Some(
        sett.options.get
          .deepMerge(Json.obj("index.store.type" -> "memory".asJson))
      )
    )
  }

  def fromCluster(clusterSettings: ClusterIndexSetting): Settings =
    Settings(options = Some(clusterSettings.asJson))
}

@JsonCodec
final case class Settings(
    replicas: Int = 1,
    shards: Int = 5,
    options: Option[Json] = None
) {

  def toJson: JsonObject = {
    var json =
      Json.obj("replicas" -> replicas.asJson, "shards" -> shards.asJson)
    if (options.isDefined) json = json.deepMerge(options.get)
    json.asObject.get
  }
}
