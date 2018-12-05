/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml.classes.coursera.ml

import java.io.{File, PrintWriter}

import breeze.linalg.DenseVector
import com.typesafe.scalalogging.StrictLogging
import me.ooon.base.syntax.id._
import me.ooon.base.test.BaseSpec

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * Week2HomeWorkSpec
  *
  * @author zhaihao
  * @version 1.0 2018-11-30 17:56
  */
class Week2HomeWorkSpec extends BaseSpec with StrictLogging {

  import breeze.linalg._

  "1.Simple Octave/MATLAB function" - {

    "return a 5 x 5 identity matrix" in {
      DenseMatrix.eye[Int](5) |> println
    }
  }

  // mathematica/couresa/ml/homework/week2.nb
  "2.Linear regression with one variable" - {

    "2.2 Gradient Descent" in {

      val ds = Source
        .fromFile("data/coursera/ex1data1.txt")
        .getLines()
        .toList
        .map(_.split(",").map(_.toDouble))
        .map(i => P(i(0), i(1)))

      import breeze.linalg._

      def addColumn(v: DenseVector[Double], a: Double) = DenseVector(v.data.+:(a))

      def compute(theta: DenseVector[Double], samples: Seq[Sample]) = {
        val res = samples.foldLeft((0L, 0.0, DenseVector.zeros[Double](theta.length)))(
          (t, s) => { // (count,cost,gradient)
            val diff     = theta.dot(s.xv) - s.y
            val loss     = math.pow(diff, 2) / 2 // (h𝛉(x) - y)^2
            val gradient = s.xv * diff // (h𝛉(x) - y) * x
            (t._1 + 1, t._2 + loss, t._3 + gradient)
          }
        )

        (res._1, res._2 / res._1, res._3 * (1.0 / res._1))
      }

      def predict(theta: DenseVector[Double], xv: DenseVector[Double]) = theta.dot(xv)

      val dataset = ds
        .map(p => Sample(DenseVector(p.x), p.y))
        .map(s => s.copy(xv = addColumn(s.xv, 1.0))) // add first column 1.0

      val cost = compute(DenseVector.zeros[Double](2), dataset)._2
      // 正确答案是 32.07
      logger.info("𝛉 = [0.0,0.0], cost = " + cost)

      val iterations = 1500
      val alpha      = 0.01
      var theta      = DenseVector(0.0, 0.0)

      val history = ArrayBuffer.empty[Iteration]

      for (i <- 1 to iterations) {
        val (_, cost, gradient) = compute(theta, dataset)
        val iter                = Iteration(i, theta, cost)
        history += iter
        logger.info(iter.toString)
        if (i != iterations) theta = theta - (gradient * alpha)
      }

      logger.info("final theta: " + theta)

      logger.info(predict(theta, DenseVector(1.0, 3.5)).toString)
      logger.info(predict(theta, DenseVector(1.0, 7.0)).toString)

      val pw = new PrintWriter(new File("log/history.csv"))
      history.foreach(iter => pw.println(iter.toCSV))
      pw.close()

      logger.info("mathematica 查看拟合函数，iter-cost，theta-cost 图")
    }
  }

