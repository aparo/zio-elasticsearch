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

package elasticsearch.orm

import elasticsearch.queries._
import elasticsearch.sort.Sorter

import scala.reflect.macros.whitebox.Context

object QueryMacro {
  lazy val queryHelpers =
    Set(
      "IntHelper",
      "StringHelper",
      "DatetimeHelper",
      "OptionHelper",
      "OptionDatetimeHelper"
    )

  def filter[T: c.WeakTypeTag](
      c: Context
  )(projection: c.Expr[T => Boolean]): c.Expr[TypedQueryBuilder[T]] = {
    import c.universe._
    //Debug code: show the raw of projection
    //    println(showRaw(projection))

    def extractFunction(tree: Tree): (String, String, Option[Tree]) =
      tree match {
        case Select(Select(Ident(_), name), op) =>
          (name.decodedName.toString, op.decodedName.toString, None)
        case Select(
            Apply(
              Select(Select(Select(_, _), _), helper),
              List(Select(_, field))
            ),
            op
            ) if queryHelpers.contains(helper.decodedName.toString) =>
          (field.decodedName.toString, op.decodedName.toString, None)
        case Select(Apply(Select(_, helper), List(Select(_, field))), op)
            if queryHelpers.contains(helper.decodedName.toString) =>
          (field.decodedName.toString, op.decodedName.toString, None)
        case Select(
            Select(
              Apply(Select(_, helper), List(Select(Ident(x1), field))),
              op
            ),
            eq
            ) if queryHelpers.contains(helper.decodedName.toString) =>
          (field.decodedName.toString, op.decodedName.toString, None)
      }

    def extractQuery(tree: Tree): Tree =
      tree match {
        case Expr(extr) => extractQuery(extr)
        case Function(vparams, body) =>
          body match {
            case Apply(fun, args) =>
              val (left, operator, funcCode) = extractFunction(fun)
              val field = Literal(Constant(left))
              val value = funcCode match {
                case None => args.head
                case Some(x) => x
              }
              operator match {
                case "==" =>
                  q"_root_.elasticsearch.queries.TermQuery($field, $value)"
                case "!=" =>
                  q"_root_.elasticsearch.queries.NotQuery(elasticsearch.queries.TermQuery($field, $value))"
                case ">" =>
                  q"_root_.elasticsearch.queries.RangeQuery.gt($field, $value)"
                case ">=" =>
                  q"_root_.elasticsearch.queries.RangeQuery.gte($field, $value)"
                case "<" =>
                  q"_root_.elasticsearch.queries.RangeQuery.lt($field, $value)"
                case "<=" =>
                  q"_root_.elasticsearch.queries.RangeQuery.lte($field, $value)"
                case "startsWith" | "prefix" =>
                  q"_root_.elasticsearch.queries.PrefixQuery($field, $value)"
                case "iStartsWith" =>
                  q"""_root_.elasticsearch.queries.RegexTermQuery($field, "^" + $value.toString.toLowerCase, ignorecase = true)"""
                case "endsWith" =>
                  q"""_root_.elasticsearch.queries.RegexTermQuery($field, ".*" + $value.toString + "$$")"""
                case "iEndsWith" =>
                  q"""_root_.elasticsearch.queries.RegexTermQuery($field, ".*" + $value.toString.toLowerCase + "$$", ignorecase = true)"""

                case "contains" =>
                  q"""_root_.elasticsearch.queries.RegexTermQuery($field, ".*" + $value.toString + ".*")"""
                case "iContains" =>
                  q"""_root_.elasticsearch.queries.RegexTermQuery($field, ".*" + $value.toString.toLowerCase + ".*", ignorecase = true)"""
                case "regex" =>
                  q"""_root_.elasticsearch.queries.RegexTermQuery($field, $value.toString)"""
                case "iRegex" =>
                  q"""_root_.elasticsearch.queries.RegexTermQuery($field, $value.toString.toLowerCase, ignorecase = true)"""

                case "isNull" =>
                  val realValue = value match {
                    case Literal(Constant(x: Boolean)) => x
                    case _ => false
                  }
                  if (realValue) {
                    q"""_root_.elasticsearch.queries.MissingQuery($field)"""
                  } else {
                    q"""_root_.elasticsearch.queries.NotQuery(MissingQuery($field))"""
                  }
                case "exists" =>
                  val realValue = value match {
                    case Literal(Constant(x: Boolean)) => x
                    case _ => false
                  }
                  if (realValue) {
                    q"""_root_.elasticsearch.queries.ExistsQuery(field)"""
                  } else {
                    q"""_root_.elasticsearch.queries.NotQuery(ExistsQuery(field))"""
                  }
                case "year" =>
                  val realValue = value match {
                    case Literal(Constant(x: Int)) => x
                    case _ => 2014 //TODO raise exception
                  }
                  q"""{
                          import io.circe._
                          import java.time.OffsetDateTime
                          val start = new OffsetDateTime(${value}, 1, 1, 0, 0)
                          val end = new OffsetDateTime(${value} + 1, 1, 1, 0, 0)
                          elasticsearch.queries.RangeQuery($field, from = Some(Json.toJson(start)), to = Some(Json.toJson(end)), includeLower = Some(true), includeUpper = Some(false))}"""
                case "in" =>
                  q"""_root_.elasticsearch.queries.TermsQuery($field, ..${args})"""

              }
          }
      }

    val myQueryTree = extractQuery(projection.tree)
    //    println(showRaw(myQueryTree))
    reify {
      (c.Expr[TypedQueryBuilder[T]](c.prefix.tree))
        .splice
        .filterF(c.Expr[Query](myQueryTree).splice)
        .asInstanceOf[TypedQueryBuilder[T]]
    }
  }

