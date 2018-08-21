/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.mllib.datatype

import me.ooon.orison.base.test.BaseSpec
import org.apache.spark.mllib.linalg.Vectors

/**
  * LocalVectorSpec
  *
  * local vector 是单机上的向量，最大维度支持到[[Int.MaxValue]]，下标从 0 开始，元素类型是[[Double]]
  *
  * @author zhaihao
  * @version 1.0 2018/8/6 15:29
  */
class LocalVectorSpec extends BaseSpec {

  "vector 有两种类型" - {
    "稀疏向量" in {
      // 两种创建方式
      val sparse1 = Vectors.sparse(3, Array(0, 2), Array(1.0, 3.0))
      val sparse2 = Vectors.sparse(3, Seq((0, 1.0), (2, 3.0)))
      assert(sparse1 === sparse2)
      println(s"sv1 = $sparse1")
      println(s"sv2 = $sparse2")
    }

    "稠密向量" in {
      val dense = Vectors.dense(1.0, 0.0, 3.0)
      println(s"dense = $dense")
    }

    "稀疏向量和稠密向量可以互相转换" in {
      val sv = Vectors.sparse(3, Array(0, 2), Array(1.0, 3.0))
      val dv = Vectors.dense(1.0, 0.0, 3.0)
      assert(sv === dv)
      assert(sv.toDense === dv)
      assert(sv === dv.toSparse)
    }
  }
}
