/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * CountSpec
  *
  * @author zhaihao
  * @version 1.0 2017/11/21 11:19
  */
class CountSpec extends SparkBaseSpec {

  "count 加条件" in {
    import spark.implicits._

    val df = List(
      ("a", 1, 10),
      ("b", 2, 11),
      ("c", 1, 13),
      ("d", 1, 14)
    ).toDF("name", "sex", "age")

    df.createOrReplaceTempView("tt")
    // error
    spark.sql("""
        |select
        |  count(age>11)
        |from tt
      """.stripMargin).show()

    spark.sql("""
        |select
        |  count(if(age>11,1,null)),
        |  count(case when sex=1 then 1 else null end)
        |from tt
      """.stripMargin).show
  }
}
