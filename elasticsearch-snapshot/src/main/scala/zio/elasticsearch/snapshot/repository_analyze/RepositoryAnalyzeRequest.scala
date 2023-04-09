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

package zio.elasticsearch.snapshot.repository_analyze
import scala.collection.mutable
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Analyzes a repository for correctness and performance
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A repository name
 * @param blobCount Number of blobs to create during the test. Defaults to 100.
 * @param concurrency Number of operations to run concurrently during the test. Defaults to 10.
 * @param detailed Whether to return detailed results or a summary. Defaults to 'false' so that only the summary is returned.
 * @param earlyReadNodeCount Number of nodes on which to perform an early read on a blob, i.e. before writing has completed. Early reads are rare actions so the 'rare_action_probability' parameter is also relevant. Defaults to 2.
 * @param maxBlobSize Maximum size of a blob to create during the test, e.g '1gb' or '100mb'. Defaults to '10mb'.
 * @param maxTotalDataSize Maximum total size of all blobs to create during the test, e.g '1tb' or '100gb'. Defaults to '1gb'.
 * @param rareActionProbability Probability of taking a rare action such as an early read or an overwrite. Defaults to 0.02.
 * @param rarelyAbortWrites Whether to rarely abort writes before they complete. Defaults to 'true'.
 * @param readNodeCount Number of nodes on which to read a blob after writing. Defaults to 10.
 * @param seed Seed for the random number generator used to create the test workload. Defaults to a random value.
 * @param timeout Explicit operation timeout. Defaults to '30s'.
 */

final case class RepositoryAnalyzeRequest(
  repository: String,
  blobCount: Option[Double] = None,
  concurrency: Option[Double] = None,
  detailed: Option[Boolean] = None,
  earlyReadNodeCount: Option[Double] = None,
  maxBlobSize: Option[String] = None,
  maxTotalDataSize: Option[String] = None,
  rareActionProbability: Option[Double] = None,
  rarelyAbortWrites: Option[Boolean] = None,
  readNodeCount: Option[Double] = None,
  seed: Option[Double] = None,
  timeout: Option[String] = None
) extends ActionRequest[Json] {
  def method: String = "POST"

  def urlPath: String = this.makeUrl("_snapshot", repository, "_analyze")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    blobCount.foreach { v =>
      queryArgs += ("blob_count" -> v.toString)
    }
    concurrency.foreach { v =>
      queryArgs += ("concurrency" -> v.toString)
    }
    detailed.foreach { v =>
      queryArgs += ("detailed" -> v.toString)
    }
    earlyReadNodeCount.foreach { v =>
      queryArgs += ("early_read_node_count" -> v.toString)
    }
    maxBlobSize.foreach { v =>
      queryArgs += ("max_blob_size" -> v)
    }
    maxTotalDataSize.foreach { v =>
      queryArgs += ("max_total_data_size" -> v)
    }
    rareActionProbability.foreach { v =>
      queryArgs += ("rare_action_probability" -> v.toString)
    }
    rarelyAbortWrites.foreach { v =>
      queryArgs += ("rarely_abort_writes" -> v.toString)
    }
    readNodeCount.foreach { v =>
      queryArgs += ("read_node_count" -> v.toString)
    }
    seed.foreach { v =>
      queryArgs += ("seed" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
