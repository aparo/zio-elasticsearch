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

package zio.elasticsearch

import zio.json.ast.Json

package object ml {
  type CategorizationAnalyzer = Json //string | MlCategorizationAnalyzerDefinition
  type ChunkingMode = String // 'auto' | 'manual' | 'off'
  type ConditionOperator = String //'gt' | 'gte' | 'lt' | 'lte'

  type DatafeedState = String //'started' | 'stopped' | 'starting' | 'stopping'

  type DataframeState = String //'started' | 'stopped' | 'starting' | 'stopping' | 'failed'

  type DeploymentAllocationState = String //'started' | 'starting' | 'fully_allocated'

  type DeploymentAssignmentState = String //'starting' | 'started' | 'stopping' | 'failed'

  type DeploymentState = String //'started' | 'starting' | 'stopping'
  type RoutingState = String //'failed' | 'started' | 'starting' | 'stopped' | 'stopping'

  type RuleAction = String //'skip_result' | 'skip_model_update'
  type ExcludeFrequent = String //'all' | 'none' | 'by' | 'over'

  type FilterType = String //'include' | 'exclude'

  type PredictedValue = Json //string | double | boolean | integer

  type CustomSettings = Json

  type JobBlockedReason = String //'delete' | 'reset' | 'revert'
  type JobState = String // 'closing' | 'closed' | 'opened' | 'failed' | 'opening'
  type MemoryStatus = String // 'ok' | 'soft_limit' | 'hard_limit'

  type TrainedModelType = String // 'tree_ensemble' | 'lang_ident' | 'pytorch'

  type TrainingPriority = String //'normal' | 'low'

  type TokenizationTruncate = String //'first' | 'second' | 'none'

  type SnapshotUpgradeState = String //'loading_old_state' | 'saving_new_state' | 'stopped' | 'failed'

  type CategorizationStatus = String //'ok' | 'warn'

  type AppliesTo = String //'actual' | 'typical' | 'diff_from_typical' | 'time'
}
