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

package zio.elasticsearch.cat

import zio.json._

sealed trait CatDatafeedColumn

object CatDatafeedColumn {

  case object ae extends CatDatafeedColumn

  case object assignment_explanation extends CatDatafeedColumn

  case object bc extends CatDatafeedColumn

  case object `buckets.count` extends CatDatafeedColumn

  case object bucketsCount extends CatDatafeedColumn

  case object id extends CatDatafeedColumn

  case object na extends CatDatafeedColumn

  case object `node.address` extends CatDatafeedColumn

  case object nodeAddress extends CatDatafeedColumn

  case object ne extends CatDatafeedColumn

  case object `node.ephemeral_id` extends CatDatafeedColumn

  case object nodeEphemeralId extends CatDatafeedColumn

  case object ni extends CatDatafeedColumn

  case object `node.id` extends CatDatafeedColumn

  case object nodeId extends CatDatafeedColumn

  case object nn extends CatDatafeedColumn

  case object `node.name` extends CatDatafeedColumn

  case object nodeName extends CatDatafeedColumn

  case object sba extends CatDatafeedColumn

  case object `search.bucket_avg` extends CatDatafeedColumn

  case object searchBucketAvg extends CatDatafeedColumn

  case object sc extends CatDatafeedColumn

  case object `search.count` extends CatDatafeedColumn

  case object searchCount extends CatDatafeedColumn

  case object seah extends CatDatafeedColumn

  case object `search.exp_avg_hour` extends CatDatafeedColumn

  case object searchExpAvgHour extends CatDatafeedColumn

  case object st extends CatDatafeedColumn

  case object `search.time` extends CatDatafeedColumn

  case object searchTime extends CatDatafeedColumn

  case object s extends CatDatafeedColumn

  case object state extends CatDatafeedColumn

  implicit final val decoder: JsonDecoder[CatDatafeedColumn] =
    DeriveJsonDecoderEnum.gen[CatDatafeedColumn]
  implicit final val encoder: JsonEncoder[CatDatafeedColumn] =
    DeriveJsonEncoderEnum.gen[CatDatafeedColumn]
  implicit final val codec: JsonCodec[CatDatafeedColumn] =
    JsonCodec(encoder, decoder)

}
