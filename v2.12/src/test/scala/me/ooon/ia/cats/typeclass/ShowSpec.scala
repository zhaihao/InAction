/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.cats.typeclass
import java.util.Date

import me.ooon.base.test.BaseSpec

/**
  * ShowSpec
  *
  * @author zhaihao
  * @version 1.0 2018/10/24 14:39
  */
class ShowSpec extends BaseSpec {
  import cats.Show

  "show instance" in {
    import cats.instances.int._
    val showInt = Show[Int]

    showInt.show(123) ==> "123"
  }

  "show syntax" in {
    import cats.instances.int._
    import cats.syntax.show._
    123.show ==> "123"
  }

  "create custom instance" in {
    implicit val dateShow: Show[Date] = (t: Date) => s"${t.getTime}ms since the epoch."
    import cats.syntax.show._
    println(new Date().show)

    case class Person(name: String, age: Int)
    implicit val personShow: Show[Person] =
      cats.Show.show[Person](p => s"${p.name} is ${p.age} years old")

    Person("Lucy", 10).show ==> "Lucy is 10 years old"
  }

}
