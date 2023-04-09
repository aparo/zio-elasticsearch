/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

package zio.elasticsearch.test

import zio.ZLayer
import zio.elasticsearch.client.ZioSttpClient
import zio.elasticsearch.client.ZioSttpClient.ElasticSearchEnvironment
import zio.elasticsearch.{ ElasticSearch, ElasticSearchConfig }

trait ZIOTestElasticSearchSupport {

  lazy val esLayer: ZLayer[Any, Throwable, ElasticSearchEnvironment] = {
    if (sys.env.getOrElse("USE_EMBEDDED", "true").toBoolean) {
      val esEmbedded: ZLayer[Any, Throwable, ElasticSearch] = DockerElasticSearch.elasticsearch()
      ZioSttpClient.buildFromElasticsearch(esEmbedded)
    } else {
      ZioSttpClient.fullFromConfig(ElasticSearchConfig())
    }
  }
}
