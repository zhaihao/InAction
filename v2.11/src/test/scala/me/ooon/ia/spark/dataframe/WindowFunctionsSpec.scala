/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * WindowFunctionsSpec
  *
  * @author zhaihao
  * @version 1.0 2017/11/6 上午11:41
  */
class WindowFunctionsSpec extends SparkBaseSpec {

  import org.apache.spark.sql.expressions.Window
  import org.apache.spark.sql.functions._
  import spark.implicits._

  "Simple Moving Average" in {
    val df = List(
      ("site1", "2017-01-01", 50),
      ("site1", "2017-01-02", 45),
      ("site1", "2017-01-03", 55),
      ("site2", "2017-01-01", 25),
      ("site2", "2017-01-02", 29),
      ("site2", "2017-01-03", 27)
    ).toDF("site", "date", "user_cnt")

    df.createOrReplaceTempView("t")

    val w = Window.partitionBy("site").orderBy("date").rowsBetween(-1, 1)
    df.withColumn("movingAvg", avg(df("user_cnt")).over(w)).show

    spark.sql("""
        |SELECT site,date,user_cnt,
        | avg(user_cnt) over(partition by site order by date rows between 1 preceding and 1 following) as movingAvg
        |FROM t
        |""".stripMargin).show()
  }

  "rank" in {

    val df = List(
      ("site1", "2017-01-01", 50),
      ("site1", "2017-01-02", 45),
      ("site1", "2017-01-03", 55),
      ("site2", "2017-01-01", 25),
      ("site2", "2017-01-02", 29),
      ("site2", "2017-01-03", 27)
    ).toDF("site", "date", "user_cnt")

    df.createOrReplaceTempView("t")
    val w = Window.partitionBy("site").orderBy("user_cnt")
    df.withColumn("rank", rank.over(w))
      .show()
  }

  "开窗函数分组求和" in {
    val df = List(
      (1, "Tom", "男"),
      (1, "Lucy", "女"),
      (1, "Lily", "女"),
      (2, "Katy", "女"),
      (2, "Jim", "男")
    ).toDF("class", "name", "sex")

    df.createOrReplaceTempView("tt")
    spark.sql("""
        |select
        |  class,
        |  name,
        |  sex,
        |  count(1) over (partition by class,sex)
        |  from tt
      """.stripMargin).show
  }

  "保留女生记录，求出女生所在班级的总人数，女生数" in {
    val df = List(
      (1, "Tom", "男"),
      (1, "Lucy", "女"),
      (1, "Lily", "女"),
      (2, "Katy", "女"),
      (2, "Jim", "男")
    ).toDF("class", "name", "sex")

    df.createOrReplaceTempView("tt")
    spark.sql("""
        |select
        |  class,
        |  name,
        |  sex,
        |  ct2 as total,
        |  ct1 as ct_f
        |from
        |(select
        |  *,
        |  count(1) over (partition by class,sex) as ct1,
        |  count(1) over (partition by class) as ct2
        |from tt)
        |where sex = '女'
      """.stripMargin).show
  }

  "填充上一个月的值，取最新一个月的记录" in {
    val df = List(
      ("site1", "2017-01-01", 50),
      ("site1", "2017-01-02", 45),
      ("site1", "2017-01-03", 55),
      ("site2", "2017-01-05", 25),
      ("site2", "2017-01-06", 29),
      ("site2", "2017-01-07", 27)
    ).toDF("site", "date", "user_cnt")

    df.createOrReplaceTempView("tt")

    // last 不行，需要使用 max
    spark.sql("""
        |SELECT
        |  site,
        |  date,
        |  user_cnt,
        |  lag(user_cnt,1,0) over (partition by site order by date) AS last_user_cnt,
        |  max(date) over (partition by site ) AS current_month1,
        |  last(date) over (partition by site order by date) AS current_month2
        |FROM tt
        |ORDER BY site,date
      """.stripMargin).show
  }
}
