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

package zio.elasticsearch.ilm.explain_lifecycle
import java.time._

import zio.json._
import zio.json.ast._

sealed trait LifecycleExplain
object LifecycleExplain {
  implicit lazy val jsonCodec: JsonCodec[LifecycleExplain] =
    LifecycleExplainUnmanaged.jsonCodec
      .asInstanceOf[JsonCodec[LifecycleExplain]]
      .orElse(LifecycleExplainManaged.jsonCodec.asInstanceOf[JsonCodec[LifecycleExplain]])

}

final case class LifecycleExplainUnmanaged(index: String, managed: Option[Boolean] = None) extends LifecycleExplain

object LifecycleExplainUnmanaged {
  implicit lazy val jsonCodec: JsonCodec[LifecycleExplainUnmanaged] = DeriveJsonCodec.gen[LifecycleExplainUnmanaged]
}

final case class LifecycleExplainManaged(
  action: Option[String] = None,
  @jsonField("action_time") actionTime: Option[LocalDateTime] = None,
  @jsonField("action_time_millis") actionTimeMillis: Option[Long] = None,
  age: Option[String] = None,
  @jsonField("failed_step") failedStep: Option[String] = None,
  @jsonField("failed_step_retry_count") failedStepRetryCount: Option[Int] = None,
  index: Option[String] = None,
  @jsonField("index_creation_date") indexCreationDate: Option[LocalDateTime] = None,
  @jsonField("index_creation_date_millis") indexCreationDateMillis: Option[Long] = None,
  @jsonField("is_auto_retryable_error") isAutoRetryableError: Option[Boolean] = None,
  @jsonField("lifecycle_date") lifecycleDate: Option[LocalDateTime] = None,
  @jsonField("lifecycle_date_millis") lifecycleDateMillis: Option[Long] = None,
  managed: Option[Boolean] = None,
  phase: String,
  @jsonField("phase_time") phaseTime: Option[LocalDateTime] = None,
  @jsonField("phase_time_millis") phaseTimeMillis: Option[Long] = None,
  policy: String,
  step: Option[String] = None,
  @jsonField("step_info") stepInfo: Option[Map[String, Json]] = None,
  @jsonField("step_time") stepTime: Option[LocalDateTime] = None,
  @jsonField("step_time_millis") stepTimeMillis: Option[Long] = None,
  @jsonField("phase_execution") phaseExecution: Option[LifecycleExplainPhaseExecution] = None,
  @jsonField("time_since_index_creation") timeSinceIndexCreation: Option[String] = None
) extends LifecycleExplain

object LifecycleExplainManaged {
  implicit lazy val jsonCodec: JsonCodec[LifecycleExplainManaged] = DeriveJsonCodec.gen[LifecycleExplainManaged]
}
