/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick.mysql

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import slick.jdbc.MySQLProfile.api._

/**
  * DB
  *
  * @author zhaihao
  * @version 1.0 23/03/2018 15:11
  */
trait DB {
  def db = DB.db
}

object DB extends StrictLogging {

  lazy val db = {
    logger.info("init slick db")

    val pwd_config = ConfigFactory.parseFile(new File("/Users/zhaihao/.pwd"))
    val pwd        = pwd_config.getString("mysql")

    val config =
      s"""
         |mysql {
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
    """.stripMargin

    Database.forConfig("mysql", ConfigFactory.parseString(config))
  }
}
