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

package zio.common

sealed trait OperativeSystem

object OperativeSystem {
  case object Windows extends OperativeSystem
  case object Linux extends OperativeSystem
  case object MacOsX extends OperativeSystem
}

object SystemAttributes {

  // Constants

  val FileSeparator: String = System.getProperty("file.separator")
  val JavaClassPath: String = System.getProperty("java.class.path")
  val JavaHome: String = System.getProperty("java.home")
  val JavaVendor: String = System.getProperty("java.vendor")
  val JavaVendorUrl: String = System.getProperty("java.vendor.url")
  val JavaVersion: String = System.getProperty("java.version")
  val LineSeparator: String = System.getProperty("line.separator")
  val OSArchitecture: String = System.getProperty("os.arch")
  val OSName: String = System.getProperty("os.name")
  val OSVersion: String = System.getProperty("os.version")
  val PathSeparator: String = System.getProperty("path.separator")
  val UserDirectory: String = System.getProperty("user.dir")
  val UserHome: String = System.getProperty("user.home")
  val UserName: String = System.getProperty("user.name")

  val operativeSystem: OperativeSystem = {
    OSName.take(3).toUpperCase match {
      case "LIN" => OperativeSystem.Linux
      case "MAC" => OperativeSystem.MacOsX
      case "WIN" => OperativeSystem.Windows
      case _ =>
        throw new IllegalStateException(s"cannot determine your operating system family from $OSName")
    }
  }

}
