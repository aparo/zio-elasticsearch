package elasticsearch

import elasticsearch.client.ClusterActionResolver
import elasticsearch.managers.ClusterManager

trait ClusterSupport extends ClusterActionResolver{
  lazy val cluster = new ClusterManager(this)

}
