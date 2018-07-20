/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.akka._0002

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.TestKit
import me.ooon.orison.base.test.BaseSpecLike

/**
  * StartStopActorSpec
  *
  * actor 创建与消亡
  *
  * @author zhaihao
  * @version 1.0 2018/7/20 11:45
  */
class StartStopActorSpec extends TestKit(ActorSystem("test-system")) with BaseSpecLike {
  "test" in {
    val firstRef = system.actorOf(Props[StartStopActor1], "first")

    firstRef ! "stop"
  }
}

class StartStopActor1 extends Actor {

  override def preStart() = {
    println("first started")
    context.actorOf(Props[StartStopActor2], "second")
  }

  override def postStop() = println("first stopped")

  override def receive = {
    case "stop" => context.stop(self)
  }
}

class StartStopActor2 extends Actor {

  override def preStart(): Unit = println("second started")

  override def postStop(): Unit = println("second stopped")

  override def receive = Actor.emptyBehavior
}
