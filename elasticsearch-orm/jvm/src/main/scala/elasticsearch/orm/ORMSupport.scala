package elasticsearch.orm

import elasticsearch.ClusterSupport
import zio.schema.SchemaService

trait ORMSupport extends SchemaService with ClusterSupport{

}
