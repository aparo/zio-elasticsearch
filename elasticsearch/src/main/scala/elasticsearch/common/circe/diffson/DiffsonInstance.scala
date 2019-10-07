/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.common.circe.diffson

abstract class DiffsonInstance[JsValue]
    extends JsonDiffSupport[JsValue]
    with JsonPatchSupport[JsValue]
    with JsonPointerSupport[JsValue]
    with JsonSupport[JsValue]
    with LowPriorityImplicits[JsValue] {

  val provider: JsonProvider

}
