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

package zio.elasticsearch.rollup

package object get_rollup_index_caps {

  /*
   * Returns the rollup capabilities of all jobs inside of a rollup index (e.g. the index where rollup data is stored).
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-get-rollup-index-caps.html
   */
  type GetRollupIndexCapsResponse = Map[String, IndexCapabilities]

}
