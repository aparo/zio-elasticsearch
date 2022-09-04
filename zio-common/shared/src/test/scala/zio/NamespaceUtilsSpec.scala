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

package zio.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NamespaceUtilsSpec extends AnyFlatSpec with Matchers {
  behavior.of("OrmUtils Specification")

  "OrmUtils" should "correct extract module" in {
    NamespaceUtils.getModule("zio.fixture.models.Pippo") should be(
      "fixture"
    )
    NamespaceUtils.getModule("zio.fixture.Pippo") should be("fixture")
    NamespaceUtils.getModule("zio.fixture.subpackage.Pippo") should be(
      "fixture"
    )
    NamespaceUtils.getModule("zio.fixture.models.subpackage.Pippo") should be("fixture")
    NamespaceUtils.getModule("fixture.models.Pippo") should be("fixture")
    NamespaceUtils.getModule("fixture.models.package.Pippo") should be(
      "fixture"
    )
    NamespaceUtils.getModule("client.gino.fixture.models.Pippo") should be(
      "fixture"
    )
    NamespaceUtils.getModule("client.gino.fixture.models.package.Pippo") should be(
      "fixture"
    )
    NamespaceUtils.getModule("fixture.Pippo") should be("fixture")
    NamespaceUtils.getModule("fixture.package.Pippo") should be("fixture")
    NamespaceUtils.getModule("com.fixture.Pippo") should be("fixture")
    NamespaceUtils.getModule("com.fixture.package.Pippo") should be("fixture")
    NamespaceUtils.getModule("zio.fixture.engines.Pippo") should be(
      "fixture"
    )
    NamespaceUtils.getModule("zio.fixture.engines.subpackage.Pippo") should be(
      "fixture"
    )
    NamespaceUtils.getModule("fixture.engines.Pippo") should be("fixture")
    NamespaceUtils.getModule("fixture.engines.package.Pippo") should be(
      "fixture"
    )
    NamespaceUtils.getModule("client.gino.fixture.engines.Pippo") should be(
      "fixture"
    )
    NamespaceUtils.getModule("client.gino.fixture.engines.package.Pippo") should be(
      "fixture"
    )

  }

}
