/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec
import org.apache.spark.sql.types.TimestampType

/**
  * TimeSpec
  *
  * @author zhaihao
  * @version 1.0 2017/10/11 下午4:37
  */
class TimeSpec extends SparkBaseSpec {

  "string转Timestamp" in {
    import org.apache.spark.sql.functions._
    import spark.implicits._
    val rdd = spark.sparkContext.parallelize(Seq("2017-10-11 12:00:00,tom,10")).toDS()
    val df1 = rdd
      .map(line => {
        val ws = line.split(",")
        (ws(0), ws(1), ws(2).toInt)
      })
      .select(
        unix_timestamp($"_1", "yyyy-MM-dd HH:mm:ss").cast(TimestampType).as("time"),
        $"_2".as("name"),
        $"_3".as("score")
      )
    df1.show()
    df1.printSchema()
  }
}
