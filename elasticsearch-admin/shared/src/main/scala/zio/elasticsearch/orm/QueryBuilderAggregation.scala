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

package zio.elasticsearch.orm

import zio.elasticsearch.aggregations._
import zio.elasticsearch.orm.GroupByOperator.GroupByOperator
import zio.elasticsearch.responses.aggregations
import zio.elasticsearch.responses.aggregations.MetricValue
import zio.json.ast._
import zio.json._

trait GroupByAggregation {
  val name: String
  def toColumn(prefix: String = ""): Json.Obj
}

trait AggGroupByAggregation extends GroupByAggregation {
  def getAggregation: Aggregation
}

trait MetricGroupByAggregation extends AggGroupByAggregation {

  def getValue(value: zio.elasticsearch.responses.aggregations.Aggregation): Json
}

final case class Sum(name: String, field: String) extends MetricGroupByAggregation {
  override def getAggregation: Aggregation = SumAggregation(field)
  override def getValue(value: aggregations.Aggregation): Json =
    Json.Num(value.asInstanceOf[MetricValue].value)

  def toColumn(prefix: String = ""): Json.Obj =
    Json.Obj(
      "label" -> (prefix + " " + name).trim.asJson,
      "field" -> (prefix + name).trim.replace(".", "_").asJson,
      "type" -> "double".asJson,
      "show" -> true.asJson,
      "required" -> false.asJson,
      "multiple" -> false.asJson,
      "tokenized" -> false.asJson
    )
}

final case class AVG(name: String, field: String) extends MetricGroupByAggregation {
  override def getAggregation: Aggregation = AvgAggregation(field)
  override def getValue(value: aggregations.Aggregation): Json =
    Json.Num(value.asInstanceOf[MetricValue].value)

  def toColumn(prefix: String = ""): Json.Obj =
    Json.Obj(
      "label" -> (prefix + " " + name).trim.asJson,
      "field" -> (prefix + name).trim.replace(".", "_").asJson,
      "type" -> "double".asJson,
      "show" -> true.asJson,
      "required" -> false.asJson,
      "multiple" -> false.asJson,
      "tokenized" -> false.asJson
    )

}

final case class Min(name: String, field: String) extends MetricGroupByAggregation {
  override def getAggregation: Aggregation = MinAggregation(field)
  override def getValue(value: aggregations.Aggregation): Json =
    Json.Num(value.asInstanceOf[MetricValue].value)

  def toColumn(prefix: String = ""): Json.Obj =
    Json.Obj(
      "label" -> (prefix + " " + name).trim.asJson,
      "field" -> (prefix + name).trim.replace(".", "_").asJson,
      "type" -> "double".asJson,
      "show" -> true.asJson,
      "required" -> false.asJson,
      "multiple" -> false.asJson,
      "tokenized" -> false.asJson
    )

}

final case class Max(name: String, field: String) extends MetricGroupByAggregation {
  override def getAggregation: Aggregation = MaxAggregation(field)
  override def getValue(value: aggregations.Aggregation): Json =
    Json.Num(value.asInstanceOf[MetricValue].value)

  def toColumn(prefix: String = ""): Json.Obj =
    Json.Obj(
      "label" -> (prefix + " " + name).trim.asJson,
      "field" -> (prefix + name).trim.replace(".", "_").asJson,
      "type" -> "double".asJson,
      "show" -> true.asJson,
      "required" -> false.asJson,
      "multiple" -> false.asJson,
      "tokenized" -> false.asJson
    )

}

final case class Count(name: String) extends GroupByAggregation {

  def toColumn(prefix: String = ""): Json.Obj =
    Json.Obj(
      "label" -> (prefix + " " + name).trim.asJson,
      "field" -> (prefix + name).trim.replace(".", "_").asJson,
      "type" -> "integer".asJson,
      "show" -> true.asJson,
      "required" -> false.asJson,
      "multiple" -> false.asJson,
      "tokenized" -> false.asJson
    )
}

final case class Concat(name: String, field: String) extends AggGroupByAggregation {
  override def getAggregation: Aggregation =
    TermsAggregation(field, size = 1000) //TODO fix size
  def toColumn(prefix: String = ""): Json.Obj =
    Json.Obj(
      "label" -> (prefix + " " + name).trim.asJson,
      "field" -> (prefix + name).trim.replace(".", "_").asJson,
      "type" -> "string".asJson,
      "show" -> true.asJson,
      "required" -> false.asJson,
      "multiple" -> true.asJson,
      "tokenized" -> false.asJson
    )
}

final case class Computed(
  name: String,
  field: String,
  operator: GroupByOperator,
  constant: Double
) extends GroupByAggregation {

  def calc(value: Double) = operator match {
    case GroupByOperator.Sum => value + constant
    case GroupByOperator.Dif => value - constant
    case GroupByOperator.Mul => value * constant
    case GroupByOperator.Div => value / constant
  }

  def toColumn(prefix: String = ""): Json.Obj =
    Json.Obj(
      "label" -> (prefix + " " + name).trim.asJson,
      "field" -> (prefix + name).trim.replace(".", "_").asJson,
      "type" -> "double".asJson,
      "show" -> true.asJson,
      "required" -> false.asJson,
      "multiple" -> false.asJson,
      "tokenized" -> false.asJson
    )
}

object GroupByAggregation {

  def fromJson(obj: Json.Obj): Option[GroupByAggregation] = {

    val field: String = obj.getOption[String]("field").getOrElse("")
    val name: String = obj.getOption[String]("label").getOrElse("")

    obj.getOption[String]("type").getOrElse("").toLowerCase match {
      case "sum"    => Some(Sum(name, field))
      case "max"    => Some(Max(name, field))
      case "min"    => Some(Min(name, field))
      case "avg"    => Some(AVG(name, field))
      case "count"  => Some(Count(name))
      case "concat" => Some(Concat(name, field))
      case _        => None
    }
  }
}

object GroupByOperator extends Enumeration {
  type GroupByOperator = Value
  val Sum, Dif, Mul, Div = Value

  def getOperation(value: String): Option[GroupByOperator] =
    value match {
      case "+" => Some(Sum)
      case "-" => Some(Dif)
      case "*" => Some(Mul)
      case "/" => Some(Div)
      case _   => None
    }
}
