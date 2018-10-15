/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick._003

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import me.ooon.base.test.BaseAsyncSpec
import me.ooon.ia.slick._001.Tables.{coffees, suppliers}
import org.scalatest.BeforeAndAfter
import slick.basic.DatabasePublisher
import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * DBIOActionSpec
  *
  * @author zhaihao
  * @version 1.0 2018/3/21 23:21
  */
class DBIOActionSpec
    extends BaseAsyncSpec
    with BeforeAndAfter
    with StrictLogging {
  val application_conf =
    """
      |h2mem1 = {
      |  url = "jdbc:h2:mem:test1"
      |  driver = org.h2.Driver
      |  connectionPool = disabled
      |  keepAliveConnection = true
      |}
    """.stripMargin

  val db =
    Database.forConfig("h2mem1", ConfigFactory.parseString(application_conf))

  before {
    val setup = DBIO.seq(
      (suppliers.schema ++ coffees.schema).create,
      // 单条插入
      suppliers += (101, "Acme, Inc.", "99 Market Street", "Grounds ville", "CA", "95199"),
      suppliers += (49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
      suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966"),
      // batch 插入
      coffees ++= Seq(
        ("Colombian", 101, 7.99, 0, 0),
        ("French_Roast", 49, 8.99, 0, 0),
        ("Espresso", 150, 9.99, 0, 0),
        ("Colombian_Decaf", 101, 8.99, 0, 0),
        ("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
    )

    Await.ready(db.run(setup), 10.seconds)
  }

  after {
    db.close()
  }

  "两种查询" - {
    "Materialized" in {
      val q = for (c <- coffees) yield c.name
      val a = q.result
      val f: Future[Seq[String]] = db.run(a)
      f.map(_ >>> ((): Unit))
    }

    "stream" in {
      val q = for (c <- coffees) yield c.name
      val a = q.result
      val p: DatabasePublisher[String] = db.stream(a)
      p.foreach(x => logger.info(s"Element: $x")).map(_ >>> ((): Unit))
    }
  }

  "action compose" - {

    val ins1 = coffees += ("aaa", 101, 7.99, 0, 0)
    val ins2 = coffees += ("bbb", 101, 8.99, 0, 0)
    val ins3 = coffees ++= Seq(
      ("ccc", 101, 6.99, 0, 0),
      ("ddd", 101, 5.99, 0, 0)
    )

    "seq 无返回值" in {
      val action = DBIO.seq(ins1, ins2)
      db.run(action).map(_ >>> ((): Unit))
    }

    "andThen 返回第二个值" in {
      val action = ins1 andThen ins3
      db.run(action).map(_ ==> Some(2))
    }

    "zip 返回两个值" in {
      val action = ins1 zip ins3
      db.run(action).map(_ ==> (1, Some(2)))
    }

    "sequence 返回所有值" in {
      val action = DBIO.sequence(Vector(ins1, ins2, ins3))
      db.run(action).map(_ ==> Vector(1, 1, Some(2)))
    }

    "andFinally 处理异常" in {
      val step1 = coffees += ("Colombian", 101, 7.99, 0, 0) // 主键冲突
      val q     = for (c <- coffees if c.name === "Colombian") yield c.price
      val step2 = q.update(0.00)
      db.run(step1 andFinally step2)
      db.run(q.result).map(_ ==> Vector(0.0))
    }
  }

  "transaction" - {
    "一个事务" in {
      val a = (for {
        ns <- coffees.filter(_.name.startsWith("F")).map(_.name).result
        _  <- DBIO.seq(ns.map(n => coffees.filter(_.name === n).delete): _*)
      } yield ()).transactionally

      db.run(a).map(_ >>> ((): Unit))
    }
  }
}
