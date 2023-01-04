/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.schema.elasticsearch

import zio.Scope
import zio.test._
import zio.test.Assertion._

object ElasticSearchSchemaSpec extends ZIOSpecDefault {

  def annotationAreSerialized = test("annotation serializer") {
    val schema = ESSchema1.schema
    val schemaAst = schema.ast
//    ESSchema1.schema.annotate()
    assert(schemaAst.toString())(equalTo("Prova"))
  }

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("ElasticSearchSchemaSpec")(
    annotationAreSerialized
  )

}
