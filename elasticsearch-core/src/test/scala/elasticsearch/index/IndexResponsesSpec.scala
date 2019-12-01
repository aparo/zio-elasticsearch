/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
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
    indextTemplateEither.right.get
      .isInstanceOf[IndicesGetTemplateResponse] should be(true)
    val indexTemplates = indextTemplateEither.right.get
    indexTemplates.size should be(25)
  }

}
