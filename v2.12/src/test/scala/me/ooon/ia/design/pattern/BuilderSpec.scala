/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.design.pattern
import me.ooon.base.test.BaseSpec

/**
  * BuilderSpec
  *
  * @see [[https://en.wikipedia.org/wiki/Builder_pattern]]
  * @author zhaihao
  * @version 1.0 2018/11/16 14:34
  */
class BuilderSpec extends BaseSpec {

  "oo" in {
    // 正常情况不要使用 case class 来定义，这里只是为了快速测试
    case class Car(seats:        Int,
                   sportsCar:    Boolean,
                   tripComputer: Boolean,
                   gps:          Boolean,
                   price:        Double,
                   year:         Int)

    object Car {
      def builder() = new Builder()

      class Builder {
        var seats:        Int     = 0
        var sportsCar:    Boolean = false
        var tripComputer: Boolean = false
        var gps:          Boolean = false
        var price:        Double  = 100.0
        var year:         Int     = 2016

        def setSeats(seats:               Int)     = { this.seats        = seats; this }
        def setSportsCar(sportsCar:       Boolean) = { this.sportsCar    = sportsCar; this }
        def setTripComputer(tripComputer: Boolean) = { this.tripComputer = tripComputer; this }
        def setGps(gps:                   Boolean) = { this.gps          = gps; this }
        def setPrice(price:               Double)  = { this.price        = price; this }
        def setYear(year:                 Int)     = { this.year         = year; this }

        def build() = new Car(seats, sportsCar, tripComputer, gps, price, year)
      }
    }

    // use builder

    val car = Car.builder().setGps(true).setYear(2018).setPrice(10000.0).build()
    // 一般会私有化 car 的构造函数
    car ==> Car(0, false, false, true, 10000.0, 2018)
  }

  "fp" in {
    // 同样不要使用 case class
    case class Car(seats:        Int,
                   sportsCar:    Boolean,
                   tripComputer: Boolean,
                   gps:          Boolean,
                   price:        Double,
                   year:         Int)

    object Car {
      // 默认值也可以直接写在 class 的主构造器上
      def apply(seats:        Int = 0,
                sportsCar:    Boolean = false,
                tripComputer: Boolean = false,
                gps:          Boolean = false,
                price:        Double = 100.0,
                year:         Int = 2016): Car = new Car(seats, sportsCar, tripComputer, gps, price, year)
    }

    Car(gps = true, year = 2018, price = 10000.0) ==> Car(0, false, false, true, 10000.0, 2018)
  }
}
