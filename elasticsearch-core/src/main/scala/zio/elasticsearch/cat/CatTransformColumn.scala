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

sealed trait CatTransformColumn

object CatTransformColumn {

  case object changes_last_detection_time extends CatTransformColumn

  case object cldt extends CatTransformColumn

  case object checkpoint extends CatTransformColumn

  case object cp extends CatTransformColumn

  case object checkpoint_duration_time_exp_avg extends CatTransformColumn

  case object cdtea extends CatTransformColumn

  case object checkpointTimeExpAvg extends CatTransformColumn

  case object checkpoint_progress extends CatTransformColumn

  case object c extends CatTransformColumn

  case object checkpointProgress extends CatTransformColumn

  case object create_time extends CatTransformColumn

  case object ct extends CatTransformColumn

  case object createTime extends CatTransformColumn

  case object delete_time extends CatTransformColumn

  case object dtime extends CatTransformColumn

  case object description extends CatTransformColumn

  case object d extends CatTransformColumn

  case object dest_index extends CatTransformColumn

  case object di extends CatTransformColumn

  case object destIndex extends CatTransformColumn

  case object documents_deleted extends CatTransformColumn

  case object docd extends CatTransformColumn

  case object documents_indexed extends CatTransformColumn

  case object doci extends CatTransformColumn

  case object docs_per_second extends CatTransformColumn

  case object dps extends CatTransformColumn

  case object documents_processed extends CatTransformColumn

  case object docp extends CatTransformColumn

  case object frequency extends CatTransformColumn

  case object f extends CatTransformColumn

  case object id extends CatTransformColumn

  case object index_failure extends CatTransformColumn

  case object `if` extends CatTransformColumn

  case object index_time extends CatTransformColumn

  case object itime extends CatTransformColumn

  case object index_total extends CatTransformColumn

  case object it extends CatTransformColumn

  case object indexed_documents_exp_avg extends CatTransformColumn

  case object idea extends CatTransformColumn

  case object last_search_time extends CatTransformColumn

  case object lst extends CatTransformColumn

  case object lastSearchTime extends CatTransformColumn

  case object max_page_search_size extends CatTransformColumn

  case object mpsz extends CatTransformColumn

  case object pages_processed extends CatTransformColumn

  case object pp extends CatTransformColumn

  case object pipeline extends CatTransformColumn

  case object p extends CatTransformColumn

  case object processed_documents_exp_avg extends CatTransformColumn

  case object pdea extends CatTransformColumn

  case object processing_time extends CatTransformColumn

  case object pt extends CatTransformColumn

  case object reason extends CatTransformColumn

  case object r extends CatTransformColumn

  case object search_failure extends CatTransformColumn

  case object sf extends CatTransformColumn

  case object search_time extends CatTransformColumn

  case object stime extends CatTransformColumn

  case object search_total extends CatTransformColumn

  case object st extends CatTransformColumn

  case object source_index extends CatTransformColumn

  case object si extends CatTransformColumn

  case object sourceIndex extends CatTransformColumn

  case object state extends CatTransformColumn

  case object s extends CatTransformColumn

  case object transform_type extends CatTransformColumn

  case object tt extends CatTransformColumn

  case object trigger_count extends CatTransformColumn

  case object tc extends CatTransformColumn

  case object version extends CatTransformColumn

  case object v extends CatTransformColumn

  implicit final val decoder: JsonDecoder[CatTransformColumn] = DeriveJsonDecoderEnum.gen[CatTransformColumn]
  implicit final val encoder: JsonEncoder[CatTransformColumn] = DeriveJsonEncoderEnum.gen[CatTransformColumn]
  implicit final val codec: JsonCodec[CatTransformColumn] = JsonCodec(encoder, decoder)

}
