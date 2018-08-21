/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * AddColumnSpec
  *
  * @author zhaihao
  * @version 1.0 2017/4/20 11:29
  */
class AddColumnSpec extends SparkBaseSpec {

  import spark.implicits._

  val df = Seq((1, "a"), (2, "b"), (3, "c")).toDF("id", "name")

  "添加一个常量值" in {
    import org.apache.spark.sql.functions._
    df.withColumn("age", lit(10)).show()
  }

  "添加一个常量array" in {
    import org.apache.spark.sql.functions._
    df.withColumn("loves", array(lit(1), lit(2))).show
  }

  "添加一个常量 map" in {
    import org.apache.spark.sql.functions._
    df.withColumn("fav", map(lit("key1"), lit(1), lit("key2"), lit(2))).show(false)
  }

  "添加一个其他列计算出来的值" in {
    df.withColumn("age", $"id" + 1).show()
  }

  "添加一个其他列经过特殊函数算出来的值" in {

    val isExt = (a: Int) => if (a % 2 == 0) 1 else 2
    import org.apache.spark.sql.functions._
    val isExtF = udf(isExt)

    df.withColumn("isExt", isExtF($"id")).show
  }
}
