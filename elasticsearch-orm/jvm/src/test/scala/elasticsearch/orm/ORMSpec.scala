/*
 * Copyright 2019-2020 Alberto Paro
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

package elasticsearch.orm

import elasticsearch.schema.ElasticSearchSchemaManagerService
import zio.test.Assertion._
import zio.test._
import zio.test.environment._

object ORMSpec extends DefaultRunnableSpec {
  def generatePersonMapping = testM("generate person Mapping") {
    for {
      mapping <- ElasticSearchSchemaManagerService.getMapping[Person]
    } yield assert(mapping.)(equalTo(7L))
  }

  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("ORMSpec")(generatePersonMapping)
}
