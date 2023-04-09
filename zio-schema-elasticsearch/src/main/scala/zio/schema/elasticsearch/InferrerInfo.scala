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

package zio.schema.elasticsearch

import zio.json._

sealed trait InferrerInfo
object InferrerInfo {
  implicit val jsonDecoder: JsonDecoder[InferrerInfo] = DeriveJsonDecoder.gen[InferrerInfo]
  implicit val jsonEncoder: JsonEncoder[InferrerInfo] = DeriveJsonEncoder.gen[InferrerInfo]
}

/**
 * It tracks if the column is renamed due to other column have the same name
 * @param oldName
 *   old column anme
 * @param newName
 *   new column name
 * @param position
 *   the position in case of CSV, TSV or other positional sources
 */
final case class ColumnRenameValidation(oldName: String, newName: String, position: Option[Int] = None)
    extends InferrerInfo
object ColumnRenameValidation {
  implicit val jsonDecoder: JsonDecoder[ColumnRenameValidation] = DeriveJsonDecoder.gen[ColumnRenameValidation]
  implicit val jsonEncoder: JsonEncoder[ColumnRenameValidation] = DeriveJsonEncoder.gen[ColumnRenameValidation]
}

/**
 * It tracks if the system was unable to infer the column. I.e not valid values
 * in it.
 * @param columnName
 *   the name of the column
 */
final case class UnableToInferValidation(columnName: String) extends InferrerInfo
object UnableToInferValidation {
  implicit val jsonDecoder: JsonDecoder[UnableToInferValidation] = DeriveJsonDecoder.gen[UnableToInferValidation]
  implicit val jsonEncoder: JsonEncoder[UnableToInferValidation] = DeriveJsonEncoder.gen[UnableToInferValidation]
}

/**
 * Keep track of original column name. Useful to manage column renames
 * @param name
 *   the column name
 * @param position
 *   the position in case of CSV, TSV or other positional sources
 */
final case class OriginalColumnName(name: String, position: Option[Int] = None) extends InferrerInfo
object OriginalColumnName {
  implicit val jsonDecoder: JsonDecoder[OriginalColumnName] = DeriveJsonDecoder.gen[OriginalColumnName]
  implicit val jsonEncoder: JsonEncoder[OriginalColumnName] = DeriveJsonEncoder.gen[OriginalColumnName]
}

//add in zio.metamodel.registry.SchemaTrait in case of new entity
