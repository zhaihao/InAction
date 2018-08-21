/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.rdd.functions.agg

import me.ooon.ia.spark.SparkBaseSpec

/**
  * AggregateByKeySpec
  *
  * @author zhaihao
  * @version 1.0 2018/8/6 15:51
  */
class AggregateByKeySpec extends SparkBaseSpec {

  "aggregateByKey" in {
    val rdd = spark.sparkContext.parallelize(List("abc", "123", "a", "ab", "cd"))
    val res = rdd
      .map(s => (s.length, s))
      .aggregateByKey(Seq.empty[String])(
        (it, s) => it.+:(s),
        (it1, it2) => it1 ++ it2
      )
    res.foreach(println)
  }
}
