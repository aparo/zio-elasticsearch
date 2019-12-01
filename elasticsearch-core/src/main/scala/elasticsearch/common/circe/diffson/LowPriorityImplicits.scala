/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.common.circe.diffson

trait LowPriorityImplicits[JsValue] { this: JsonPointerSupport[JsValue] =>

  implicit val errorHandler: PointerErrorHandler = {
    case (_, name, parent) =>
      throw new PointerException(
        s"element $name does not exist at path ${parent.serialize}"
      )
  }

}
