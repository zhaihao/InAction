/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.cats.typeclass
import me.ooon.base.test.BaseSpec

/**
  * PrintableSpec
  *
  * @author zhaihao
  * @version 1.0 2018/10/24 10:39
  */
class PrintableSpec extends BaseSpec {
  "exercise" in {
    trait Printable[A] {
      def format(a: A): String
    }

    object Printable {
      def format[A](a: A)(implicit pa: Printable[A]): String = pa.format(a)
      def print[A](a:  A)(implicit pa: Printable[A]): Unit   = println(format(a))
    }

    object PrintableInstances {
      implicit val intPrintable:    Printable[Int]    = (a: Int) => a.toString
      implicit val stringPrintable: Printable[String] = (s: String) => s
    }

    final case class Cat(name: String, age: Int, color: String)

    import PrintableInstances._

    implicit val catPrintable: Printable[Cat] = (a: Cat) => {
      val name  = Printable.format(a.name)
      val age   = Printable.format(a.age)
      val color = Printable.format(a.color)
      s"$name is a $age year-old $color cat."
    }

    Printable.print(Cat("Tom", 5, "gray"))
  }
}
