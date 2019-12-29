/*
 * Copyright 2019 - NTTDATA Italia S.P.A. All Rights Reserved.
 */

package zio.common

import java.util.{UUID => JUUID}

object UUID {
  //TODO from Bytes is broken due to not implementation in scala-js

  def randomBase64UUID(): String = JUUID.randomUUID().toString.replace("-", "")

  def fromString(name: String): String =
    JUUID.fromString(name).toString.replace("-", "")

  def fromBytes(name: Array[Byte]): String =
    JUUID.fromString(new String(name)).toString.replace("-", "")

}
