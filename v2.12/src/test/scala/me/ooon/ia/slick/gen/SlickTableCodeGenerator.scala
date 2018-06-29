/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick.gen

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import me.ooon.orison.base.test.BaseAsyncSpec
import org.scalatest.BeforeAndAfter
import slick.jdbc.MySQLProfile.api._
import slick.model.{Model, QualifiedName, Table}

/**
  * SlickTableCodeGenerator
  *
  * @author zhaihao
  * @version 1.0 2018/3/24 17:27
  */
class SlickTableCodeGenerator
    extends BaseAsyncSpec
    with BeforeAndAfter
    with StrictLogging
    with Settings {

  var db: Database = Database.forConfig("db", config)

  "gen" in {
    logger.info("start generator code")

    val modelAction =
      CustomMySQLProfile.createModel(Some(CustomMySQLProfile.defaultTables))

    db.run(modelAction)
      .map(model => {
        val tables: Seq[Table] = model.tables.map((table: Table) =>
          Table(
            name =
              QualifiedName(table = table.name.table, schema = None, catalog = table.name.catalog),
            columns     = table.columns,
            primaryKey  = table.primaryKey,
            foreignKeys = table.foreignKeys,
            indices     = table.indices,
            options     = table.options
        ))
        Model(tables, model.options)
      })
      .map(model => {
        new CustomSourceCodeGenerator(model).writeToFile(
          config.getString("gen.profile"),
          config.getString("gen.output.dir"),
          config.getString("gen.package.name"),
          config.getString("gen.table.name"),
          config.getString("gen.table.name") + ".scala"
        )
      })
      .map(_ >>> ((): Unit))
  }

  after { db.close }
}

trait Settings {
  def config = Settings.config
}

object Settings {
  lazy val config = {
    val pwd_config = ConfigFactory.parseFile(new File("/Users/zhaihao/.pwd"))
    val pwd        = pwd_config.getString("mysql")
    val config_str =
      s"""
         |db {
         |    url  = "jdbc:mysql://vps.ooon.me:3306/MyDB?characterEncoding=utf8&useSSL=false"
         |    driver = com.mysql.jdbc.Driver
         |    user = root
         |    password = $pwd
         |    connectionPool = HikariCP
         |    connectionTimeout = 10000
         |    validationTimeout = 1000
         |    idleTimeout = 30000
         |    maxLifetime = 600000
         |    leakDetectionThreshold = 0
         |    initializationFailFast = false
         |    maxConnections = 20
         |    minConnections = 1
         |    autoCommit = true
         |}
         |
         |gen {
         |  profile = "slick.jdbc.MySQLProfile"
         |  table.name = "Tables"
         |  package.name = "me.ooon.slick"
         |  output.dir = "src/test/scala"
         |  customType = {
         |    timestamp = "Timestamp"
         |    date = "Date"
         |    time = "Time"
         |  }
         |}
    """.stripMargin
    ConfigFactory.parseString(config_str)
  }
}
