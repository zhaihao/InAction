/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.cats.monoid
import me.ooon.base.test.BaseSpec

/**
  * MonoidSpec
  *
  * @author zhaihao
  * @version 1.0 2018/10/25 14:07
  */
class MonoidSpec extends BaseSpec {
  "define" in {
    trait Semigroup[A] {
      def combine(a: A, b: A): A
    }

    trait Monoid[A] extends Semigroup[A] {
      def empty: A
    }

    //noinspection ScalaUnusedSymbol
    def identityLaw[A](x: A)(implicit m: Monoid[A]): Boolean =
      (m.combine(x, m.empty) == x) && (m.combine(m.empty, x) == x)

    //noinspection ScalaUnusedSymbol
    def associativeLaw[A](x: A, y: A, z: A)(implicit m: Monoid[A]): Boolean =
      m.combine(x, m.combine(y, z)) == m.combine(m.combine(x, y), z)
  }

  "Monoid Instances" in {
    import cats.Monoid
    import cats.instances.string._
    Monoid[String].combine("Hi ", "cats") ==> "Hi cats"

    import cats.instances.int._
    Monoid[Int].combine(1, 2) ==> 3

    import cats.instances.option._
    Monoid[Option[Int]].combine(Some(1), Some(2)) ==> Some(3)
  }

  "Monoid Syntax" in {
    import cats.Monoid
    import cats.instances.int._
    import cats.syntax.semigroup._
    (1 |+| 1)                       ==> 2
    (1 |+| 1 |+| Monoid[Int].empty) ==> 2

    import cats.instances.string._
    ("a" |+| "b") ==> "ab"
  }

  "add all" in {
    import cats.Monoid
    import cats.syntax.monoid._
    def add[A: Monoid](items: List[A]) = items.foldLeft(Monoid[A].empty)(_ |+| _)
    import cats.instances.int._
    add(List(1, 2, 3, 4, 5)) ==> 15

    import cats.instances.option._
    add(List(Some(1), Some(2), Some(3), None)) ==> Some(6)
  }
}
