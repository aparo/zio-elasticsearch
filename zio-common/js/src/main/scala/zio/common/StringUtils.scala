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

import java.util.Locale

import scala.util.matching.Regex

package object StringUtils {

  Locale.setDefault(Locale.ITALY)

  lazy val formatNumber = {
    val locale = Locale.forLanguageTag("it_IT")
    val formatter = java.text.NumberFormat.getNumberInstance(locale)
    formatter
  }

  implicit def string2InflectorString(word: String) =
    new Inflector.InflectorString(word)

  implicit def int2InflectorInt(number: Int) =
    new Inflector.InflectorInt(number)

  object Slug {
    def apply(input: String) = slugify(input)

    def slugify(input: String): String = {
      import java.text.Normalizer
      Normalizer
        .normalize(input, Normalizer.Form.NFD)
        .replaceAll("[^\\w\\s-]", "") // Remove all non-word, non-space or non-dash characters
        .replace('-', ' ') // Replace dashes with spaces
        .trim // Trim leading/trailing whitespace (including what used to be leading/trailing dashes)
        .replaceAll("\\s+", "-") // Replace whitespace (including newlines and repetitions) with single dashes
        .toLowerCase // Lowercase the final results
    }
  }

  implicit class StringImprovements(val s: String) {
    def slug = Slug(s)

    def plural = inflect.plural(s)

    def singular = inflect.singular(s)

    import scala.util.control.Exception._

    def toIntOpt = catching(classOf[NumberFormatException]).opt(s.toInt)

    def sha256Hash: String = s //TODO Implement in scala.js

    def convertCamelToSnakeCase: String =
      s.foldLeft("") {
        case (agg, char) =>
          if (agg.nonEmpty && char != char.toLower) {
            agg ++ "_" + char.toString.toLowerCase
          } else {
            agg + char.toLower
          }
      }

    def leftStrip(badCharacters: String = "") = {
      @scala.annotation.tailrec
      def start(n: Int): String =
        if (n == s.length) ""
        else if (badCharacters.indexOf(s.charAt(n).toInt) < 0)
          s.substring(n, s.length)
        else start(1 + n)

      if (s.isEmpty)
        s.replaceAll("^\\s+", "")
      else
        start(0)

    }

    def rightStrip(badCharacters: String = "") = {
      @scala.annotation.tailrec
      def end(a: Int, n: Int): String =
        if (n <= a) s.substring(a, n)
        else if (badCharacters.indexOf(s.charAt(n - 1).toInt) < 0)
          s.substring(a, n)
        else end(a, n - 1)

      if (s.isEmpty)
        s.replaceAll("\\s+$", "")
      else
        end(0, s.length)
    }

    //example: stripAll("  , , , hello , ,,,, ", " ,") => "hello"
    def stripAll(badCharacters: String): String = {

      @scala.annotation.tailrec
      def start(n: Int): String =
        if (n == s.length) ""
        else if (badCharacters.indexOf(s.charAt(n).toInt) < 0) end(n, s.length)
        else start(1 + n)

      @scala.annotation.tailrec
      def end(a: Int, n: Int): String =
        if (n <= a) s.substring(a, n)
        else if (badCharacters.indexOf(s.charAt(n - 1).toInt) < 0)
          s.substring(a, n)
        else end(a, n - 1)

      start(0)
    }
  }

  implicit class ByteImprovements(val d: Double) {
    def toHumanBytes = ToByte.toByte(d)
  }

  object ToByte {
    def apply(input: Long): String = toByte(input.asInstanceOf[scala.Long])

    def apply(input: java.lang.Long): String = toByte(input.toLong)

    def apply(input: Float): String = toByte(input.toLong)

    def apply(input: Int): String = toByte(input.toLong)

    def toInt(s: String): Int =
      try s.toInt
      catch {
        case _: Exception => 0
      }

    def stringTwoDecimal(s: String): String =
      if (s.contains('.')) {
        val part = s.split('.')
        val decimal: Int = toInt(part(1).toCharArray.take(2).mkString)
        if (decimal > 0)
          part(0) + "." + decimal
        else
          part(0)
      } else s

    def toByte(input: Double): String =
      try input.toLong match {
        case x if x < 1024L => x.toString + "B"
        case x if x >= 1024L && x < math.pow(1024L, 2L) =>
          stringTwoDecimal((x / 1024L).toString) + "KB"
        case x if x >= math.pow(1024L, 2) && x < math.pow(1024L, 3L) =>
          stringTwoDecimal((x / math.pow(1024L, 2L)).toString) + "MB"
        case x if x >= math.pow(1024L, 3) && x < math.pow(1024L, 4L) =>
          stringTwoDecimal((x / math.pow(1024L, 3L)).toString) + "GB"
        case x if x >= math.pow(1024L, 4) =>
          stringTwoDecimal((x / math.pow(1024L, 4L)).toString) + "TB"
        case _ =>
          println("[ERROR] Naboo stringUtils toByte match error ")
          ""
      } catch {
        case e: Throwable =>
          e.printStackTrace()
          "errore"
      }

    def toByte(input: Long): String =
      try input.asInstanceOf[scala.Long] match {
        case x if x < 1024L => x.toString + "B"
        case x if x >= 1024L && x < math.pow(1024L, 2L) =>
          stringTwoDecimal((x / 1024L).toString) + "KB"
        case x if x >= math.pow(1024L, 2) && x < math.pow(1024L, 3L) =>
          stringTwoDecimal((x / math.pow(1024L, 2L)).toString) + "MB"
        case x if x >= math.pow(1024L, 3) && x < math.pow(1024L, 4L) =>
          stringTwoDecimal((x / math.pow(1024L, 3L)).toString) + "GB"
        case x if x >= math.pow(1024L, 4) =>
          stringTwoDecimal((x / math.pow(1024L, 4L)).toString) + "TB"
        case _ =>
          println("[ERROR] Naboo stringUtils toByte match error ")
          ""
      } catch {
        case e: Throwable =>
          e.printStackTrace()
          "errore"
      }
  }

  object inflect {

    def plural(str: String): String = str match {
      case x if x.matches("$")                         => "s"
      case x if x.matches("^(?i:s)$")                  => x
      case x if x.matches(".*?(?i:fish|rice|police)$") => x
      case x if x.matches(".*?(?i:person)$") =>
        replaceAndRespectCase(x, "(.*?)((?i:person))$", "people")
      case x if x.matches(".*?(?i:man)$") =>
        replaceAndRespectCase(x, "(.*?)((?i:man))$", "men")
      case x if x.matches(".*?(?i:child)$") =>
        replaceAndRespectCase(x, "(.*?(?i:child))(.*?)$", "ren")
      case x if x.matches(".*?(?i:sex)$") =>
        replaceAndRespectCase(x, "(.*?(?i:sex))(.*?)$", "es")
      case x if x.matches(".*?(?i:move)$") =>
        replaceAndRespectCase(x, "(.*?(?i:move))(.*?)$", "s")
      case x if x.matches(".*?(?i:cow)$") =>
        replaceAndRespectCase(x, "(.*?)((?i:cow))$", "kine")
      case x if x.matches(".*?(?i:zombie)$") =>
        replaceAndRespectCase(x, "(.*?(?i:zombie))(.*?)$", "s")
      case x if x.matches("^(?i:ox)$") =>
        replaceAndRespectCase(x, "^((?i:ox))(.*?)$", "en")
      case x if x.matches("^(?i:oxen)$")   => x
      case x if x.matches("^(?i:qualia)$") => x
      case x if x.matches(".*?(?i:ax|test)(?i:is)$") =>
        replaceAndRespectCase(x, "^(.*?(?i:ax|test))((?i:is))$", "es")
      case x if x.matches(".*?(?i:octop|vir)(?i:us)$") =>
        replaceAndRespectCase(x, "(.*?(?i:octop|vir))((?i:us))$", "i")
      case x if x.matches(".*?(?i:octop|vir)(?i:i)$") =>
        replaceAndRespectCase(x, "(.*?(?i:octop|vir))((?i:i))$", "i")
      case x if x.matches(".*?(?i:alias|status)$") =>
        replaceAndRespectCase(x, "^(.*?(?i:alias|status))(.*?)$", "es")
      case x if x.matches(".*?(?i:bu)(?i:s)$") =>
        replaceAndRespectCase(x, "(.*?(?i:bu))((?i:s))$", "es")
      case x if x.matches(".*?(?i:buffal|tomat)(?i:o)$") =>
        replaceAndRespectCase(x, "(.*?(?i:buffal|tomat))((?i:o))$", "oes")
      case x if x.matches(".*?(?i:[ti])(?i:um)$") =>
        replaceAndRespectCase(x, "(.*?(?i:[ti]))((?i:um))$", "a")
      case x if x.matches(".*?(?i:[ti]a)$") => x
      case x if x.matches(".*?(?i:sis)$") =>
        replaceAndRespectCase(x, "(.*?)((?i:sis))$", "ses")
      case x if x.matches(".*?([^fF])(?i:fe)$") =>
        replaceAndRespectCase(x, "(.*?([^fF]))((?i:fe))$", "ves")
      case x if x.matches(".*?([lrLR])(?i:f)$") =>
        replaceAndRespectCase(x, "(.*?([lrLR]))((?i:f))$", "ves")
      case x if x.matches(".*?(?i:hive)$") =>
        replaceAndRespectCase(x, "^(.*?(?i:hive))(.*?)$", "s")
      case x if x.matches(".*?(?i:[^aeiouy]|qu)(?i:y)$") =>
        replaceAndRespectCase(x, "(.*?(?i:[^aeiouy]|qu))((?i:y))$", "ies")
      case x if x.matches(".*?(?i:matr|vert|ind)(?i:ix|ex)$") =>
        replaceAndRespectCase(x, "(.*?(?i:matr|vert|ind))((?i:ix|ex))$", "ices")
      case x if x.matches(".*?(?i:x|ch|ss|sh)$") =>
        replaceAndRespectCase(x, "^(.*?(?i:x|ch|ss|sh))(.*?)$", "es")
      case x if x.matches(".*?(?i:m|l)(?i:ouse)$") =>
        replaceAndRespectCase(x, "(.*?(?i:m|l))((?i:ouse))$", "ice")
      case x if x.matches("^(?i:lice)$") => x
      case x if x.matches(".*?(?i:lice)$") =>
        replaceAndRespectCase(x, "^(.*?(?i:lice))(.*?)$", "s")
      case x if x.matches(".*?(?i:m)(?i:ice)$") => x
      case x if x.matches(".*?(?i:ce)$") =>
        replaceAndRespectCase(x, "^(.*?(?i:ce))(.*?)$", "s")
      case x if x.matches(".*?(?i:quiz)$") =>
        replaceAndRespectCase(x, "^(.*?(?i:quiz))(.*?)$", "zes")
      case x if x.matches(".*?(?i:ase)$") =>
        replaceAndRespectCase(x, "^(.*(?i:ase))(.*?)$", "s")
      case x if x.matches(".*?(?i:[^aeious])$") =>
        replaceAndRespectCase(x, "^(.*(?i:[^aeious]))(.*?)$", "s")
      case x => x
    }

    def singular(str: String): String = str match {
      case x if x.matches("$")           => ""
      case x if x.matches(".*?(?i:ss)$") => x
      case x if x.matches(".*?(?i:people)$") =>
        replaceAndRespectCase(x, "(.*?)((?i:people))$", "person")
      case x if x.matches(".*?(?i:men)$") =>
        replaceAndRespectCase(x, "(.*?)((?i:men))$", "man")
      case x if x.matches(".*?(?i:children)$") =>
        replaceAndRespectCase(x, "(.*?(?i:child))((?i:ren))$", "")
      case x if x.matches(".*?(?i:sexes)$") =>
        replaceAndRespectCase(x, "(.*?(?i:sex))((?i:es))$", "")
      case x if x.matches(".*?(?i:moves)$") =>
        replaceAndRespectCase(x, "(.*?(?i:move))((?i:s))$", "")
      case x if x.matches(".*?(?i:kine)$") =>
        replaceAndRespectCase(x, "(.*?)((?i:kine))$", "cow")
      case x if x.matches(".*?(?i:zombies)$") =>
        replaceAndRespectCase(x, "(.*?(?i:zombie))((?i:s))$", "")
      case x if x.matches("^(?i:news)$") => x
      case x if x.matches("(.*?(?i:[ye])){0,1}(?i:oxen)$") =>
        replaceAndRespectCase(x, "(.*?(?i:ox))((?i:en))$", "")
      case x if x.matches(".*?(?i:[ti])(?i:a)$") =>
        replaceAndRespectCase(x, "(.*?(?i:[ti]))((?i:a))$", "um")
      case x if x.matches(".*?(?i:database)(?i:s)$") =>
        replaceAndRespectCase(x, "(.*?(?i:database))((?i:s))$", "")
      case x
          if x.matches(
            ".*(?i:analy|ba|diagno|parenthe|progno|synop|the)(?i:sis|ses)$"
          ) =>
        replaceAndRespectCase(
          x,
          "(.*?(?i:analy|ba|diagno|parenthe|progno|synop|the))((?i:sis|ses))$",
          "sis"
        )
      case x if x.matches(".*?(?i:hive|tive)(?i:s)$") =>
        replaceAndRespectCase(x, "(.*?(?i:hive|tive))((?i:s))$", "")
      case x if x.matches(".*?(?i:[lr])(?i:ves)$") =>
        replaceAndRespectCase(x, "(.*?(?i:[lr]))((?i:ves))$", "f")
      case x if x.matches(".*?(?i:[^f])(?i:ves)$") =>
        replaceAndRespectCase(x, "(.*?(?i:[^f]))((?i:ves))$", "fe")
      case x if x.matches(".*?(?i:series)$") => x
      case x if x.matches(".*?(?i:movie)(?i:s)$") =>
        replaceAndRespectCase(x, "(.*?(?i:movie))((?i:s))$", "")
      case x if x.matches(".*?(?i:[^aeiouy]|qu)(?i:ies)$") =>
        replaceAndRespectCase(x, "(.*?(?i:[^aeiouy]|qu))((?i:ies))$", "y")
      case x if x.matches(".*?(?i:ax)(?i:[ie]s)$") =>
        replaceAndRespectCase(x, "(.*?(?i:ax))((?i:[ie]s))$", "is")
      case x if x.matches(".*?(?i:x|ch|ss|sh)(?i:es)$") =>
        replaceAndRespectCase(x, "(.*?(?i:x|ch|ss|sh))((?i:es))$", "")
      case x if x.matches(".*?(?i:mice)$") =>
        replaceAndRespectCase(x, "(.*?(?i:m))((?i:ice))$", "ouse")
      case x if x.matches("(.*?(?i:[ye])){0,1}(?i:lice)$") =>
        replaceAndRespectCase(x, "(.*?(?i:l))((?i:ice))$", "ouse")
      case x if x.matches(".*?(?i:lice)(?i:s)$") =>
        replaceAndRespectCase(x, "(.*?(?i:lice))((?i:s))$", "")
      case x if x.matches(".*?(?i:vert|ind)(?i:ices)$") =>
        replaceAndRespectCase(x, "(.*?(?i:vert|ind))((?i:ices))$", "ex")
      case x if x.matches(".*?(?i:matr)(?i:ices)$") =>
        replaceAndRespectCase(x, "(.*?(?i:matr))((?i:ices))$", "ix")
      case x if x.matches(".*?(?i:ces)$") =>
        replaceAndRespectCase(x, "(.*?(?i:ce))((?i:s))$", "")
      case x if x.matches(".*?(?i:bus)(?i:es)$") =>
        replaceAndRespectCase(x, "(.*?(?i:bus))((?i:es))$", "")
      case x if x.matches(".*?(?i:sho)(?i:es)$") =>
        replaceAndRespectCase(x, "(.*?(?i:sho))((?i:es))$", "e")
      case x if x.matches(".*?(?i:obo)(?i:es)$") =>
        replaceAndRespectCase(x, "(.*?(?i:o))((?i:es))$", "e")
      case x if x.matches(".*?(?i:o)(?i:es)$") =>
        replaceAndRespectCase(x, "(.*?(?i:o))((?i:es))$", "")
      case x if x.matches(".*?(?i:cris|test)(?i:is|es)$") =>
        replaceAndRespectCase(x, "(.*?(?i:cris|test))((?i:is|es))$", "is")
      case x if x.matches(".*?(?i:octop|vir)(?i:us|i)$") =>
        replaceAndRespectCase(x, "(.*?(?i:octop|vir))((?i:us|i))$", "us")
      case x if x.matches(".*?(?i:alias|status)(?i:es)$") =>
        replaceAndRespectCase(x, "(.*?(?i:alias|status))((?i:es))$", "")
      case x if x.matches(".*?(?i:quiz)(?i:zes)$") =>
        replaceAndRespectCase(x, "(.*?(?i:quiz))((?i:zes))$", "")
      case x if x.matches(".*?(?i:ases)$") =>
        replaceAndRespectCase(x, "^(.*(?i:ase))((?i:s))$", "")
      case x if x.matches(".*?(?i:[^aeiou]s)$") =>
        replaceAndRespectCase(x, "^(.*(?i:[^aeiou]))((?i:s))$", "")
      case x => x
    }

    def replaceAndRespectCase(
      str: String,
      pattern: String,
      rstr: String
    ): String = {
      val suffix = str.replaceAll(pattern, "$2")
      val pcase = defCase(if (suffix != "") suffix else str)
      var rrstr = rstr
      if (pcase == "upper") {
        rrstr = rstr.toUpperCase()
      } else if (pcase == "lower") {
        rrstr = rstr.toLowerCase()
      }
      str.replaceAll(pattern, "$1" + rrstr)
    }

    def defCase(str: String): String = str match {
      case x if x == x.toUpperCase() => "upper"
      case x if x == x.toLowerCase() => "lower"
      case _                         => "mixed"
    }
  }

  val KB_FACTOR = 1024
  val MB_FACTOR = 1024 * KB_FACTOR
  val GB_FACTOR = 1024 * MB_FACTOR

  def humanByteStringToLong(myString: String): Long = {
    var text = myString.toLowerCase.stripSuffix("b").stripPrefix("<")
    val multiplier = text.last match {
      case 'k' =>
        text = text.substring(0, text.size - 1); KB_FACTOR
      case 'm' =>
        text = text.substring(0, text.size - 1); MB_FACTOR
      case 'g' =>
        text = text.substring(0, text.size - 1); GB_FACTOR
      case _ => 1

    }
    (text.toDouble * multiplier).toLong
  }

  //  Which can then be used like this:
  //
  //    scala> "The ${name} of the ${game}"
  //  res0: java.lang.String = The ${name} of the ${game}
  //
  //  scala> Map("name" -> "X", "game" -> "Y")
  //  res1: scala.collection.immutable.Map[java.lang.String,java.lang.String] = Map(name -> X, game -> Y)
  //
  //  scala> interpolate(res0, res1)
  //  res2: String = The X of the Y
  //

  //  Some new stuff I was waiting for has arrived on Scala. We can now have a limited form of string interpolation very easily:

  def interpolate(text: String, vars: Map[String, String]) =
    """\$\{([^}]+)\}""".r.replaceAllIn(
      text,
      (_: scala.util.matching.Regex.Match) match {
        case Regex.Groups(v) => vars.getOrElse(v, "")
        case _               => ""
      }
    )

  /**
   * Random string generator
   *
   * @param len
   *   lenght of string
   * @return
   *   a random string
   */
  def randomString(len: Int): String =
    scala.util.Random.alphanumeric.take(len).mkString

}
