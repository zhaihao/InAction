/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.scala.string.interpolation

import me.ooon.orison.base.test.BaseSpec

/**
  * SQLContextSpec
  *
  * @author zhaihao
  * @version 1.0 2018/7/31 15:36
  */
class SQLContextSpec extends BaseSpec {

  "interpolation" in {
    val name="lucy"
    val age = 10
    import SQLContext._

    sl"select * from T_User_Bill where name='$name' and age=$age".exec
  }
}

case class SQLContext(sql: String) {
  def exec = println(sql)
}

object SQLContext {
  implicit class StringInterpolations(sc: StringContext) {
    def sl(args: Any*): SQLContext = SQLContext(sc.s(args: _*))
  }
}
