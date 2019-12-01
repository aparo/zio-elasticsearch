/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
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