  "3.Linear regression with multiple variables" in {
    val ds = Source
      .fromFile("data/coursera/ex1data2.txt")
      .getLines()
      .toList
      .map(line => {
        val t = line.split(",").map(_.toDouble)
        Sample(DenseVector(t(0), t(1)), t(2))
      })

    def addColumn(v: DenseVector[Double], a: Double) = DenseVector(v.data.+:(a))

    def compute(theta: DenseVector[Double], samples: Seq[Sample]) = {
      val res = samples.foldLeft((0L, 0.0, DenseVector.zeros[Double](theta.length)))(
        (t, s) => { // (count,cost,gradient)
          val diff     = theta.dot(s.xv) - s.y
          val loss     = math.pow(diff, 2) / 2 // (h𝛉(x) - y)^2
          val gradient = s.xv * diff // (h𝛉(x) - y) * x
          (t._1 + 1, t._2 + loss, t._3 + gradient)
        }
      )

      (res._1, res._2 / res._1, res._3 * (1.0 / res._1))
    }

    def predict(theta: DenseVector[Double], xv: DenseVector[Double]) = theta.dot(xv)

    def scaleFuture(scale: Array[(Double, Double)], xv: DenseVector[Double], size: Int) = {
      for (i <- 0 until size) {
        xv(i) = (xv(i) - scale(i)._2) / scale(i)._1
      }
      xv
    }

    def meanNormalization(ds: Seq[Sample]) = {
      val size        = ds.size
      val featureSize = ds.head.xv.length
      //max-min,sum/size
      val scale: Array[(Double, Double)] = ds
        .map(_.xv.data)
        .foldLeft(ArrayBuffer.fill(featureSize)((0.0, 0.0, 0.0)))((t3, x) => {
          for (i <- 0 until featureSize) {
            val max = if (t3(i)._1 < x(i)) x(i) else t3(i)._1
            val min = if (t3(i)._2 > x(i)) x(i) else t3(i)._2
            val sum = t3(i)._3 + x(i)
            t3(i) = (max, min, sum)
          }
          t3
        })
        .map(t => (t._1 - t._2, t._3 / size))
        .toArray

      val _ds = ds.map(s => s.copy(xv = scaleFuture(scale, s.xv, featureSize)))
      (_ds, scale)
    }

    val (_ds, scale) = meanNormalization(ds)

    val dataset = _ds.map(s => s.copy(xv = addColumn(s.xv, 1.0)))

    val iterations = 5000
    val alpha      = 0.1
    var theta      = DenseVector(0.0, 0.0, 0.0)

    val history = ArrayBuffer.empty[Iteration]

    for (i <- 1 to iterations) {
      val (_, cost, gradient) = compute(theta, dataset)
      val iter                = Iteration(i, theta, cost)
      history += iter
      logger.info(iter.toString)
      if (i != iterations) theta = theta - (gradient * alpha)
    }

    logger.info("final theta: " + theta)

    val pw = new PrintWriter(new File("log/history.csv"))
    history.foreach(iter => pw.println(iter.toCSV))
    pw.close()

    logger.info("mathematica 查看拟合函数，iter-cost，theta-cost 图")
    logger.info(
      predict(theta, addColumn(scaleFuture(scale, DenseVector(2400.0, 3.0), 2), 1.0)).toString)
    logger.info(
      predict(theta, addColumn(scaleFuture(scale, DenseVector(1416, 2), 2), 1.0)).toString)
  }

  "3. Normal Equations" in {
    val ds = Source
      .fromFile("data/coursera/ex1data2.txt")
      .getLines()
      .toList
      .map(line => {
        val t = line.split(",").map(_.toDouble)
        Sample(DenseVector(t(0), t(1)), t(2))
      })

    def addColumn(v: DenseVector[Double], a: Double) = DenseVector(v.data.+:(a))
    val dataset = ds.map(s => s.copy(xv = addColumn(s.xv, 1.0)))

    val X: DenseMatrix[Double] = DenseMatrix(dataset.map(_.xv): _*)
    val y: DenseVector[Double] = DenseVector(dataset.map(_.y):  _*)

    val theta = inv(X.t * X) * X.t * y
    logger.info("theta: " + theta)

    val cost = (y - X * theta).data.map(a => a * a).sum / (2 * ds.length)

    logger.info("cost: " + cost)

    def predict(theta: DenseVector[Double], xv: DenseVector[Double]) = theta.dot(xv)

    logger.info(predict(theta, addColumn(DenseVector(2400.0, 3.0), 1.0)).toString)
    logger.info(predict(theta, addColumn(DenseVector(1416.0, 2), 1.0)).toString)
  }
}

case class P(x: Double, y: Double)

case class Sample(xv: DenseVector[Double], y: Double)

case class Iteration(n: Long, theta: DenseVector[Double], cost: Double) {
  override def toString = s"#$n \t iter: 𝛉 = $theta \t cost = $cost"
  def toCSV = s"$n,${theta.data.mkString(",")},$cost"
}
