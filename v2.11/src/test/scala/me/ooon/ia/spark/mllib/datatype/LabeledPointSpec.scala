/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.mllib.datatype

import me.ooon.ia.spark.{SparkBaseSpec, TmpFileSpec}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD

/**
  * LabeledPointSpec
  * labeled point 由一个 local vector 与 一个 label 组成，通常用于监督学习
  * label 也是 [[Double]] 类型，所以 labeled point 既可以用于回归也可以用于分类
  *
  * @author zhaihao
  * @version 1.0 2017/5/4 10:57
  */
class LabeledPointSpec extends SparkBaseSpec with TmpFileSpec {

  "用稀疏向量创建 labeled point" in {
    val labeledPoint = LabeledPoint(1.0, Vectors.sparse(3, Array(0, 2), Array(1.0, 3.0)))
    println(s"labeledPoint = $labeledPoint")
  }

  "用稠密向量创建 labeled point" in {
    val labeledPoint = LabeledPoint(0.0, Vectors.dense(1.0, 0.0, 3.0))
    println(s"labeledPoint = $labeledPoint")
  }

  // label index1:value1 index2:value2 ...
  "labeled point 通用的存储格式 LIBSVM" in {
    val lps: RDD[LabeledPoint] = MLUtils.loadLibSVMFile(sc, "data/sample_libsvm_data.txt")
    lps.take(3).foreach(println)
    // 也可以存储
    MLUtils.saveAsLibSVMFile(lps, TmpFile)
  }
}
