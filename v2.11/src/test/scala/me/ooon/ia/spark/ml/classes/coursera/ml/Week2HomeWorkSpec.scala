/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml.classes.coursera.ml
import me.ooon.base.syntax.id._
import me.ooon.base.test.BaseSpec

import scala.io.Source

/**
  * Week2HomeWorkSpec
  *
  * @author zhaihao
  * @version 1.0 2018-11-30 17:56
  */
class Week2HomeWorkSpec extends BaseSpec {

  import breeze.linalg._
  import breeze.plot._

  "1.Simple Octave/MATLAB function" - {

    "return a 5 x 5 identity matrix" in {
      DenseMatrix.eye[Int](5) |> println
    }
  }

  "2.Linear regression with one variable" - {
    "2.1.Plotting the Data" in {
      val list= Source
      .fromFile("data/coursera/ex1data1.txt")
      .getLines()
      .toList
      .map(_.split(",").map(_.toDouble)).map(i=>(i(0),i(1)))

      val f = Figure()
      val p: Plot = f.subplot(0)


      f.saveas("tmp/123.png")

    }
  }
}
