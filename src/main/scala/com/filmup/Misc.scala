package com.filmup

import scala.util.Random

/**
  * Misc helper classes and methods
  */
object Misc {
  val alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  def readFile(loc: String): String = {
    val source = io.Source.fromFile(loc)
    source.getLines mkString "\n"
  }

  def randomString(len: Int): String = {
    val size = alpha.length
    (1 to len).map(x => alpha(Random.nextInt.abs % size)).mkString
  }
}
