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

package zio.elasticsearch.nodes

trait PressureMemory {
  def all: Option[String]
  def allInBytes: Option[Long]
  def combinedCoordinatingAndPrimary: Option[String]
  def combinedCoordinatingAndPrimaryInBytes: Option[Long]
  def coordinating: Option[String]
  def coordinatingInBytes: Option[Long]
  def primary: Option[String]
  def primaryInBytes: Option[Long]
  def replica: Option[String]
  def replicaInBytes: Option[Long]
  def coordinatingRejections: Option[Long]
  def primaryRejections: Option[Long]
  def replicaRejections: Option[Long]
}
