/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * BooleanSpec
  *
  * @author zhaihao
  * @version 1.0 2017/11/22 11:35
  */
class BooleanSpec extends SparkBaseSpec {

  "对Boolean列过滤" in {
    import spark.implicits._
    val df = List(
      (1, "a", true),
      (1, "b", true),
      (1, "c", false)
    ).toDF("id", "name", "b")

    df.where($"b").show()
    df.where(!$"b").show()
  }

}
