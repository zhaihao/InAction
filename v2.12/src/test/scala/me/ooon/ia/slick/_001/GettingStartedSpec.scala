/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick._001

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import me.ooon.base.test.BaseAsyncSpec
import me.ooon.ia.slick._001.Tables._
import org.scalatest.BeforeAndAfter
import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Test
  *
  * @author zhaihao
  * @version 1.0 21/03/2018 14:23
  */
class GettingStartedSpec extends BaseAsyncSpec with BeforeAndAfter with StrictLogging {

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

  "select" - {

    "select all table" in {
      val future: Future[Seq[(String, Int, Double, Int, Int)]] =
        db.run(coffees.result)

      future
        .map(_.foreach {
          case (name, supID, price, sales, total) =>
            logger.info("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
        })
        .map(_ ==> ((): Unit))

    }

    "select all table again" in {
      val query = for (c <- coffees)
        yield
          LiteralColumn(" ") ++
            c.name ++ "\t" ++ c.supID.asColumnOf[String] ++ "\t" ++ c.price
            .asColumnOf[String] ++
            "\t" ++ c.sales.asColumnOf[String] ++ "\t" ++ c.total
            .asColumnOf[String]

      db.stream(query.result).foreach(println).map(_ >>> ((): Unit))
    }

    "select where" in {
      val query = for {
        c <- coffees if c.price < 9.0
        s <- suppliers if s.id === c.supID
      } yield (c.name, s.name)

      db.run(query.result).map(_ >>> ((): Unit))
    }
  }
}
