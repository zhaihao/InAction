/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.scala

import me.ooon.base.test.BaseSpec

/**
  * RegexSpec
  *
  * @author zhaihao
  * @version 1.0 2018/5/29 10:54
  */
class RegexSpec extends BaseSpec {

  /**
    * java正则表达式中默认的是贪心匹配。如果要实现非贪心匹配，需要使用?匹配符。
    */
  "贪心匹配 与 非贪心匹配" - {

    "贪心 1" in {
      // language=RegExp
      val r    = """.*(\d+)期""".r
      val r(d) = "哈哈12期"
      d ==> "2"
    }

    "非贪心 1" in {
      // language=RegExp
      val r    = """.+?(\d+)期""".r
      val r(d) = "哈哈12期"
      d ==> "12"
    }

    "贪心 2" in {
      // language=RegExp
      val r    = """(sa+?).*""".r
      val r(s) = "saaa"
      s ==> "sa"
    }

    "非贪心 2" in {
      // language=RegExp
      val r    = """(sa+).*""".r
      val r(s) = "saaa"
      s ==> "saaa"
    }

  }

  /**
    * (?:pattern) 是匹配型括号     匹配pattern，但不捕获匹配结果
    * (pattern)   是捕获型括号     匹配pattern，匹配pattern并捕获结果，自动获取组号
    */
  "group 括号与 匹配括号" in {

    // language=RegExp
    val r = """.+?(\d+)期.*本期应还款(\d+(?:[.]\d+)?).*余期为(\d+)期.*""".r

    def f(s: String) = s match {
      case r(a, b, c) => (a, b, c)
      case _          => ("e", "e", "e")
    }

    f("哈哈 36期 哈哈 本期应还款3000.0.余期为4期哈哈") ==> ("36", "3000.0", "4")
    f("哈哈 36期 哈哈 本期应还款3000.余期为4期哈哈")   ==> ("36", "3000", "4")
  }

  "词组匹配" - {

    "1" in {
      // language=RegExp
      val regex    = """.*(中国|日本).*""".r
      val regex(s) = "你好中国"
      s ==> "中国"
    }

    "2" in {
      // language=RegExp
      val regex       = """.*(\d+)-(\d+)(?:个体|号).*""".r
      val regex(m, n) = "3-12个体"
      (m, n) ==> ("3", "12")

      assertThrows[MatchError] {
        val regex(_, _) = "3-12个"
      }
    }
  }
}
