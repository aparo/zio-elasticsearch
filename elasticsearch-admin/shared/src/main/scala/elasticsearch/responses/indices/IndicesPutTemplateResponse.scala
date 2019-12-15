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

package elasticsearch.responses.indices

import io.circe.derivation.annotations._
/*
 * Creates or updates an index template.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
 *
 * @param name The name of the template
 * @param body body the body of the call
 * @param create Whether the index template should only be added if new or can also replace an existing one
 * @param flatSettings Return settings in flat format (default: false)
 * @param includeTypeName Whether a type should be returned in the body of the mappings.
 * @param masterTimeout Specify timeout for connection to master
 * @param order The order for this template when merging multiple matching ones (higher numbers are merged later, overriding the lower numbers)
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class IndicesPutTemplateResponse() {}
