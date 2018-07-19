/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * MulCollectSpec
  *
  * @author zhaihao
  * @version 1.0 2018/7/19 17:29
  */
class MulCollectSpec extends SparkBaseSpec {

  import spark.implicits._

  val df = Seq(
    (1, 11, 111),
    (3, 33, 333),
    (4, 44, 444),
    (2, 22, 222),
    (5, 55, 555),
    (6, 66, 666)
  ).toDF("f1", "f2", "f3")

  // 不保证一定是这样，集群环境下未做测试，task失败的情况下也需要测试
  "三个 list 内的元素顺序是一致的" in {

    df.repartition(6).createOrReplaceTempView("t")

    spark.sql("select collect_list(f1),collect_list(f2),collect_list(f3) from t").show(false)
  }

  "三个 set 内的元素顺序不是一致的" in {
    df.repartition(6).createOrReplaceTempView("t")

    spark.sql("select collect_set(f1),collect_set(f2),collect_set(f3) from t").show(false)
  }
}
