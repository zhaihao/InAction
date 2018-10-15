/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick._004

import com.typesafe.scalalogging.StrictLogging
import me.ooon.base.test.BaseAsyncSpec
import org.scalatest.BeforeAndAfter
import slick.jdbc.H2Profile.api._

/**
  * SchemaSpec
  *
  * @author zhaihao
  * @version 1.0 22/03/2018 13:53
  */
class SchemaSpec extends BaseAsyncSpec with BeforeAndAfter with StrictLogging {

  "table" in {
    class Coffees(tag: Tag)
        extends Table[(String, Int, Option[Double], Int, Int)](tag, "COFFEES") { // 表名
      def name = column[String]("COF_NAME", O.PrimaryKey, O.AutoInc) // 主键,自增

      def supID = column[Int]("SUP_ID", O.Unique) // 唯一索引

      def price = column[Option[Double]]("PRICE") // null able

      def sales = column[Int]("SALES", O.Default(0)) // 默认值

      def total = column[Int]("TOTAL", O.Default(0))

      def * = (name, supID, price, sales, total)
    }

    // 给 TableQuery 添加自定义的方法
    object coffees extends TableQuery(new Coffees(_)) {
      val findByName = this.findBy(_.name)
    }

    succeed
  }

  "mapper" in {
    case class User(id: Option[Int], first: String, last: String)

    class Users(tag: Tag) extends Table[User](tag, "users") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

      def first = column[String]("first")

      def last = column[String]("last")

      def * = (id.?, first, last) <> (User.tupled, User.unapply)
    }

    succeed
  }

  "foreign key" in {
    class Suppliers(tag: Tag)
        extends Table[(Int, String, String, String, String, String)](
          tag,
          "T_SUPPLIERS") {
      def id = column[Int]("SUP_ID", O.PrimaryKey)

      def name = column[String]("SUP_NAME")

      def street = column[String]("STREET")

      def city = column[String]("CITY")

      def state = column[String]("STATE")

      def zip = column[String]("ZIP")

      def * = (id, name, street, city, state, zip)

      def idx = index("idx_a", (name, street), unique = true) // 联合索引
    }

    val suppliers = TableQuery[Suppliers]

    class Coffees(tag: Tag)
        extends Table[(String, Int, Double, Int, Int)](tag, "T_COFFEES") {
      def name = column[String]("COF_NAME", O.PrimaryKey)

      def supID = column[Int]("SUP_ID")

      def price = column[Double]("PRICE")

      def sales = column[Int]("SALES")

      def total = column[Int]("TOTAL")

      def supplier =
        foreignKey("SUP_PK", supID, suppliers)(_.id,
                                               onUpdate =
                                                 ForeignKeyAction.Restrict,
                                               onDelete =
                                                 ForeignKeyAction.Cascade)

      override def * = (name, supID, price, sales, total)
    }

    val coffees = TableQuery[Coffees]

    succeed
  }
}
