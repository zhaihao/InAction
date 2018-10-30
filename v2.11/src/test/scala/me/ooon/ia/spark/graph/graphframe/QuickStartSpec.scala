/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.graph.graphframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * QuickStartSpec
  *
  * @author zhaihao
  * @version 1.0 2018/10/10 10:36
  */
class QuickStartSpec extends SparkBaseSpec {

  "quick start" - {
    // 创建 GraphFrame

    // 定点，必须有 id 列
    val v = spark
      .createDataFrame(
        List(
          ("a", "Alice", 34),
          ("b", "Bob", 36),
          ("c", "Charlie", 30)
        ))
      .toDF("id", "name", "age")

    // 边，必须有 src dst 列
    val e = spark
      .createDataFrame(
        List(
          ("a", "b", "friend"),
          ("b", "c", "follow"),
          ("c", "b", "follow")
        ))
      .toDF("src", "dst", "relationship")

    import org.graphframes.GraphFrame
    val g = GraphFrame(v, e)

    "查看定点 frame" in {
      g.vertices.show()
    }

    "查看边 frame" in {
      g.edges.show()
    }

    "查询入度" in {
      g.inDegrees.show()
    }

    "查询出度" in {
      g.outDegrees.show()
    }

    "查询 follow 关系数量" in {
      val count = g.edges.filter("relationship = 'follow'").count()
      println(count)
    }

    "执行 PageRank 计算" in {
      val pr = g.pageRank.resetProbability(0.01).maxIter(3).run()

      pr.vertices.show()
      pr.edges.show()
    }
  }
}
