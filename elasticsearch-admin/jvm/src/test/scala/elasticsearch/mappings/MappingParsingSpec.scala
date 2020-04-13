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

package elasticsearch.mappings

import elasticsearch.SpecHelper
import elasticsearch.responses.indices.IndicesGetMappingResponse
import org.scalatest.{FlatSpec, Matchers}

class MappingParsingSpec extends FlatSpec with Matchers with SpecHelper {
  "Mapping" should "parse Getmappings Response" in {
    val json = readResourceJSON("/elasticsearch/mappings/mappings_get.json")
    val oResult = json.as[IndicesGetMappingResponse]
    //println(oResult)
    oResult.isRight should be(true)
    val result = oResult.right.get
    result.size should be(1)

  }
}
