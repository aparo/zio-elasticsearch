/*
 * Copyright 2019-2020 Alberto Paro
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

import java.net.{JarURLConnection, URI, URL, URLConnection}
import java.io.File
import java.util.zip.ZipFile
import scala.IllegalArgumentException
import sun.net.www.protocol.file.FileURLConnection

/**
  * Provide resources helpers
  */
object Resources {

  /**
    * Returns a {@code URL} pointing to {@code resourceName} if the resource is
    * found using the Thread#getContextClassLoader() context class
    * loader. In simple environments, the context class loader will find
    * resources from the class path. In environments where different threads can
    * have different class loaders, for example app servers, the context class
    * loader will typically have been set to an appropriate loader for the
    * current thread.
    *
    * <p>In the unusual case where the context class loader is null, the class
    * loader that loaded this class ({@code Resources}) will be used instead.
    *
    * throws scala.IllegalArgumentException if the resource is not found
    */
  def getResource(resourceName: String): URL = {
    val loader = Thread.currentThread.getContextClassLoader
    val url = loader.getResource(resourceName)
    url
  }

  /**
    * Given a {@code resourceName} that is relative to {@code contextClass},
    * returns a {@code URL} pointing to the named resource.
    *
    * throws scala.IllegalArgumentException if the resource is not found
    */
  def getResource(contextClass: Class[_], resourceName: String): URL = {
    val url = contextClass.getResource(resourceName)
    url
  }

  def isDirectory(classLoader: ClassLoader, url: URL) = url.getProtocol match {
    case "file" => new File(url.toURI).isDirectory
    case "jar" => isZipResourceDirectory(url)
    case "zip" => isZipResourceDirectory(url)
    case "bundle" => isBundleResourceDirectory(classLoader, url)
    case _ =>
      throw new IllegalArgumentException(
        s"Cannot check isDirectory for a URL with protocol='${url.getProtocol}'"
      )
  }

  /**
    * Tries to work out whether the given URL connection is a directory or not.
    *
    * Depends on the URL connection type whether it's accurate.  If it's unable to determine whether it's a directory,
    * this returns false.
    */
  def isUrlConnectionADirectory(urlConnection: URLConnection) =
    urlConnection match {
      case file: FileURLConnection => new File(file.getURL.toURI).isDirectory
      case jar: JarURLConnection =>
        if (jar.getJarEntry.isDirectory) {
          true
        } else {
          // JarEntry.isDirectory is rubbish....
          val is = jar.getJarFile.getInputStream(jar.getJarEntry)
          if (is == null) {
            true
          } else {
            is.close()
            false
          }
        }
      case other => false
    }

  /**
    * Close a URL connection.
    *
    * This works around a JDK bug where if the URL connection is to a JAR file, and the entry is a directory, an NPE is
    * thrown.
    */
  def closeUrlConnection(connection: URLConnection): Unit =
    connection match {
      case jar: JarURLConnection =>
        if (!jar.getUseCaches) {
          jar.getJarFile.close()
        }
      case other =>
        other.getInputStream.close()
    }

  private def isBundleResourceDirectory(
      classLoader: ClassLoader,
      url: URL
  ): Boolean = {
    /* ClassLoader within an OSGi container behave differently than the standard classloader.
     * One difference is how getResource returns when the resource's name end with a slash.
     * In a standard JVM, getResource doesn't care of ending slashes, and return the URL of
     * any existing resources. In an OSGi container (tested with Apache Felix), ending slashes
     * refers to a directory (return null otherwise). */

    val path = url.getPath
    val pathSlash = if (path.last == '/') path else path + '/'

    classLoader.getResource(path) != null && classLoader.getResource(pathSlash) != null
  }

  private def isZipResourceDirectory(url: URL): Boolean = {
    val path = url.getPath
    val bangIndex = url.getFile.indexOf("!")

    val startIndex = if (path.startsWith("zip:")) 4 else 0
    val fileUri = path.substring(startIndex, bangIndex)
    val fileProtocol = if (fileUri.startsWith("/")) "file://" else ""
    val absoluteFileUri = fileProtocol + fileUri

    val zipFile: File = new File(URI.create(absoluteFileUri))
    val resourcePath =
      URI.create(path.substring(bangIndex + 1)).getPath.drop(1)
    val zip = new ZipFile(zipFile)

    try {
      val entry = zip.getEntry(resourcePath)
      if (entry.isDirectory) true
      else {
        val stream = zip.getInputStream(entry)
        val isDir = stream == null
        if (stream != null) stream.close()
        isDir
      }
    } finally {
      zip.close()
    }
  }
}
