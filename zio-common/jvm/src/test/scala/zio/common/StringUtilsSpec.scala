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

package zio.common

import java.nio.ByteBuffer

import org.scalatest.{ FlatSpec, Matchers }

class StringUtilsSpec extends FlatSpec with Matchers {
  import StringUtils._
  behavior.of("StringUtils")

  "StringUtils.randomString" should "generate a random string" in {
    val result = StringUtils.randomString(10)

    result.length should be(10)
  }

  "StringUtils.camelcase" should "generate a camelcase string" in {
    StringUtils.camelcase("test") should be("test")
    StringUtils.camelcase("test_function") should be("testFunction")
  }

  "StringUtils.snakecase" should "generate a snakecase string" in {
    StringUtils.snakecase("test") should be("test")
    StringUtils.snakecase("testFunction") should be("test_function")
  }

  "StringUtils.leftStrip" should "generate a leftstrip string" in {
    "    test".leftStrip(" ") should be("test")
    "$$$$test".leftStrip("$") should be("test")
    "".leftStrip(" ") should be("")
    " ".leftStrip(" ") should be("")
  }

  "StringUtils.rightStrip" should "generate a rightstrip string" in {
    "test    ".rightStrip(" ") should be("test")
    "test$$$$$$".rightStrip("$") should be("test")
    "".rightStrip(" ") should be("")
  }

  "StringUtils.stripAll" should "generate a stripAll string" in {
    "    test    ".stripAll(" ") should be("test")
    "$$$$test$$$$".stripAll("$") should be("test")
    "".stripAll(" ") should be("")
    " test    ".stripAll(" ") should be("test")
  }

  "StringUtils.toByte" should "generate a String from a Long" in {
    ToByte(1) should be("1B")
    ToByte(1L.asInstanceOf[java.lang.Long]) should be("1B")
    ToByte(3) should be("3B")
    ToByte(1L) should be("1B")
    ToByte(1f) should be("1B")
    ToByte(1024) should be("1KB")
    ToByte(3096) should be("3KB")
    ToByte(1024 * 1024) should be("1MB")
    ToByte(3096 * 1024) should be("3.2MB")
    ToByte(1024 * 1024 * 1024) should be("1GB")
    ToByte(1024L * 1024L * 1024L * 1024L) should be("1TB")

  }
  "StringUtils.plural" should "generate the Plural name from the Singular" in {
    "DOG".plural should be("DOGS")
    inflect.plural("") should be("s")
    inflect.plural("dog") should be("dogs")
    inflect.plural("dogs") should be("dogs")
    inflect.plural("fish|rice|police") should be("fish|rice|police")
    inflect.plural("person") should be("people")
    inflect.plural("man") should be("men")
    inflect.plural("child") should be("children")
    inflect.plural("sex") should be("sexes")
    inflect.plural("move") should be("moves")
    inflect.plural("cow") should be("kine")
    inflect.plural("zombie") should be("zombies")
    inflect.plural("ox") should be("oxen")
    inflect.plural("oxen") should be("oxen")
    inflect.plural("qualium") should be("qualia")
    inflect.plural("ax") should be("axes")
    inflect.plural("axis") should be("axes")
    inflect.plural("octop") should be("octops")
    inflect.plural("octopi") should be("octopi")
    inflect.plural("octopus") should be("octopi")
    inflect.plural("vir") should be("virs")
    inflect.plural("alias") should be("aliases")
    inflect.plural("status") should be("statuses")
    inflect.plural("buffalo") should be("buffaloes")
    inflect.plural("wharf") should be("wharves")
    inflect.plural("story") should be("stories")
    inflect.plural("tomato") should be("tomatoes")
    inflect.plural("bus") should be("bues")
    inflect.plural("splice") should be("splices")
    inflect.plural("analysis") should be("analyses")
    inflect.plural("mouse") should be("mice")
    inflect.plural("wife") should be("wives")
    inflect.plural("quiz") should be("quizzes")
    inflect.plural("case") should be("cases")
    inflect.plural("matrix") should be("matrices")
    inflect.plural("vertex") should be("vertices")
    inflect.plural("index") should be("indices")
    inflect.plural("hive") should be("hives")
    inflect.plural("ice") should be("ices")
  }
  "StringUtils.singular" should "generate the Singular name from the Plural" in {
    inflect.singular("") should be("")
    "DOGS".singular should be("DOG")
    inflect.singular("dogs") should be("dog")
    inflect.singular("dog") should be("dog")
    inflect.singular("fish|rice|police") should be("fish|rice|police")
    inflect.singular("people") should be("person")
    inflect.singular("men") should be("man")
    inflect.singular("virs") should be("vir")
    inflect.singular("children") should be("child")
    inflect.singular("sexes") should be("sex")
    inflect.singular("databases") should be("database")
    inflect.singular("movies") should be("movie")
    inflect.singular("witches") should be("witch")
    inflect.singular("moves") should be("move")
    inflect.singular("kine") should be("cow")
    inflect.singular("zombies") should be("zombie")
    inflect.singular("oxen") should be("ox")
    inflect.singular("qualia") should be("qualium")
    inflect.singular("axes") should be("axis")
    inflect.singular("octops") should be("octop")
    inflect.singular("octopi") should be("octopus")
    inflect.singular("aliases") should be("alias")
    inflect.singular("statuses") should be("status")
    inflect.singular("splices") should be("splice")
    inflect.singular("stories") should be("story")
    inflect.singular("hoboes") should be("hoboe")
    inflect.singular("analyses") should be("analysis")
    inflect.singular("wharves") should be("wharf")
    inflect.singular("buffaloes") should be("buffalo")
    inflect.singular("tomatoes") should be("tomato")
    inflect.singular("bus") should be("bus")
    inflect.singular("wives") should be("wife")
    inflect.singular("mice") should be("mouse")
    inflect.singular("lice") should be("louse")
    inflect.singular("buses") should be("bus")
    inflect.singular("crisis") should be("crisis")
    inflect.singular("shoes") should be("shoe")
    inflect.singular("quizzes") should be("quiz")
    inflect.singular("cases") should be("case")
    inflect.singular("matrices") should be("matrix")
    inflect.singular("vertices") should be("vertex")
    inflect.singular("indices") should be("index")
    inflect.singular("hives") should be("hive")
    inflect.singular("ices") should be("ice")
  }

