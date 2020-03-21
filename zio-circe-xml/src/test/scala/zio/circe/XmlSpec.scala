package io.circe

import org.scalatest._
import scala.xml.NodeSeq

class XmlSpec extends FlatSpec with Matchers {
  import Xml._

  trait SetUp {
    val xml =
      <fruits>
        <fruit>
          <name>banana</name>
          <price>1000</price>
          <season>true</season>
          <delicious>true</delicious>
        </fruit>
        <fruit>
          <name>strawberry</name>
          <price>3000</price>
          <season>false</season>
          <delicious>true</delicious>
        </fruit>
      </fruits>

    val xmlNodeSeq: NodeSeq =
      <fruits><fruit><name>banana</name><price>1000</price><season>true</season><delicious>true</delicious></fruit><fruit><name>strawberry</name><price>3000</price><season>false</season><delicious>true</delicious></fruit></fruits>
        .foldLeft(NodeSeq.Empty) { (a, b) =>
          a ++ b
        }

    val json = io.circe.parser
      .parse(
        """
        |{
        |   "fruits":{
        |      "fruit":[
        |         {
        |            "name":"banana",
        |            "price":1000,
        |            "season":true,
        |            "delicious":true
        |         },
        |         {
        |            "name":"strawberry",
        |            "price":3000,
        |            "season":false,
        |            "delicious":true
        |         }
        |      ]
        |   }
        |}
      """.stripMargin
      )
      .right
      .get

    val jsonS1 = parser.parse("""{"test":["12", 3]}""").right.get
    val xmlNodeSeqS1: NodeSeq = <test><item>12</item><item>3</item></test>.foldLeft(NodeSeq.Empty) { (a, b) =>
      a ++ b
    }
  }

  "toJson" should "convert xml to json" in new SetUp {
    toJson(xml) should equal(json)
  }

  "xml.toJson" should "convert xml to json implicitly" in new SetUp {
    xml.toJson should equal(json)
  }

  "toXml" should "convert json to xml" in new SetUp {
    toXml(json) should equal(xmlNodeSeq)
    toXml(jsonS1, true) should equal(xmlNodeSeqS1)
  }

  "json.toXml" should "convert json to xml implicitly" in new SetUp {
    json.toXml should equal(xmlNodeSeq)
  }
}
