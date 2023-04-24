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

package zio

import scala.scalajs.js

object I18N {

  var i18n: String => String = localDictionaryI18N

  def localDictionaryI18N(text: String): String =
    dictionary.getOrElse(
      text,
      dictionaryLower.getOrElse(text.toLowerCase(), text)
    )

  var dictionary: Map[String, String] = Map()

  var dictionaryLower: Map[String, String] = Map()

  var languages: List[(String, String)] =
    List("en" -> "English", "it" -> "Italiano")

  var currentLanguage = "en"

  def setLanguage(lang: String, dict: Map[String, String]): Unit = {
    if (languages.exists(_._1 == lang)) {
      currentLanguage = lang
    }
    dictionary = dict
    dictionaryLower = dict.map(v => v._1.toLowerCase -> v._2)

  }

  val daysOfWeek: Map[String, js.Array[String]] = Map(
    "en" -> js.Array("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    "it" -> js.Array("Dom", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab")
  )

  val monthNames: Map[String, js.Array[String]] = Map(
    "en" -> js.Array(
      "January",
      "February",
      "March",
      "April",
      "May",
      "June",
      "July",
      "August",
      "September",
      "October",
      "November",
      "December"
    ),
    "it" -> js.Array(
      "Gennaio",
      "Febbraio",
      "Marzo",
      "Aprile",
      "Maggio",
      "Giugno",
      "Luglio",
      "Agosto",
      "Settembre",
      "Ottobre",
      "Novembre",
      "Dicembre"
    )
  )

  val firstDay: Map[String, Int] = Map(
    "en" -> 0,
    "it" -> 1
  )

  val dateFormat: Map[String, String] = Map(
    "en" -> "MM/DD/YYYY",
    "it" -> "DD/MM/YYYY"
  )
}
