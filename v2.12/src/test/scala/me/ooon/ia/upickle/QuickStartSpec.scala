/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.upickle

import me.ooon.orison.base.test.BaseSpec

/**
  * QuickStartSpec
  *
  * @author zhaihao
  * @version 1.0 2018/5/24 17:01
  */
class QuickStartSpec extends BaseSpec {

  "started" in {
    import upickle.default._
    write(1)                  ==> "1"
    write(Seq(1, 2, 3))       ==> "[1,2,3]"
    read[Seq[Int]]("[1,2,3]") ==> List(1, 2, 3)
    write((1, "omg", true))   ==> """[1,"omg",true]"""
    type Tup = (Int, String, Boolean)
    read[Tup]("""[1,"omg",true]""") ==> (1, "omg", true)
    read[Long]("\"4000000000000\"") ==> 4000000000000L
  }

  "basics" in {
    import upickle.default._
    write(true:  Boolean) ==> "true"
    write(false: Boolean) ==> "false"
    write(12:    Int)     ==> "12"
    write(12:    Short)   ==> "12"
    write(12:    Byte)    ==> "12"
    write(Int.MaxValue) ==> "2147483647"
    write(Int.MinValue) ==> "-2147483648"
    write(12.5f:          Float)  ==> "12.5"
    write(12.5:           Double) ==> "12.5"
    write(12:             Long)   ==> "\"12\""
    write(4000000000000L: Long)   ==> "\"4000000000000\""
    write(1.0 / 0:        Double) ==> "\"Infinity\""
    write(Float.PositiveInfinity) ==> "\"Infinity\""
    write(Float.NegativeInfinity) ==> "\"-Infinity\""
    write('o')                    ==> "\"o\""
    write("omg")                  ==> "\"omg\""
    write(Array(1, 2, 3))         ==> "[1,2,3]"
    write(Array(1, 2, 3), indent = 4) ==>
      """[
        |    1,
        |    2,
        |    3
        |]""".stripMargin

    write(Seq(1, 2, 3))    ==> "[1,2,3]"
    write(Vector(1, 2, 3)) ==> "[1,2,3]"
    write(List(1, 2, 3))   ==> "[1,2,3]"
    import collection.immutable.SortedSet
    write(SortedSet(1, 3, 2)) ==> "[1,2,3]"
    write(Some(1))            ==> "[1]"
    write(None)               ==> "[]"
    write((1, "omg"))         ==> """[1,"omg"]"""
    write((1, "omg", true))   ==> """[1,"omg",true]"""
  }

  "case class" in {
    import upickle.default._
    write(Thing(1, "gg"))                             ==> """{"myFieldA":1,"myFieldB":"gg"}"""
    read[Thing]("""{"myFieldA":1,"myFieldB":"gg"}""") ==> Thing(1, "gg")

    write(Big(1, true, "lol", 'Z', Thing(7, ""))) ==>
      """{"i":1,"b":true,"str":"lol","c":"Z","t":{"myFieldA":7,"myFieldB":""}}"""

    write(Big(1, true, "lol", 'Z', Thing(7, "")), indent = 4) ==>
      """{
        |    "i": 1,
        |    "b": true,
        |    "str": "lol",
        |    "c": "Z",
        |    "t": {
        |        "myFieldA": 7,
        |        "myFieldB": ""
        |    }
        |}""".stripMargin
  }
}

import upickle.default.{macroRW, ReadWriter => RW}

case class Thing(myFieldA: Int, myFieldB: String)

object Thing {
  implicit def rw: RW[Thing] = macroRW
}

case class Big(i: Int, b: Boolean, str: String, c: Char, t: Thing)

object Big {
  implicit def rw: RW[Big] = macroRW
}
