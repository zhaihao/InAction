/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * CollectSpec
  *
  * @author zhaihao
  * @version 1.0 2017/11/20 17:03
  */
class CollectSpec extends SparkBaseSpec {

  "去重转字符串" in {
    import spark.implicits._

    val df = List(
      ("tom", 1),
      ("tom", 2),
      ("tom", 1),
      ("tom", 3),
      ("lucy", 1),
      ("lucy", 1),
      ("lucy", 1),
      ("lucy", 1)
    ).toDF("name", "fi")

    df.createOrReplaceTempView("tt")
    spark.sql("""
        |select
        |  name,
        |  regexp_replace(format_string("%s",collect_set(fi)),'[\\[\\]]','')
        |from tt
        |group by name
      """.stripMargin).show
  }

}
