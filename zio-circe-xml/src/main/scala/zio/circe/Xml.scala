package io.circe

import scala.util.{ Failure, Success, Try }
import scala.xml._

object Xml {
  implicit class RichCirceJson(val xml: NodeSeq) {

    def toJson: Json =
      Xml.toJson(xml)
  }

  implicit class RichXml(val json: Json) {

    def toXml: NodeSeq =
      Xml.toXml(json)
  }

  def toJson(xml: NodeSeq): Json = {

    def isEmpty(node: Node): Boolean = node.child.isEmpty
    def isLeaf(node: Node) = {
      def descendant(n: Node): List[Node] = n match {
        case g: Group => g.nodes.toList.flatMap(x => x :: descendant(x))
        case _ =>
          n.child.toList.flatMap { x =>
            x :: descendant(x)
          }
      }
      !descendant(node).exists(_.isInstanceOf[Elem])
    }

    def isArray(nodeNames: Seq[String]) =
      nodeNames.size != 1 && nodeNames.toList.distinct.size == 1
    def directChildren(n: Node): NodeSeq =
      n.child.filter(c => c.isInstanceOf[Elem])
    def nameOf(n: Node) =
      (if (n.prefix ne null) n.prefix + ":" else "") + n.label
    def buildAttrs(n: Node) =
      n.attributes.map((a: MetaData) => (a.key, XValue(a.value.text))).toList

    sealed abstract class XElem extends Product with Serializable
    case class XValue(value: String) extends XElem
    case class XLeaf(value: (String, XElem), attrs: List[(String, XValue)]) extends XElem
    case class XNode(fields: List[(String, XElem)]) extends XElem
    case class XArray(elems: List[XElem]) extends XElem

    def toJson(x: XElem): Json = x match {
      case x @ XValue(_) => xValueToJson(x)
      case XLeaf((name, value), attrs) =>
        (value, attrs) match {
          case (_, Nil)         => toJson(value)
          case (XValue(""), xs) => Json.fromFields(mkFields(xs))
          case (_, _)           => Json.fromFields(Seq(name -> toJson(value)))
        }
      case XNode(xs)     => Json.fromFields(mkFields(xs))
      case XArray(elems) => Json.fromValues(elems.map(toJson))
    }

    def xValueToJson(xValue: XValue): Json =
      (Try(xValue.value.toInt), Try(xValue.value.toBoolean)) match {
        case (Success(v), Failure(_)) => Json.fromInt(v)
        case (Failure(_), Success(v)) => Json.fromBoolean(v)
        case _                        => Json.fromString(xValue.value)
      }

    def mkFields(xs: List[(String, XElem)]): List[(String, Json)] =
      xs.flatMap {
        case (name, value) =>
          (value, toJson(value)) match {
            case (XLeaf(_, _ :: _), o) if o.isObject => o.asObject.get.toList
            case (_, json)                           => Seq(name -> json)
          }
      }

    def buildNodes(xml: NodeSeq): List[XElem] = xml match {
      case n: Node =>
        if (isEmpty(n)) XLeaf((nameOf(n), XValue("")), buildAttrs(n)) :: Nil
        else if (isLeaf(n))
          XLeaf((nameOf(n), XValue(n.text)), buildAttrs(n)) :: Nil
        else {
          val children = directChildren(n)
          XNode(buildAttrs(n) ::: children.map(nameOf).toList.zip(buildNodes(children))) :: Nil
        }
      case nodes: NodeSeq =>
        val allLabels = nodes.map(_.label)
        if (isArray(allLabels)) {
          val arr = XArray(nodes.toList.flatMap { n =>
            if (isLeaf(n) && n.attributes.length == 0) XValue(n.text) :: Nil
            else buildNodes(n)
          })
          XLeaf((allLabels.head, arr), Nil) :: Nil
        } else nodes.toList.flatMap(buildNodes)
    }

    buildNodes(xml) match {
      case List(x @ XLeaf(_, _ :: _)) => toJson(x)
      case List(x)                    => Json.obj(nameOf(xml.head) -> toJson(x))
      case x                          => Json.fromValues(x.map(toJson))
    }

  }

  def toXml(json: Json, withItems: Boolean = false): NodeSeq = {
    def toXml(name: String, json: Json): NodeSeq = json match {
      case jo if jo.isObject =>
        val children = jo.asObject.get.toList.flatMap {
          case (n, v) => toXml(n, v)
        }
        XmlNode(name, children)
      case xs if xs.isArray =>
        if (withItems) {
          XmlNode(name, xs.asArray.get.flatMap { v =>
            toXml("item", v)
          })
        } else {
          xs.asArray.get.flatMap { v =>
            toXml(name, v)
          }
        }
      case v if v.isNumber =>
        XmlElem(name, v.asNumber.get.toString)
      case v if v.isBoolean =>
        XmlElem(name, v.asBoolean.get.toString)
      case o if o.isString =>
        XmlElem(name, o.asString.get)
      case Json.Null =>
        XmlElem(name, "null")
    }

    json match {
      case jo if jo.isObject =>
        jo.asObject.get.toList.toList.flatMap { case (n, v) => toXml(n, v) }
      case x =>
        toXml("root", x)
    }
  }

  private[this] case class XmlNode(name: String, children: Seq[Node])
      extends Elem(null, name, xml.Null, TopScope, true, children: _*)

  private[this] case class XmlElem(name: String, value: String)
      extends Elem(null, name, xml.Null, TopScope, true, Text(value))
}
