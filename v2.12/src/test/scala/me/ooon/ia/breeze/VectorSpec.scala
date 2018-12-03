/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.breeze
import breeze.linalg.DenseVector
import me.ooon.base.test.BaseSpec
import me.ooon.base.syntax.id._

/**
  * VectorSpec
  *
  * @author zhaihao
  * @version 1.0 2018-12-04 14:04
  */
class VectorSpec extends BaseSpec {

  "vector map" in {
    val a = DenseVector(1, 2, 3, 4)
    a.map(_ * 2) ==> DenseVector(2, 4, 6, 8)
    a            ==> DenseVector(1, 2, 3, 4)
  }
}
