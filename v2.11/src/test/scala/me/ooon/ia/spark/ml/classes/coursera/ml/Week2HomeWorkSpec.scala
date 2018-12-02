/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml.classes.coursera.ml
import breeze.linalg.DenseVector
import com.typesafe.scalalogging.StrictLogging
import me.ooon.base.syntax.id._
import me.ooon.base.test.BaseSpec
import me.ooon.ia.vegas.PlotLike

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * Week2HomeWorkSpec
  *
  * @author zhaihao
  * @version 1.0 2018-11-30 17:56
  */
class Week2HomeWorkSpec extends BaseSpec with PlotLike with StrictLogging {

  import breeze.linalg._

  "1.Simple Octave/MATLAB function" - {

    "return a 5 x 5 identity matrix" in {
      DenseMatrix.eye[Int](5) |> println
    }
  }

  "2.Linear regression with one variable" - {
    val ds = Source
      .fromFile("data/coursera/ex1data1.txt")
      .getLines()
      .toList
      .map(_.split(",").map(_.toDouble))
      .map(i => P(i(0), i(1)))

    "2.1.Plotting the Data" in {
      import vegas._
      plot = Vegas(width = 400.0, height = 300.0)
        .withCaseClasses(ds)
        .mark(Point)
        .encodeX("x", Quantitative)
        .encodeY("y", Quantitative)
    }

    "2.2 Gradient Descent" - {
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

//      var theta = DenseVector.zeros[Double](2) // init [0.0,0.0]
      var theta = DenseVector(-4.0, 2.0)

      "测试 cost function" in {
        val cost = compute(theta, dataset)._2
        // 正确答案是 32.07
        logger.info("𝛉 = [0.0,0.0], cost = " + cost)
      }

      "迭代" - {
        val iterations = 1500
        val alpha      = 0.0001

        val history = ArrayBuffer.empty[Iteration]

        for (i <- 1 to iterations) {
          val (_, cost, gradient) = compute(theta, dataset)
          val iter                = Iteration(i, theta, cost)
          history += iter
          logger.info(iter.toString)
          theta = theta - (gradient * alpha)
        }

        "收敛曲线" in {
          import vegas._
          plot = Vegas(width = 800.0, height = 600.0)
            .withCaseClasses(history)
            .mark(Line)
            .encodeX("n", Quantitative, title = "iter")
            .encodeY("cost", Quantitative, title = "J")
        }

        "predict" in {
          logger.info(predict(theta, DenseVector(1.0, 3.5)).toString)
          logger.info(predict(theta, DenseVector(1.0, 7.0)).toString)
        }
      }
    }
  }
}

case class P(x: Double, y: Double)

case class Sample(xv: DenseVector[Double], y: Double)

case class Iteration(n: Long, theta: DenseVector[Double], cost: Double) {
  override def toString = s"#$n \t iter: 𝛉 = $theta \t cost = $cost "
}
