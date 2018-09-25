/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.play.reactive

import java.io.File
import java.util.Date

import akka.actor.ActorSystem
import me.ooon.orison.base.test.BaseAsyncSpec
import play.api.libs.iteratee.{Concurrent, Enumeratee, Enumerator, Iteratee}

import scala.concurrent.Future

/**
  * EnumeratorSpec
  *
  * @author zhaihao
  * @version 1.0 2018/9/25 14:44
  */
class EnumeratorSpec extends BaseAsyncSpec {

  "hold" - {

    "Enumerator" in {
      val enumerateUsers:   Enumerator[String]               = Enumerator("Tom", "Lucy", "Lily", "Jack")
      val consume:          Iteratee[String, String]         = Iteratee.consume[String]()
      val newIteratee:      Future[Iteratee[String, String]] = enumerateUsers(consume)
      val eventuallyResult: Future[String]                   = newIteratee.flatMap(i => i.run)
      eventuallyResult.map(_ ==> "TomLucyLilyJack")
    }

    "flatten" in {
      val enumerateUsers:   Enumerator[String]       = Enumerator("Tom", "Lucy", "Lily", "Jack")
      val consume:          Iteratee[String, String] = Iteratee.consume[String]()
      val newIteratee:      Iteratee[String, String] = Iteratee.flatten(enumerateUsers(consume))
      val eventuallyResult: Future[String]           = newIteratee.run
      eventuallyResult.map(_ ==> "TomLucyLilyJack")
    }

    "combine" in {
      val colors             = Enumerator("Red", "Blue", "Green")
      val moreColors         = Enumerator("Grey", "Orange", "Yellow")
      val combinedEnumerator = colors.andThen(moreColors)
      val consume            = Iteratee.consume[String]()
      val eventuallyIteratee = Iteratee.flatten(combinedEnumerator(consume)).run

      eventuallyIteratee.map(_ ==> "RedBlueGreenGreyOrangeYellow")
    }

    "symbolic methods" - {
      "|>>" in {
        val enumerateUsers:   Enumerator[String]       = Enumerator("Tom", "Lucy", "Lily", "Jack")
        val consume:          Iteratee[String, String] = Iteratee.consume[String]()
        val eventuallyResult: Future[String]           = Iteratee.flatten(enumerateUsers |>> consume).run
        eventuallyResult.map(_ ==> "TomLucyLilyJack")
      }

      ">>>" in {
        val colors     = Enumerator("Red", "Blue", "Green")
        val moreColors = Enumerator("Grey", "Orange", "Yellow")
        val consume    = Iteratee.consume[String]()

        val eventuallyIteratee = Iteratee.flatten(colors >>> moreColors |>> consume).run
        eventuallyIteratee.map(_ ==> "RedBlueGreenGreyOrangeYellow")
      }

      ">-" in {
        val colors     = Enumerator("Red", "Blue", "Green")
        val moreColors = Enumerator("Grey", "Orange", "Yellow")
        val consume    = Iteratee.consume[String]()

        val eventuallyIteratee = Iteratee.flatten(colors >- moreColors |>> consume).run
        eventuallyIteratee.map(_ ==> "RedBlueGreenGreyOrangeYellow")
      }
    }

    "from file" in {
      //noinspection ScalaUnusedSymbol
      val fileEnumerator: Enumerator[Array[Byte]] =
        Enumerator.fromFile(new File("./README.md"))

      Future(1).map(_ >>> ((): Unit))
    }

    "use akka" in {
      import akka.pattern.after
      import scala.concurrent.duration._
      val system = ActorSystem()
      val timeStream = Enumerator.generateM {
        after(100.milliseconds, system.scheduler) {
          Future(Some(new Date))
        }
      }

      val sink: Iteratee[Date, Unit] = Iteratee.foreach[Date] { date =>
        println(date)
      }

      Iteratee
        .flatten(timeStream |>> sink)
        .run
        .map(_ >>> ((): Unit))
    }

    "channel" in {
      val enumerator = Concurrent.unicast[String](onStart = channel => {
        channel.push("Hello")
        channel.push("World")
        channel.eofAndEnd()
      })

      Iteratee
        .flatten(enumerator |>> Iteratee.foreach(println))
        .run
        .map(_ >>> ((): Unit))
    }

    "Enumeratee adapt Iteratee " in {
      val strings: Enumerator[String] = Enumerator("1", "2", "3", "4")
      val sum   = Iteratee.fold[Int, Int](0)((s, e) => s + e)
      val toInt = Enumeratee.map[String](s => s.toInt)

      val adaptedIteratee: Iteratee[String, Int] = toInt.transform(sum)
      val run:             Future[Int]           = Iteratee.flatten(strings |>> adaptedIteratee).run
      run.map(_ ==> 10)

      // symbolic

      val run2 = Iteratee.flatten(strings |>> toInt &>> sum).run
      run2.map(_ ==> 10)
    }

    "Enumeratee adapt Enumerator " in {
      val strings: Enumerator[String] = Enumerator("1", "2", "3", "4")
      val sum   = Iteratee.fold[Int, Int](0)((s, e) => s + e)
      val toInt = Enumeratee.map[String](s => s.toInt)

      val adaptedEnumerator = strings.through(toInt)
      val run: Future[Int] = Iteratee.flatten(adaptedEnumerator |>> sum).run
      run.map(_ ==> 10)

      // symbolic

      val run2 = Iteratee.flatten(strings &> toInt |>> sum).run
      run2.map(_ ==> 10)
    }

    "applyOn vs transform" in {
      val sum   = Iteratee.fold[Int, Int](0)((s, e) => s + e)
      val toInt = Enumeratee.map[String](s => s.toInt)
      val adaptedSum: Iteratee[String, Iteratee[Int, Int]] = toInt(sum) // no transform

      val push1 = Enumerator("1", "2", "3", "4") |>> adaptedSum

      val run1: Future[Iteratee[Int, Int]] = Iteratee.flatten(push1).run

      Iteratee.flatten(run1).run.map(_ ==> 10)

      val originalIteratee: Iteratee[Int, Int]         = Iteratee.flatten(run1)
      val moreInts:         Future[Iteratee[Int, Int]] = Enumerator(5, 6, 7) |>> originalIteratee
      val sumFuture:        Future[Int]                = Iteratee.flatten(moreInts).run

      sumFuture.map(_ ==> 28)
    }

    "Enumeratee methods" in {
      val sum              = Iteratee.fold[Int, Int](0)((s, e) => s + e)
      val nums             = Enumerator(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
      // filter ,take, drop,
      // map,mapInput
      val filterEnumeratee = Enumeratee.filter[Int](a => a % 2 != 0)

      Iteratee.flatten(nums &> filterEnumeratee |>> sum).run.map(_ ==> 25)
    }
  }
}
