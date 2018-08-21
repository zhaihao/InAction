/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.{SparkBaseSpec, TmpFileSpec}

/**
  * Distribute
  *
  * @author zhaihao
  * @version 1.0 30/01/2018 17:44
  */
class Distribute extends SparkBaseSpec with TmpFileSpec {

  "distribute by" in {
    import spark.implicits._
    val df = List("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12").toDF("id")

    df.createOrReplaceTempView("t")

    spark.sql("SET spark.sql.shuffle.partitions = 4")
    spark.sql("select * from t DISTRIBUTE BY id%4").write.text(TmpFile)

    // 等价于
    // df.repartition(6, $"id").write.text(TmpFile)
  }
}
