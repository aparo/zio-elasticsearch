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

package elasticsearch.index

import elasticsearch.SpecHelper
import elasticsearch.responses.indices.IndicesGetTemplateResponse
import org.scalatest._
import org.scalatest.FlatSpec

class IndexResponsesSpec extends FlatSpec with Matchers with SpecHelper {

  "Index templates" should "correctly deserialized" in {
    val json = readResourceJSON("/elasticsearch/index/templates.json")
    val indextTemplateEither = json.as[IndicesGetTemplateResponse]
    //println(indextTemplateEither)
    indextTemplateEither.isRight should be(true)
    indextTemplateEither.right.get.isInstanceOf[IndicesGetTemplateResponse] should be(true)
    val indexTemplates = indextTemplateEither.right.get
    indexTemplates.size should be(16)
  }

}
