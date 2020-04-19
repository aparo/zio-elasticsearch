package elasticsearch

import zio.{Has, UIO, ZIO}
import zio.exception.FrameworkException

object ElasticSearch {
  type ElasticSearch = Has[Service]
  trait Service {
    def start(): UIO[Unit]
    def migrate(): ZIO[Any, FrameworkException, Unit]
    def stop(): UIO[Unit]
    def esConfig: UIO[ElasticSearchConfig]
  }
}
