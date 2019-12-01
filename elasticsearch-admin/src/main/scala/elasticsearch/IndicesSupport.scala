package elasticsearch

import elasticsearch.client.IndicesActionResolver
import elasticsearch.managers.ClusterManager

trait IndicesSupport extends IndicesActionResolver {
  lazy val cluster = new ClusterManager(this)

}