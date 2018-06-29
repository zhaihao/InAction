/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick._005

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import me.ooon.orison.base.test.BaseAsyncSpec
import me.ooon.ia.slick._001.Tables.{coffees, suppliers}
import org.scalatest.BeforeAndAfter
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * QueriesSpec
  *
  * @author zhaihao
  * @version 1.0 22/03/2018 15:23
  */
class QueriesSpec extends BaseAsyncSpec with BeforeAndAfter with StrictLogging {

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

  "query" - {
    "where - filter" in {
      val query  = coffees.filter(_.supID === 101)
      val action = query.result
      db.run(action).map(_ >>> ((): Unit))
    }

    "limit - take - drop" in {
      // drop ==> offset
      // take - drop ==> limit
      val action = coffees.take(9).drop(5).result
      db.run(action).map(_ >>> ((): Unit))
    }

    // 推荐这种，语义与 sql 一致
    "limit - drop - take" in {
      // drop ==> offset
      // take ==> limit
      val action = coffees.drop(5).take(9).result
      db.run(action).map(_ >>> ((): Unit))
    }

    "sort - order nulls first" in {
      val action = coffees.sortBy(_.name.desc.nullsFirst).result
      db.run(action).map(_ >>> ((): Unit))
    }

    "dynamic filter" in {

      val criteriaColombian = Option("Colombian")
      val criteriaEspresso  = Option("Espresso")
      val criteriaRoast: Option[String] = None

      val action = coffees.filter { coffee =>
        List(
          criteriaColombian.map(coffee.name === _),
          criteriaEspresso.map(coffee.name === _),
          criteriaRoast.map(coffee.name === _)
        ).collect { case Some(criteria) => criteria }
          .reduceLeftOption(_ || _)
          .getOrElse(true: Rep[Boolean])
      }.result

      db.run(action).map(_ >>> ((): Unit))
    }

    "join" - {
      "Applicative joins" - {
        // 笛卡尔
        "cross" in {
          val crossJoin = for {
            (c, s) <- coffees join suppliers
          } yield (c.name, s.name)

          db.run(crossJoin.result).map(_ >>> ((): Unit))
        }

        // 交集
        "inner" in {
          val innerJoin = for {
            (c, s) <- coffees join suppliers on (_.supID === _.id)
          } yield (c.name, s.name)

          db.run(innerJoin.result).map(_ >>> ((): Unit))
        }

        "left" in {
          val leftOuterJoin = for {
            (c, s) <- coffees joinLeft suppliers on (_.supID === _.id)
          } yield (c.name, s.map(_.name))

          db.run(leftOuterJoin.result).map(_ >>> ((): Unit))
        }

        "right" in {
          val rightOuterJoin = for {
            (c, s) <- coffees joinRight suppliers on (_.supID === _.id)
          } yield (c.map(_.name), s.name)

          db.run(rightOuterJoin.result).map(_ >>> ((): Unit))
        }

        // left union right
        "full outer" in {
          val fullOuterJoin = for {
            (c, s) <- coffees joinFull suppliers on (_.supID === _.id)
          } yield (c.map(_.name), s.map(_.name))

          db.run(fullOuterJoin.result).map(_ >>> ((): Unit))
        }
      }

      "monadic joins" - {
        "cross" in {
          val monadicCrossJoin = for {
            c <- coffees
            s <- suppliers
          } yield (c.name, s.name)

          db.run(monadicCrossJoin.result).map(_ >>> ((): Unit))
        }

        "inner" in {
          val monadicInnerJoin = for {
            c <- coffees
            s <- suppliers if c.supID === s.id
          } yield (c.name, s.name)

          db.run(monadicInnerJoin.result).map(_ >>> ((): Unit))
        }
      }
    }

    "union" in {
      val q1 = coffees.filter(_.price < 8.0)
      val q2 = coffees.filter(_.price > 9.0)

      // 去重
      val unionQuery = q1 union q2

      db.run(unionQuery.result).map(_ >>> ((): Unit))
    }

    "union all" in {
      val q1 = coffees.filter(_.price < 8.0)
      val q2 = coffees.filter(_.price > 9.0)

      // 允许重复
      val unionAllQuery = q1 ++ q2

      db.run(unionAllQuery.result).map(_ >>> ((): Unit))
    }

    "Aggregation" - {

      val q = coffees.map(_.price)
      "min" in {
        db.run(q.min.result).map(_ ==> Some(7.99))
      }

      "max" in {
        db.run(q.max.result).map(_ ==> Some(9.99))
      }

      "avg" in {
        db.run(q.avg.result).map(_ ==> Some(9.190000000000001))
      }

      "sum" in {
        db.run(q.sum.result).map(_ ==> Some(45.95))
      }

      "count" in {
        val q = coffees.length
        db.run(q.result).map(_ ==> 5)
      }

      "exists" in {
        val q = coffees.exists
        db.run(q.result).map(_ ==> true)
      }
    }

    "group" in {
      val q = (for {
        c <- coffees
        s <- c.supplier
      } yield (c, s)).groupBy(_._1.supID)

      val q2 = q.map {
        case (supID, css) =>
          (supID, css.length, css.map(_._1.price).avg)
      }

      db.run(q2.result).map(_ >>> ((): Unit))
    }
  }

  "update" - {

    "delete" in {
      val q      = coffees.filter(_.supID === 101)
      val action = q.delete
      db.run(action).map(_ ==> 2)
    }

    "insertOrUpdate" in {
      val ins = coffees.insertOrUpdate(("Colombian", 101, 7.99, 1, 1))
      db.run(ins).map(_ ==> 1)
      val q = coffees.filter(_.name === "Colombian")
      db.run(q.result).map(_ >>> ((): Unit))
    }

    "update" in {
      val q = coffees.filter(_.name === "Colombian").map(_.price)
      val a = q.update(99.99)
      db.run(a).map(_ >>> ((): Unit))

      val q1 = for (c <- coffees if c.name === "Colombian") yield c.price
      val a1 = q1.update(0.00)
      db.run(a1).map(_ >>> ((): Unit))
    }

    "compile" in {
      def coffeeByPrice(min: Rep[Double], max: Rep[Double]) =
        for {
          c <- coffees if c.price >= min && c.price < max
        } yield c

      val coffeeByPriceCompiled = Compiled(coffeeByPrice _)
      val action                = coffeeByPriceCompiled(8.00, 9.00)
      db.run(action.result).map(_ >>> ((): Unit))
    }

  }
}
