/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml.mnist.linear
import me.ooon.base.test.BaseSpec

/**
  * LinerClassifiersModelSpec
  *
  * @author zhaihao
  * @version 1.0 2018-12-06 15:44
  */
class LinerClassifiersModelSpec extends BaseSpec {
  "train" - {
    "读取 label 数据" in {
      val idx1 = new IDX1UByteFile("data/mnist/")
    }
  }

  "test" - {}

  "predict" - {}
}
