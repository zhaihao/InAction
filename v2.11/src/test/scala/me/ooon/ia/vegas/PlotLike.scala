/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.vegas
import java.io.{File, PrintWriter}

import org.scalatest.{TestSuite, TestSuiteMixin}
import vegas.DSL.ExtendedUnitSpecBuilder

/**
  * PlotLike
  *
  * @author zhaihao
  * @version 1.0 2018-12-01 13:42
  */
trait PlotLike extends TestSuiteMixin {
  this: TestSuite =>

  var plot: ExtendedUnitSpecBuilder = _

  val buildInServerHost = "http://localhost:63342/"

  abstract override def withFixture(test: NoArgTest) = {
    try super.withFixture(test)
    finally {
      if (plot != null) {
        val view = plot.html.pageHTML()
        val pw   = new PrintWriter(new File("log/vegas.html"))
        pw.write(view)
        pw.close()
      }
    }
  }

}
