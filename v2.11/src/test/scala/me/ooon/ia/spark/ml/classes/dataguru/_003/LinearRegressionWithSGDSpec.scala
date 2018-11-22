/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml.classes.dataguru._003
import com.typesafe.scalalogging.StrictLogging
import me.ooon.ia.spark.SparkBaseSpec
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.syntax.dataset._

/**
  * LinearRegressionWithSGDSpec
  *
  * spark 官方 线性最小二乘法 demo
  *
  * @see [[https://spark.apache.org/docs/latest/mllib-linear-methods.html#linear-least-squares-lasso-and-ridge-regression]]
  * @author zhaihao
  * @version 1.0 2018-11-29 11:30
  */
class LinearRegressionWithSGDSpec extends SparkBaseSpec with StrictLogging {

  "demo" in {
    import spark.implicits._
    val ds: Dataset[LabeledPoint] = spark.read
      .textFile("data/lpsa.data")
      .map(line => {
        val tmp      = line.split(",")
        val label    = tmp(0).toDouble
        val features = Vectors.dense(tmp(1).split("\\s+").map(_.toDouble))
        LabeledPoint(label, features)
      })

    ds.cache()

    logger.info("记录数：" + ds.count())
    val arrayDS = ds.randomSplit(Array(0.8, 0.2))
    val trainDS = arrayDS(0)
    val testDS  = arrayDS(1)
    logger.info("训练集记录数：" + trainDS.count())
    logger.info("测试集记录数：" + testDS.count())

    trainDS.rdd.cache()

    //noinspection ScalaDeprecation
    val linearRegressionModel =
      LinearRegressionWithSGD.train(trainDS.rdd, numIterations = 100, stepSize = 0.001)

    logger.info("model w：" + linearRegressionModel.weights + "," + linearRegressionModel.intercept)

    val testResult = testDS
      .map(sample => {
        val prediction = linearRegressionModel.predict(sample.features)
        (sample.label, prediction)
      })

    testResult.toDF("label", "prediction").log(truncate = 0)

    val rmse =
      math.sqrt(testResult.map { case (l, p) => math.pow(l - p, 2) }.reduce(_ + _) / testDS.count())
    logger.info("rmse: " + rmse)
  }
}