  def sortBy[T: c.WeakTypeTag](
      c: Context
  )(projection: c.Expr[T => Any]): c.Expr[TypedQueryBuilder[T]] = {
    import c.universe._
    //Debug code: show the raw of projection
    //    println(showRaw(projection))
    val result = projection match {
      case Expr(
          Function(
            List(ValDef(mods, x1, TypeTree(), EmptyTree)),
            Select(Ident(x1_2), field)
          )
          ) =>
        q"elasticsearch.sort.FieldSort(${Literal(Constant(field.decodedName.toString))}, true)"
    }

    reify {
      (c.Expr[TypedQueryBuilder[T]](c.prefix.tree))
        .splice
        .sortBy(c.Expr[Sorter](result).splice)
        .asInstanceOf[TypedQueryBuilder[T]]
    }

  }

  def reverseSortBy[T: c.WeakTypeTag](
      c: Context
  )(projection: c.Expr[T => Any]): c.Expr[TypedQueryBuilder[T]] = {
    import c.universe._
    //Debug code: show the raw of projection
    //    println(showRaw(projection))
    val result = projection match {
      case Expr(
          Function(
            List(ValDef(mods, x1, TypeTree(), EmptyTree)),
            Select(Ident(x1_2), field)
          )
          ) =>
        q"elasticsearch.sort.FieldSort(${Literal(Constant(field.decodedName.toString))}, false)"
    }

    reify {
      (c.Expr[TypedQueryBuilder[T]](c.prefix.tree))
        .splice
        .sortBy(c.Expr[Sorter](result).splice)
        .asInstanceOf[TypedQueryBuilder[T]]
    }

  }

  //  def idValue[T: c.WeakTypeTag, U: c.WeakTypeTag](c: Context)(
  //    projection: c.Expr[T => U]): c.Expr[Iterator[(String, U)]] = {
  //    import c.universe._
  //    //Debug code: show the raw of projection
  //    //    println(showRaw(projection))
  //    val result = projection match {
  //      case Expr(
  //      Function(List(ValDef(mods, x1, TypeTree(), EmptyTree)),
  //      Select(Ident(x1_2), field))) =>
  //        field.decodedName.toString
  //    }
  //
  //    reify {
  //      val queryBuilder = (c
  //        .Expr[TypedQueryBuilder[T]](c.prefix.tree))
  //        .splice
  //        .take(1000)
  //        .setFields(Seq(result))
  //        .setScan()
  //        .toQueryBuilder
  //      new ESCursorIDField[U](new NativeCursorRaw(queryBuilder), result)
  //    }
  //
  //  }

}
