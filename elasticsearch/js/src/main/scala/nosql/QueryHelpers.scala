package elasticsearch.nosql

import java.time.OffsetDateTime

import elasticsearch.DateKind
import elasticsearch.aggregations.Aggregation
import elasticsearch.geo.{ DistanceType, DistanceUnit }
import elasticsearch.queries.Query
import elasticsearch.sort.SortOrder
import elasticsearch.exception.InvalidQuery

object QueryHelpers {

  trait StandardAggregation {

    def termAggregation(size: Int = 10): Aggregation =
      throw new InvalidQuery("You shouldn't be here")

    def histogramAggregation(
      keyField: Option[String] = None,
      valueField: Option[String] = None,
      interval: Option[Long] = None,
      timeInterval: Option[String] = None,
      keyScript: Option[String] = None,
      valueScript: Option[String] = None,
      params: Map[String, Any] = Map.empty[String, Any],
      scope: Option[String] = None,
      nested: Option[Boolean] = None,
      isGlobal: Option[Boolean] = None,
      facetFilter: Option[Query] = None
    ): Aggregation =
      throw new InvalidQuery("You shouldn't be here")

    def rangeAggregation(
      ranges: List[Range] = Nil,
      keyField: Option[String] = None,
      valueField: Option[String] = None,
      keyScript: Option[String],
      valueScript: Option[String] = None,
      params: Map[String, Any] = Map.empty[String, Any],
      scope: Option[String] = None,
      nested: Option[Boolean] = None,
      isGlobal: Option[Boolean] = None,
      facetFilter: Option[Query] = None
    ): Aggregation =
      throw new InvalidQuery("You shouldn't be here")

    def termStatsAggregation(
      size: Option[Int] = Some(10),
      order: SortOrder = SortOrder.Asc,
      keyField: Option[String] = None,
      valueField: Option[String] = None,
      keyScript: Option[String],
      valueScript: Option[String] = None,
      params: Map[String, Any] = Map.empty[String, Any],
      scope: Option[String] = None,
      nested: Option[Boolean] = None,
      isGlobal: Option[Boolean] = None,
      facetFilter: Option[Query] = None
    ): Aggregation =
      throw new InvalidQuery("You shouldn't be here")

    def statisticalAggregation(
      field: Option[String],
      script: Option[String] = None,
      params: Map[String, Any] = Map.empty[String, Any],
      scope: Option[String] = None,
      nested: Option[Boolean] = None,
      isGlobal: Option[Boolean] = None,
      facetFilter: Option[Query] = None
    ): Aggregation =
      throw new InvalidQuery("You shouldn't be here")

  }

  trait DateAggregations extends StandardAggregation {

    def dateHistogramAggregation(
      field: Option[String] = None,
      interval: DateKind,
      keyField: Option[String] = None,
      valueField: Option[String] = None,
      keyScript: Option[String] = None,
      valueScript: Option[String] = None,
      params: Map[String, Any] = Map.empty[String, Any],
      scope: Option[String] = None,
      nested: Option[Boolean] = None,
      isGlobal: Option[Boolean] = None,
      facetFilter: Option[Query] = None
    ): Aggregation =
      throw new InvalidQuery("You shouldn't be here")

  }

  trait GeoAggregations extends StandardAggregation {

    def eoDistanceAggregation(
      pin: String,
      ranges: List[Range] = Nil,
      valueField: Option[String] = None,
      distanceUnit: DistanceUnit = DistanceUnit.km,
      distanceType: DistanceType = DistanceType.Arc,
      valueScript: Option[String] = None,
      params: Map[String, Any] = Map.empty[String, Any],
      scope: Option[String] = None,
      nested: Option[Boolean] = None,
      isGlobal: Option[Boolean] = None,
      facetFilter: Option[Query] = None
    ): Aggregation =
      throw new InvalidQuery("You shouldn't be here")

  }

  implicit class StringHelper(s: String) extends StandardAggregation {

    def in(s: String*): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def iStartsWith(value: String): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def startsWith(value: String): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def prefix(value: String): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def iEndsWith(value: String): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def endsWith(value: String): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def iContains(value: String): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def contains(value: String): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def iRegex(value: String): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def regex(value: String): Boolean =
      throw new InvalidQuery("You shouldn't be here")

    def isNull: Boolean = throw new InvalidQuery("You shouldn't be here")

    def exists: Boolean = throw new InvalidQuery("You shouldn't be here")

    //facet
    //query and filter facet
  }

  implicit class IntHelper(i: Int) extends StandardAggregation {
    def in(i: Int*): Boolean = throw new InvalidQuery("You shouldn't be here")

    def isNull: Boolean = throw new InvalidQuery("You shouldn't be here")

    def exists: Boolean = throw new InvalidQuery("You shouldn't be here")

  }

  implicit class DatetimeHelper(d: OffsetDateTime) extends DateAggregations {
    def in(i: Int*): Boolean = throw new InvalidQuery("You shouldn't be here")

    def isNull: Boolean = throw new InvalidQuery("You shouldn't be here")

    def exists: Boolean = throw new InvalidQuery("You shouldn't be here")

    def year: Int = throw new InvalidQuery("You shouldn't be here")

  }

  implicit class OptionDatetimeHelper(d: Option[OffsetDateTime]) extends DateAggregations {
    def in(i: Int*): Boolean = throw new InvalidQuery("You shouldn't be here")

    def isNull: Boolean = throw new InvalidQuery("You shouldn't be here")

    def exists: Boolean = throw new InvalidQuery("You shouldn't be here")

    def year: Int = throw new InvalidQuery("You shouldn't be here")

  }

  implicit class OptionHelper(o: Option[_]) extends StandardAggregation {
    def isNull: Boolean = throw new InvalidQuery("You shouldn't be here")

    def exists: Boolean = throw new InvalidQuery("You shouldn't be here")

  }

//  implicit def queryHelper[T](meta: NoSqlMeta[T]) = new QueryHelper(meta)
//
//  class QueryHelper[T](val meta: NoSqlMeta[T]) extends AnyVal {
//
//    def objects(implicit context: NoSql.NoSqlContext, formatter: Format[T]) =
//    //      new NoSql.orm.TypedQueryBuilder[T](
//    //        namespace = meta.namespace,
//    //        table =
//    // meta.tableName
//    //      )
//
//    //TODO fix it
//      new NoSql.orm.TypedQueryBuilder[T](
//        namespace = "",
//        table = ""
//      )
//
//    def getByPartialKey(partialKey: String)(implicit context: NoSql.NoSqlContext,
//                                            formatter: Format[T]): CloseableIterator[T] =
//      CloseableIterator.empty
//  }

}
