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

package zio.elasticsearch.ingest

import zio.{ Chunk, Scope, ZIO }
import zio.elasticsearch.SpecHelper
import zio.elasticsearch.ingest.requests.SimulateRequestBody
import zio.elasticsearch.ingest.simulate.SimulateRequest
import zio.test.Assertion._
import zio.test._

import scala.io.Source
import zio.json.ast._
import zio.json._

object ProcessorSpec extends ZIOSpecDefault {

  def readResourceJSON(name: String): zio.IO[String, Json] = {
    val source = Source.fromInputStream(getClass.getResourceAsStream(name))
    val res = source.mkString
    source.close
    ZIO.fromEither(res.fromJson[Json])
  }

  def readPipelineJSON(name: String): zio.IO[String, Pipeline] =
    for {
      pipeJson <- readResourceJSON(s"/zio/elasticsearch/ingest/processors/$name.json")
      pipeline <- ZIO.fromEither(pipeJson.as[Pipeline])
    } yield pipeline

  def readProcessorJSON(name: String): zio.IO[String, Processor] =
    for {
      pipeJson <- readResourceJSON(s"/zio/elasticsearch/ingest/processors/$name.json")
      pipeline <- ZIO.fromEither(pipeJson.as[Processor])
    } yield pipeline

  def readSimulateJSON(name: String): zio.IO[String, SimulateRequestBody] =
    for {
      pipeJson <- readResourceJSON(s"/zio/elasticsearch/ingest/processors/$name.json")
      pipeline <- ZIO.fromEither(pipeJson.as[SimulateRequestBody])
    } yield pipeline

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("ProcessorSpec")(
    test("append") {
      for {
        genericProcessor <- readProcessorJSON("append")
        processor = genericProcessor.asInstanceOf[AppendProcessor]
      } yield assert(processor.field)(equalTo("tags")) &&
        assert(processor.value)(equalTo(Chunk("production", "{{{app}}}", "{{{owner}}}").map(s => Json.Str(s))))
    },
    test("attachment") {
      for {
        pipeline <- readPipelineJSON("attachment")
        processor = pipeline.processors.head.asInstanceOf[AttachmentProcessor]
      } yield assert(processor.field)(equalTo("data")) &&
        assert(processor.removeBinary)(equalTo(Some(false)))
    },
    test("bytes") {
      for {
        genericProcessor <- readProcessorJSON("bytes")
        processor = genericProcessor.asInstanceOf[BytesProcessor]
      } yield assert(processor.field)(equalTo("file.size"))
    },
    test("circle") {
      for {
        pipeline <- readPipelineJSON("circle")
        processor = pipeline.processors.head.asInstanceOf[CircleProcessor]
      } yield assert(processor.field)(equalTo("circle")) &&
        assert(processor.errorDistance)(equalTo(28.0)) &&
        assert(processor.shapeType)(equalTo(ShapeType.geo_shape))

    },
    test("convert") {
      for {
        pipeline <- readPipelineJSON("convert")
        processor = pipeline.processors.head.asInstanceOf[ConvertProcessor]
      } yield assert(processor.field)(equalTo("id")) &&
        assert(processor.`type`)(equalTo(ConvertType.integer))

    },
    test("csv") {
      for {
        genericProcessor <- readProcessorJSON("csv")
        processor = genericProcessor.asInstanceOf[CsvProcessor]
      } yield assert(processor.field)(equalTo("my_field")) &&
        assert(processor.targetFields)(equalTo(Chunk("field1", "field2")))
    },
    test("date-index-name") {
      for {
        pipeline <- readPipelineJSON("date-index-name")
        processor = pipeline.processors.head.asInstanceOf[DateIndexNameProcessor]
      } yield assert(processor.field)(equalTo("date1")) &&
        assert(processor.indexNamePrefix)(equalTo(Some("my-index-"))) &&
        assert(processor.dateRounding)(equalTo("M"))

    },
    test("date") {
      for {
        pipeline <- readPipelineJSON("date")
        processor = pipeline.processors.head.asInstanceOf[DateProcessor]
      } yield assert(processor.field)(equalTo("initial_date")) &&
        assert(processor.targetField)(equalTo(Some("timestamp"))) &&
        assert(processor.formats)(equalTo(Chunk("ISO8601"))) &&
        assert(processor.timezone)(equalTo(Some("{{{my_timezone}}}"))) &&
        assert(processor.locale)(equalTo(Some("{{{my_locale}}}")))
    },
    test("dot-expander") {
      for {
        genericProcessor <- readProcessorJSON("dot-expander")
        processor = genericProcessor.asInstanceOf[DotExpanderProcessor]
      } yield assert(processor.field)(equalTo("foo.bar"))
    },
    test("drop") {
      for {
        genericProcessor <- readProcessorJSON("drop")
        processor = genericProcessor.asInstanceOf[DropProcessor]
      } yield assert(processor.`if`)(equalTo(Some("ctx.network_name == 'Guest'")))
    },
    test("fail") {
      for {
        genericProcessor <- readProcessorJSON("fail")
        processor = genericProcessor.asInstanceOf[FailProcessor]
      } yield assert(processor.`if`)(equalTo(Some("ctx.tags.contains('production') != true"))) &&
        assert(processor.message)(equalTo("The production tag is not present, found tags: {{{tags}}}"))
    },
//    test("fingerprint") {
//      for {
//        genericProcessor <- readSimulateJSON("fingerprint")
//        processor = genericProcessor.asInstanceOf[FingerprintProcessor]
//      } yield assert(processor.`if`)(equalTo(Some("ctx.tags.contains('production') != true"))) &&
//        assert(processor.message)(equalTo("The production tag is not present, found tags: {{{tags}}}"))
//    },
    test("foreach") {
      for {
        genericProcessor <- readProcessorJSON("foreach")
        processor = genericProcessor.asInstanceOf[ForeachProcessor]
      } yield assert(processor.field)(equalTo("values")) &&
        assert(processor.processor.isInstanceOf[UppercaseProcessor])(equalTo(true))
    },
    /*
{
      "fail": {
        "if" : "ctx.tags.contains('production') != true",
        "message": "The production tag is not present, found tags: {{{tags}}}"
      }
    }

     * * */
    test("geoip") {
      for {
        pipeline <- readPipelineJSON("geoip")
        processor = pipeline.processors.head.asInstanceOf[GeoIpProcessor]
      } yield assert(processor.field)(equalTo("ip")) &&
        assert(processor.targetField)(equalTo(Some("geo"))) &&
        assert(processor.databaseFile)(equalTo(Some("GeoLite2-Country.mmdb")))
    }
  )
}
