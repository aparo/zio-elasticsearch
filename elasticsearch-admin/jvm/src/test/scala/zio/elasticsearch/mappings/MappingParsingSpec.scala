/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch.mappings

import zio.elasticsearch.SpecHelper
import zio.elasticsearch.responses.indices.IndicesGetMappingResponse
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MappingParsingSpec extends AnyFlatSpec with Matchers with SpecHelper {
  "Mapping" should "parse Getmappings Response" in {
    val json = readResourceJSON("/zio/elasticsearch/mappings/mappings_get.json")
    val oResult = json.as[IndicesGetMappingResponse]
    //println(oResult)
    oResult.isRight should be(true)
    val result = oResult.value
    result.size should be(1)

  }
}
