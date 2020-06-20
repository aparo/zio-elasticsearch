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

package protocol {

  import java.io.{ ByteArrayOutputStream, DataOutputStream }

  import scala.annotation.implicitNotFound

  @implicitNotFound(msg = "Could not find a Writes for ${T}")
  trait Writes[T] {

    def writes(value: T): Array[Byte]
  }

  class DataOutputStreamWrites[T](writeValue: (DataOutputStream, T) => Unit) extends Writes[T] {

    def writes(value: T): Array[Byte] = {
      val bos = new ByteArrayOutputStream
      val dos = new DataOutputStream(bos)
      writeValue(dos, value)
      dos.flush()
      val byteArray = bos.toByteArray
      bos.close()
      byteArray
    }
  }

  object defaults {

    implicit object WritesString extends Writes[String] {
      def writes(value: String) = value.getBytes("UTF-8")
    }

    implicit object WritesLong extends DataOutputStreamWrites[Long](_.writeLong(_))

    implicit object WritesInt extends DataOutputStreamWrites[Int](_.writeInt(_))

    implicit object WritesShort extends DataOutputStreamWrites[Short](_.writeShort(_))

  }

}

package crypto {

  import javax.crypto.Cipher
  import javax.crypto.spec.SecretKeySpec

  import zio.common.protocol.Writes

  trait Encryption {
    def encrypt(secret: String, dataBytes: Array[Byte]): Array[Byte]

    def encrypt(secret: String, data: String): Array[Byte] =
      encrypt(secret, data.getBytes)

    def decrypt(secret: String, codeBytes: Array[Byte]): Array[Byte]

    def decrypt(secret: String, data: String): Array[Byte] =
      decrypt(secret, data.getBytes)

    def encrypt[T: Writes](secret: String, data: T): Array[Byte] =
      encrypt(secret, implicitly[Writes[T]].writes(data))
  }

  class JavaCryptoEncryption(
    algorithmName: String,
    cipher: String = "CBC",
    padding: String = "PKCS5Padding"
  ) extends Encryption {

    def encrypt(secret: String, bytes: Array[Byte]): Array[Byte] = {
      val secretKey =
        new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
      val encipher = Cipher.getInstance(s"$algorithmName/$cipher/$padding")
      encipher.init(Cipher.ENCRYPT_MODE, secretKey)
      encipher.doFinal(bytes)
    }

    def decrypt(secret: String, bytes: Array[Byte]): Array[Byte] = {
      val secretKey =
        new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
      val encipher = Cipher.getInstance(s"$algorithmName/$cipher/$padding")
      encipher.init(Cipher.DECRYPT_MODE, secretKey)
      encipher.doFinal(bytes)
    }
  }

  object DES extends JavaCryptoEncryption("DES")

  object AES extends JavaCryptoEncryption("AES")

  /*
 *  import org.apache.commons.codec.binary.Base64

  import crypto._
  import protocol.defaults._

  def encodeBase64(bytes: Array[Byte]) = Base64.encodeBase64String(bytes)

  println(encodeBase64(DES.encrypt("01234567", "hoge")))
  //=> vyudTtnBJfs=

  println(encodeBase64(AES.encrypt("hoge", "0123456789012345")))
  //=> QWSouZUMVYMfS86xFyBgtQ==

  println(encodeBase64(DES.encrypt(123L, "01234567")))
  //=> Cqw2ipxTtvIIu122s3wG1w==

  println(encodeBase64(DES.encrypt(123, "01234567")))
  //=> BV+LSCSYmUU=
 * */

}
