/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.graphframe.dataframe
import me.ooon.base.test.TestFolderLike
import me.ooon.ia.spark.SparkBaseSpec
import org.graphframes.lib.AggregateMessages

/**
  * UserGuideSpec
  *
  * @author zhaihao
  * @version 1.0 2018/10/10 17:14
  */
class UserGuideSpec extends SparkBaseSpec with TestFolderLike {

  import org.graphframes.GraphFrame
  // Vertex DataFrame
  val v = spark
    .createDataFrame(
      List(
        ("a", "Alice", 34),
        ("b", "Bob", 36),
        ("c", "Charlie", 30),
        ("d", "David", 29),
        ("e", "Esther", 32),
        ("f", "Fanny", 36),
        ("g", "Gabby", 60)
      ))
    .toDF("id", "name", "age")
  // Edge DataFrame
  val e = spark
    .createDataFrame(
      List(
        ("a", "b", "friend"),
        ("b", "c", "follow"),
        ("c", "b", "follow"),
        ("f", "c", "follow"),
        ("e", "f", "follow"),
        ("e", "d", "friend"),
        ("d", "a", "friend"),
        ("a", "e", "friend")
      ))
    .toDF("src", "dst", "relationship")

  val g = GraphFrame(v, e)

  "创建 graph frame" - {
    "根据 Vertex DataFrame 和 Edge DataFrame 创建" in {
      // Create a GraphFrame
      val g1 = GraphFrame(v, e)
      g1.vertices.show()
    }

    "只根据 Edge DataFrame 创建" in {
      val g1 = GraphFrame.fromEdges(e)
      g1.vertices.show()
    }
  }

  "图的基础查询" - {
    "显示 vertex 和 edge" in {
      g.vertices.show()
      g.edges.show()
    }

    "计算入度，出度" in {
      g.inDegrees.show()
      g.outDegrees.show()
    }

    "查询 vertex" in {
      g.vertices.groupBy().min("age").show()
    }

    "查询 edge" in {
      val l = g.edges.filter("relationship = 'follow'").count()
      println(l)
    }
  }

  "主题查询" - {
    "(a)-[e]->(b)" in {
      g.find("(a)-[e]->(b)").show()
      // 等价于
      g.triplets.show()
    }

    "(a)-[e]->(b);(b)-[e2]->(a)" in {
      g.find("(a)-[e]->(b);(b)-[e2]->(a)").show()
    }

    "!(a)-[]->(b)" in {
      g.find("!(a)-[]->(b)").filter("a.id != b.id").show()
    }

    "(a)-[]->(b);(b)-[]-(c)" in {
      g.find("(a)-[]->(b);(b)-[]->(c)").filter("a.id != c.id").show
    }

    "(a)-[ab]->(b); (b)-[bc]->(c); (c)-[cd]->(d)" in {
      val chain4 = g.find("(a)-[ab]->(b); (b)-[bc]->(c); (c)-[cd]->(d)")

      import org.apache.spark.sql.Column
      import org.apache.spark.sql.functions._
      def sumFriends(cnt: Column, relationship: Column): Column =
        when(relationship === "friend", cnt + 1).otherwise(cnt)

      val condition = Seq("ab", "bc", "cd").foldLeft(lit(0)) { (cnt, e) =>
        sumFriends(cnt, col(e)("relationship"))
      }

      chain4.show()

      val chain4With2Friends = chain4.where(condition >= 2)
      chain4With2Friends.show()
    }
  }

  "sub graph 子图" - {

    "vertex and edge filters" in {
      val g1 = g
        .filterVertices("age > 30")
        .filterEdges("relationship = 'friend'")
        .dropIsolatedVertices()

      g1.triplets.show()
    }

    "triplet filters" in {
      val newEdge = g
        .find("(a)-[e]->(b)")
        .filter("e.relationship = 'follow'")
        .filter("a.age < b.age")
        .select("e.*")

      newEdge.show()

      val newGraph = GraphFrame(g.vertices, newEdge)
      newGraph.triplets.show()
    }
  }

  "图算法" - {
    "Breadth-first search (BFS) 宽度优先算法" in {
      val path = g.bfs.fromExpr("name = 'Esther'").toExpr("age = 30").run()
      path.show()
    }

    // 不考虑方向
    "Connected components 连通分量" in {
      sc.setCheckpointDir(testFolder.getPath)
      // component id 相同的是一个 子图
      g.connectedComponents.run().show()
    }

    // 考虑方向
    "Strongly connected component 强连通分量" in {
      g.stronglyConnectedComponents.maxIter(2).run().show()
    }

    // 标签传播算法
    "Label Propagation Algorithm (LPA)" in {
      g.labelPropagation.maxIter(5).run().show()
    }

    "PageRank 算法" - {
      "org.apache.spark.graphx.graph 实现" in {
        g.pageRank.resetProbability(0.15).tol(0.01).run().triplets.show(false)
      }

      "org.apache.spark.graphx.Pregel 实现" in {
        g.pageRank.resetProbability(0.15).maxIter(10).run().triplets.show(false)
      }

      "personalized PageRank" in {
        g.pageRank.resetProbability(0.15).maxIter(10).sourceId("a").run().triplets.show(false)

        // https://github.com/graphframes/graphframes/blob/dc11c4a7c1b94cfe801f8480e6ea5c64064a8b99/src/test/scala/org/graphframes/lib/ParallelPersonalizedPageRankSuite.scala#L79
        // spark 2.2 无法使用
        // spark 2.4 将会修复
        assertThrows[IllegalArgumentException] {
          g.parallelPersonalizedPageRank
            .resetProbability(0.15)
            .maxIter(10)
            .sourceIds(Array("a", "b", "c", "d"))
            .run
        }
      }

    }

    "svd++" in {
      val g1 = org.graphframes.examples.Graphs.ALSSyntheticData()
      g1.triplets.show()
      g1.svdPlusPlus.maxIter(2).run().show()
    }

    "Shortest paths" in {
      g.shortestPaths.landmarks(Seq("a", "d")).run().show(false)
    }

    "Triangle count" in {
      g.triangleCount.run().show(false)
    }

  }

  "Saving and loading GraphFrames" in {
    g.vertices.write.parquet(testFolder.getPath + "/myLocation/vertices")
    g.edges.write.parquet(testFolder.getPath + "/myLocation/edges")

    val v1 = spark.read.parquet(testFolder.getPath + "/myLocation/vertices")
    val e1 = spark.read.parquet(testFolder.getPath + "/myLocation/edges")

    val g1 = GraphFrame(v1, e1)
    g1.triplets.show(false)
  }

  "Message passing via AggregateMessages" in {
    import org.apache.spark.sql.functions._
    val AM       = AggregateMessages
    val msgToSrc = AM.dst("age")
    val msgToDst = AM.src("age")
    // 不包含自身的age
    g.aggregateMessages
      .sendToDst(msgToDst)
      .sendToSrc(msgToSrc)
      .agg(sum(AM.msg).as("summedAge"))
      .show(false)
  }

  "GraphX-GraphFrame conversions" in {
    import org.apache.spark.graphx.Graph
    import org.apache.spark.sql.Row
    val gx: Graph[Row, Row] = g.toGraphX
    assertThrows[UnsupportedOperationException] {
      val g2 = GraphFrame.fromGraphX(gx)
      g2.triplets.show(false)
    }
  }
}
