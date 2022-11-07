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

package zio.elasticsearch.queries

import elasticsearch.SpecHelper
import zio.json._
import io.circe.derivation.annotations._
import io.circe.parser._
import zio.json._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class QuerySpec extends AnyFlatSpec with Matchers with SpecHelper {

  @jsonDerive
  case class Search(query: Query)

//  "Query" should "deserialize script" in {
//
//    val json = readResourceJSON("/elasticsearch/queries/script.json")
//    val script = json.as[Search]
//    println(script)
//    script.isRight should be(true)
//    script.value.query.isInstanceOf[ScriptQuery] should be(true)
//  }

  "Query" should "serialize and deserialize search" in {
    val obj = Search(ExistsQuery("test"))
    val oSearch = parse(obj.asJson.toString()).getOrElse(Json.obj()).as[Search]
    obj should be(oSearch.value)

  }

  it should "serialize and deserialize BoolQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/bool_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[BoolQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[BoolQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize BoostingQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/boosting_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[BoostingQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[BoostingQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

//  it should "serialize and deserialize CommonTermsQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/common_terms_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[CommonTermsQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[CommonTermsQuery]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }
//
//  it should "serialize and deserialize ConstantScoreQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/constant_score_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[ConstantScoreQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[ConstantScoreQuery]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }

  it should "serialize and deserialize DisMaxQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/dis_max_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[DisMaxQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[DisMaxQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize ExistsQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/exists_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[ExistsQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[ExistsQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize FieldMaskingSpanQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/field_masking_span_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[FieldMaskingSpanQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[FieldMaskingSpanQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

//  it should "serialize and deserialize FunctionScoreQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/function_score_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[FunctionScoreQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[FunctionScoreQuery]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }

  it should "serialize and deserialize FuzzyQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/fuzzy_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[FuzzyQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[FuzzyQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize GeoBoundingBoxQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/geo_bounding_box_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[GeoBoundingBoxQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[GeoBoundingBoxQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize GeoDistanceQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/geo_distance_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[GeoDistanceQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[GeoDistanceQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize GeoPolygonQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/geo_polygon_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[GeoPolygonQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[GeoPolygonQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize GeoShapeQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/geo_shape_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[GeoShapeQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[GeoShapeQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize HasChildQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/has_child_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[HasChildQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[HasChildQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize HasParentQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/has_parent_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[HasParentQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[HasParentQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize IdsQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/ids_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[IdsQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[IdsQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize MatchAllQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/match_all_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[MatchAllQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[MatchAllQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

//  it should "serialize and deserialize MatchNoneQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/match_none_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[MatchNoneQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[MatchNoneQuery]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }

  it should "serialize and deserialize MoreLikeThisQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/more_like_this_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[MoreLikeThisQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[MoreLikeThisQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  //TODO

//  it should "serialize and deserialize MultiMatchQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/multi_match_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[MultiMatchQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[MultiMatchQuery]
//    val nJson = oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }
//
//  it should "serialize and deserialize NestedQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/nested_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[NestedQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[NestedQuery]
//    val nJson = oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }

//  it should "serialize and deserialize ParentIdQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/parent_id_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[ParentIdQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[ParentIdQuery]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }

  it should "serialize and deserialize PrefixQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/prefix_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[PrefixQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[PrefixQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize QueryStringQuery" in {
    val json =
      readResourceJSON("/elasticsearch/queries/query_string_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[QueryStringQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[QueryStringQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize RangeQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/range_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[RangeQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[RangeQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize RegexpQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/regexp_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[RegexpQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[RegexpQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize ScriptQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/script_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[ScriptQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[ScriptQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

//  it should "serialize and deserialize SimpleQueryString" in {
//    val json = readResourceJSON("/elasticsearch/queries/simple_query_string.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[SimpleQueryString] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[SimpleQueryString]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }
//
//  it should "serialize and deserialize SpanContainingQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/span_containing_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[SpanContainingQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[SpanContainingQuery]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }

  it should "serialize and deserialize SpanFirstQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_first_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[SpanFirstQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[SpanFirstQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

//  it should "serialize and deserialize SpanMultiTermQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/span_multi_term_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[SpanMultiTermQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[SpanMultiTermQuery]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }

  it should "serialize and deserialize SpanNearQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_near_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[SpanNearQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[SpanNearQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize SpanNotQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_not_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[SpanNotQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[SpanNotQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize SpanOrQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_or_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[SpanOrQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[SpanOrQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize SpanTermQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/span_term_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[SpanTermQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[SpanTermQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

//  it should "serialize and deserialize SpanWithinQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/span_within_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[SpanWithinQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[SpanWithinQuery]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }

  it should "serialize and deserialize TermQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/term_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[TermQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[TermQuery]
    realQuery.field should be("exact_value")
    realQuery.value.asString.get should be("Quick Foxes!")
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize TermsQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/terms_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[TermsQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[TermsQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize TypeQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/type_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[TypeQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[TypeQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

  it should "serialize and deserialize WildcardQuery" in {
    val json = readResourceJSON("/elasticsearch/queries/wildcard_query.json")
    val oQuery = json.as[Query]
    oQuery.isRight should be(true)
    oQuery.value.isInstanceOf[WildcardQuery] should be(true)
    val realQuery = oQuery.value.asInstanceOf[WildcardQuery]
    val nJson = oQuery.value.asJson
    nJson.as[Query].value should be(realQuery)
  }

//  it should "serialize and deserialize WrapperQuery" in {
//    val json = readResourceJSON("/elasticsearch/queries/wrapper_query.json")
//    val oQuery = json.as[Query]
//    oQuery.isRight should be(true)
//    oQuery.value.isInstanceOf[WrapperQuery] should be(true)
//    val realQuery = oQuery.value.asInstanceOf[WrapperQuery]
//    val nJson= oQuery.value.asJson
//    nJson.as[Query].value should be(realQuery)
//  }

}
