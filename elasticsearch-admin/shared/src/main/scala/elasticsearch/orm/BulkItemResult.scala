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

package zio.elasticsearch.orm

import elasticsearch.requests._
import elasticsearch.responses.{ BulkItemResponse, BulkResponse, IndexResponse }

case class BulkItemResult(original: BulkItemResponse) {

  def id = original.response.id

  def index = original.response.index

  def opType = original.opType

  def `type` = original.response.docType

  def version = original.response.version

  def isFailure: Boolean = original.isFailed

  def isConflict: Boolean = original.isConflict

}

case class IndexResult(original: IndexResponse) {

  // java method aliases
  def getId = id

  def id = original.id

  def getType = `type`

  def `type` = original.docType

  def getIndex = index

  def index = original.index

  def getVersion = original.version

  def isCreated: Boolean = created

  def created: Boolean = original.version == 1

  def version: Long = original.version
}

case class BulkResult(original: BulkResponse) {

  import scala.concurrent.duration._

  //  def failureMessage: String = original.buildFailureMessage

  def took: FiniteDuration = original.took.millis

  def hasFailures: Boolean = original.items.exists(_.response.error.isDefined)

  def hasSuccesses: Boolean = original.items.exists(_.response.error.isEmpty)

  def failures: Seq[BulkItemResult] = items.filter(_.isFailure)

  def successes: Seq[BulkItemResult] = items.filterNot(_.isFailure)

  def items: Seq[BulkItemResult] = original.items.map(BulkItemResult.apply)
}

case class BulkDefinition(requests: Seq[BulkActionRequest]) {

  def build = builder

  private val builder = {
    BulkRequest(body = requests.map(_.toBulkString).mkString(""))
  }
}
