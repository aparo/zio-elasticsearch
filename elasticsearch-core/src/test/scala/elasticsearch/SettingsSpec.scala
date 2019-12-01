/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch
import org.scalatest.Matchers
import org.scalatest.FlatSpec

class SettingsSpec extends FlatSpec with Matchers {

  behavior.of("Settings")

  "ElasticSearchTestBase" should "read correctly from a json file" in {
    val tempSingleshard = Settings.SingleShard

    //info(tempSingleshard.toString)
    tempSingleshard.index.number_of_replicas shouldBe (1)
    tempSingleshard.analysis
      .filter("shingler2")
      .get("min_shingle_size")
      .get should be("2")
    tempSingleshard.analysis
      .filter("shingler3")
      .get("min_shingle_size")
      .get should be("3")

  }
}
