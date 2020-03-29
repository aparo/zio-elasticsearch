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

//package elasticsearch.responses.suggest
//
//import org.qdb.search.suggest.Suggest
//import org.qdb.search.suggest.completion.CompletionSuggestion
//import org.qdb.search.suggest.phrase.PhraseSuggestion
//import org.qdb.search.suggest.term.TermSuggestion
//
//.ListBuffer
//
///* Class to manage Suggest response in JVM */
//object SuggestResponses {
//
//  def suggestFromResponse(suggest: Suggest): Map[String, List[SuggestResponse]] = {
//    if (suggest == null)
//      return Map.empty[String, List[SuggestResponse]]
//    val values = new ListBuffer[(String, List[SuggestResponse])]()
//    val iterator = suggest.iterator()
//    while (iterator.hasNext) {
//      val sug = iterator.next()
//      sug match {
//        case s: TermSuggestion if sug.getType == TermSuggestion.TYPE ⇒
//          values += (s.getName → termSuggestResponseFromSuggestion(s))
//        case s: CompletionSuggestion if sug.getType == CompletionSuggestion.TYPE ⇒
//        case s: PhraseSuggestion if sug.getType == PhraseSuggestion.TYPE ⇒
//        case _ ⇒
//      }
//
//    }
//    values.toMap
//  }
//
//  def termSuggestResponseFromSuggestion(suggestion: TermSuggestion): List[TermSuggestResponse] = {
//    import scala.collection.JavaConversions._
//    val entries = suggestion.getEntries.toList
//    entries.map { entry ⇒
//      new TermSuggestResponse(
//        text = entry.getText.toString,
//        offset = entry.getOffset,
//        lenght = entry.getLength,
//        options = entry.getOptions.toList.map(opt ⇒
//          OptionTerm(text = opt.getText.toString, score = opt.getScore, freq = opt.getFreq)))
//    }
//  }
//}
