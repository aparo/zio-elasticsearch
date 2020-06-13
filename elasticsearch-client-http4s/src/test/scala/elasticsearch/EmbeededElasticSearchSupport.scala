package elasticsearch

import zio.logging.slf4j.Slf4jLogger
import zio.logging.LogAnnotation._

trait EmbeededElasticSearchSupport {

  val logLayer = Slf4jLogger.makeWithAnnotationsAsMdc(List(Level, Name, Throwable))

}