  "StringUtils.defCase" should "generate the type of the word in a String" in {
    inflect.defCase("TEST") should be("upper")
    inflect.defCase("test") should be("lower")
    inflect.defCase("tEsT") should be("mixed")
  }

  "StringUtils.slug" should "generate the slug String" in {
    "a Wise Man".slug should be("a-wise-man")
  }

  "StringUtils.interpolate" should "interpolate a String" in {
    StringUtils.interpolate(
      "The ${name} of the ${game}",
      Map("name" -> "X", "game" -> "Y")
    ) should be("The X of the Y")
  }
  "StringUtils.convertCamelToSnakeCase" should "convert to SnakeCase a string" in {
    StringUtils.convertCamelToSnakeCase("stringToBeConverted") shouldBe "string_to_be_converted"
  }
  "StringUtils.repeated" should "repeat a string" in {
    StringUtils.repeated("trialstring", 2) shouldBe "trialstringtrialstring"
  }

  "Inflector.ordinalize" should "generate the String in  order type" in {
    10.ordinalize should be("10th")
    "10".ordinalize should be("10th")
  }

  "StringUtils.byteBuffer2ArrayByte" should "transform a byteBuffer in an Array of buffer" in {
    val bytebuffer = ByteBuffer.allocate(8)
    bytebuffer.putInt(1)
    bytebuffer.putInt(2)
    var resu = false
    var output = StringUtils.byteBuffer2ArrayByte(bytebuffer)
    if (output(3) == 1 && output(7) == 2 && output(5) == 0) {
      resu = true
    }

    resu shouldBe true
  }

  "StringUtils.hex2Bytes" should "transform an hexadecimal string in bytes " in {
    val toCompare = new Array[Int](2)
    toCompare(0) = 18
    toCompare(1) = 52
    StringUtils.hex2Bytes("1234") shouldBe toCompare

  }

  "StringUtils.leftPad" should "leftPad a string" in {
    StringUtils.leftPad("toBePadded", 12, '0') shouldBe "00toBePadded"

  }
  "StringUtils.leftPad" should "leftPad a string if null" in {
    StringUtils.leftPad(null, 12, '0') shouldBe null

  }
  "StringUtils.rightPad" should "rightPad a string" in {
    StringUtils.rightPad("toBePadded", 12, '0') shouldBe "toBePadded00"

  }
  "StringUtils.rightPad" should "rightPad a string null" in {
    StringUtils.rightPad(null, 12, '0') shouldBe null

  }

  "StringUtils.pascalCaseSplit" should "split a pascale case in a list string" in {
    var resu =
      StringUtils.pascalCaseSplit("PascalCaseSplit".toCharArray.toList)
    resu.head shouldBe "Pascal"
  }

  "StringUtils.toHex" should "from array byte to hex" in {
    var toCompare = Array[Byte](4.toByte, 2.toByte, 1.toByte, 9.toByte)
    StringUtils.toHex(toCompare) shouldBe "04020109"

  }
  //to do stringSeq2bytesArray bytebuffer2 arraybyte hex2bytes leftpad rightpad  pascalcasesplit  tohex  escape

}
