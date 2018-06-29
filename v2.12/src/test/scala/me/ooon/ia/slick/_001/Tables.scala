/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick._001

import slick.jdbc.H2Profile.api._

/**
  * Tables
  *
  * @author zhaihao
  * @version 1.0 21/03/2018 15:03
  */
object Tables {

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
  }

  val suppliers = TableQuery[Suppliers]

  class Coffees(tag: Tag)
      extends Table[(String, Int, Double, Int, Int)](tag, "T_COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)

    def supID = column[Int]("SUP_ID")

    def price = column[Double]("PRICE")

    def sales = column[Int]("SALES")

    def total = column[Int]("TOTAL")

    def supplier = foreignKey("SUP_PK", supID, suppliers)(_.id)

    override def * = (name, supID, price, sales, total)
  }

  val coffees = TableQuery[Coffees]
}
