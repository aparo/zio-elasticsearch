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

package zio.common

import java.util.{ Collections, Map => JavaMap }

import scala.jdk.CollectionConverters._

trait EnvHacker {

  /**
   * Portable method for setting env vars on both *nix and Windows.
   * @see
   *   http://stackoverflow.com/a/7201825/293064
   */
  def setEnv(newEnvs: (String, String)*): Unit = {
    val newEnv = newEnvs.toMap
    try {
      val processEnvironmentClass =
        Class.forName("java.lang.ProcessEnvironment")
      val theEnvironmentField =
        processEnvironmentClass.getDeclaredField("theEnvironment")
      theEnvironmentField.setAccessible(true)
      val env =
        theEnvironmentField.get(null).asInstanceOf[JavaMap[String, String]]
      env.putAll(newEnv.asJava)
      val theCaseInsensitiveEnvironmentField =
        processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
      theCaseInsensitiveEnvironmentField.setAccessible(true)
      val cienv = theCaseInsensitiveEnvironmentField.get(null).asInstanceOf[JavaMap[String, String]]
      cienv.putAll(newEnv.asJava)
    } catch {
      case e: NoSuchFieldException =>
        try {
          val classes = classOf[Collections].getDeclaredClasses()
          val env = System.getenv()
          for (cl <- classes)
            if (cl.getName() == "java.util.Collections$UnmodifiableMap") {
              val field = cl.getDeclaredField("m")
              field.setAccessible(true)
              val obj = field.get(env)
              val map = obj.asInstanceOf[JavaMap[String, String]]
              map.clear()
              map.putAll(newEnv.asJava)
            }
        } catch {
          case e2: Exception => e2.printStackTrace()
        }

      case e1: Exception => e1.printStackTrace()
    }
  }
}
