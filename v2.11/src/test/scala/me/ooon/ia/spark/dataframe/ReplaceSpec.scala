/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * ReplaceSpec
  *
  * @author zhaihao
  * @version 1.0 2017/11/22 14:26
  */
class ReplaceSpec extends SparkBaseSpec {

  "单引号字符" in {
    import spark.implicits._
    val df = List("a'b'c").toDF("name")
    df.createOrReplaceTempView("tt")
    spark.sql("select regexp_replace(name,'\\'', '') from tt").show
  }

  "星号字符" in {
    import spark.implicits._
    val df = List("abc***", "****").toDF("name")
    df.createOrReplaceTempView("tt")
    spark.sql("select regexp_replace(name,'\\\\*', '') from tt").show

    import org.apache.spark.sql.functions._
    df.withColumn("name", regexp_replace($"name", "\\*", "")).show()
  }

  "加号替换" in {
    import spark.implicits._
    val df = List("+8615613727352").toDF("phone")
    df.createOrReplaceTempView("tt")
    spark.sql("select regexp_replace(phone,'\\\\+86', '') from tt").show
  }
}
