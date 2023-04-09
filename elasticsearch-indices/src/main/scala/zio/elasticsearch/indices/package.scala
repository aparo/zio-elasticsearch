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

import zio.Chunk
import zio.elasticsearch.indices.recovery.RecoveryStatus

package object indices {

//  type DFIIndependenceMeasure = String //'standardized' | 'saturated' | 'chisquared'
//
//  type DFRAfterEffect = String // 'no' | 'b' | 'l'
//
//  type DFRBasicModel = String // 'be' | 'd' | 'g' | 'if' | 'in' | 'ine' | 'p'
//
//  type StorageType = String //  'fs' | '' | 'niofs' | 'mmapfs' | 'hybridfs'| string
//
//  type TranslogDurability = String // 'request' | 'REQUEST' | 'async' | 'ASYNC'
//
//  type AddBlockIndicesBlockOptions = String //'metadata' | 'read' | 'read_only' | 'write'

//  type Feature = String // 'aliases' | 'mappings' | 'settings'

  type TextToAnalyze = Chunk[String]
  type TokenFilter = String

//  type Normalization = String // 'no' | 'h1' | 'h2' | 'h3' | 'z'
//
//  type IBDistribution = String //'ll' | 'spl'
//
//  type IBLambda = String //'df' | 'ttf'

  type RecoveryResponse = Map[String, RecoveryStatus]

  type IndexMetadataState = String // 'open' | 'close'
//
  type ShardRoutingState = String // 'UNASSIGNED' | 'INITIALIZING' | 'STARTED' | 'RELOCATING'
  type ShardStoreAllocation = String //= 'primary' | 'replica' | 'unused'
//
//  type SegmentSortMissing = String // '_last' | '_first'
//
//  type SegmentSortMode = String //'min' | 'MIN' | 'max' | 'MAX'
//
//  type SegmentSortOrder = String // 'asc' | 'ASC' | 'desc' | 'DESC'

}
