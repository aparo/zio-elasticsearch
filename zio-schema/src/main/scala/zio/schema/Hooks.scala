package zio.schema

import io.circe.JsonObject
import zio.auth.AuthContext

trait PreSaveHooks[BaseDocument] {
  def preSaveHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PreSaveJsonHooks {
  def preSaveJsonHooks: List[(AuthContext, JsonObject) => JsonObject]
}

trait PostSaveHooks[BaseDocument] {
  def postSaveHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PreLoadHooks {
  def preLoadHooks: List[(AuthContext, JsonObject) => JsonObject]
}

trait PreUpdateJsonHooks[BaseDocument] {
  def preUpdateJsonHooks: List[(AuthContext, JsonObject) => JsonObject]
}

trait PostLoadHooks[BaseDocument] {
  def postLoadHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PreDeleteHooks[BaseDocument] {
  def preDeleteHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PostDeleteHooks[BaseDocument] {
  def postDeleteHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PreUpdateHooks[BaseDocument] {
  def preUpdateHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}

trait PostUpdateHooks[BaseDocument] {
  def postUpdateHooks: List[(AuthContext, BaseDocument) => BaseDocument]
}
