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

sealed trait CatTrainedModelsColumn

object CatTrainedModelsColumn {

  case object create_time extends CatTrainedModelsColumn

  case object ct extends CatTrainedModelsColumn

  case object created_by extends CatTrainedModelsColumn

  case object c extends CatTrainedModelsColumn

  case object createdBy extends CatTrainedModelsColumn

  case object data_frame_analytics_id extends CatTrainedModelsColumn

  case object df extends CatTrainedModelsColumn

  case object dataFrameAnalytics extends CatTrainedModelsColumn

  case object dfid extends CatTrainedModelsColumn

  case object description extends CatTrainedModelsColumn

  case object d extends CatTrainedModelsColumn

  case object heap_size extends CatTrainedModelsColumn

  case object hs extends CatTrainedModelsColumn

  case object modelHeapSize extends CatTrainedModelsColumn

  case object id extends CatTrainedModelsColumn

  case object `ingest.count` extends CatTrainedModelsColumn

  case object ic extends CatTrainedModelsColumn

  case object ingestCount extends CatTrainedModelsColumn

  case object `ingest.current` extends CatTrainedModelsColumn

  case object icurr extends CatTrainedModelsColumn

  case object ingestCurrent extends CatTrainedModelsColumn

  case object `ingest.failed` extends CatTrainedModelsColumn

  case object `if` extends CatTrainedModelsColumn

  case object ingestFailed extends CatTrainedModelsColumn

  case object `ingest.pipelines` extends CatTrainedModelsColumn

  case object ip extends CatTrainedModelsColumn

  case object ingestPipelines extends CatTrainedModelsColumn

  case object `ingest.time` extends CatTrainedModelsColumn

  case object it extends CatTrainedModelsColumn

  case object ingestTime extends CatTrainedModelsColumn

  case object license extends CatTrainedModelsColumn

  case object l extends CatTrainedModelsColumn

  case object operations extends CatTrainedModelsColumn

  case object o extends CatTrainedModelsColumn

  case object modelOperations extends CatTrainedModelsColumn

  case object version extends CatTrainedModelsColumn

  case object v extends CatTrainedModelsColumn

  implicit final val decoder: JsonDecoder[CatTrainedModelsColumn] = DeriveJsonDecoderEnum.gen[CatTrainedModelsColumn]
  implicit final val encoder: JsonEncoder[CatTrainedModelsColumn] = DeriveJsonEncoderEnum.gen[CatTrainedModelsColumn]
  implicit final val codec: JsonCodec[CatTrainedModelsColumn] = JsonCodec(encoder, decoder)

}
