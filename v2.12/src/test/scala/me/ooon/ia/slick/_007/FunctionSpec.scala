/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick._007

import com.typesafe.scalalogging.StrictLogging
import me.ooon.base.test.BaseAsyncSpec
import me.ooon.ia.slick.Tables._
import me.ooon.ia.slick.mysql.DB
import org.scalatest.BeforeAndAfter
import slick.jdbc.MySQLProfile.api._

/**
  * FunctionSpec
  *
  * @author zhaihao
  * @version 1.0 23/03/2018 14:54
  */
class FunctionSpec
    extends BaseAsyncSpec
    with DB
    with BeforeAndAfter
    with StrictLogging {

  "sql function" - {

    "md5" in {
      val md5 = SimpleFunction.unary[String, String]("md5")
      val q   = for (u <- TUser if u.id === 1L) yield md5(u.name)
      val a   = q.result.headOption
      db.run(a).map(_ ==> Some("d9ffaca46d5990ec39501bcdf22ee7a1"))
    }

  }
}
