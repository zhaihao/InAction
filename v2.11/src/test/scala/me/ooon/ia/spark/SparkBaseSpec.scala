/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark

import me.ooon.base.test.BaseSpec
import org.apache.spark.sql.SparkSession

/**
  * SparkBaseSpec
  *
  * @author zhaihao
  * @version 1.0 2018/5/30 15:34
  */
trait SparkBaseSpec extends BaseSpec {
  val spark = SparkSession.builder().appName("spark-test-spec").master("local[*]").getOrCreate()
  val sc    = spark.sparkContext
  sc.setLogLevel("ERROR")
}
