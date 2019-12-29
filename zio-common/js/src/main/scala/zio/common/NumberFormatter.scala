/*
 * Copyright 2019 - NTTDATA Italia S.P.A. All Rights Reserved.
 */

package zio.common

class NumberFormatter() {

  def format(i: Int): String = {
    i.toString.grouped(3).toList.mkString(".")
  }

  def format(i: Long): String = {
    i.toString.grouped(3).toList.mkString(".")
  }

  def format(i: Double): String = {
    i.toString.grouped(3).toList.mkString(".")
  }
}
