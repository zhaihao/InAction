/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick._006

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import me.ooon.base.test.BaseAsyncSpec
import slick.codegen.SourceCodeGenerator
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

/**
  * SchemaCodeGenerationSpec
  *
  * @author zhaihao
  * @version 1.0 22/03/2018 19:29
  */
class SchemaCodeGenerationSpec extends BaseAsyncSpec with StrictLogging {

  //language=SQL
  val sql =
    """
     CREATE TABLE T_User (
       id          BIGINT PRIMARY KEY AUTO_INCREMENT,
       name        VARCHAR(200) NOT NULL,
       mail        VARCHAR(200) UNIQUE NOT NULL ,
       age         INT,
       address     VARCHAR(200),
       create_time TIMESTAMP          DEFAULT current_timestamp,
       update_time TIMESTAMP          DEFAULT current_timestamp ON UPDATE current_timestamp
     );
   """

  val pwd_config = ConfigFactory.parseFile(new File("/Users/zhaihao/.pwd"))
  val pwd        = pwd_config.getString("mysql")

  val config =
    s"""
       |db {
       |    url  = "jdbc:mysql://vps.ooon.me:3306/MyDB?characterEncoding=utf8&useSSL=false"
       |    driver = com.mysql.jdbc.Driver
       |    user = root
       |    password = $pwd
       |    connectionPool = HikariCP
       |    connectionTimeout = 1000
       |    validationTimeout = 1000
       |    idleTimeout = 30000
       |    maxLifetime = 600000
       |    leakDetectionThreshold = 0
       |    initializationFailFast = false
       |    maxConnections = 20
       |    minConnections = 1
       |    poolName = mysql-h
       |}
    """.stripMargin

  val profile = ConfigFactory.parseString(config).getString("db.driver") match {
    case x if x.contains("mysql") => MySQLProfile
    case _                        => throw new Exception("unsupport db type")
  }

  val db = Database.forConfig("db", ConfigFactory.parseString(config))

  "code gen" in {
    logger.warn("starting gen table code")
    val codegenFuture = db
      .run(
        profile.defaultTables.flatMap {
          profile.createModelBuilder(_, true).buildModel
        }
      )
      .map(model => {
        new SourceCodeGenerator(model) {
          // 自定义 scala case class 名字
          override def entityName =
            dbTableName => dbTableName.drop(2).toCamelCase

          // 自定义 TableQuery 的名字
          override def tableName = dbTableName => dbTableName.toCamelCase

          override def Table = new Table(_) {

            override def EntityType = new EntityType {
              override def classEnabled = true
            }

            override def Column = new Column(_) {
              override def asOption = autoInc
            }
          }

        }
      })

    codegenFuture
      .map(
        _.writeToFile(
          profile.getClass.getName.replace("$", ""),
          "src/test/scala",
          "me.ooon.slick"
        ))
      .map(i => {
        logger.warn("gen table code completed")
        i >>> ((): Unit)
      })
  }
}
