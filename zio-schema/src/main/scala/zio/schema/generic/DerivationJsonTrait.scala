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

package zio.schema.generic

import scala.collection.mutable.ListBuffer
import scala.reflect.macros.blackbox

import io.circe.derivation.annotations.Configuration
import io.circe.{ JsonDecoder, JsonEncoder }

// code taken from circe.derivation
trait DerivationJsonTrait extends DerivationHelperTrait {
  val c: blackbox.Context

  import c.universe._

  private[this] val DecoderClass = typeOf[JsonDecoder[_]].typeSymbol.asType
  private[this] val EncoderClass = typeOf[JsonEncoder[_]].typeSymbol.asType
  private[this] val ObjectEncoderClass =
    typeOf[JsonEncoder[_]].typeSymbol.asType

  private[this] val macroName: Tree = {
    c.prefix.tree match {
      case Apply(Select(New(name), _), _) => name
      case _                              => c.abort(c.enclosingPosition, "Unexpected macro application")
    }
  }

  private[this] val defaultCfg: Tree =
    q"_root_.io.circe.derivation.annotations.Configuration.default"

  private[this] val (codecType: JsonCodecType, config: Tree) = {
    c.prefix.tree match {
      case q"new ${`macroName` }()" => (JsonCodecType.Both, defaultCfg)
      case q"new ${`macroName` }(config = $cfg)" =>
        (codecFrom(c.typecheck(cfg)), cfg)
      case q"new ${`macroName` }($cfg)" => (codecFrom(c.typecheck(cfg)), cfg)
      case _ =>
        c.abort(
          c.enclosingPosition,
          s"Unsupported arguments supplied to @$macroName"
        )
    }
  }

  private[this] def codecFrom(tree: Tree): JsonCodecType =
    tree.tpe.dealias match {
      case t if t == typeOf[Configuration.Codec] =>
        JsonCodecType.Both
      case t if t == typeOf[Configuration.DecodeOnly] =>
        JsonCodecType.DecodeOnly
      case t if t == typeOf[Configuration.EncodeOnly] =>
        JsonCodecType.EncodeOnly
      case t =>
        c.warning(
          c.enclosingPosition,
          "Couldn't determine type of configuration - will produce both encoder and decoder"
        )
        JsonCodecType.Both
    }

  private[this] val cfgNameTransformation =
    q"$config.transformMemberNames"
  private[this] val cfgUseDefaults =
    q"$config.useDefaults"
  private[this] val cfgDiscriminator =
    q"$config.discriminator"

  private[this] val defaultDiscriminator: Tree =
    q"_root_.io.circe.derivation.Discriminator.default"

  protected def codec(clsDef: ClassDef, objdefs: Seq[Tree]): List[Tree] = {
    val tpname = clsDef.name
    val tparams = clsDef.tparams
    val decodeNme = TermName("decode" + tpname.decodedName)
    val encodeNme = TermName("encode" + tpname.decodedName)
    val result = new ListBuffer[Tree]()

    val (decoder, encoder) = if (tparams.isEmpty) {
      val Type = tpname

      result += q"""def _internalDecoderJson: $DecoderClass[$Type] = $decodeNme"""
      result += q"""def _internalEncoderJson: $ObjectEncoderClass[$Type] = $encodeNme"""

      (
        q"""implicit val $decodeNme: $DecoderClass[$Type] =
            _root_.io.circe.derivation.deriveDecoder[$Type]($cfgNameTransformation, $cfgUseDefaults, $cfgDiscriminator)""",
        q"""implicit val $encodeNme: $ObjectEncoderClass[$Type] =
            _root_.io.circe.derivation.deriveEncoder[$Type]($cfgNameTransformation, $cfgDiscriminator)"""
      )
    } else {
      val tparamNames = tparams.map(_.name)
      def mkImplicitParams(typeSymbol: TypeSymbol) =
        tparamNames.zipWithIndex.map {
          case (tparamName, i) =>
            val paramName = TermName(s"instance$i")
            val paramType = tq"$typeSymbol[$tparamName]"
            q"$paramName: $paramType"
        }
      val decodeParams = mkImplicitParams(DecoderClass)
      val encodeParams = mkImplicitParams(EncoderClass)
      val Type = tq"$tpname[..$tparamNames]"

      (
        q"""implicit def $decodeNme[..$tparams](implicit ..$decodeParams): $DecoderClass[$Type] =
           _root_.io.circe.derivation.deriveDecoder[$Type]($cfgNameTransformation, $cfgUseDefaults, $cfgDiscriminator)""",
        q"""implicit def $encodeNme[..$tparams](implicit ..$encodeParams): $ObjectEncoderClass[$Type] =
           _root_.io.circe.derivation.deriveEncoder[$Type]($cfgNameTransformation, $cfgDiscriminator)"""
      )
    }

    if (!existsImplicit(objdefs, decodeNme.toString)) {
      result += decoder
    }
    if (!existsImplicit(objdefs, encodeNme.toString)) {
      result += encoder
    }
    result.toList
  }

}

private sealed trait JsonCodecType
private object JsonCodecType {
  case object Both extends JsonCodecType
  case object DecodeOnly extends JsonCodecType
  case object EncodeOnly extends JsonCodecType
}
