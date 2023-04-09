/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

// /*
//  * Copyright 2018-2022 - Alberto Paro on Apache 2 Licence. All Rights Reserved.
//  */

// package zio.elasticsearch.test

// import zio.elasticsearch.ElasticSearch
// import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner
// import zio.exception.FrameworkException
// import zio.{ UIO, ZIO, ZLayer, ZManaged }

// object EmbeddedClusterService {

//   case class EmbeddedES(runner: ElasticsearchClusterRunner) extends ElasticSearch {

//     override def start(): UIO[Unit] = ZIO.succeed {
//       runner.build(ElasticsearchClusterRunner.newConfigs().baseHttpPort(9200).numOfNode(1))
//       runner.ensureYellow()
//     }

//     override def migrate(): ZIO[Any, FrameworkException, Unit] = ZIO.unit

//     override def stop(): UIO[Unit] = ZIO.succeed {
//       runner.close()
//       runner.clean()
//     }

//     override def esConfig: UIO[ElasticSearchConfig] =
//       ZIO.succeed(ElasticSearchConfig("localhost:9201"))
//   }

//   val embedded: ZLayer[Any, Throwable, ElasticSearch] = ZLayer.fromManaged {
//     ZManaged.make {
//       val runner = EmbeddedES(new ElasticsearchClusterRunner())
//       for {
//         _ <- runner.start()
//       } yield runner
//     }(_.stop())
//   }
// }
