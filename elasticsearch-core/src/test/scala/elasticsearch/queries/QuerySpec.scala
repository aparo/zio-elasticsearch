/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.queries

import io.circe._
import io.circe.parser._
import io.circe.syntax._
import elasticsearch.SpecHelper
import org.scalatest._
import io.circe.derivation.annotations._
import org.scalatest.FlatSpec

class QuerySpec extends FlatSpec with Matchers with SpecHelper {

  @JsonCodec
  case class Search(query: Query)

//  "Query" should "deserialize script" in {
//
//    val json = readResourceJSON("/elasticsearch/queries/script.json")
//    val script = json.as[Search]
//    println(script)
//    script.isRight should be(true)
//    script.right.get.query.isInstanceOf[ScriptQuery] should be(true)
//  }

  "Query" should "serialize and deserialize search" in {
    val obj = Search(ExistsQuery("test"))
    val oSearch = parse(obj.asJson.toString()).getOrElse(Json.obj()).as[Search]
    obj should be(oSearch.right.get)

  }

  it should "serialize and deserialize BoolQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/bool_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[BoolQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[BoolQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize BoostingQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/boosting_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[BoostingQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[BoostingQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

//  it should "serialize and deserialize CommonTermsQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/common_terms_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[CommonTermsQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[CommonTermsQuery]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }
//
//  it should "serialize and deserialize ConstantScoreQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/constant_score_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[ConstantScoreQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[ConstantScoreQuery]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }

  it should "serialize and deserialize DisMaxQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/dis_max_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[DisMaxQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[DisMaxQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize ExistsQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/exists_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[ExistsQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[ExistsQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize FieldMaskingSpanQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/field_masking_span_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[FieldMaskingSpanQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[FieldMaskingSpanQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

//  it should "serialize and deserialize FunctionScoreQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/function_score_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[FunctionScoreQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[FunctionScoreQuery]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }

  it should "serialize and deserialize FuzzyQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/fuzzy_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[FuzzyQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[FuzzyQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize GeoBoundingBoxQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/geo_bounding_box_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[GeoBoundingBoxQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[GeoBoundingBoxQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize GeoDistanceQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/geo_distance_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[GeoDistanceQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[GeoDistanceQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize GeoPolygonQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/geo_polygon_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[GeoPolygonQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[GeoPolygonQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize GeoShapeQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/geo_shape_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[GeoShapeQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[GeoShapeQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize HasChildQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/has_child_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[HasChildQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[HasChildQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize HasParentQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/has_parent_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[HasParentQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[HasParentQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize IdsQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/ids_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[IdsQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[IdsQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize MatchAllQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/match_all_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[MatchAllQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[MatchAllQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

//  it should "serialize and deserialize MatchNoneQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/match_none_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[MatchNoneQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[MatchNoneQuery]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }

  it should "serialize and deserialize MoreLikeThisQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/more_like_this_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[MoreLikeThisQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[MoreLikeThisQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  //TODO

//  it should "serialize and deserialize MultiMatchQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/multi_match_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[MultiMatchQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[MultiMatchQuery]
//    val nJson = oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }
//
//  it should "serialize and deserialize NestedQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/nested_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[NestedQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[NestedQuery]
//    val nJson = oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }

//  it should "serialize and deserialize ParentIdQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/parent_id_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[ParentIdQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[ParentIdQuery]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }

  it should "serialize and deserialize PrefixQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/prefix_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[PrefixQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[PrefixQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize QueryStringQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/query_string_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[QueryStringQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[QueryStringQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize RangeQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/range_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[RangeQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[RangeQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize RegexpQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/regexp_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[RegexpQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[RegexpQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize ScriptQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/script_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[ScriptQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[ScriptQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

//  it should "serialize and deserialize SimpleQueryString" in {
//    val json = readResourceJSON("/elasticsearch/queries/simple_query_string.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[SimpleQueryString] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[SimpleQueryString]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }
//
//  it should "serialize and deserialize SpanContainingQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/span_containing_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[SpanContainingQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[SpanContainingQuery]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }

  it should "serialize and deserialize SpanFirstQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_first_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[SpanFirstQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[SpanFirstQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

//  it should "serialize and deserialize SpanMultiTermQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/span_multi_term_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[SpanMultiTermQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[SpanMultiTermQuery]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }

  it should "serialize and deserialize SpanNearQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_near_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[SpanNearQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[SpanNearQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize SpanNotQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_not_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[SpanNotQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[SpanNotQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize SpanOrQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_or_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[SpanOrQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[SpanOrQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize SpanTermQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_term_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[SpanTermQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[SpanTermQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

//  it should "serialize and deserialize SpanWithinQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/span_within_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[SpanWithinQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[SpanWithinQuery]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }

  it should "serialize and deserialize TermQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/term_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[TermQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[TermQuery]
    realQuery.field should be("exact_value")
    realQuery.value.asString.get should be("Quick Foxes!")
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize TermsQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/terms_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[TermsQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[TermsQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize TypeQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/type_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[TypeQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[TypeQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

  it should "serialize and deserialize WildcardQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/wildcard_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.right.get.isInstanceOf[WildcardQuery] should be(true)
    val realQuery = oQuery.right.get.asInstanceOf[WildcardQuery]
    val nJson = oQuery.right.get.asJson
    nJson.as[Query].right.get should be(realQuery)
  }

//  it should "serialize and deserialize WrapperQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/wrapper_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.right.get.isInstanceOf[WrapperQuery] should be(true)
//    val realQuery = oQuery.right.get.asInstanceOf[WrapperQuery]
//    val nJson= oQuery.right.get.asJson
//    nJson.as[Query].right.get should be(realQuery)
//  }

}
