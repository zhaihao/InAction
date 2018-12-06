/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml.mnist.linear
import java.io.{DataInputStream, File, FileInputStream}
import java.util.zip.GZIPInputStream

/**
  * IDXUByteFile
  *
  * IDX 是一种存储矩阵的格式
  *
  * @author zhaihao
  * @version 1.0 2018-12-06 15:28
  */
class IDXUByteFile(path: String) extends File(path) {
  lazy val stream = new DataInputStream(new GZIPInputStream(new FileInputStream(this)))
}

// label data
class IDX1UByteFile(path: String) extends IDXUByteFile(path) {
  assert(stream.readInt() == 2049) // magic number
  val size    = stream.readInt() // 记录数
  val rows    = stream.readInt() // 行数
  val columns = stream.readInt() // 列数
}

// feature data
class IDX3UByteFile(path: String) extends IDXUByteFile(path) {
  assert(stream.readInt() == 2051) // magic number
}
