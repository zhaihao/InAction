/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick._002

import me.ooon.base.test.BaseSpec
import org.scalatest.BeforeAndAfter

/**
  * DatabaseConfigurationSpec
  *
  * @author zhaihao
  * @version 1.0 2018/3/21 21:36
  */
class DatabaseConfigurationSpec extends BaseSpec with BeforeAndAfter {

  "typesafe config" in {

    //noinspection ScalaUnusedSymbol
    val pg =
      """
        |mydb = {
        |  dataSourceClass = "org.postgresql.ds.PGSimpleDataSource"
        |  properties = {
        |    databaseName = "mydb"
        |    user = "myuser"
        |    password = "secret"
        |  }
        |  numThreads = 10
        |}
      """.stripMargin
    //     val db = Database.forConfig("mydb",ConfigFactory.parseString(pg))
  }

  "jdbc url" in {
    //    val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
  }

  "DataSource" in {
    //    val ds :DataSource = null
    //    val maxConnection = 30
    //    val db = Database.forDataSource(ds,Some(maxConnection))
  }

  "jndi" in {
    //    val db = Database.forName(jndiName: String, Some(size: Int))
  }

}
