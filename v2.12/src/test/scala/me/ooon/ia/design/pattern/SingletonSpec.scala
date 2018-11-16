/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.design.pattern
import me.ooon.base.test.BaseSpec

/**
  * SingletonSpec
  *
  * @author zhaihao
  * @version 1.0 2018/11/16 15:15
  */
class SingletonSpec extends BaseSpec {
  "fp" in {
    object DB extends AutoCloseable {
      override def close() = println("close")
      def execute(sql: String): String = s"exec $sql"
    }

    DB.execute("123") ==> "exec 123"
  }
}
