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

package elasticsearch.nosql

import scala.language.implicitConversions

case class Indexes(values: Seq[String])

object Indexes {
  implicit def apply(index: String): Indexes = Indexes(Seq(index))
  implicit def apply(first: String, rest: String*): Indexes =
    Indexes(first +: rest)
  implicit def apply(indexes: Iterable[String]): Indexes =
    Indexes(indexes.toSeq)
}

case class IndexAndType(index: String, `type`: String)

case class IndexAndTypes(index: String, types: Seq[String])

object IndexAndTypes {
  implicit def apply(string: String): IndexAndTypes =
    string.split("/") match {
      case Array(index)    => IndexAndTypes(index, Nil)
      case Array(index, t) => IndexAndTypes(index, t.split(","))
      case _ =>
        sys.error(s"Could not parse '$string' into index/type1[,type2,...]")
    }
  implicit def apply(indexAndType: IndexAndType): IndexAndTypes =
    apply(indexAndType.index, indexAndType.`type`)
  implicit def apply(index: String, `type`: String): IndexAndTypes =
    IndexAndTypes(index, Seq(`type`))
  implicit def apply(indexAndType: (String, String)): IndexAndTypes =
    apply(indexAndType._1, indexAndType._2)
}

case class IndexesAndTypes(indexes: Seq[String], types: Seq[String])

object IndexesAndTypes {

  implicit def apply(string: String): IndexesAndTypes =
    string.split("/") match {
      case Array(index)    => IndexesAndTypes(index.split(","), Nil)
      case Array(index, t) => IndexesAndTypes(index.split(","), t.split(","))
      case _ =>
        sys.error(
          s"Could not parse '$string' into index1[,index2,...]/type1[,type2,...]"
        )
    }

  implicit def apply(indexAndType: IndexAndType): IndexesAndTypes =
    apply(indexAndType.index, indexAndType.`type`)
  implicit def apply(indexType: IndexAndTypes): IndexesAndTypes =
    IndexesAndTypes(Seq(indexType.index), indexType.types)

  // iterables of strings are assumed to be lists of indexes with no types
  implicit def apply(indexes: String*): IndexesAndTypes = apply(indexes)
  implicit def apply(indexes: Iterable[String]): IndexesAndTypes =
    IndexesAndTypes(indexes.toSeq, Nil)

  // a tuple is assumed to be an index and a type
  implicit def apply(indexAndType: (String, String)): IndexesAndTypes =
    apply(indexAndType._1, indexAndType._2)
  implicit def apply(index: String, `type`: String): IndexesAndTypes =
    IndexesAndTypes(Seq(index), Seq(`type`))
}

case class IndexesAndType(indexes: Seq[String], `type`: String)

object IndexesAndType {
  implicit def apply(indexAndType: IndexAndType): IndexesAndType =
    IndexesAndType(Seq(indexAndType.index), indexAndType.`type`)
}
