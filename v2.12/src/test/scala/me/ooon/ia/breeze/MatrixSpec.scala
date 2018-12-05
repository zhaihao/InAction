/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.breeze
import breeze.linalg.DenseMatrix
import me.ooon.base.test.BaseSpec

/**
  * MatrixSpec
  *
  * @author zhaihao
  * @version 1.0 2018-12-05 11:05
  */
class MatrixSpec extends BaseSpec {

  val A = DenseMatrix(
    (1, 2, 3),
    (1, 2, 1)
  )

  val B = DenseMatrix(
    (2, 1),
    (3, 1),
    (0, 0)
  )

  "乘法" in {
    A * B ==> DenseMatrix(
      (8, 3),
      (8, 3)
    )
  }
}
