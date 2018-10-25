/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.cats.typeclass

/**
  * EqSpec
  *
  * @author zhaihao
  * @version 1.0 2018/10/24 15:15
  */
object EqSpec {

  def main(args: Array[String]): Unit = {
    compareInt
    eqSyntax
    compareOption
    exercise
  }

  def compareInt = {
    import cats.Eq
    import cats.instances.int._
    val eqInt = Eq[Int]
    eqInt.eqv(123, 123)
  }

  def eqSyntax = {
    import cats.instances.int._
    import cats.syntax.eq._
    1 === 1
    1 =!= 2
  }

  def compareOption = {
    import cats.instances.int._
    import cats.instances.option._
    import cats.syntax.eq._
    (Some(1): Option[Int]) =!= None
  }

  def exercise = {
    case class Cat(name: String, age: Int, color: String)

    val cat1 = Cat("Garfield", 38, "orange and black")
    val cat2 = Cat("Heath Cliff", 33, "orange and black")

    import cats.Eq
    import cats.instances.int._
    import cats.instances.string._
    import cats.syntax.eq._

    implicit val catEq: Eq[Cat] = Eq.instance[Cat] { (c1, c2) =>
      (c1.name === c2.name) && (c1.age === c2.age) && (c1.color === c2.color)
    }

    cat1 === cat2

    import cats.instances.option._

    Option(cat1) =!= Option.empty[Cat]
  }
}
