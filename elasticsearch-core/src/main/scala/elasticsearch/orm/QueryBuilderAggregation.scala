/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.orm

import elasticsearch.orm.GroupByOperator.GroupByOperator
import elasticsearch.responses.aggregations
import elasticsearch.responses.aggregations.MetricValue
import io.circe._
import io.circe.syntax._
import elasticsearch.aggregations._

trait GroupByAggregation {
  val name: String
  def toColumn(prefix: String = ""): JsonObject
}

trait AggGroupByAggregation extends GroupByAggregation {
  def getAggregation: Aggregation
}

trait MetricGroupByAggregation extends AggGroupByAggregation {

  def getValue(value: elasticsearch.responses.aggregations.Aggregation): Json
}

final case class Sum(name: String, field: String) extends MetricGroupByAggregation {
  override def getAggregation: Aggregation = SumAggregation(field)
  override def getValue(value: aggregations.Aggregation): Json =
    Json.fromDoubleOrString(value.asInstanceOf[MetricValue].value)

  def toColumn(prefix: String = ""): JsonObject =
    JsonObject.fromMap(
      Map(
        "label" -> (prefix + " " + name).trim.asJson,
        "field" -> (prefix + name).trim.replace(".", "_").asJson,
        "type" -> "double".asJson,
        "show" -> true.asJson,
        "required" -> false.asJson,
        "multiple" -> false.asJson,
        "tokenized" -> false.asJson
      )
    )
}

final case class AVG(name: String, field: String) extends MetricGroupByAggregation {
  override def getAggregation: Aggregation = AvgAggregation(field)
  override def getValue(value: aggregations.Aggregation): Json =
    Json.fromDoubleOrString(value.asInstanceOf[MetricValue].value)

  def toColumn(prefix: String = ""): JsonObject =
    JsonObject.fromMap(
      Map(
        "label" -> (prefix + " " + name).trim.asJson,
        "field" -> (prefix + name).trim.replace(".", "_").asJson,
        "type" -> "double".asJson,
        "show" -> true.asJson,
        "required" -> false.asJson,
        "multiple" -> false.asJson,
        "tokenized" -> false.asJson
      )
    )
}

final case class Min(name: String, field: String) extends MetricGroupByAggregation {
  override def getAggregation: Aggregation = MinAggregation(field)
  override def getValue(value: aggregations.Aggregation): Json =
    Json.fromDoubleOrString(value.asInstanceOf[MetricValue].value)

  def toColumn(prefix: String = ""): JsonObject =
    JsonObject.fromMap(
      Map(
        "label" -> (prefix + " " + name).trim.asJson,
        "field" -> (prefix + name).trim.replace(".", "_").asJson,
        "type" -> "double".asJson,
        "show" -> true.asJson,
        "required" -> false.asJson,
        "multiple" -> false.asJson,
        "tokenized" -> false.asJson
      )
    )

}

final case class Max(name: String, field: String) extends MetricGroupByAggregation {
  override def getAggregation: Aggregation = MaxAggregation(field)
  override def getValue(value: aggregations.Aggregation): Json =
    Json.fromDoubleOrString(value.asInstanceOf[MetricValue].value)

  def toColumn(prefix: String = ""): JsonObject =
    JsonObject.fromMap(
      Map(
        "label" -> (prefix + " " + name).trim.asJson,
        "field" -> (prefix + name).trim.replace(".", "_").asJson,
        "type" -> "double".asJson,
        "show" -> true.asJson,
        "required" -> false.asJson,
        "multiple" -> false.asJson,
        "tokenized" -> false.asJson
      )
    )
}

final case class Count(name: String) extends GroupByAggregation {

  def toColumn(prefix: String = ""): JsonObject =
    JsonObject.fromMap(
      Map(
        "label" -> (prefix + " " + name).trim.asJson,
        "field" -> (prefix + name).trim.replace(".", "_").asJson,
        "type" -> "integer".asJson,
        "show" -> true.asJson,
        "required" -> false.asJson,
        "multiple" -> false.asJson,
        "tokenized" -> false.asJson
      )
    )
}

final case class Concat(name: String, field: String) extends AggGroupByAggregation {
  override def getAggregation: Aggregation =
    TermsAggregation(field, size = 1000) //TODO fix size
  def toColumn(prefix: String = ""): JsonObject =
    JsonObject.fromMap(
      Map(
        "label" -> (prefix + " " + name).trim.asJson,
        "field" -> (prefix + name).trim.replace(".", "_").asJson,
        "type" -> "string".asJson,
        "show" -> true.asJson,
        "required" -> false.asJson,
        "multiple" -> true.asJson,
        "tokenized" -> false.asJson
      )
    )
}

final case class Computed(
  name: String,
  field: String,
  operator: GroupByOperator,
  costant: Double
) extends GroupByAggregation {

  def calc(value: Double) = operator match {
    case GroupByOperator.Som => value + costant
    case GroupByOperator.Dif => value - costant
    case GroupByOperator.Mol => value * costant
    case GroupByOperator.Div => value / costant
  }

  def toColumn(prefix: String = ""): JsonObject =
    JsonObject.fromMap(
      Map(
        "label" -> (prefix + " " + name).trim.asJson,
        "field" -> (prefix + name).trim.replace(".", "_").asJson,
        "type" -> "double".asJson,
        "show" -> true.asJson,
        "required" -> false.asJson,
        "multiple" -> false.asJson,
        "tokenized" -> false.asJson
      )
    )
}

object GroupByAggregation {

  def fromJson(obj: JsonObject): Option[GroupByAggregation] = {

    val field: String = obj("field").flatMap(_.asString).getOrElse("")
    val name: String = obj("label").flatMap(_.asString).getOrElse("")

    obj("type").flatMap(_.asString).getOrElse("").toLowerCase match {
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
  val Som, Dif, Mol, Div = Value

  def getOperation(value: String): Option[GroupByOperator] =
    value match {
      case "+" => Some(Som)
      case "-" => Some(Dif)
      case "*" => Some(Mol)
      case "/" => Some(Div)
      case _   => None
    }
}
