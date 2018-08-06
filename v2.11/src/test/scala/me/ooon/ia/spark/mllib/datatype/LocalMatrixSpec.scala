/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.mllib.datatype

import org.apache.spark.mllib.linalg.{Matrices, Matrix, SparseMatrix}
import org.scalatest.FreeSpec

/**
  * LocalMatrixSpec
  * local matrix 的元素个数是 [[Int.MaxValue]], 元素的值是[[Double]]类型
  *
  * local point,local matrix,labeled point 单个数据都是存储在一台机器上
  * distributed matrix 的一个矩阵是存储在多台机器上的
  *
  * @author zhaihao
  * @version 1.0 2017/5/4 18:18
  */
class LocalMatrixSpec extends FreeSpec {
  "创建稠密矩阵" in {
    val dm = Matrices.dense(3, 2, Array(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
    println(s"dm = \n$dm")
  }

  "创建稀疏矩阵" in {
    val sm: Matrix = Matrices.sparse(3, 2, Array(0, 1, 3), Array(0, 2, 1), Array(9, 6, 8))
    println(s"sm = \n$sm")
  }

  "稀疏矩阵和稠密矩阵可以互相转换" in {
    val sm: Matrix = Matrices.sparse(3, 2, Array(0, 1, 3), Array(0, 2, 1), Array(9, 6, 8))
    println(s"sm = \n$sm")
    println(s"dm = \n${sm.asInstanceOf[SparseMatrix].toDense}")
  }
}
