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

package elasticsearch.queries

import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

@JsonCodec
final case class Rescorer(
  query: Query,
  @JsonKey("rescore_query_weight") rescoreQueryWeight: Option[Float] = None,
  @JsonKey("query_weight") queryWeight: Option[Float] = None,
  @JsonKey("score_mode") scoreMode: Option[String] = None
)
