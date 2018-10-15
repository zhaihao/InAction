/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.akka.routing._001

import akka.actor.{Actor, ActorSystem, Props, Terminated}
import akka.routing._
import akka.testkit.TestKit
import me.ooon.base.test.BaseSpecLike
import scala.concurrent.duration._

/**
  * RouterTypeSpec
  *
  * akka 内置的几种 router
  *
  * @author zhaihao
  * @version 1.0 2018/9/17 14:03
  */
class RouterTypeSpec extends TestKit(ActorSystem()) with BaseSpecLike {

  "round-robin" in {
    val master = system.actorOf(Master.props("round-robin"), "master")
    for (i <- 0 until 20) {
      val m = Work(i.toString)
      master ! m
    }

    expectNoMessage()
  }

  "random" in {
    val master = system.actorOf(Master.props("random"), "master")
    for (i <- 0 until 20) {
      val m = Work(i.toString)
      master ! m
    }

    expectNoMessage()
  }

  "smallest-mailbox" in {
    val master = system.actorOf(Master.props("smallest-mailbox"), "master")
    for (i <- 0 until 20) {
      val m = Work(i.toString)
      master ! m
    }

    expectNoMessage()
  }

  "broadcast" in {
    val master = system.actorOf(Master.props("broadcast"), "master")
    for (i <- 0 until 20) {
      val m = Work(i.toString)
      master ! m
    }

    expectNoMessage()
  }

  "first-complete" in {
    val master = system.actorOf(Master.props("first-complete"), "master")
    for (i <- 0 until 20) {
      val m = Work(i.toString)
      master ! m
    }

    expectNoMessage()
  }

  "tail-chopping" in {
    val master = system.actorOf(Master.props("tail-chopping"), "master")
    for (i <- 0 until 20) {
      val m = Work(i.toString)
      master ! m
    }

    expectNoMessage()
  }

  "consistent-hashing" in {
    val master = system.actorOf(Master.props("consistent-hashing"), "master")
    for (i <- 0 until 20) {
      val m = Work(i.toString)
      master ! m
    }

    expectNoMessage()
  }

}

final case class Work(payload: String)

object Master {
  def props(kind: String) = Props(new Master(kind))
}

class Master(kind: String) extends Actor {

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[Worker])
      context.watch(r)
      ActorRefRoutee(r)
    }

    Router(
      kind match {
        case "round-robin"      => RoundRobinRoutingLogic() // 逐个轮询
        case "random"           => RandomRoutingLogic() //随机
        case "smallest-mailbox" => SmallestMailboxRoutingLogic() // 最小邮箱
        case "broadcast"        => BroadcastRoutingLogic() // 广播，发所有人
        case "first-complete"   => ScatterGatherFirstCompletedRoutingLogic(3.seconds)
        // 广播给所有人，只有第一个完成的结果会返回给sender
        case "tail-chopping" =>
          TailChoppingRoutingLogic(context.system.scheduler,
                                   3.seconds,
                                   3.seconds,
                                   context.dispatcher)
        // 随机选取一个发送消息，一小段延迟后再随机选取剩下中的一个发送消息, 直到收到第一个返回，并将结果返回，

        case "consistent-hashing" =>
          ConsistentHashingRoutingLogic(context.system, 5) // 一致性hash, 消息必须 提供 hash 规则
      },
      routees
    )
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
