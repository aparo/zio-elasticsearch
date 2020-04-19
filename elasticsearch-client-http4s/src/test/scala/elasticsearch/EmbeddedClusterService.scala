package elasticsearch

import elasticsearch.ElasticSearch.ElasticSearch
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner
import zio.exception.FrameworkException
import zio.{UIO, ZIO, ZLayer, ZManaged}

object EmbeddedClusterService {

  case class EmbeddedES(runner: ElasticsearchClusterRunner) extends ElasticSearch.Service {

    override def start(): UIO[Unit] = ZIO.effectTotal {
      runner.build(ElasticsearchClusterRunner.newConfigs().baseHttpPort(9200).numOfNode(1))
      runner.ensureYellow()
    }

    override def migrate(): ZIO[Any, FrameworkException, Unit] = ZIO.unit

    override def stop(): UIO[Unit] = ZIO.effectTotal {
      runner.close()
      runner.clean()
    }

    override def esConfig: UIO[ElasticSearchConfig] =
      ZIO.succeed(ElasticSearchConfig("localhost:9201"))
  }

  val embedded: ZLayer[Any, Throwable, ElasticSearch] = ZLayer.fromManaged {
    ZManaged.make(ZIO.effect {
      val runner = EmbeddedES(new ElasticsearchClusterRunner())
      runner.start()
      runner
    })(_.stop())
  }
}
