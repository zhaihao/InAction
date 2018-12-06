/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.mllib.algorithm.kmeans

import java.io.{File, PrintWriter}

import me.ooon.ia.spark.ml.MLDataFile
import me.ooon.ia.spark.{SparkBaseSpec, TmpFileSpec}
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors

/**
  * WholesaleCustomerData
  *
  * [[https://www.ibm.com/developerworks/cn/opensource/os-cn-spark-practice4/index.html]]
  * [[https://ooon.me/2017/08/k-means/]]
  *
  * 在本文中，我们所用到目标数据集是来自 UCI Machine Learning Repository 的 [Wholesale customer Data Set]
  * (http://archive.ics.uci.edu/ml/datasets/Wholesale+customers)。
  * UCI 是一个关于机器学习测试数据的下载中心站点，里面包含了适用于做聚类，分群，回归等各种机器学习问题的数据集。
  * Wholesale customer Data Set 是引用某批发经销商的客户在各种类别产品上的年消费数。
  * 为了方便处理，本文把原始的 CSV 格式转化成了两个文本文件，分别是训练用数据和测试用数据。
  *
  * @author zhaihao
  * @version 1.0 2018/8/6 14:39
  */
class WholesaleCustomerData extends SparkBaseSpec with TmpFileSpec {

  val dataFile = MLDataFile("data/Wholesale customers data.csv")

  val rdd = sc.textFile(dataFile.getPath)
    .filter(line => if (line != null && line.contains("Channel")) false else true)
    .map(_.split(",").map(_.toDouble))
    .map(Vectors.dense)
    .cache()

  "k-means 给顾客分类" in {

    val arr        = rdd.randomSplit(Array(0.8, 0.2))
    val train_data = arr(0)
    val test_data  = arr(1)

    /**
      * data 训练数据
      * k 期望聚类的个数
      * maxIterations 最大迭代次数
      * 初始 K 点的选择方式
      * random seed 集群随机初始化
      */
    val kMeansModel = KMeans.train(data = train_data,
                 k = 8,
                 maxIterations = 30,
                                   initializationMode = "k-means||",
                 seed = 1L)
    println("Cluster Number: " + kMeansModel.clusterCenters.length) //聚类个数

    println("Cluster Centers Information Overview: ")
    var clusterIndex = 0
    kMeansModel.clusterCenters.foreach(x => {
      println("Center Point of Cluster " + clusterIndex + ":") // 中心点
      println(x)
      clusterIndex += 1
    })

    test_data
      .collect()
      .foreach(point => {
        val i = kMeansModel.predict(point) // 预测的分类
        println("The data " + point.toString + " belongs to cluster " + i)
      })
  }

  "合理选择 k 的个数" in {

    val res = (3 to 40).map(_k => {
      val kMeansModel = KMeans.train(data = rdd, k = _k, maxIterations = 30,
                                     initializationMode = "k-means||", seed = 1L)
      val d = kMeansModel.computeCost(rdd) / 10E9
      println((_k, d))
      (_k, d)
    })

    import vegas._
    val plot = Vegas(name = "test1")
      .withXY(res)
      .encodeX(field = "x", dataType = Nominal, title = "k")
      .encodeY(field = "y", dataType = Quantitative, title = "cost")
      .mark(Line)

    val view = plot.html.pageHTML()

    val pw = new PrintWriter(new File("vegas.html"))
    pw.write(view)
    pw.close()

    // 根据图示 选择 k=8 比较合适
  }
}
