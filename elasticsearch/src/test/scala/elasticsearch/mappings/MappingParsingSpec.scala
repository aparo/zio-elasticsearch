/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.mappings

import elasticsearch.SpecHelper
import elasticsearch.responses.GetMappingsResponse
import org.scalatest.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class MappingParsingSpec extends AnyFlatSpec with Matchers with SpecHelper {
  "Mapping" should "parse Getmappings Response" in {
    val json = readResourceJSON("/elasticsearch/mappings/mappings_get.json")
    val oResult = json.as[GetMappingsResponse]
    //println(oResult)
    oResult.isRight should be(true)
    val result = oResult.right.get
    result.size should be(28)

  }
}
