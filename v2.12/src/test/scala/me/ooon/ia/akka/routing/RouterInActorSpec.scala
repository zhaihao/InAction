/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.akka.routing

import akka.actor.{Actor, ActorSystem, Props, Terminated}
import akka.routing._
import akka.testkit.TestKit
import me.ooon.orison.base.test.BaseSpecLike

/**
  * RouterInActorSpec
  *
  * 在 Actor 内部管理 routee
  *
  * @author zhaihao
  * @version 1.0 2018/9/17 11:38
  */
class RouterInActorSpec extends TestKit(ActorSystem()) with BaseSpecLike {
  "router in actor" in {
    val master = system.actorOf(Props[Master], "master")
    for (i <- 0 to 20) {
      val m = Work(i.toString)
      master ! m
    }

    expectNoMessage()
  }
}

final case class Work(payload: String)

class Master extends Actor {

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[Worker])
      context.watch(r)
      ActorRefRoutee(r)
    }

    Router(new RoundRobinRoutingLogic, routees)
  }

  override def receive = {
    case w: Work => router.route(w, sender())
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[Worker])
      context watch r
      router = router.addRoutee(a)
  }
}

class Worker extends Actor {
  override def receive = {
    case sa => println(s"$self 收到了 $sa")
  }
}
