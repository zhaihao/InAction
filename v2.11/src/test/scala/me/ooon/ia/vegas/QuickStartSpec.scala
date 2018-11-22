/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.vegas
import me.ooon.base.test.BaseSpec

import scala.io.{Source, StdIn}

/**
  * QuickStartSpec
  *
  * @author zhaihao
  * @version 1.0 2018-12-01 12:57
  */
class QuickStartSpec extends BaseSpec {

  "Quick start" in {
    import vegas._
    val plot = Vegas("Country Pop")
      .withData(
        Seq(
          Map("country" -> "USA", "population" -> 314),
          Map("country" -> "UK", "population"  -> 64),
          Map("country" -> "DK", "population"  -> 80)
        )
      )
      .encodeX("country", Nom) // 枚举类型
      .encodeY("population", Quant) //数值类型
      .mark(Bar) // 长条图

    plot.show
    StdIn.readLine()
  }
}
