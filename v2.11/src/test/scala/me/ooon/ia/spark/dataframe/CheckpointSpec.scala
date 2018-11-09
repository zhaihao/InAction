/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe
import me.ooon.base.test.TestFolderLike
import me.ooon.ia.spark.SparkBaseSpec

/**
  * CheckpointSpec
  *
  * 是否切断血缘关系
  *
  * @author zhaihao
  * @version 1.0 2018/11/9 17:01
  */
class CheckpointSpec extends SparkBaseSpec with TestFolderLike {
  "eager" in {
    import spark.implicits._
    sc.setCheckpointDir(testFolder.getPath)
    val df = List("1", "2", "3").toDF("id")

    val df2 = df.checkpoint(true)

    println("---true---")
    println(df.rdd.toDebugString)
    println(df2.rdd.toDebugString)

  }

  "no-eager" in {
    import spark.implicits._
    sc.setCheckpointDir(testFolder.getPath)
    val df = List("1", "2", "3").toDF("id")

    val df2 = df.checkpoint(false)
    println("---false---")
    println(df.rdd.toDebugString)
    println(df2.rdd.toDebugString)
    println()
  }
}
