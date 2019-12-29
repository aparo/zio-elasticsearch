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

package zio.schema
import io.circe.derivation.annotations.JsonCodec

@JsonCodec
sealed trait InferrerInfo

/**
 * It tracks if the column is renamed due to other column have the same name
 * @param oldName old column anme
 * @param newName new column name
 * @param position the position in case of CSV, TSV or other positional sources
 */

@JsonCodec
case class ColumnRenameValidation(oldName: String, newName: String, position: Option[Int] = None) extends InferrerInfo

/**
 * It tracks if the system was unable to infer the column. I.e not valid values in it.
 * @param columnName the name of the column
 */
@JsonCodec
case class UnableToInferValidation(columnName: String) extends InferrerInfo

/**
 * Keep track of original column name. Useful to manage column renames
 * @param name the column name
 * @param position the position in case of CSV, TSV or other positional sources
 */
@JsonCodec
case class OriginalColumnName(name: String, position: Option[Int] = None) extends InferrerInfo

//add in zio.metamodel.registry.SchemaTrait in case of new entity
