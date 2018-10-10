/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.graphframe.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * QuickStartSpec
  *
  * @author zhaihao
  * @version 1.0 2018/10/10 10:36
  */
class QuickStartSpec extends SparkBaseSpec {

  "quick start" in {
    val v = spark
      .createDataFrame(
        List(
          ("a", "Alice", 34),
          ("b", "Bob", 36),
          ("c", "Charlie", 30)
        ))
      .toDF("id", "name", "age")

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

    g.inDegrees.show()

    // Query: Count the number of "follow" connections in the graph.
    println(g.edges.filter("relationship = 'follow'").count())

    // Run PageRank algorithm, and show results.
    val results = g.pageRank.resetProbability(0.01).maxIter(10).run()
    results.vertices.select("id", "pagerank").show()
  }
}
