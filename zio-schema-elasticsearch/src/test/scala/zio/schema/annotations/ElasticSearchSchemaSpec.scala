package zio.schema.elasticsearch

import zio.Scope
import zio.test._
import zio.test.Assertion._

object ElasticSearchSchemaSpec extends ZIOSpecDefault {

  def annotationAreSerialized = test("annotation serializer") {
    val schemaAst = ESSchema1.schema.ast
    ESSchema1.schema.annotate()
    assert(schemaAst.toString())(equalTo("Prova"))
  }

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("ElasticSearchSchemaSpec")(
    annotationAreSerialized
  )

}
