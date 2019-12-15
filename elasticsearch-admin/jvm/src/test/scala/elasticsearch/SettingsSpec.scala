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

package elasticsearch
import org.scalatest.Matchers
import org.scalatest.FlatSpec

class SettingsSpec extends FlatSpec with Matchers {

  behavior.of("Settings")

  "ElasticSearchTestBase" should "read correctly from a json file" in {
    val tempSingleshard = Settings.SingleShard

    //info(tempSingleshard.toString)
    tempSingleshard.index.number_of_replicas shouldBe (1)
    tempSingleshard.analysis.filter("shingler2").get("min_shingle_size").get should be("2")
    tempSingleshard.analysis.filter("shingler3").get("min_shingle_size").get should be("3")

  }
}
