/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * ExplodeSpec
  *
  * explode 的笛卡尔积只在原纪录内做
  *
  * @author zhaihao
  * @version 1.0 2017/4/13 10:46
  */
class ExplodeSpec extends SparkBaseSpec {

  import spark.implicits._

  val df = Seq(
    User(1, Array("Tom", "Tomi"), Array("sing", "dance")),
    User(2, Array("Lucy", "Lulu"), Array("read", "write"))
  ).toDF()

  df.show(false)
  df.createOrReplaceTempView("t1")

  "explode 一个字段" in {
    spark.sql("select * from t1 lateral view explode(name) tab as ns").show(false)
  }

  "explode 两个字段" in {
    spark
      .sql(
        "select * from t1 lateral view explode(name) tab as ns lateral view explode(fav) tab as fs")
      .show(false)
  }
}

case class User(
    id:   Int,
    name: Array[String],
    fav:  Array[String]
)
