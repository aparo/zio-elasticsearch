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

package elasticsearch.common.circe.diffson

/** The interface to classes that computes the longest common subsequence between
  *  two sequences of elements
  *
  *  @author Lucas Satabin
  */
trait Lcs[T] {

  /** Computes the longest commons subsequence between both inputs.
    *  Returns an ordered list containing the indices in the first sequence and in the second sequence.
    */
  def lcs(seq1: Seq[T], seq2: Seq[T]): List[(Int, Int)] =
    lcs(seq1, seq2, 0, seq1.size, 0, seq2.size)

  /** Computest the longest common subsequence between both input slices.
    *  Returns an ordered list containing the indices in the first sequence and in the second sequence.
    */
  def lcs(
      seq1: Seq[T],
      seq2: Seq[T],
      low1: Int,
      high1: Int,
      low2: Int,
      high2: Int
  ): List[(Int, Int)]

}
