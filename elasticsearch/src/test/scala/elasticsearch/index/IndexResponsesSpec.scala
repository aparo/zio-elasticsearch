/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.index

import elasticsearch.responses.GetIndexTemplatesResponse
import elasticsearch.SpecHelper
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec

class IndexResponsesSpec extends AnyFlatSpec with Matchers with SpecHelper {

  "Index templates" should "correctly deserialized" in {
    val json = readResourceJSON("/elasticsearch/index/templates.json")
    val indextTemplateEither = json.as[GetIndexTemplatesResponse]
    //println(indextTemplateEither)
    indextTemplateEither.isRight should be(true)
    indextTemplateEither.right.get.isInstanceOf[GetIndexTemplatesResponse] should be(true)
    val indexTemplates = indextTemplateEither.right.get
    indexTemplates.size should be(25)
  }

}
