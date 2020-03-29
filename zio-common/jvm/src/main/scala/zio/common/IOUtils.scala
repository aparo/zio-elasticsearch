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

import java.io._
import java.net.URL
import java.nio.file.{ Files, Path }

import scala.io.Codec

/**
 * IO utilities.
 */
object IOUtils {

  /**
   * Read the given stream into a byte array.
   *
   * Closes the stream.
   */
  private def readStream(stream: InputStream): Array[Byte] =
    try {
      val buffer = new Array[Byte](8192)
      var len = stream.read(buffer)
      val out = new ByteArrayOutputStream() // Doesn't need closing
      while (len != -1) {
        out.write(buffer, 0, len)
        len = stream.read(buffer)
      }
      out.toByteArray
    } finally closeQuietly(stream)

  /**
   * Read the file into a byte array.
   */
  def readFile(file: Path): Array[Byte] =
    readStream(Files.newInputStream(file))

  /**
   * Read the given stream into a String.
   *
   * Closes the stream.
   */
  def readStreamAsString(stream: InputStream)(implicit codec: Codec): String =
    new String(readStream(stream), codec.name)

  /**
   * Read the URL as a String.
   */
  def readUrlAsString(url: URL)(implicit codec: Codec): String =
    readStreamAsString(url.openStream())

  /**
   * Read the file as a String.
   */
  def readFileAsString(file: Path)(implicit codec: Codec): String =
    readStreamAsString(Files.newInputStream(file))

  /**
   * Close the given closeable quietly.
   *
   * Logs any IOExceptions encountered.
   */
  def closeQuietly(closeable: Closeable) =
    try {
      if (closeable != null) {
        closeable.close()
      }
    } catch {
      case e: IOException => println(s"Error closing stream: $e")
    }
}
