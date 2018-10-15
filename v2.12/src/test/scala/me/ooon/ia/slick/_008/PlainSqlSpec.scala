/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick._008

import com.typesafe.scalalogging.StrictLogging
import me.ooon.base.test.BaseAsyncSpec
import me.ooon.ia.slick.Tables._
import me.ooon.ia.slick.mysql.DB
import org.scalatest.BeforeAndAfter
import slick.jdbc.GetResult
import slick.jdbc.MySQLProfile.api._

/**
  * PlainSqlSpec
  *
  * @author zhaihao
  * @version 1.0 23/03/2018 16:59
  */
class PlainSqlSpec extends BaseAsyncSpec with DB with BeforeAndAfter with StrictLogging {

  "plan sql" - {
    "select" in {
      val a = sql"SELECT * FROM T_User WHERE id=1".as[User].headOption

      db.run(a)
        .map(u => {
          logger.info(u.toString)
          u >>> ((): Unit)
        })
    }

    "select some column" in {
      db.run(
          sql"SELECT name,mail FROM T_User WHERE id=1"
            .as[(String, String)]
            .headOption)
        .map {
          case Some(t) =>
            val (name, mail) = t
            s"""{ "name" : $name, "mail" : $mail }"""
          case None => """{ "message" : "user not found" }"""
        }
        .map(s => {
          logger.info(s)
          s >>> ((): Unit)
        })
    }

    "select some column map custom case class" in {

      case class MU(name: String, email: String)

      implicit val getMu = GetResult(r => MU(r.<<, r.<<))

      db.run(sql"SELECT name,mail FROM T_User WHERE id=1".as[MU].head).map { u =>
        logger.info(u.toString)
        u ==> MU("Tom", "tom@gamil.com")
      }
    }

    "select literal" in {
      val table = "T_User"
      val a     = sql"SELECT * FROM #$table WHERE id=1".as[User].headOption

      db.run(a)
        .map(u => {
          logger.info(u.toString)
          u >>> ((): Unit)
        })
    }

    def insert(u: User): DBIO[Int] =
      sqlu"INSERT INTO T_User (name, mail, age, address) VALUES (${u.name}, ${u.mail}, ${u.age}, ${u.address})"

    "insert" in {
      val u = U("Lucy", "lucy@gmail.com")
      db.run(insert(u)).map(_ ==> 1)
    }

    "insert batch" in {
      val inserts: Seq[DBIO[Int]] = Seq(
        U("Lucy", "lucy@gmail.com"),
        U("Lily", "lily@gmail.com"),
        U("Katy", "katy@gmail.com", 10)
      ).map(insert)

      val a1: DBIO[Unit] = DBIO.seq(inserts: _*)

      val a2: DBIO[Seq[Int]] = DBIO.sequence(inserts)

      val res = for {
        _ <- db.run(sqlu"DELETE FROM T_User WHERE id>1")
        _ <- db.run(a1)
        _ <- db.run(sqlu"DELETE FROM T_User WHERE id>1")
        r <- db.run(a2)
      } yield r

      res.map(_ ==> Seq(1, 1, 1))
    }

    "delete" in {
      val a = sqlu"DELETE FROM T_User WHERE id>1"
      db.run(a).map(_ >>> ((): Unit))
    }
  }

}

object U {
  def apply(name: String, mail: String) =
    User(None, name, mail, None, None, None, None)

  def apply(name: String, mail: String, age: Int) =
    User(None, name, mail, Some(age), None, None, None)
}
