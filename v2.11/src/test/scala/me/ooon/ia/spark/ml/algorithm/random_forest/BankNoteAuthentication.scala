/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml.algorithm.random_forest

import me.ooon.ia.spark.SparkBaseSpec
import me.ooon.ia.spark.ml.MLDataFile
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

/**
  * BankNoteAuthentication
  *
  * 本文所使用的测试数据集来自 UCI 的 banknote authentication data set ，
  * [[http://archive.ics.uci.edu/ml/datasets/banknote+authentication]]
  * 这是一个从纸币鉴别过程中的图片里提取的数据集，总共包含五个列，前 4 列是指标值 (连续型)，最后一列是真假标识。
  *
  * 四列依次是小波变换图像的方差，小波变换图像的偏态，小波变换图像的峰度，图像熵，类别标签。其实读者并不需要知道什么是小波变换及其相关改变，
  * 只需要知道这是四个特征指标的值，我们将根据这些指标训练模型使用模型预测类别。对于该数据集的更多信息，读者可以参考 UCI 官网的描述。
  *
  * @author zhaihao
  * @version 1.0 2018/8/6 11:54
  */
class BankNoteAuthentication extends SparkBaseSpec {

  "使用RandomForest辨别纸币真假" in {
    val schema = StructType(
      Array(
        StructField("f1", DoubleType, nullable    = false),
        StructField("f2", DoubleType, nullable    = false),
        StructField("f3", DoubleType, nullable    = false),
        StructField("f4", DoubleType, nullable    = false),
        StructField("label", DoubleType, nullable = false)
      ))

    val dataFile = MLDataFile("data/data_banknote_authentication.txt")

    val df = spark.read
      .option("header", "true")
      .schema(schema)
      .csv(dataFile.getPath)

    val Array(train_data, test_data) = df.randomSplit(Array(0.8, 0.2))

    val vectorAssembler =
      new VectorAssembler().setInputCols(Array("f1", "f2", "f3", "f4")).setOutputCol("features")

    val randomForestClassifier = new RandomForestClassifier()
      .setLabelCol("label")
      .setFeaturesCol("features")
      .setNumTrees(5)

    val pipeline = new Pipeline().setStages(Array(vectorAssembler, randomForestClassifier))
    val model    = pipeline.fit(train_data)

    val predictionResult = model.transform(test_data)
    predictionResult.show(100, truncate = false)

    // rawPrediction 代表各个计算分类的结果
    // probability 根据 rawPrediction 求出来的 概率
    // prediction 预测结果

    // 计算正确率
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    val d = evaluator.evaluate(predictionResult)
    println(s"Testing Success Rate: $d")
  }
}
