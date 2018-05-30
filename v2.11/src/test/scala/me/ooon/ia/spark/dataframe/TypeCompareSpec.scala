/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * TypeCompareSpec
  *
  * @author zhaihao
  * @version 1.0 2018/5/30 15:35
  */
class TypeCompareSpec extends SparkBaseSpec {

  "double 与 int 判等" in {
    spark.sql("select 1=1.0").show

    import spark.implicits._
    val df = Seq(
      ("a", 1, 1.0),
      ("b", 2, 2.1)
    ).toDF("f1", "f2", "f3")
    df.printSchema()
    df.createOrReplaceTempView("t1")
    spark.sql("select f1,(f2=f3) as f4 from t1").show
  }
}
